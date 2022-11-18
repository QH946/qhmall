package com.qh.qhmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qh.common.utils.PageUtils;
import com.qh.common.utils.Query;
import com.qh.qhmall.product.dao.CategoryDao;
import com.qh.qhmall.product.entity.CategoryBrandRelationEntity;
import com.qh.qhmall.product.entity.CategoryEntity;
import com.qh.qhmall.product.service.CategoryBrandRelationService;
import com.qh.qhmall.product.service.CategoryService;
import com.qh.qhmall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }


    /**
     * 查出所有分类以及子分类，以树形结构组装起来
     *
     * @return {@link List}<{@link CategoryEntity}>
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        //1查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        //2组装成父子的树形结构
        //2.1找到所有的一级分类，给children设置子分类
        return entities.stream()
                // 过滤找出一级分类
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                // 处理，给一级菜单递归设置子菜单
                .peek(menu -> menu.setChildren(getChildren(menu, entities)))
                // 按sort属性排序
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());
    }

    /**
     * 批量删除分类
     *
     * @param asList 正如列表
     */
    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查当前删除的菜单，是否被别的地方引用
        List<CategoryBrandRelationEntity> categoryBrandRelation =
                categoryBrandRelationService.list(new QueryWrapper<CategoryBrandRelationEntity>()
                        .in("catelog_id", asList));
        if (categoryBrandRelation.size() == 0) {
            //逻辑删除
            baseMapper.deleteBatchIds(asList);
        } else {
            throw new RuntimeException("该菜单下面还有属性，无法删除!");
        }
    }


    /**
     * 查询catelogId的完整路径；
     *
     * @param catelogId catelog id
     * @return {@link Long[]}
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        //递归查询是否还有父节点
        List<Long> parentPath = findParentPath(catelogId, paths);
        //进行逆序排列
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新分类的所有关联的数据
     *
     * @param category 类别
     */
    @CacheEvict(value = {"category"}, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCascade(CategoryEntity category) {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("catalogJson-lock");
        //创建写锁
        RLock rLock = readWriteLock.writeLock();
        try {
            rLock.lock();
            //更新分类表中的分类名称
            this.baseMapper.updateById(category);
            //更新关联表中的分类
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        //同时修改缓存中的数据
        //删除缓存,等待下一次主动查询进行更新
    }

    /**
     * 查询所有的一级分类
     *
     * @return {@link List}<{@link CategoryEntity}>
     */
    @Cacheable(value = {"category"}, key = "#root.method.name", sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categories() {
//        System.out.println("get Level 1 Categories........");
//        long l = System.currentTimeMillis();
        //parent_cid为0则是一级目录
        List<CategoryEntity> categoryEntities = baseMapper.selectList(
                new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
//        System.out.println("消耗时间：" + (System.currentTimeMillis() - l));
        return categoryEntities;
    }


    /**
     * 查询二级、三级分类数据
     *
     * @return {@link Map}<{@link String}, {@link List}<{@link Catelog2Vo}>>
     */
    @Cacheable(value = "category", key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        System.out.println("查询了数据库");
        //将数据库的多次查询变为一次
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);
        //查询所有分类
        //查询所有一级分分类并封装成vo
        List<CategoryEntity> level1Categories = getParentCid(selectList, 0L);
        return level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //查询一级分类的二级分类并封装成vo
            List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo =
                            new Catelog2Vo(v.getCatId().toString(), null,
                                    l2.getCatId().toString(), l2.getName());
                    List<CategoryEntity> level3Catelog = getParentCid(selectList, l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.Category3Vo> category3Vos =
                                level3Catelog.stream().map(l3 -> new Catelog2Vo.Category3Vo(l2.getCatId().toString(),
                                        l3.getCatId().toString(), l3.getName())).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(category3Vos);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
    }

    public Map<String, List<Catelog2Vo>> getCatalogJson2() {
        //给缓存中放json字符串，拿出的json字符串，反序列为能用的对象
        /**
         * 1、空结果缓存：解决缓存穿透问题
         * 2、设置过期时间(加随机值)：解决缓存雪崩
         * 3、加锁：解决缓存击穿问题
         */
        //1、加入缓存逻辑,缓存中存的数据是json字符串
        //JSON跨语言。跨平台兼容。
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String catalogJson = ops.get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            System.out.println("缓存不命中...查询数据库...");
            //2、缓存中没有数据，查询数据库
            return getCatalogJsonFromDbWithRedissonLock();
        }
        System.out.println("缓存命中...直接返回...");
        //转为指定的对象
        return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
    }

    /**
     * 从数据库查询并封装数据::分布式锁
     * <p>
     * 解决缓存数据一致性问题
     * 1)、双写模式
     * 2)、失效模式
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedissonLock() {
        //（锁的粒度，越细越快:具体缓存的是某个数据，11号商品） product-11-lock
        //RLock catalogJsonLock = redissonClient.getLock("catalogJson-lock");
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("catalogJson-lock");
        RLock rLock = readWriteLock.readLock();
        Map<String, List<Catelog2Vo>> dataFromDb;
        try {
            rLock.lock();
            //加锁成功...执行业务
            dataFromDb = getDataFromDb();
        } finally {
            rLock.unlock();
        }
        return dataFromDb;
    }

    /**
     * 从数据库查询并封装数据::分布式锁
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedisLock() {
        //1、占分布式锁。去redis占坑设置过期时间必须和加锁是同步的，保证原子性（避免死锁）
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式锁成功...");
            Map<String, List<Catelog2Vo>> dataFromDb;
            try {
                //加锁成功...执行业务
                dataFromDb = getDataFromDb();
            } finally {
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                //删除锁
                stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Collections.singletonList("lock"), uuid);

            }
            return dataFromDb;
        } else {
            System.out.println("获取分布式锁失败...等待重试...");
            //加锁失败...重试机制
            //休眠一百毫秒
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //自旋的方式
            return getCatalogJsonFromDbWithRedisLock();
        }
    }

    /**
     * 查询二级三级分类数据::redis缓存
     *
     * @return {@link Map}<{@link String}, {@link List}<{@link Catelog2Vo}>>
     */
    private Map<String, List<Catelog2Vo>> getDataFromDb() {
        //得到锁以后，我们应该再去缓存中确定一次，如果没有才需要继续查询
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (!StringUtils.isEmpty(catalogJson)) {
            //缓存不为空直接返回
            return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
        }
        System.out.println("查询了数据库");
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);
        List<CategoryEntity> level1Categories = getParentCid(selectList, 0L);
        Map<String, List<Catelog2Vo>> parentCid =
                level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                    List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getCatId());
                    List<Catelog2Vo> catelog2Vos = null;
                    if (categoryEntities != null) {
                        catelog2Vos = categoryEntities.stream().map(l2 -> {
                            Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null,
                                    l2.getCatId().toString(), l2.getName());
                            List<CategoryEntity> level3Catelog = getParentCid(selectList, l2.getCatId());
                            if (level3Catelog != null) {
                                List<Catelog2Vo.Category3Vo> category3Vos = level3Catelog.stream().map(l3 -> {
                                    Catelog2Vo.Category3Vo category3Vo =
                                            new Catelog2Vo.Category3Vo(l2.getCatId().toString(),
                                                    l3.getCatId().toString(), l3.getName());
                                    return category3Vo;
                                }).collect(Collectors.toList());
                                catelog2Vo.setCatalog3List(category3Vos);
                            }
                            return catelog2Vo;
                        }).collect(Collectors.toList());
                    }
                    return catelog2Vos;
                }));
        //将查到的数据放入缓存,将对象转为json
        String valueJson = JSON.toJSONString(parentCid);
        stringRedisTemplate.opsForValue().set("catalogJson", valueJson, 1, TimeUnit.DAYS);
        return parentCid;
    }

    /**
     * 从数据库查询并封装数据::本地锁
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithLocalLock() {
        // //如果缓存中有就用缓存的
        // Map<String, List<Catelog2Vo>> catalogJson = (Map<String, List<Catelog2Vo>>) cache.get("catalogJson");
        // if (cache.get("catalogJson") == null) {
        //     //调用业务
        //     //返回数据又放入缓存
        // }
        //只要是同一把锁，就能锁住这个锁的所有线程
        //1、synchronized (this)：SpringBoot所有的组件在容器中都是单例的。
        //TODO 本地锁：synchronized，JUC（Lock),在分布式情况下，想要锁住所有，必须使用分布式锁
        synchronized (this) {
            //得到锁以后，我们应该再去缓存中确定一次，如果没有才需要继续查询
            return getDataFromDb();
        }

    }

    /**
     * 查询当前等级分类下的子级分类
     *
     * @param selectList 选择列表
     * @param parentCid  父母cid
     * @return {@link List}<{@link CategoryEntity}>
     */
    private List<CategoryEntity> getParentCid(List<CategoryEntity> selectList, Long parentCid) {
        return selectList
                .stream()
                .filter(item -> item.getParentCid()
                        .equals(parentCid))
                .collect(Collectors.toList());
    }

    /**
     * 查询当前节点的父节点路径
     *
     * @param catelogId catelog id
     * @param paths     路径
     * @return {@link List}<{@link Long}>
     */
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }


    /**
     * 递归查找所有菜单的子菜单
     *
     * @param root 根
     * @param all  所有
     * @return {@link List}<{@link CategoryEntity}>
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {

        return all.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid().equals(root.getCatId()))
                .peek(categoryEntity -> {
                    //找到子菜单
                    categoryEntity.setChildren(getChildren(categoryEntity, all));
                })
                //排序菜单
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());
    }

}
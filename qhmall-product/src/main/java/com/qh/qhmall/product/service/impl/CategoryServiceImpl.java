package com.qh.qhmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qh.common.utils.PageUtils;
import com.qh.common.utils.Query;
import com.qh.qhmall.product.dao.CategoryDao;
import com.qh.qhmall.product.entity.CategoryEntity;
import com.qh.qhmall.product.service.CategoryBrandRelationService;
import com.qh.qhmall.product.service.CategoryService;
import com.qh.qhmall.product.vo.Catalogs2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private StringRedisTemplate redisTemplate;
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
        // TODO  1、检查当前删除的菜单，是否被别的地方引用
        // 逻辑删除
        baseMapper.deleteBatchIds(asList);
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
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }


    /**
     * 级联更新分类的所有关联的数据
     *
     * @param category 类别
     */
    @CacheEvict(value = {"category"},allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCascade(CategoryEntity category) {
        //更新分类表中的分类名称
        this.updateById(category);
        //更新关联表中的分类
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
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
     * 查询三级分类数据加Redisson分布式锁
     * 缓存里的数据如何和数据库的数据保持一致
     * <p>
     * 1)、双写模式
     * 2)、失效模式
     *
     * @return {@link Map}<{@link String}, {@link List}<{@link Catalogs2Vo}>>
     */
    public Map<String, List<Catalogs2Vo>> getCatalogJsonFromDbWithRedissonLock() {
        //（锁的粒度，越细越快:具体缓存的是某个数据，11号商品） product-11-lock
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("catalogJson-lock");
        RLock rLock = readWriteLock.readLock();
        Map<String, List<Catalogs2Vo>> dataFromDb;
        try {
            rLock.lock();
            //加锁成功...执行业务
            dataFromDb = getCatalogJsonFromDB();
        } finally {
            rLock.unlock();
        }
        return dataFromDb;
    }


    /**
     * 从数据库查询并封装数据::分布式锁
     *
     * @return {@link Map}<{@link String}, {@link List}<{@link Catalogs2Vo}>>
     */
    public Map<String, List<Catalogs2Vo>> getCatalogJsonFromDbWithRedisLock() {
        //占分布式锁,去redis占坑置过期时间必须和加锁是同步的，保证原子性（避免死锁）
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式锁成功...");
            Map<String, List<Catalogs2Vo>> dataFromDb;
            try {
                //加锁成功...执行业务
                dataFromDb = getCatalogJsonFromDB();
            } finally {
                // lua 脚本解锁
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                // 删除锁
                redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList("lock"), uuid);
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
     * 获取二级、三级分类数据
     *
     * @return {@link Map}<{@link String}, {@link List}<{@link Catalogs2Vo}>>
     */
    @Override
    public Map<String, List<Catalogs2Vo>> getCatalogJson() {
        //从缓存中读取分类信息
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            //若缓存中没有，则查询数据库
            Map<String, List<Catalogs2Vo>> catalogJsonFromDB = getCatalogJsonFromDB();
            //查询到的数据存放到缓存中，将对象转成 JSON 存储
            redisTemplate.opsForValue().set("catalogJSON", JSON.toJSONString(catalogJsonFromDB));
            return catalogJsonFromDB;
        }
        return JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catalogs2Vo>>>() {
        });
    }

    /**
     * 获取二级、三级分类数据（加缓存前,只读取数据库）
     *
     * @return {@link Map}<{@link String}, {@link List}<{@link Catalogs2Vo}>>
     */
    public Map<String, List<Catalogs2Vo>> getCatalogJsonFromDB() {
        System.out.println("查询了数据库");
        //性能优化：将数据库的多次查询变为一次，查询所有分类
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //查询一级分类
        List<CategoryEntity> level1Categories = getParentCid(selectList, 0L);
        //封装数据
        return level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //查询每个一级分类的二级分类并封装成vo
            List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getCatId());
            List<Catalogs2Vo> catalogs2Vos = null;
            if (categoryEntities != null) {
                catalogs2Vos = categoryEntities.stream().map(l2 -> {
                    Catalogs2Vo catalogs2Vo =
                            new Catalogs2Vo(v.getCatId().toString(), null,
                                    l2.getCatId().toString(), l2.getName());
                    //查询当前二级分类的三级分类并封装成vo
                    List<CategoryEntity> level3Catelog = getParentCid(selectList, l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catalogs2Vo.Category3Vo> category3Vos =
                                level3Catelog
                                        .stream()
                                        .map(l3 -> new Catalogs2Vo.Category3Vo(l2.getCatId().toString(),
                                                l3.getCatId().toString(), l3.getName()))
                                        .collect(Collectors.toList());
                        catalogs2Vo.setCatalog3List(category3Vos);
                    }
                    return catalogs2Vo;
                }).collect(Collectors.toList());
            }
            return catalogs2Vos;
        }));
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
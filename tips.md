##  克隆必看

想要clone无缝运行该项目、知道以下几点是必备的。

### 1、项目整体架构

如果你能来到商城首页、那么你肯定在浏览器地址栏输⼊了qhamll.com、你的请求将会被nginx解析host地址、代理到qhamll-gateway网关、⽹关服务将根据保存的映射规则将你的请求负载均衡到合适的qhmall-product服务上、前提是这个服务能在nacos 上拉取到、最后qhmall-product会查询Redis/MySQL将页面的动态数据返回给你、而你需要的静态数据也会自动请求nginx服务。当然、只有这个请求是这样的。

### 2、依赖环境

项目是使用Maven搭建的聚合工程、通过父POM文件控制全局依赖版本、核心依赖版本如下

```
 <properties>
        <java.version>1.8</java.version>
        <qhmall.version>0.0.1-SNAPSHOT</qhmall.version>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <lombok.version>1.18.18</lombok.version>
        <mybatis-plus.version>3.4.2</mybatis-plus.version>
        <mysql.version>8.0.28</mysql.version>
        <http.components.version>4.4.13</http.components.version>
        <commons.lang.version>2.6</commons.lang.version>
        <spring.boot.version>2.2.5.RELEASE</spring.boot.version>
        <spring.cloud.version>Hoxton.SR3</spring.cloud.version>
        <cloud.alibaba.version>2.2.1.RELEASE</cloud.alibaba.version>
        <elasticsearch.version>7.4.2</elasticsearch.version>
    </properties>
```

### 3、插件

为加快开发效率、本项目使用了mybatis-plus和lombok插件、缺少lombok插件将无法编译!

### 4、三方组件


### 5、模块声明

```
    <modules>
        <module>renren-generator</module>        <!--不做介绍-->
        <module>renren-fast</module>             <!--不做介绍-->
        <module>qhmall-common</module>             <!--通用依赖和工具类的封装-->
        <module>qhmall-product</module>            <!--商品模块、最为核心-->
        <module>qhmall-gateway</module>            <!--统一网关、路由分发、与nginx对接-->
        <module>qhmall-member</module>             <!--会员相关服务、-->
        <module>qhmall-coupon</module>             <!--商品优惠价相关服务-->
        <module>qhmall-ware</module>               <!--商品库存相关-->
        <module>qhmall-order</module>              <!--订单模块-->
        <module>qhmall-search</module>             <!--前台检索模块、对接ES-->
        <module>qhmall-login-auth</module>         <!--登录认证模块-->
        <module>qhmall-third-party</module>        <!--第三方服务模块-->
        <module>qhmall-sso-server</module>         <!--单点登录测试-服务端模块-->
        <module>qhmall-sso-client</module>         <!--单点登录测试-客户端模块-->
        <module>qhmall-sso-client1</module>         <!--单点登录测试-客户端模块-->
        <module>qhmall-cart</module>               <!--购物车模块-->
    </modules>
```

### 6、想run一下？

#### 6.1、修改本地host文件、
商城域名及子域名映射如下、我的虚拟机地址是这个

xml
192.168.56.10  search.qhmall.com
192.168.56.10  qhmall.com
192.168.56.10  item.qhmall.com


#### 6.2、nginx.conf配置



#### 6.3、qhmall.conf配置






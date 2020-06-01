<h1 style="text-align:center;">常用技术</h1>

# 0 Java相关知识
## 0.1 JVM

## 0.2 程序性能优化

## 0.3 Tomcat

## 0.4 并发编程进阶

## 0.5 Netty

## 0.6 数据结构

## 0.7 软件工程



# 1 登录（SSO）
## 1.1 Shiro

## 1.2 Spring Security

# 2 前端框架

## 2.1 JS框架

* vue

## 2.2 UI 框架

* Bootstrap

* ElementUI

# 3 模板引擎
## 3.1 Apache Tiles

## 3.2 Themeleaf



# 4 Spring框架
## 4.1 Spring 

## 4.2 SpringMVC 

## 4.3 SpringBoot

## 4.4 SpringCloud



# 5 持久层
## 5.1 Mybatis

# 6 数据库
## 6.1 关系型：

* Oracle

* MySQL

## 6.2 非关系型：

* MongoDB 可视化工具[robomongo](https://robomongo.org/)
* Redis  redis可视化工具 [RedisDesktop](https://redisdesktop.com/)

## 6.3 数据库设计工具

* [PowerDesigner](http://powerdesigner.de/)

# 7 缓存
* EHCache

* Memcached

# 8 日志管理

# 9 数据检索
##  9.1 Solr

##  9.2 Elasticsearch 

# 10 文档编写
## 10.1 任务分配

* Project

## 10.2 流程图

* Viso
* [diagrams.net](ps://www.diagrams.net/)
* [processon](https://www.processon.com/)

## 10.3 思维导图

* Xmind
* [mindmaster](https://www.edrawsoft.cn/mindmaster/)

## 10.4 Markdown

* Typora

# 11 IDE
* Idea

* Eclipse

# 12 代码管理
## 12.1 版本管理

* SVN

* Git

## 12.2 jar包管理

* maven
* gradle

## 12.3 代码质量管理

* sonar

# 14 UI设计软件
* Mockups
* axure

	

# 15 系统监控
## 15.1 zabbix



# 16 持续集成
## 16.1 Jekins



# 17 负载均衡

## 17.1 客户端负载均衡

* ngnix 

	

# 18 消息处理
## 18.1 RabbitMQ

## 18.2 Kafka



# 19 BigData
## 19.1 Hadoop 2

## 19.2 Spark

## 19.3 Hive

## 19.4 Hbase



# 20 微服务
##  20.1 SpringCloud 

##  20.2 Dubbo



# 21 Python


# 22 Linux



# 23 工具/软件
## 23.1 http请求调试
* Postman

	

# 24 Office在线处理
## 24.1 openoffice



# 25 容器技术

* Docker

* K8S

	

# 26  JWT

https://jwt.io/introduction/

https://github.com/auth0/java-jwt

token包含三个组成部分

- Header
- Payload
- Signature

token格式：xxx.yyy.zzz

```java
 @Test
    public void createJWT(){
        //secret the secret to use in the verify or signing instance.
        Algorithm algorithm = Algorithm.HMAC256("123");
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("product","02");
        String token = JWT.create()
                .withHeader(headerClaims)//header
                .withIssuer("admin")//Payload
                .sign(algorithm);//algorithm
        System.out.println(token);

        /*
        header
        {
          "product": "02",
          "typ": "JWT",
          "alg": "HS256"
        }
        PAYLOAD:
        {
          "iss": "admin"
        }
         */
    }

    @Test
    public void verifyToken(){
        String token="eyJwcm9kdWN0IjoiMDIiLCJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhZG1pbiJ9.YC0_5I3Ux77euyw46UintYkflrr32G1RhctQOYfVmiA";
        Algorithm algorithm = Algorithm.HMAC256("123");
        Map<String, Object> headerClaims = new HashMap<>();
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("admin")//Payload
                .build(); //Reusable verifier instance
        DecodedJWT jwt = verifier.verify(token);
    }
```




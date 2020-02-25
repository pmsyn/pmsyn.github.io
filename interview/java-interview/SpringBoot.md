<h1 style="text-align:center">SpringBoot</h1>
# 1.环境设置

## 1.1 maven设置

在setting文件中配置jdk版本

```xml
<profile>
    <id>jdk-1.8</id>
    <activation>
        <activeByDefault>true</activeByDefault>
        <jdk>1.8</jdk>
    </activation>
    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>	  
    </properties>
</profile>
```

# 2.SpringBoot

## 2.1 SpringBoot 配置文件：

``` java
//1.加载全局配置文件（application.yml）中配置
@ConfigurationProperties("param")
// 2.加载指定配置文件 yml 字段和类字段一直 @PropertySource("")
// 3.导入spring xml 配置文件@ImportResource
// 4.使用java类进行配置，在类上加入注解 @Configuration 在方法上加上@Bean
Class Demo{
    
    @Bean("demoService")
    public Demo demoService(){
        new Demo();
    }
}
```

### 2.1.1 配置文件占位符

```yml
param:
  interval: ${random.int}
  #设置默认值
  charset2: ${param.charset:utf-8}
```



### 2.1.2 配置文件优先级

springboot启动会扫描一下位置的application.properties或者application.yml作为默认的配置文件
		工程根目录:./config/
		工程根目录：./
		classpath:/config/
		classpath:/

加载的优先级顺序是从上向下加载，并且所有的文件都会被加载，**高优先级的内容会覆盖底优先级的内容**，形成互补配置。

也可以通过指定配置spring.config.location来改变默认配置，一般在项目已经打包后，我们可以通过指令 
java -jar xxxx.jar --spring.config.location=D:/kawa/application.yml来加载外部的配置



[SpringBoot 官方文档](https://docs.spring.io/spring-boot/docs/2.2.4.RELEASE/reference/htmlsingle)

### 2.1.2 模板配置

使用webjar加入静态资源：[webjar查询](https://www.webjars.org/)

静态资源文件位置：

```
"classpath:/META-INF/resources/",
"classpath:/resources/",
"classpath:/static/", 
"classpath:/public/" 
/ 当前项目根路径
```


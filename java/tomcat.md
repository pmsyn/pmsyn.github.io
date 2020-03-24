### 一、Tomcat优化

## 1.1 关闭AJP

 ```xml
    <!-- Define an AJP 1.3 Connector on port 8009 -->
    <Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />
 ```



## 1.2 设置线程池

```xml
<Executor name="tomcatThreadPool" namePrefix="catalina-exec-"
    maxThreads="150" minSpareThreads="4" prestartminSpareThreads="true" maxQueueSize="100"/>
 <!--参数说明：
maxThreads：最大并发数，默认设置 200，一般建议在 500 ~ 1000，根据硬件设施和业
务来判断
minSpareThreads：Tomcat 初始化时创建的线程数，默认设置 25
prestartminSpareThreads： 在 Tomcat 初始化的时候就初始化 minSpareThreads 的
参数值，如果不等于 true，minSpareThreads 的值就没啥效果了
maxQueueSize，最大的等待队列数，超过则拒绝请求
‐‐>

<!--在Connector中设置executor属性指向上面的执行器 -->
<Connector executor="tomcatThreadPool"
               port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />
```


## 1.3 设置运行模式

tomcat的运行模式有3种：
1. bio
默认的模式,性能非常低下,没有经过任何优化处理和支持.
2. nio
nio(new I/O)，是Java SE 1.4及后续版本提供的一种新的I/O操作方式(即java.nio包及
其子包)。Java nio是一个基于缓冲区、并能提供非阻塞I/O操作的Java API，因此nio
也被看成是non-blocking I/O的缩写。它拥有比传统I/O操作(bio)更好的并发运行性
能。
3. apr
安装起来最困难,但是从操作系统级别来解决异步的IO问题,大幅度的提高性能.
推荐使用nio，不过，在tomcat8中有最新的nio2，速度更快，建议使用nio2.
设置nio2：

```xml
<Connector port="8443" protocol="org.apache.coyote.http11.Http11Nio2Protocol"
           maxThreads="150" SSLEnabled="true"/>
```


## 1.4 修改JVM参数

堆内存

初始内存
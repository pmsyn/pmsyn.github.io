
# 常见知识点

## 1. Volatile

Java虚拟机提供的轻量级同步机制（synchronize）

特性：

​		1.1、保证可见性
​		1.2、不保证原子性
​		1.3、禁止指令重排（有序性）

## 2. JMM内存模型

JMM(Java内存模型Java Memory Model,简称JMM)本身是一种抽象的概念**并不真实存在**，它描述的是一组规则或规范，通过这组规范定义了程序中各个变量(包括实例字段，静态字段和构成数组对象的元素)的访问方式。

JMM关于同步的规定:

1、线程解锁前，必须把共享变量的值刷新回主内存

2、线程加锁前，必须读取主内存的最新值到自己的工作内存

3、加锁解锁是同一把锁

由于JVM运行程序的实体是线程，而每个线程创建时JVM都会为其创建一个工作内存(有些地方称为栈空间)，工作内存是每个线程的私有数据区域，而Java内存模型中规定所有变量都存储在**主内存**，主内存是共享内存区域，所有线程都可以访问，**但线程对变量的操作(读取赋值等)必须在工作内存中进行，首先要将变量从主内存拷贝的自己的工作内存空间，然后对变量进行操作，操作完成后再将变量写回主内存**，不能直接操作主内存中的变量，各个线程中的工作内存中存储着主内存中的**变量副本拷贝**，因此不同的线程间无法访问对方的工作内存，线程间的通信(传值)必须通过主内存来完成，其简要访问过程如下图:

![image-20191120143840885](C:\Users\user\AppData\Roaming\Typora\typora-user-images\image-20191120143840885.png)

JMM特性：

　1.原子性（操作是不可分、操作不可被中断）：是指一个操作是不可中断的。即使是多个线程一起执行的时候，一个操作一旦开始，就不会被其他线程干扰。（synchronized、Lock）

　2.可见性（保障数据的一致，数据安全一部分）：是指当一个线程修改了某一个共享变量的值，其他线程是否能够立即知道这个修改。（Volatile、Synchronized）

 3.有序性（按照自己想要执行的顺序执行线程）：有序性是指程序在执行的时候，程序的代码执行顺序和语句的顺序是一致的。（Join）

![image-20191120144031235](C:\Users\user\AppData\Roaming\Typora\typora-user-images\image-20191120144031235.png)

![image-20191120144037164](C:\Users\user\AppData\Roaming\Typora\typora-user-images\image-20191120144037164.png)

![image-20191120144043480](C:\Users\user\AppData\Roaming\Typora\typora-user-images\image-20191120144043480.png)

## 3. 单例模式DCL（Double Check Lock）双端检锁机制

![image-20191120144133170](C:\Users\user\AppData\Roaming\Typora\typora-user-images\image-20191120144133170.png)

![image-20191120144138524](C:\Users\user\AppData\Roaming\Typora\typora-user-images\image-20191120144138524.png)



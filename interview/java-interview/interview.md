
# 常见知识点

## 1. Volatile

Java虚拟机提供的轻量级同步机制（synchronize）

特性：

1.1、保证可见性

​1.2、不保证原子性

​1.3、禁止指令重排（有序性）

## 2. JMM内存模型

JMM(Java内存模型Java Memory Model,简称JMM)本身是一种抽象的概念**并不真实存在**，它描述的是一组规则或规范，通过这组规范定义了程序中各个变量(包括实例字段，静态字段和构成数组对象的元素)的访问方式。

JMM关于同步的规定:

1、线程解锁前，必须把共享变量的值刷新回主内存

2、线程加锁前，必须读取主内存的最新值到自己的工作内存

3、加锁解锁是同一把锁

由于JVM运行程序的实体是线程，而每个线程创建时JVM都会为其创建一个工作内存(有些地方称为栈空间)，工作内存是每个线程的私有数据区域，而Java内存模型中规定所有变量都存储在**主内存**，主内存是共享内存区域，所有线程都可以访问，**但线程对变量的操作(读取赋值等)必须在工作内存中进行，首先要将变量从主内存拷贝的自己的工作内存空间，然后对变量进行操作，操作完成后再将变量写回主内存**，不能直接操作主内存中的变量，各个线程中的工作内存中存储着主内存中的**变量副本拷贝**，因此不同的线程间无法访问对方的工作内存，线程间的通信(传值)必须通过主内存来完成，其简要访问过程如下图:

![](https://github.com/pengmengsheng/pengmengsheng.github.io/blob/master/interview/java-interview/img/2%20(1).png)

JMM特性：

1.**原子性**（操作是不可分、操作不可被中断）：是指一个操作是不可中断的。即使是多个线程一起执行的时候，一个操作一旦开始，就不会被其他线程干扰。（synchronized、Lock）；

2.**可见性**（保障数据的一致，数据安全一部分）：是指当一个线程修改了某一个共享变量的值，其他线程是否能够立即知道这个修改。（Volatile、Synchronized）

3.**有序性**（按照自己想要执行的顺序执行线程）：有序性是指程序在执行的时候，程序的代码执行顺序和语句的顺序是一致的。（Join）

![](https://github.com/pengmengsheng/pengmengsheng.github.io/blob/master/interview/java-interview/img/2%20(2).png)

![](https://github.com/pengmengsheng/pengmengsheng.github.io/blob/master/interview/java-interview/img/2%20(3).png)

![](https://github.com/pengmengsheng/pengmengsheng.github.io/blob/master/interview/java-interview/img/2%20(4).png)

## 3. 单例模式DCL（Double Check Lock）双端检锁机制

 ![](https://github.com/pengmengsheng/pengmengsheng.github.io/blob/master/interview/java-interview/img/3%20(1).png)

![](https://github.com/pengmengsheng/pengmengsheng.github.io/blob/master/interview/java-interview/img/3%20(2).png)

## 4.CAS(比较并交换)
CAS的全称为Compare-And-Swap, **它是一条CPU并发原语**。
它的功能是判断内存某个位置的值是否为预期值，如果是则更改为新的值，这个过程是原子的。

CAS并发原语体现在JAVA语言中就是sun.misc.Unsafe类中的各个方法。调用UnSafe类中的CAS方法，JVM会帮我们实现出CAS汇编指令。这是一种完全依赖于硬件的功能，通过它实现了原子操作。再次强调，由于CAS是一种系统原语，原语属于操作系统用语范畴，是由若干条指令组成的，用于完成某个功能的一个过程，**并且原语的执行必须是连续的，在执行过程中不允许被中断，也就是说CAS是一条CPU的原子指令，不会造成所谓的数据不一-致问题。**

4.1 自旋锁

4.2 Unsafe

1.Unsafe是CAS的核心类，由于Java方法无法直接访问底层系统，需要通过本地(native)方法来访问，Unsafe相当于一个后门，基于该
类可以直接操作特定内存的数据。Unsafe类存在于sun.misc包中，其内部方法操作可以像C的指针- -样直接操作内存，因为Java中
CAS操作的执行依赖于Unsafe类的方法。


**注意**:Unsafe类中的所有方法都是native修饰的，也就是说Unsafe类中的方法都直接调用操作系统底层资源执行相应任务

2.变量valueOffset，表示该变量值在内存中的偏移地址，因为Unsafe就是根据内存偏移地址获取数据的。

	/**
     * Atomically sets to the given value and returns the old value.
     *
     * @param newValue the new value
     * @return the previous value
     */
    public final int getAndSet(int newValue) {
        return unsafe.getAndSetInt(this, valueOffset, newValue);
    }

3.变量value用volatile修饰，保证了多线程之间的内存可见性。


写作规范参考：[《中文技术文档的写作规范》](https://github.com/ruanyf/document-style-guide "中文技术文档的写作规范")
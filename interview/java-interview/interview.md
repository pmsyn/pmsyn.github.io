---
typorarooturl: img
---

[TOC]

 <h1 style="text-align:center;">常见知识点</h1>
## 1. Volatile

Java虚拟机提供的轻量级同步机制（synchronize）
**特性**：

1. **保证可见性**
2. **不保证原子性**

3. **禁止指令重排（有序性）**

## 2. JMM内存模型  
JMM(Java内存模型Java Memory Model)本身是一种抽象的概念**并不真实存在**，它描述的是一组规则或规范，通过这组规范定义了程序中各个变量(包括实例字段，静态字段和构成数组对象的元素)的访问方式。 

JMM关于同步的规定：  

1. 线程解锁前，必须把共享变量的值刷新回主内存；

2. 线程加锁前，必须读取主内存的最新值到自己的工作内存；

3. 加锁解锁是同一把锁；

由于JVM运行程序的实体是线程，而每个线程创建时JVM都会为其创建一个**工作内存(栈空间)**，工作内存是每个线程的私有数据区域，而Java内存模型中规定所有变量都存储在**主内存**，主内存是共享内存区域，所有线程都可以访问，**但线程对变量的操作(读取赋值等)必须在工作内存中进行，首先要将变量从主内存拷贝的自己的工作内存空间，然后对变量进行操作，操作完成后再将变量写回主内存**，不能直接操作主内存中的变量，各个线程中的工作内存中存储着主内存中的**变量副本拷贝**，因此不同的线程间无法访问对方的工作内存，线程间的通信(传值)必须通过主内存来完成，其简要访问过程如下图：

![](img/2%20(1).png)

JMM特性：

1. **原子性**（操作是不可分、操作不可被中断）：是指一个操作是不可中断的。即使是多个线程一起执行的时候，一个操作一旦开始，就不会被其他线程干扰。（Synchronized、Lock）；

2. **可见性**（保障数据的一致，数据安全一部分）：是指当一个线程修改了某一个共享变量的值，其他线程是否能够立即知道这个修改。（Volatile、Synchronized）

3.  **有序性**（按照自己想要执行的顺序执行线程）：有序性是指程序在执行的时候，程序的代码执行顺序和语句的顺序是一致的。（Join）

计算机在执行程序时，为了提高性能，编译器和处理器的常常会对指令做重排，一般分以下3种：
**源代码—><font color="red">编译器优化的重排</font>—><font color="red">指令并行的重排</font>—><font color="red">内存系统的重排</font>—>最终执行的指令**

单线程环境里面确保程序最终执行结果和代码顺序执行的结果一致。
处理器在进行重排序时必须要考虑指令之间的**数据优赖性**
多线程环境中线程交替执行，由于编译器优化重排的存在，两个线程中使用的变量能否保证一致性是无法确定的,结果无法预测。

volatile实现**禁止指令重排优化**。从而避免多线程环境下程序出现乱序执行的现象
**内存屏障(Memory Barrier)**又称内存栅栏，是一个CPU指令，作用:

* 保证特定操作的执行顺序。

* 保证某些变量的内存可见性(利用该特性实现volatile的内存可见性) 。

由于编译器和处理都能执行指令重排优化。如果在指令间插入一条Memory Barrier则会告诉编译器和CPU,不管什么指令都不能和这条Memory Barrier指令重排序，即**通过插入内存屏障禁止在内存屏障前后的指令执行重排序优化**。内存屏障另外一个作用是**强制刷出各种CPU的缓存数据**，因此任何CPU上的线程都能读取到这些数据的最新版本。

![](img/2%20(4).png)



## 3. 单例模式DCL（Double Check Lock）双端检锁机制

```java
//DCL (Quble Check Lock 双端检锁机制)
public static SingletonDemo getInstance(){
    if(instance== null){
        synchronized (SingletonDemo.class){
            if(instance == nu11){
                instance = new SingletonDemo();
            }
        }
    }
    return instance;
}
```

## 4.CAS(比较并交换)
CAS的全称为CompareAndSwap， **它是一条CPU并发原语**。  
它的功能是判断内存某个位置的值是否为预期值，如果是则更改为新的值，这个过程是原子的。  
CAS并发原语体现在JAVA语言中就是sun.misc.Unsafe类中的各个方法。调用UnSafe类中的CAS方法，JVM会帮我们实现出CAS汇编指令。这是一种完全依赖于硬件的功能，通过它实现了原子操作。再次强调，由于CAS是一种系统原语，原语属于操作系统用语范畴，是由若干条指令组成的，用于完成某个功能的一个过程，**并且原语的执行必须是连续的，在执行过程中不允许被中断，也就是说CAS是一条CPU的原子指令，不会造成所谓的数据不一致问题。**

## 4.1 自旋锁

Unsafe类

* 1.Unsafe是CAS的核心类，由于Java方法无法直接访问底层系统，需要通过本地(native)方法来访问，Unsafe相当于一个后门，基于该类可以直接操作特定内存的数据。Unsafe类存在于sun.misc包中，其内部方法操作可以像C的指针一样直接操作内存，因为Java中CAS操作的执行依赖于Unsafe类的方法。  
  **注意**：Unsafe类中的所有方法都是native修饰的，也就是说Unsafe类中的方法都直接调用操作系统底层资源执行相应任务 。
  
* 2.变量**valueOffset**，表示该变量值在内存中的偏移地址，因为Unsafe就是根据内存偏移地址获取数据的。

   ```java
   /**
   * Atomically sets to the given value and returns the old value.
	*
   * @param newValue the new value
	* @return the previous value
   */
   public final int getAndSet(int newValue) {
       return unsafe.getAndSetInt(this, valueOffset,newValue);
   } 
   ```
   
* 3.变量value用volatile修饰，保证了多线程之间的内存可见性。  
CAS>Unsafe>CAS底层思想>ABA>原子引用更新>如何避免ABA问题  
CAS算法实现一个重要前提需要取出内存中某时刻的数据并在当下时刻比较并替换，那么在这个时间差类会导致数据的变化。  
比如说一个线程one从内存位置V中取出A，这时候另一个线程two也从内存中取出A，并且线程two进行了一些操作将值变成了B，然后线程two又将V位置的数据变成A，这时候线程one进行CAS操作发现内存中仍然是A，然后线程one操作成功。
尽管线程one的CAS操作成功，但是不代表这个过程就是没有问题的。

**如何解决ABA**：
理解原子引用+新增一种机制，修改版本号（类似时间戳）
AtomicStampedRefrence 带时间戳原子引用

```java
//initialRef,initialStamp
AtomicStampedReference<Integer> stampedReference = new AtomicStampedReference<Integer>(1, 1);
//Returns the current value of the stamp
int expectedStamp = stampedReference.getStamp();
new Thread(() -> {
    while (true) {
        //Returns the current value of the reference
        Integer expectedReference = stampedReference.getReference();
        //expectedReference,newReference,expectedStamp,newStamp
        if(stampedReference.compareAndSet(1, expectedReference + 1, 1, expectedStamp + 1)){
            System.out.println("新值:"+stampedReference.getReference()+"，新时间戳："+stampedReference.getStamp());
            break;
        }
    }
}, "atomicStamedReference").start();
```

## 5.集合类不安全解决
### 5.1 List
1. Vector

2. Collections.synchronizedList

3. CopyOnWriteArrayList

### 5.2 Set

1. Collections.synchronizedSet

2. CopyOnWriteArraySet

### 5.3 Map
1. Collections.synchronizedMap

2. ConcurrentHashMap

## 6.锁
### 6.1公平锁/非公平锁
ReentrantLock **默认：NonfairSync（非公平锁）**，传入true，ReentrantLock(true)公平锁。

关于两者区别：  

* **公平锁：** Threads acquire a fair lock in the order in which they requested it  
公平锁，就是很公平，在并发环境中，每个线程在获取锁时会先查看此锁维护的等待队列，如果为空，或者当前线程是等待队列的第一个，就占有锁，否则就会加入到等待队列中，以后会按照 FIFO 的规则从队列中取到自己  
* **非公平锁：** a nonfair lock permits barging： threads requesting a lock can jump ahead of the queue of waiting threads if the lockhappens to be available when it is requested.  
非公平锁比较粗鲁，上来就直接尝试占有锁，如果尝试失败，就再采用类似公平锁那种方式。

### 6.2可重入锁（递归锁）：synchronized、ReentrantLock
线程可以进入任何一个他已经拥有的锁所同步着的代码块。

```java
public synchronized void a () {
	b();
}
public synchronized void b () {
	
}
```

### 6.3自旋锁（spinlock）
- 尝试获取锁的线程**不会立即阻塞**，而是**采用循环的方式去尝试获取锁**，这样的好处是减少线程上下文切换的消耗，缺点是循环会消耗CPU。

``` java
AtomicReferencec Thread> atomicReference=new AtomicReference<>();
public void myLock(){
    Thread thread =Thread.currentThread();
    while(!atomicReference.compareAndset(nu11, thread)){
        
    }
}
```

### 6.4独占锁（写锁）/共享锁（读锁）/互斥锁
* 独占锁：该锁只能被一个线程所持有。ReentrantLock 和Synchronized都是独占锁
* 共享锁：该锁可以被多个线程所持有 
ReentrantReadWriteLock 其读锁是共享锁，其写锁是独占锁。
读锁的共享锁可保证并发读是非常高效的，读写、写读、写写过程是互斥的。
	
	```java
	ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	//读锁
	lock.readLock().lock();
	//写锁
	lock.writeLock().lock();
	```

### 6.5.Lock显示锁
- 通过lock()方法上锁，unlock()释放锁。

	```JAVA
	Lock lock = new ReentrantLock();
	lock.lock();
	lock.unlock();
	```

### 7. 闭锁CountDownLatch
- CountDownLatch一个正数计数器，countDown方法对计数器做减操作，await方法等待计数器达到0。所有await的线程都会阻塞直到计数器为0或者等待线程中断或者超时。

``` java
CountDownLatch latch = new CountDownLatch(5);
try {
    //执行线程操作
    for(int i =0;i<6;i++) {
        new Thread(() >{
            latch.countDown();
        }，"线程"+i).start();				
    }
    //等待线程执行完成
    latch.await();
    System.out.println("完成");
} catch (InterruptedException e) {
    e.printStackTrace();
}
```


## 8.CyclicBarrier
栅栏类似于闭锁，它能阻塞一组线程直到某个事件的发生。

栅栏与闭锁的关键区别在于:

1. 所有的线程必须同时到达栅栏位置，才能继续执行。
2. 闭锁用于等待事件，而栅栏用于等待其他线程

```java
CyclicBarrier cyclicBarrier = new CyclicBarrier(8， () > {
    System.out.println("线程执行结束");
});

for(int i=0;i<8;i++) {
    new Thread(() >{
        System.out.println(Thread.currentThread().getName());
        try {
            //线程阻塞，直到所有线程执行完成
            cyclicBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }， "第"+i+"个线程");
}
```

**CountDownLatch**和**CyclicBarrier**的比较

1.  **CountDownLatch是线程组之间的等待**，即一个(或多个)线程等待N个线程完成某件事情之后再执行；而**CyclicBarrier则是线程组内的等待**，即每个线程相互等待，即N个线程都被拦截之后，然后依次执行。
2.  CountDownLatch是减计数方式，而CyclicBarrier是加计数方式。
3.  CountDownLatch计数为0无法重置，而CyclicBarrier计数达到初始值，则可以重置。
4.  CountDownLatch不可以复用，而CyclicBarrier可以复用。

## 9.Semaphore 信号灯/信号量
 主要作用：

1. 用于**多个共享资源的互斥使用**，

2. 用于**并发线程数的控制**。

``` java
//模拟6个线程使用3个资源
Semaphore semaphore = new Semaphore(3);
for(int i=1;i<=6;i++) {
    new Thread(() ->{
        try {
            semaphore.acquire();//获取资源
            System.out.println(Thread.currentThread().getName()+"获得资源");
            TimeUnit.SECONDS.sleep(3);
            System.out.println(Thread.currentThread().getName()+"释放资源");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            semaphore.release();
        }

    } , "线程"+i).start();
}
```

## 10.队列

* **ArrayBlockingQueue** ：由数组结构组成的有界阻塞队列。  
* **LinkedBlockingQueue**：由链表结构组成的有界队列(但大小默认值为Integer.MAX_VALUE)。  
* PriorityBlockingQueue：支持优先级排序的无界阻塞队列。  
* DelayQueue：使用优先级队列实现的延迟无界阻塞队列。  
* **SynchronousQueue**：不存储元素的阻塞队列，也即单个元素的队列  
* LinkedTransferQueue：由链麦结构组成的无界阻塞队列。  
* LinkedBlockingDeque：由链表结构组成的双向阻塞队列。

多线程判断使用while循环防止虚假唤醒

## 11.Synchornized和Lock区别联系
* 1.原始构成  
synchronized是关键字属于JVM层面，monitorenter(底层是通过tmoni tor对象来完成，其实wait/notify 等方法也依赖monitor对象只有在同步块或方法中才能调wait/notify等方法monitorexit   
Lock是具体类(java.util.concurrent.Locks.Lock)是api层面的锁

* 2.使用方法  
synchronized不需要用户去手动释放锁，当synchronized代码执 行完后系统会自动让线程释放对锁的占用；  ReentrantLock则需要用户去手动释放锁若没有主动释放锁，就有可能导致出现死锁现象。

* 3.等待是否可中断  
synchronized不可中断，除非抛出异常或者正常运行完成，ReentrantLock可中断，  
1.设置超时方法tryLock(Long timeout， TimeUnit unit)  
2.lockInterruptibly()放代码块中，调用interrupt() 方法可中断

* 4.加锁是否公平  
synchronized非公平锁
Reentrantlock两者都可以，默认非公平锁，构造方法可以传入boolean值， true 为公平锁，false为非公平锁

* 5.锁绑定多个条件Condition  
synchronized没有
Reentrantlock用来实现分组唤醒得要唤醒的线程们，可以精确唤醒， 而不是像synchronized 要么随机唤醒一个线程，要么唤醒全部线程。

## 12.Callable接口
带返回值的线程
    
```java
FutureTask result = new FutureTask<>(CallalbelImpl);//CallalbelImpl实现类
new Thread(result).start();
result.get();//获取返回值

```

FutureTask也可用于闭锁的操作。

## 13.线程池 
线程池做的工作主要是控制运行的线程的数量，处理过程中将任务放入队列，然后在线程创建后启动这些任务，如果线程数量超过了最大数量超出数量的线程排队等候，等其它线程执行完毕，再从队列中取出任务来执行。

### 13.1 线程池特点：
线程复用；控制最大并发数；管理线程。  

- 第一：降低资源消耗。通过重复利用己创建的线程降低线程创建和销毁造成的消耗。  
- 第二：提高响应速度。当任务到达时，任务可以不需要的等到线程创建就能立即执行。  
- 第三：提高线程的可管理性。线程是稀缺资源，如果无限制的创建，不仅会消耗系统资源，还会降低系统的稳定性，使用线程池可以进行统一的分配， 调优和监控。

```java
//一池5线程，执行长期的任务，性能好很多
ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
//一池1线程，一个任务执行的场景
ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
//一池N线程，执行很多短期异步的小程序或负载较轻的服务
ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
try {
	for(int i = 1;i<=10;i++){
				//执行
		fixedThreadPool.execute(() > {
        	System.out.println(Thread.currentThread().getName()+"处理任务");
         });
      }
} catch (Exception e) {
           
}finally{
	fixedThreadPool.shutdown();
}
```

### 13.2 ThreadPoolExecutor7个重要参数说明


```java
public ThreadPoolExecutor(int corePoolSize，//核心线程数
                          int maximumPoolSize，//最大线程数
                          long keepAliveTime，//空闲线程存活时间
                          TimeUnit unit，//存活时间单位
                          BlockingQueue<Runnable> workQueue，//任务队列
                          ThreadFactory threadFactory，//线程工程
                          RejectedExecutionHandler handler//拒绝策略)
{
    if (corePoolSize < 0 ||
        maximumPoolSize <= 0 ||
        maximumPoolSize < corePoolSize ||
        keepAliveTime < 0)
        throw new IllegalArgumentException();
    if (workQueue == null || threadFactory == null || handler == null)
        throw new NullPointerException();
    this.acc = System.getSecurityManager() == null ?
            null ：
            AccessController.getContext();
    this.corePoolSize = corePoolSize;
    this.maximumPoolSize = maximumPoolSize;
    this.workQueue = workQueue;
    this.keepAliveTime = unit.toNanos(keepAliveTime);
    this.threadFactory = threadFactory;
    this.handler = handler;
}
```

* 1.**corePoolSize**：线程池中的常驻核心线程数口
* 2.**maximumPoolSize**：线程池能够容纳同时执行的最大线程数，此值必须大于等于1
* 3.**keepAliveTime**：多余的空闲线程的存活时间。
 当前线程池数量超过corePoolsize时，当空闲时间达到keepAliveTime值时，多余空闲线程会被销毁直到只剩下corePoolSize个线程为止。
* 4.**unit**： keepAliveTime的 单位。
* 5.**workQueue**：任务队列，被提交但尚未被执行的任务。
* 6.threadFactory：表示生成线程池中工作线程的线程工厂，用于创建线程般用默认的即可。
* 7.handler：拒绝策略，表示当队列满了并且工作线程大于等于线程池的最大线程(maximumPoolSize)时如何来拒绝。

----
1. 在创建了线程池后，等待提交过来的任务请求。
2. 当调用execute()方法添加一个请求任务时，线程池会做如下判断：  
 	2.1 如果正在运行的线程数量小于corePoolSize，那么马上创建线程运行这个任务;  
	2.2 如果正在运行的线程数量大于或等于corePoolSize，那么将这个任务放入队列;  
	2.3 如果这时候队列满了且正在运行的线程数量还小于maximumPoolSize，那么还是要创建非核心线程立刻运行这个任务;  
	2.4 如果队列满了且正在运行的线程数量大于或等于maximumPoolSize，那么线程池会启动饱和拒绝策略来执行。  
3. 当一个线程完成任务时，它会从队列中取下一个任务来执行。
4. 当一个线程无事可做超过定的时间(keepAliveTime) 时，线程池会判断：
	* 如果当前运行的线程数大于corePoolSize，那么这个线程就被停掉。
	* 所以线程池的所有任务完成后它最终会收缩到corePoolSize的大小。


### 13.3 RejectedExecutionHandler：
**拒绝策略**：当任务数大于最大线程数(maximumPoolSize)+任务队列数(workQueue)时采取的策略

```java
ExecutorService threadPoolExecutor = new ThreadPoolExecutor(
		2,//corePoolSize
		5,//maximumPoolSize
		1,//keepAliveTime
		TimeUnit.SECONDS,//unit
		new LinkedBlockingQueue(3),//workQueue
		Executors.defaultThreadFactory(),//threadFactory
		new ThreadPoolExecutor.AbortPolicy());//handler
try {
    for(int i = 1;i<=9;i++){
        threadPoolExecutor.execute(() > {
            System.out.println(Thread.currentThread().getName());
        });
    }
} catch (Exception e) {
    threadPoolExecutor.shutdown();
}
```

1. **AbortPolicy(默认)**：直接抛出RejectedExecutionException 异常阻止系统正常运行。
2. **CallerRunsPolicy**："调用者运行"一种调节机制，该策略既不会抛弃任务，也不会抛出异常，而是将某些任务回退到调用者，从而降低新任务的流量。
3. **DiscardPolicy**：直接丢弃任务，不予任何处理也不抛出异常。如果允许任务丢失，这是最好的一一种方案。
4. **DiscardOldestPolicy**：拋弃队列中等待最久的任务，然后把当前任务加入队列中尝试再次提交当前任务。

**注意**：在线程池的使用过程中不要使用Executors创建线程池，由于 LinkedBlockingQueue的默认大小 为 Integer.MAX_VALUE，防止OOM。手动使用ThreadPoolExecutor创建线程池

### 13.4 配置合理线程数
1. CPU 密集型  
	* CPU 密集：该任务需要大量的运算，而没有阻塞，CPU 一直全速运行。
	* CPU 密集任务只有在真正的多核 CPU 上才可能得到加速(通过多线程)。  
	* CPU 密集型任务配置尽可能少的线程数量减少线程切换：  
	 **一般公式： CPU核数+1**

 	 CPU核数 = Runtime.getRuntime().availableProcessors()

2. IO 密集型  
IO 密集型，即该任务需要大量的 IO，即大量的阻塞。  
在单线程上运行 IO 密集型的任务会导致浪费大量的CPU运算能力浪费在等待。  
所以在 IO密集型任务中使用多线程可以大大的加速程序运行，即使在单核 CPU 上，这种加速主要就是利用了被浪费掉的阻塞时间。  
由于 IO 密集型任务线程并不是一直在执行任务，则应配置尽可能多的线程，如：
**CPU 核数\*2**

IO 密集型时，大部分线程都阻塞，故需要多配置线程数：

**参考公式**： CPU 核数/1阻塞系数  阻塞系数在 0.8~0.9 之间

比如 8 核 CPU： 8/10.9 = 80 个线程数

参考：[https：//blog.csdn.net/youanyyou/article/details/78990156](https：//blog.csdn.net/youanyyou/article/details/78990156 "")

## 14.死锁
### 14.1原因
死锁是指两个或两个以上的进程在执行过程中,因争夺资源而造成的一种互相等待的现象,若无外力干涉那它们都将无法推进下去，如果系统资源充足，进程的资源请求都能够得到满足，死锁出现的可能性就很低，否则就会因争夺有限的资源而陷入死锁。  
### 14.2 代码：

```java
class DeadLockDemo implements  Runnable{
    private String lockA;
    private String lockB;

    DeadLockDemo(String lockA,String lockB){
        this.lockA = lockA;
        this.lockB = lockB;
    }

    @Override
    public void run() {
        synchronized (lockA){
            System.out.println(Thread.currentThread().getName()+"持有"+lockA+"尝试持有："+lockB);
            try{ TimeUnit.SECONDS.sleep(2); }catch(Exception e){ }
            synchronized (lockB){
                System.out.println(Thread.currentThread().getName()+"持有"+lockB+"尝试持有："+lockA);
            }
        }
    }
}
```

### 14.3 解决办法：
* 查看进程：jps定位进程号 
* jstack找到死锁查看

## 15.JVM
### 15.1 JVM内存结构

![](img/JVM%E4%BD%93%E7%B3%BB%E7%BB%93%E6%9E%84%E6%A6%82%E8%A7%88.jpg)

### 15.2 GC作用域

### 15.3 常见的垃圾回收算法
* **引用计数**:有对象引用 引用计数加1无引用减1。不常用

* **复制**

	MinorGC的过程( 复制>清空>互换)
	
	1. eden、 SurvivorFrom 复制到SurvivorTo，年龄+1
	   首先，当Eden区 满的时候会触发第一次GC,把还活 着的对象拷贝到SurvivorFrom区，当Eden区再次触发GC的时候会扫描Eden区和From区域,对这两个区域进行垃圾回收，经过这次回收后还存活的对象，则直接复制到To区域(如果有对象的年龄已经达到了老年的标准，则赋值到老年代区)，同时把这些对象的年龄+1 。
	2. 清空eden、SurvivorFrom 然后，清空Eden和SurvivorFrom中的对象，也即复制之后有交换，谁空谁是to 
	3. SurvivorTo和 SurvivorFrom互换最后，SurvivorTo和SurvivorFrom互 换，原SurvivorTo成 为下一次GC时的SurvivorFrom区。部分对象会在From和To区域中复制来复制去，如此交换15次(由JVM参数MaxTenuringThreshold 决定，这个参数默认是15),最终如果还是存活，就存入到老年代。
	
* **标记清除(MarkSweep) **

  算法分成标记和清除两个阶段，先标记出要回收的对象，然后统一回收这些对象。

  <img src="img/%E6%A0%87%E8%AE%B0%E6%B8%85%E9%99%A4%E7%AE%97%E6%B3%95.jpg" style="zoom:80%;" />

  

* **标记整理**  
	
	![](img/%E6%A0%87%E8%AE%B0%E6%95%B4%E7%90%86%E7%AE%97%E6%B3%95.jpg)
## 16.GC Roots
- 垃圾：内存中已经不再被使用到的空间

- 如果判断一个对象是否可以被回收
	1. 引用计数法
	
	2. 可达性分析（根搜索路径）
	
	    作为GC Roots的对象：
	
	    * 虚拟机栈（栈帧中的局部变量区，也叫局部变量表）中引用的对象。
	    * 方法区中的类静态属性引用的对象。
	    * 方法区中常量引用的对象。
	    * 本地方法栈中的JNI（Native方法）引用对象。
## 17.JVM参数
### 17.1 JVM参数

* 标配参数：javaversion;help;showversion
* X参数（了解）：
	* Xint（解释执行）；
	* Xcomp（第一次使用就编译成本地代码）；
	* Xmixed（混合模式）
* **XX参数**
	* Boolean类型 **XX:+/开启（关闭）参数** 如：XX:+PrintGCDetails
	* KV设值类型 **XX:属性Key=属性value**  如：XX:MetaspaceSize=128m

### 17.2查看配置参数命令：

#### 17.2.1 查看参数第一种方法
- 查看具体参数配置：jinfo flag 参数名 进程号
- 显示所有配置：jinfo flags 进程号  
- 两个重要参数：  
	* **Xms**:初始堆内存等价于（XX:InitialHeapSize）  
	* **Xmx**:最大堆内存等价于（XX:MaxHeapSize）

#### 17.2.2 查看参数第二种方法
- 初始参数：java XX:+PrintFlagsInitial
- 修改后参数：java XX:+PrintFlagsFinal
- 运行时加的参数：java XX:+PrintFlagsFinal XX:MetaspaceSize=521m Test
- 查看默认垃圾收集器：java XX:+PrintCommandLineFlags version

参数符号说明：“:=” 是修改后的参数值而普通“=”是初始参数
### 17.3 常用参数
#### 17.3.1 Xms
初始内存，默认为物理内存1/64，等价于 XX:InitialHeapSize；
#### 17.3.2 Xmx
最大分配内存，默认为物理内存1/4，等价于 XX:MaxHeapSize；
#### 17.3.3  Xss
设置单个线程栈的大小，一般默认为512k~1024k，等价于 XX:ThreadStackSize
#### 17.3.4  Xmn
设置年轻代大小
#### 17.3.5  XX:+MetaspaceSize
设置元空间大小，元空间的本质和永久代类似，都是对JVM规范中方法区的实现，不过**元空间与永久代之间最大的区别在于**：**元空间并不在虚拟机中，而是使用本地内存**。因此，默认情况下，元空间的大小仅受本地内存限制。 

Xms128m Xmx4096m Xss1024k XX: MetaspaceSize=512m XX: PrintCommandLineFlags XX:+PrintGCDetails XX:+UseSeria1GC

#### 17.3.6  XX:+PrintGCDetails
打印垃圾回收日志 

[**GC** (Allocation Failure) [PSYoungGen: 1861K>488K(2560K)] 1861K>732K(9728K), 0.0084952 secs] [Times: user=0.00 sys=0.00, real=0.02 secs]   
[GC (Allocation Failure) [PSYoungGen: 488K>488K(2560K)] 732K>732K(9728K), 0.0012515 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]   
[**Full GC** (Allocation Failure) [PSYoungGen: 488K>0K(2560K)] [ParOldGen: 244K>632K(7168K)] 732K>632K(9728K), [Metaspace: 3358K>3358K(1056768K)], 0.0071535 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]     
[GC (Allocation Failure) [PSYoungGen: 0K>0K(2560K)] 632K>632K(9728K), 0.0003614 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]   
[Full GC (Allocation Failure) [PSYoungGen: 0K>0K(2560K)] [ParOldGen: 632K>615K(7168K)] 632K>615K(9728K), [Metaspace: 3358K>3358K(1056768K)], 0.0061335 secs] [Times: user=0.11 sys=0.00, real=0.01 secs]   
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space at interview.App.main(App.java:11)

日志说明：
![](img/GC%E6%97%A5%E5%BF%97%E4%BF%A1%E6%81%AF.jpg)

**GC规律**：GC类型 GC前内存>GC后内存（该区总内存）

#### 17.3.7 XX:SurvivorRatio

![](img/%E5%A0%86%E7%9A%84%E7%BB%93%E6%9E%84.jpg)

设置新生代中eden和S0/S1空间的比例 
默认: XX:SurvivorRatio=8,Eden:S0:S1=8:1:1  
例如：XX:SurvivorRatio=4,Eden:S0:S1=4:1:1  
SurvivorRatio值设置eden区比例占多少，S0/S1相同

**Heap 新生代堆空间（1/3）老年代堆空间（2/3）**  
**PSYoungGen total 2560K**, used 126K [0x00000000ffd00000, 0x0000000100000000, 0x0000000100000000)    **eden space 2048K**, 6% used [0x00000000ffd00000,0x00000000ffd1f9b0,0x00000000fff00000)  
  **from space 512K**, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)  
  **to   space 512K**, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)

 **ParOldGen       total 7168K**, used 615K [0x00000000ff600000, 0x00000000ffd00000, 0x00000000ffd00000) 
 object space 7168K, 8% used [0x00000000ff600000,0x00000000ff699e98,0x00000000ffd00000)  
 Metaspace       used 3461K, capacity 4496K, committed 4864K, reserved 1056768K  
  class space    used 379K, capacity 388K, committed 512K, reserved 1048576K

**MinorGC的过程( 复制>清空>互换) ** 
**1: eden、 SuryivorFrom复制到SuryivorTo， 年龄+1**  
首先，当Eden区满的时候会触发第一 次GC,把还活着的对象拷贝到SurvivorFrom区， 当Eden区再次触发GC的时候会扫描Eden区和From区域,对这两个区域进行垃圾回收，经过这次回收后还存活的对象,则直接复制到To区域(如果有对象的年龄已经达到了老年的标准，则赋值到老年代区)，同时把这些对象的年龄+1  
**2:清空eden、SurvivorFrom**  
然后，清空Eden和SurvivorFrom中的对象，也即复制之后有交换，谁空谁是to  
**3: SurvivorTo和 SurvivorFrom 互换**  
最后，SurvivorTo和SurvivorFrom互换，原SurvivorTo成为 下次GC时的SurvivorFrom区。部分对象会在From和To区域中复制来复制去，如此交换15次(由JVM参数axTenuringThreshold决定.这个参数默认是15.最终如果还是存活.就在入到老年代。

#### 17.3.8 XX:NewRatio
配置年轻代与老年代在堆结构的占比  
默认  
XX:NewRatio=2新生代占1，老年代2，年轻代占整个堆的1/3  
例如  
XX:NewRatjo=4新生代占1,老年代4，年轻代占整个堆的1/5  
NewRatio值就是设置老年代的占比，剩下的1给新生代

#### 17.3.9 XX:MaxTenuringThreshold 
设置垃圾最大年龄：XX:MaxTenuringThreshold=0；默认:15，值为015  
如果设置为0的话，则年轻代对象不经过Survivor区，直接进入年老代。对于年老代比较多的应用，可以提高效率。如果将此值设置为一个较大值，则年轻代对象会在Survivor区进行多次复制，这样可以增加对象再年轻代的存活时间，增加在年轻代即被回收的概论。


## 18 引用
### 18.1 强引用Reference
Object obj = new Object();

当内存不足，JVM开始垃圾回收，对于强引用的对象，就算是出现了OOM也不会对该对象进行回收，死都不收。  
强引用是我们最常见的普通对象引用，**只要还有强引用指向一个对象，就能表明对象还“活着”，垃圾收集器不会碰这种对象**。在Java中最常见的就是强引用，把一个对象赋给一个引用变量， 这个引用变量就是 一个强引用。当一个对象被强引用变量引用时，它处于可达状态，它是不可能被垃圾回收机制回收的，即使该对象以后永远都不会被用到JVM也不会回收。因此强引用是造成Java内存泄漏的主要原因之一。  
对于一个普通的对象，如果没有其他的引用关系，只要超过了引用的作用域或者显式地将相应(强)引用赋值为null,
一般认为就是可以被垃圾收集的了(当然具体回收时机还是要看垃圾收集策略)。

### 18.2软引用SoftReference
**内存足够的时候不回收，内存不够的时候进行回收**  
软引用是一种相对强引用弱化了一些的引用，需要用 java.lang.ref.SoftReference类来实现，可以让对象豁免一些垃圾收集。  
软引用通常用在对内存敏感的程序中，比如高速缓存就有用到软引用，内存够用的时候就保留，不够用就回收!

### 18.3 弱引用WeakReference
只要GC就进行回收，用 java.lang.ref.WeakReference类来实现

```java
Object obj = new Object();
WeakReference<Object> weakReference = new WeakReference(obj);
System.out.println(obj);//java.lang.Object@4554617c
System.out.println(weakReference.get());//java.lang.Object@4554617c
System.out.println("");
obj=null;
System.gc();
System.out.println(obj);//null
System.out.println(weakReference.get());//null

```

### 18.4 虚引用PhantomReference
虛引用需要 java.lang.ref.PhantomReference 类来实现。  

顾名思义，就是形同虚设，与其他几种引用都不同，虚引用并不会决定对象的生命周期。  
如果一个对象仅持有虚引用，那么它就和没有任何引用一样，在任何时候都可能被垃圾收集器回收，它不能单独使用也不能通过它访问对象，虚引用必须和引用队列(ReferenceQueue)联合使用。
虚引用的主要作用是跟踪对象被垃圾回收的状态。仅仅是提供了一种确保对象被 finalize 以后，做某些事情的机制。  
PhantomReference 的get方法总是返回null,因此无法访问对应的引用对象。其意义在于说明一个对 象已经进入 finalization 阶段，可以被gc回收， 用来实现比 fialization 机制更灵活的回收操作。
换句话说，设置虛引用关联的唯一目的，就是在这个对象被收集器回收的时候收到一个系统通知或者后续添加进一步的处理。  
Java技术允许使用finalize()方法在垃圾收集器将对象从内存中清除出去之前做必要的清理工作。

**GC 回收之前放到 ReferenceQueue 引用队列中**虚引用通知机制


```java
Object obj = new Object();
ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
PhantomReference phantomReference = new PhantomReference(obj,referenceQueue);
System.out.println("GC前");  
System.out.println(obj);//java.lang.Object@4554617c
System.out.println(phantomReference.get());//null
System.out.println(referenceQueue.poll());//null

System.out.println("GC后");
obj = null;
System.gc();
System.out.println(obj);//null
System.out.println(phantomReference.get());//null
System.out.println(referenceQueue.poll());//java.lang.ref.PhantomReference@74a14482  
```

## 19 OOM

### 19.1 java.lang.StackOverflowError

管运行   

### 19.2 java.lang.OutOfMemoryError  

 Java heap space 管存储  

#### 19.2.1 java.lang.OutOfMemoryError ：GC overhead limit exceeded  
xx : MaxDirectMemorysize= 5m  

* GC回收时间过长时会抛出OutOfMemroyError，**超过98%的时间用来做GC并且回收了不到2%的堆内存**
* 连续多次GC都只回收了不到2%的极端情况下才会抛出。假如不抛出GC overhead limit 错误会发生什么停况呢?
* 那就是GC清理的这么点内存很快会再次填满，迫使GC再次执行.这样就形成恶性循环,
* CPU使用率直是100%， 而GC 却没有任何成果

#### 19.2.2 java.lang.OutOfMemoryError：Direct buffer memory  

* 导致原因:
 写NIO程序经常使ByteBuffer来读取或者写入数据， 这是一种基于通道(Channel)|与缓冲区(Buffer)的I/0方式,
它可以使用Native函数库直接分配堆外内存，然后通过一个 存储在Java雄里面的DirectByteBuffer对象作为这块内存的引用进行操作。
这样能在些场景中显蓍提高性能，因为避免了在Java堆和Native堆中来回复制数据。  
ByteBuffer.allocate(capability)第种方式是分配JVM堆内存，属于GC 管辖范围，由于需要拷贝所以速度相对较慢  
ByteBuffer.allocteDirect(capability)第一种方式是分配OS 本地内存，不属FGC管辖范围，由于不需要内存拷贝所以速度相对较快。  
* 但如果不断分配本地内存， 堆内存很少使用，那么JVM就不需要执行GC, DirectByteBuffer对象 们就不会被回收,这时候堆内存充足，但本地内存可能已经使用光了，再次尝试分配本地内存就会出现OutOfMemoryError,那程序就直接崩溃了。

Xms5m Xmx5m XX:+PrintGCDetails XX:MaxDirectMemorySize=5m  

```java
System.out.println("初始JVM最大内存："+VM.maxDirectMemory());
ByteBuffer byteBuffer = ByteBuffer.allocateDirect(10*1024*1024);//10m
```
结果：  
```[GC (Allocation Failure) [PSYoungGen: 1024K->488K(1536K)] 1024K->592K(5632K), 0.0007910 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]  
初始JVM最大内存：5242880  
[GC (System.gc()) [PSYoungGen: 1313K>488K(1536K)] 1417K>688K(5632K), 0.0008659 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]  
[Full GC (System.gc()) [PSYoungGen: 488K>0K(1536K)] [ParOldGen: 200K>635K(4096K)] 688K>635K(5632K), [Metaspace: 3424K>3424K(1056768K)], 0.0056662 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]  
Exception in thread "main" java.lang.OutOfMemoryError: Direct buffer memory
```

#### 19.2.3 java.lang.OutOfMemoryError：unable to create new native thread  

非root用户登陆Linux系统测试；服务器级别调参调优

高并发请求服务器时，准确的讲native thread异常与对应的平台有关
导致原因:

  1. 你的应用创建 了太多线程了，一个应用进程创建多个线程,超过系统承裁极限；
    
  2. 你的服务器并不允许你的应用程序创建这么多线程, linux系统默认允许单个进程可以创建的线程数是1024个，你的应用创建超过这个数量,就会报**java. lang. OutOfMemoryError: unable to create new native thread**

解决办法: 

   1. 想办法降低应用程序创建线程的数量，分析应用是否真的需要创建这么多线程，如果不是，改代码将线程数降到最低；

   2. 对于有的应用,确实需要创建很多线程远超边Linux系统的默认1024个线程的限制,可以通过修改Linux服务器配置,扩大Linux默认限制。

#### 19.2.4 java.lang.OutOfMemoryError: Metaspace  

使用XX:PrintFlagsInitial 查看初始参数

JVM参数XX:Metaspacesize8m  XX:MaxMetaspacesize=8m
Java 8及之后的版本使用Metaspace来替代永久代。

Metaspace是方法在HotSpot中的实现，它与持久代最大的区别在于: Metaspace并不在虚拟机内存中而是使用本地内存也即在java8中class metadata(the virtual machines internal presentation of Java class), 被存储在叫做Metaspace 的native memory
永久代(java8后被原空间Metaspace取代)存放了以下信息:
虚拟机加载的类信息
常量池
静态变量
即时编译后的代码

## 20.垃圾收集器

GC算法（引用计数/复制/标记清除/标记整理）是内存回收的方法论，垃圾收集器是算法的具体实现。

查看默认收集器参数：XX:+CommandLineFlags

### 20.1 Serial(串行垃圾收集器)

![SerialGC](img%5CSerialGC.jpg)

串行收集器**采用单线程**stoptheworld的方式进行收集， 所以不适合服务器环境。它最适合单处理器计算机，因为它不能利用多处理器硬件，它在多处理器上对数据集较小（最大约100 MB）的应用很有用，因此Serial垃圾收集器依然是java虚拟机运行在Client模式下默认的新生代垃圾收集器。

```JVM参数：XX:+UseSerialGC ```

开启后默认使用：Serial（Young区） + Serial Old（Old区）
新生代使用复制算法，老年代使用标记整理算法

### 20.2 ParNew并行垃圾收集器）

使用多线程进行垃圾回收，在垃圾收集时，会StoptheWorld暂停其他所有的工作线程直到它收集结束。

**ParNew收集器其实就是Serial收集器新生代的并行多线程版本**，最常见的应用场景是**配合老年代的CMSGC**工作，其余的行为和Serial收集器完全一样， ParNew垃圾收 器在垃圾收集过程中同样也要暂停所有其他的工作线程。**它是很多java虚拟机运行在Server模式下新生代的默认垃圾收集器**。

常用对应JVM参数: XX:+UseParNewGC 启用ParNew收集器， 只影响新生代的收集，不影响老年代。
开启上述参数后，会使用: ParNew(Young区用) + Serial Old的收集器组合，**该组合不推荐使用，新生代使用复制算法，老年代采用标记整理算法**

XX：ParallelGCThreads 限制线程数量，默认开启和CPU数目相同的线程数

### 20.3 Parallel/Parallel Scavenge(并行垃圾收集器)

![ParellelGC](img%5CParellelGC.jpg)

并行收集器也称为吞吐量收集器，它是类似于串行收集器的分代收集器。**串行收集器和并行收集器之间的主要区别是并行收集器具有多个线程**，这些线程用于加速垃圾收集。使用复制算法，**串行收集器在新生代和老年代的并行化**。

并行收集器旨在用于具有在多处理器或多线程硬件上运行的中型到大型数据集的应用程序。

它重点关注的是:
可控制的吞吐量(**Thoughput=运行用户代码时间/(运行用户代码时间+垃圾收集时间),也即比如程序运行100分钟，垃圾收集时间1分钟，吞吐量就是99%**)。高吞吐量意味若高效利用CPU的时间，它多用于在后台运算而不需要太多交互的任务。
**自适应调节策略也是ParallelScavenge收集器与ParNew收集器的一个重要区别**。**自适应调节策略**:虚拟机会根据当前系统的运行情况收集性能监控信息，动态调整这些参数以提供最合适的停顿时间(XX:MaxGCPauseMilis)或最大的吞吐量。

**JVM参数**: XX:+UseParallelGC或XX:+UseParallelOldGC(可互相激活)使用Parallel Scanvenge收集器。

并行压缩是使并行收集器能够并行执行主要收集的功能。如果没有并行压缩，则使用单个线程执行主要集合，这会大大限制可伸缩性。如果`XX:+UseParallelGC`指定了该选项，则默认情况下启用并行压缩。您可以使用` XX:UseParallelOldGC `选项禁用它。

### 20.4 CMS(并发垃圾收集器)

![](img%5CCMSGC.jpg)

**用户线程和垃圾收集线程同时执行**(不一定是并行， 可能**交替执行)**，此收集器用于那些希望较短暂停的垃圾收集并能与垃圾收集共享处理器资源的应用程序。

如果在垃圾收集中花费了总时间的98％以上，而回收不到2％的堆，则抛出OutOfMemoryError 。

XX:+UseConcMarkSweepGC

**从JDK 9开始不推荐使用CMS收集器。推荐使用G1垃圾收集器**

###  20.5 Serial Old(MSC)

**SerialOld是Serial垃圾收集器老年代版本**，它同样是个单线程的收集器，使用标记整理算法，这个收集器也主要是运行在Client默认的java虚拟机默认的年老代垃圾收集器。
在Server模式下，主要有两个用途:

1. 在JDK1.5之 前版本中与新生代的Parallel Scavenge收集器搭配使用。(Parallel Scavenge + Serial Old )

2. 作为老年代版中使用CMS收集器的后备垃圾收集方案。

   **Jdk8后已弃用**

   ```verilog
   Error: Could not create the Java Virtual Machine.
   Error: A fatal exception has occurred. Program will exit.
   Unrecognized VM option 'UseSerialOldGC'
   Did you mean '(+/)UseSerialGC'?
   ```

### 20.6 ParallelOldGC

XX:+UseParallelOldGC 

### 20.7 G1垃圾收集器

G1垃圾收集器**将堆内存分割成不同的区域然后并发的对其进行垃圾回收**。

![](img%5CG1.jpg)

![](img/G1%E5%8C%BA%E5%9F%9F)

GarbageFirst（G1）垃圾收集器的目标是具有大量内存的多处理器计算机。它减少垃圾收集暂停时间，同时几乎不需要配置即可实现高吞吐量。G1的目标是使当前的目标应用程序和环境在延迟和吞吐量之间达到最佳平衡，其特点包括：

- 堆大小最大为数十GB或更大，其中超过50％的Java堆占用实时数据。
- 对象分配和升级的速率可能会随时间而显着变化。
- 可预测的暂停时间不超过几百毫秒，避免了长时间的垃圾收集暂停。

G1取代了并发标记扫描（CMS）收集器。作为jdk9的默认收集器

XX:+UseG1GC

G1与其他收集器主要区别：

- 并行GC只能从整体上压缩和回收旧一代中的空间。G1将这项工作逐步分配到多个较短的集合中。这大大缩短暂停时间吞吐量的潜在开销。
- 与CMS类似，G1并发执行部分旧空间回收。然而，CMS无法对旧堆进行碎片整理，最终会遇到较长的Full GC。
- 由于并发性吞吐量的影响，G1可能比其他收集器需要更高的开销。

由于其工作方式，G1具有一些独特的机制来提高垃圾收集效率：

- G1可以在任何收集期间回收一些旧的完全空的，较大的区域。这样可以避免许多其他不必要的垃圾收集，而无需付出很多努力即可释放大量空间。

- G1可以选择尝试同时对Java堆上的重复字符串进行重复数据删除。

常用参数：

* XX:+UseG1GC
* XX:G1HeapRegionSize=n:设置的G1区域的大小。值是2的幂，范围是1MB到32MB。目标是根据最小的Java堆大小划分出region区域
* XX:MxGCPauseMillis=n:最大GC停顿时间，这是个软目标，JVM将尽可能(但不保证)停顿小于这个时间
* XX:nitiatingHeapOccupancyPercent=n:堆占用了多少的时候就触发GC，默认为45
* XX:ConcGCThreads=n: 并发GC使用的线程数
* XX:G1ReservePercenten: 设置作为空闲空间的预留内存百分比，以降低目标空间溢出的风险，默认值是10%

### 20.8 ZGC垃圾收集器

Z垃圾收集器（ZGC）是可伸缩的低延迟垃圾收集器。ZGC同时执行所有昂贵的工作，而不会停止执行应用程序线程。

ZGC适用于要求低延迟（少于10毫秒的暂停）或使用非常大的堆（数TB）的应用程序。

XX:+UseZGC

从JDK 11开始，ZGC作为实验功能可用。

**YoungGC**:

1. Serial;

2. Parallel Scavenge;

3.  ParNew;


**Old Gen** : 

1. Serial Old( MSC)；

2. Parallel Old；

3. CMS;

**G1**:不在区分Young和Old区

<img src="img/GC.jpg" style="zoom:80%;" />

### 20.9 收集器的选择

除非您的应用程序有非常严格的暂停时间要求，否则请先运行您的应用程序选择收集器。

如有必要，请调整堆大小以提高性能。如果性能仍然不能满足您的目标，请使用以下准则作为选择收集器的起点：

- 如果应用程序的数据集较小（最大约100 MB），则选择带有选项的串行收集器`XX:+UseSerialGC`。
- 如果应用程序将在单个处理器上运行，并且没有暂停时间要求，则选择带有选项的串行收集器`XX:+UseSerialGC`。
- 如果（a）峰值应用性能是第一要务，并且（b）没有暂停时间要求，或者可接受一秒或更长时间的暂停，则让VM选择收集器或使用选择并行收集器`XX:+UseParallelGC`。
- 如果响应时间比整体吞吐量更重要，并且必须将垃圾收集暂停时间保持在大约一秒钟以内，那么请使用`XX:+UseG1GC`或选择一个主要是并发的收集器`XX:+UseConcMarkSweepGC`。
- 如果响应时间是高优先级或您使用的堆非常大，请使用选择一个完全并发的收集器`XX:UseZGC`。

这些准则仅提供选择收集器的起点，因为性能取决于堆的大小，应用程序维护的实时数据量以及可用处理器的数量和速度。

如果推荐的收集器没有达到期望的性能，则首先尝试调整堆和生成大小以达到期望的目标。如果性能仍然不足，请尝试使用其他收集器：使用并发收集器来减少暂停时间，并使用并行收集器来增加多处理器硬件上的总体吞吐量。

<table>
<tr>
<th> 参数</th><th>新生代收集器 </th><th> 新生代算法</th><th> 老年代收集器</th><th>老年代算法</th>
</tr>
<tr>
    <td> XX:+UseSerialGC </td><td>SerialGC</td><td width="120px">复制</td><td>SerialOldGC</td><td>标整</td> 
</tr>
<tr>
    <td>XX:+UseParNewGC</td><td>ParNewGC</td><td>复制</td><td>SerialOldGC</td><td>标整</td>
</tr>
<tr>
    <td>XX:+UseParellelGC/XX:+UseParellelGC</td><td> ParellelGC</td><td>复制</td><td>ParellelOldGC</td><td>标整</td>
</tr>
<tr>
<td>XX:+UseConcMarkSweepGC</td><td>ParNewGC</td><td>复制</td><td>SerialOldGC</td><td>标清 </td>
</tr>
<tr>
    <td>XX:+UseG1GC</td><td>G1GC</td><td colspan=3>整体采用标记整理，局部使用复制算法，不会产生内存碎片</td> </tr>
</table>


参考：[HotSpot Virtual Machine Garbage Collection Tuning Guide](https://docs.oracle.com/en/java/javase/12/gctuning,"HotSpot "Virtual Machine Garbage Collection Tuning Guide")

## 21.Linux服务器性能查看命令：

### 21.1 整机: top

### 21.2 CPU: vmstat

vmstatn 23

一般vmstat工具的使用是通过两个数字参数来完成的，第一个参 数是采样的时间间隔数单位是秒，第:二个参数是采样的次数

  - **procs**
    ● r:运行和等待CPU时间片的进程数，原则上1核的CPU的运行队列不要超过2，整个系统的运行队列不能超过总核数的2倍,  否则代表系统压力过大
    ● b:等待资源的进程数，比如正在等待磁盘I/0、 网络I/0等。

- **cpu**
  
    ● us:
    用户进程消耗CPU时间百分比，us值高，用户进程消耗CPU时间多，如果长期大于50%，优化程序;
  ● sy:内核进程消耗的CPU时间百分比;
  
  ● us + sy参考值为80%，如果us + sy大于80%，说明可能存在CPU不足。
  
    id:处于空闲的CPU百分比
    wa: 系统等行I0的CPU时间百分比
    st:来自于一个虚拟机偷取的CPU时间的百分比
  
  **查看所有cpu核信息：mpstat P ALLj2** 
  **每个进程使用cpu的用量分解信息：pidstatu1 p进程编号**
  
  ### 21.3 内存: free
  
  ### 21.4 硬盘: df
  
  ### 21.5  磁盘: iostat
  
  ### 21.6 网络: ifstat

## 22. CPU占用过高的思路分析和定位

   1.先用top命令找出CPU占比最高的进程

2. ps ef或者jps进一步定位，找到后台程序

3. **定位到具体线程或者代码**

  ps mp 进程 o THREAD,tid,time
  参数解释：
  m ：显示所有的线程
  p：pid进程使用cpu的时间
  o：该参数后是用户自定义格式

4. 将需要的**线程ID**转换为16进制格式(英文小写格式)

  printf "%x\n"有问题的线程ID

5. jstack进程ID | grep tid(16进制线程ID小写英文) A60

## 23. JDK自带的JVM监控和性能分析工具

调试+排查+检索



## 24.Github使用

### 24.1 常用名词：

watch:会持续收到该项目的动态
fork，复制某个项目到自己的Github仓库中
star，可以理解为点赞
clone，将项目下载至本地
follow，关注你感兴趣的作者，会收到他们的动态.

### 24.2 关键词

#### 24.2.1 in限制搜索范围

公式：xxx关键词in:name或description或readme

xxx in:name项目名包含xx的
xxx in:description项目描述包含xx的
xxx in:readme项目的readme文件中包含xx的

组合使用：xxx in:name,description,readme

#### 24.2.2 stars或fork数量关键词查找

公式：

1. xxx关键词 starts 通配符（:>或:>=）

2. 区间范围数字 数字1到数字2 (数字1..数字2)

例如：

1. springboot stars:>=5000
2. springboot stars:5000..10000
3. springcloud fork:>=1000

组合使用：springboot stars:5000..10000 fork 1000..10000

### 24.3 awesome加强搜索

一般是用来收集学习、 工具、 书籍类相关的项目

公式：awesome+关键词

### 24.4 高亮显示关键代码行数

		1. 指定一行：java路径+#L行号
  		2. 多行：java路径+#L行号1L行号2

### 24.5 项目内搜索

在项目下输入 t

### 24.6搜索某个地区内的用户

location:beijing language:java



### 24.7 使用文档

Github使用文档：[Github使用文档](https://help.github.com/en/github/gettingstartedwithgithub "https://help.github.com/en/github/gettingstartedwithgithub")

Git教程：[Git教程廖雪峰官网](https://www.liaoxuefeng.com/wiki/896043488029600 "https://www.liaoxuefeng.com/wiki/896043488029600") 

​				[https://help.github.com/cn/github/usinggit](https://help.github.com/cn/github/usinggit "https://help.github.com/cn/github/usinggit")


写作规范参考：[《中文技术文档的写作规范》](https：//github.com/ruanyf/documentstyleguide "https：//github.com/ruanyf/documentstyleguide")


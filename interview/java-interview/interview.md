
# 常见知识点

## 1. Volatile

Java虚拟机提供的轻量级同步机制（synchronize）  
特性：  

* 1.1、保证可见性
* 1.2、不保证原子性
* 1.3、禁止指令重排（有序性）

## 2. JMM内存模型  
JMM(Java内存模型Java Memory Model，简称JMM)本身是一种抽象的概念**并不真实存在**，它描述的是一组规则或规范，通过这组规范定义了程序中各个变量(包括实例字段，静态字段和构成数组对象的元素)的访问方式。  
JMM关于同步的规定：  

* 1、线程解锁前，必须把共享变量的值刷新回主内存
* 2、线程加锁前，必须读取主内存的最新值到自己的工作内存
* 3、加锁解锁是同一把锁

由于JVM运行程序的实体是线程，而每个线程创建时JVM都会为其创建一个工作内存(有些地方称为栈空间)，工作内存是每个线程的私有数据区域，而Java内存模型中规定所有变量都存储在**主内存**，主内存是共享内存区域，所有线程都可以访问，**但线程对变量的操作(读取赋值等)必须在工作内存中进行，首先要将变量从主内存拷贝的自己的工作内存空间，然后对变量进行操作，操作完成后再将变量写回主内存**，不能直接操作主内存中的变量，各个线程中的工作内存中存储着主内存中的**变量副本拷贝**，因此不同的线程间无法访问对方的工作内存，线程间的通信(传值)必须通过主内存来完成，其简要访问过程如下图：

![](https：//github.com/pengmengsheng/pengmengsheng.github.io/blob/master/interview/java-interview/img/2%20(1).png)

JMM特性：

* 1.**原子性**（操作是不可分、操作不可被中断）：是指一个操作是不可中断的。即使是多个线程一起执行的时候，一个操作一旦开始，就不会被其他线程干扰。（synchronized、Lock）；

* 2.**可见性**（保障数据的一致，数据安全一部分）：是指当一个线程修改了某一个共享变量的值，其他线程是否能够立即知道这个修改。（Volatile、Synchronized）

* 3.**有序性**（按照自己想要执行的顺序执行线程）：有序性是指程序在执行的时候，程序的代码执行顺序和语句的顺序是一致的。（Join）

![](https：//github.com/pengmengsheng/pengmengsheng.github.io/blob/master/interview/java-interview/img/2%20(2).png)

![](https：//github.com/pengmengsheng/pengmengsheng.github.io/blob/master/interview/java-interview/img/2%20(3).png)

![](https：//github.com/pengmengsheng/pengmengsheng.github.io/blob/master/interview/java-interview/img/2%20(4).png)

## 3. 单例模式DCL（Double Check Lock）双端检锁机制

 ![](https：//github.com/pengmengsheng/pengmengsheng.github.io/blob/master/interview/java-interview/img/3%20(1).png)

![](https：//github.com/pengmengsheng/pengmengsheng.github.io/blob/master/interview/java-interview/img/3%20(2).png)

## 4.CAS(比较并交换)
CAS的全称为Compare-And-Swap， **它是一条CPU并发原语**。  
它的功能是判断内存某个位置的值是否为预期值，如果是则更改为新的值，这个过程是原子的。  
CAS并发原语体现在JAVA语言中就是sun.misc.Unsafe类中的各个方法。调用UnSafe类中的CAS方法，JVM会帮我们实现出CAS汇编指令。这是一种完全依赖于硬件的功能，通过它实现了原子操作。再次强调，由于CAS是一种系统原语，原语属于操作系统用语范畴，是由若干条指令组成的，用于完成某个功能的一个过程，**并且原语的执行必须是连续的，在执行过程中不允许被中断，也就是说CAS是一条CPU的原子指令，不会造成所谓的数据不一-致问题。**

## 4.1 自旋锁

Unsafe

* 1.Unsafe是CAS的核心类，由于Java方法无法直接访问底层系统，需要通过本地(native)方法来访问，Unsafe相当于一个后门，基于该
类可以直接操作特定内存的数据。Unsafe类存在于sun.misc包中，其内部方法操作可以像C的指针一样直接操作内存，因为Java中CAS操作的执行依赖于Unsafe类的方法。  
**注意**：Unsafe类中的所有方法都是native修饰的，也就是说Unsafe类中的方法都直接调用操作系统底层资源执行相应任务  
* 2.变量valueOffset，表示该变量值在内存中的偏移地址，因为Unsafe就是根据内存偏移地址获取数据的。

		/**
		 * Atomically sets to the given value and returns the old value.
		 *
		 * @param newValue the new value
		 * @return the previous value
		 */
		public final int getAndSet(int newValue) {
		    return unsafe.getAndSetInt(this， valueOffset， newValue);
		}

* 3.变量value用volatile修饰，保证了多线程之间的内存可见性。  
CAS--->Unsafe--->CAS底层思想--->ABA--->原子引用更新--->如何避免ABA问题  
CAS算法实现一个重要前提需要取出内存中某时刻的数据并在当下时刻比较并替换，那么在这个时间差类会导致数据的变化。  
比如说一个线程onE从内存位置V中取出A，这时候另一个线程two也从内存中取出A，并且线程two进行了一些操作将值变成了B，然后线程two又将V位置的数据变成A，这时候线程one进行CAS操作发现内存中仍然是A，然后线程one操作成功。
尽管线程one的CAS操作成功，但是不代表这个过程就是没有问题的。

如何解决ABA
理解原子引用+新增一种机制，修改版本号（类似时间戳）
AtomicStampedRefrence 带时间戳原子引用

## 5.集合类不安全解决
### 5.1 List
* 1.Vector
* 2.Collections.synchronizedList
* 3.CopyOnWriteArrayList

### 5.2 Set

* 1.Collections.synchronizedSet
* 2.CopyOnWriteArraySet

### 5.3 Map
* 1.Collections.synchronizedMap
* 2.ConcurrentHashMap

## 6.锁
### 6.1公平锁/非公平锁
ReentrantLock **默认：NonfairSync（非公平锁）**，传入true，ReentrantLock(true)公平锁。

关于两者区别：  

* **公平锁：** Threads acquire a fair lock in the order in which they requested it  
公平锁，就是很公平，在并发环境中，每个线程在获取锁时会先查看此锁维护的等待队列，如果为空，或者当前线程是等待队列的第一个，就占有锁，否则就会加入到等待队列中，以后会按照 FIFO 的规则从队列中取到自己  
* **非公平锁：** a nonfair lock permits barging： threads requesting a lock can jump ahead of the queue of waiting threads if the lockhappens to be available when it is requested.  
非公平锁比较粗鲁，上来就直接尝试占有锁，如果尝试失败，就再采用类似公平锁那种方式。

### 6.2可重入锁（递归锁）——synchronized、ReentrantLock
线程可以进入任何一个他已经拥有的锁所同步着的代码块。

	public synchronized void a () {
		b();
	}
	public synchronized void b () {
		
	}

### 6.3自旋锁（spinlock）
- 尝试获取锁的线程**不会立即阻塞**，而是**采用循环的方式去尝试获取锁**，这样的好处是减少线程上下文切换的消耗，缺点是循环会消耗CPU。

		AtomicReferencec Thread> atomicReference=new AtomicReference<>();
		public void myLock(){
		Thread thread =Thread.currentThread();
		while(!atomicReference.compareAndset(nu11， thread)){

			}
		}

### 6.4独占锁（写锁）/共享锁（读锁）/互斥锁
* 独占锁：该锁只能被一个线程所持有。ReentrantLock 和Synchronized都是独占锁
* 共享锁：该锁可以被多个线程所持有 
ReentrantReadWriteLock 其读锁是共享锁，其写锁是独占锁。
读锁的共享锁可保证并发读是非常高效的，读写、写读、写写过程是互斥的。
	
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		//读锁
		lock.readLock().lock();
		//写锁
		lock.writeLock().lock();

### 6.5.Lock显示锁
- 通过lock()方法上锁，unlock()释放锁。

	    Lock lock = new ReentrantLock();
	    lock.lock();
	    lock.unlock();

### 7. 闭锁CountDownLatch
- CountDownLatch一个正数计数器，countDown方法对计数器做减操作，await方法等待计数器达到0。所有await的线程都会阻塞直到计数器为0或者等待线程中断或者超时。
	
		try {
			CountDownLatch latch = new CountDownLatch(5);
			//执行线程操作
			for(int i =0;i<6;i++) {
				new Thread(() ->{
					latch.countDown();
				}，"线程"+i).start();				
			}

			//等待线程执行完成
			latch.await();
			System.out.println("完成");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


## 8.CyclicBarrier
栅栏类似于闭锁，它能阻塞一组线程直到某个事件的发生。栅栏与闭锁的关键区别在于，所有的线程必须同时到达栅栏位置，才能继续执行。闭锁用于等待事件，而栅栏用于等待其他线程

	CyclicBarrier cyclicBarrier = new CyclicBarrier(8， () -> {
			System.out.println("线程执行结束");
		});
		
		for(int i=0;i<8;i++) {
			new Thread(() ->{
				System.out.println(Thread.currentThread().getName());
				try {
					//线程阻塞，直到所有线程执行完成
					cyclicBarrier.await();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}， "第"+i+"个线程");
		}

**CountDownLatch**和**CyclicBarrier**的比较

* 1. CountDownLatch是线程组之间的等待，即一个(或多个)线程等待N个线程完成某件事情之后再执行；而CyclicBarrier则是线程组内的等待，即每个线程相互等待，即N个线程都被拦截之后，然后依次执行。
* 2. CountDownLatch是减计数方式，而CyclicBarrier是加计数方式。
* 3. CountDownLatch计数为0无法重置，而CyclicBarrier计数达到初始值，则可以重置。
* 4. CountDownLatch不可以复用，而CyclicBarrier可以复用。

## 9.Semaphore 信号灯/信号量
 主要作用：

- 1、用于**多个共享资源的互斥使用**，
- 2、用于**并发线程数的控制**。

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
synchronized不需要用户去手动释放锁，当synchronized代码执 行完后系统会自动让线程释放对锁的占用；  
ReentrantLock则需要用户去手动释放锁若没有主动释放锁，就有可能导致出现死锁现象。

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
    
	FutureTask result = new FutureTask<>(CallalbelImpl);//CallalbelImpl实现类
	new Thread(result).start();
	result.get();//获取返回值

FutureTask也可用于闭锁的操作。

## 13.线程池 
线程池做的工作主要是控制运行的线程的数量，处理过程中将任务放入队列，然后在线程创建后启动这些任务，如果线程数量超过了最大数量超出数量的线程排队等候，等其它线程执行完毕，再从队列中取出任务来执行。

### 13.1 线程池特点：
线程复用；控制最大并发数；管理线程。  

- 第一：降低资源消耗。通过重复利用己创建的线程降低线程创建和销毁造成的消耗。  
- 第二：提高响应速度。当任务到达时，任务可以不需要的等到线程创建就能立即执行。  
- 第三：提高线程的可管理性。线程是稀缺资源，如果无限制的创建，不仅会消耗系统资源，还会降低系统的稳定性，使用线程池可以进行统一的分配， 调优和监控。

	 	//一池5线程，执行长期的任务，性能好很多
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        //一池1线程，一个任务执行的场景
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        //一池N线程，执行很多短期异步的小程序或负载较轻的服务
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        try {
            for(int i = 1;i<=10;i++){
				//执行
                fixedThreadPool.execute(() -> {
                    System.out.println(Thread.currentThread().getName()+"处理任务");
                });
            }
        } catch (Exception e) {
           
        }finally{
			 fixedThreadPool.shutdown();
		}

### 13.2 ThreadPoolExecutor7个重要参数说明


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

* 1.**corePoolSize**：线程池中的常驻核心线程数口
* 2.**maximumPoolSize**：线程池能够容纳同时执行的最大线程数，此值必须大于等于1
* 3.**keepAliveTime**：多余的空闲线程的存活时间。
 当前线程池数量超过corePoolsize时，当空闲时间达到keepAliveTime值时，多余空闲线程会被销毁直到只剩下corePoolSize个线程为止。
* 4.**unit**： keepAliveTime的 单位。
* 5.**workQueue**：任务队列，被提交但尚未被执行的任务。
* 6.threadFactory：表示生成线程池中工作线程的线程工厂，用于创建线程--般用默认的即可。
* 7.handler：拒绝策略，表示当队列满了并且工作线程大于等于线程池的最大线程(maximumPoolSize)时如何来拒绝。

----
1. 在创建了线程池后，等待提交过来的任务请求。
2. 当调用execute()方法添加一个请求任务时，线程池会做如下判断：  
 	2.1 如果正在运行的线程数量小于corePoolSize，那么马上创建线程运行这个任务;  
	2.2 如果正在运行的线程数量大于或等于corePoolSize，那么将这个任务放入队列;  
	2.3 如果这时候队列满了且正在运行的线程数量还小于maximumPoolSize，那么还是要创建非核心线程立刻运行这个任务;  
	2.4 如果队列满了且正在运行的线程数量大于或等于maximumPoolSize，那么线程池会启动饱和拒绝策略来执行。  
3. 当一个线程完成任务时，它会从队列中取下一个任务来执行。
4. 当一个线程无事可做超过-定的时间(keepAliveTime) 时，线程池会判断：
	* 如果当前运行的线程数大于corePoolSize，那么这个线程就被停掉。
	* 所以线程池的所有任务完成后它最终会收缩到corePoolSize的大小。


### 13.3 RejectedExecutionHandler：
**拒绝策略**：当任务数大于最大线程数(maximumPoolSize)+任务队列数(workQueue)时采取的策略

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
                threadPoolExecutor.execute(() -> {
                    System.out.println(Thread.currentThread().getName());
                });
            }
		} catch (Exception e) {
            threadPoolExecutor.shutdown();
	}

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
 
**参考公式**： CPU 核数/1-阻塞系数  阻塞系数在 0.8~0.9 之间

比如 8 核 CPU： 8/1-0.9 = 80 个线程数

参考：[https：//blog.csdn.net/youanyyou/article/details/78990156](https：//blog.csdn.net/youanyyou/article/details/78990156)

## 14.死锁
### 14.1原因
死锁是指两个或两个以上的进程在执行过程中,因争夺资源而造成的一种互相等待的现象,若无外力干涉那它们都将无法推进下去，如果系统资源充足，进程的资源请求都能够得到满足，死锁出现的可能性就很低，否则就会因争夺有限的资源而陷入死锁。  
### 14.2 代码：

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

### 14.3 解决办法：
* 查看进程：jps定位进程号 
* jstack找到死锁查看

## 15.JVM
### 15.1 JVM内存结构

### 15.2 GC作用域

### 15.3 常见的垃圾回收算法
* **引用计数**:有对象引用 引用计数加1无引用减1。不常用

* **复制**

	MinorGC的过程( 复制->清空->互换)  
	1: eden、 SurvivorFrom 复制到SurvivorTo，年龄+1  
	首先，当Eden区 满的时候会触发第一次GC,把还活 着的对象拷贝到SurvivorFrom区，当Eden
	区再次触发GC的时候会扫描Eden区和From区域,对这两个区域进行垃圾回收，经过这次回
	收后还存活的对象，则直接复制到To区域(如果有对象的年龄已经达到了老年的标准，则赋
	值到老年代区)，同时把这些对象的年龄+1  
	2:清空eden、SurvivorFrom	然后，清空Eden和SurvivorFrom中的对象，也即复制之后有交换，谁空谁是to  
	3: SurvivorTo和 SurvivorFrom互换
	最后，SurvivorTo和SurvivorFrom互 换，原SurvivorTo成 为下一次GC时的SurvivorFrom区。部
	分对象会在From和To区域中复制来复制去，如此交换15次(由JVM参数MaxTenuringThreshold 决定，这个参数默认是15),最终如果还是存活，就存入到老年代。

* **标记清除**

	垃圾收集算法：标记清除法(Mark-Sweep)  
	算法分成标记和清除两个阶段，先标记出要回收的对象，然后统一回收这些对象。
	形如:

* **标记整理**  
	1、标记  
	2、压缩（整理）：再次扫描，并往一端移动存活对象  
	* 好处：无内存碎片，可以利用bump  
	* 缺点：需要移动对象成本  
## 16.GC Roots
- 垃圾：内存中已经不再被使用到的空间

- 如果判断一个对象是否可以被回收
	- 引用计数法
	- 可达性分析（根搜索路径），可作为GC Roots的对象：  
	 	虚拟机栈（栈帧中的局部变量区，也叫局部变量表）中引用的对象。  
		方法区中的类静态属性引用的对象。  
		方法区中常量引用的对象。  
		本地方法栈中的JNI（Native方法）引用对象。
## 17.JVM
###17.1 JVM参数
* 标配参数：java-version;-help;-showversion
* X参数（了解）：
	* -Xint（解释执行）；
	* -Xcomp（第一次使用就编译成本地代码）；
	* -Xmixed（混合模式）
* **XX参数**
	* Boolean类型 **-XX:+/-开启（关闭）参数** 如：-XX:+PrintGCDetails
	* KV设值类型 **-XX:属性Key=属性value**  如：-XX:MetaspaceSize=128m

### 17.2查看配置参数命令：

#### 17.2.1 查看参数第一种方法
- 查看具体参数配置：jinfo -flag 参数名 进程号
- 显示所有配置：jinfo -flags 进程号  
- 两个重要参数：  
	* **Xms**:初始堆内存等价于（-XX:InitialHeapSize）  
	* **Xmx**:最大堆内存等价于（-XX:MaxHeapSize）

#### 17.2.2 查看参数第二种方法
- 初始参数：java -XX:+PrintFlagsInitial
- 修改后参数：java -XX:+PrintFlagsFinal
- 运行时加的参数：java -XX:+PrintFlagsFinal -XX:MetaspaceSize=521m Test
- 查看默认垃圾回收器：java -XX:+PrintCommandLineFlags -version

参数符号说明：“:=” 是修改后的参数值而普通“=”是初始参数
### 17.3 常用参数
#### 17.3.1 -Xms
初始内存，默认为物理内存1/64，等价于 -XX:InitialHeapSize；
#### 17.3.2 -Xmx
最大分配内存，默认为物理内存1/4，等价于 -XX:MaxHeapSize；
#### 17.3.3  -Xss
设置单个线程栈的大小，一般默认为512k~1024k，等价于 -XX:ThreadStackSize
#### 17.3.4  -Xmn
设置年轻代大小
#### 17.3.5  -XX:+MetaspaceSize
设置元空间大小，元空间的本质和永久代类似，都是对JVM规范中方法区的实现，不过**元空间与永久代之间最大的区别在于**：**元空间并不在虚拟机中，而是使用本地内存**。因此，默认情况下，元空间的大小仅受本地内存限制。  
-Xms128m -Xmx4096m -Xss1024k -XX: MetaspaceSize=512m -XX: PrintCommandLineFlags -XX:+PrintGCDetails -XX:+UseSeria1GC

#### 17.3.6  -XX:+PrintGCDetails
打印垃圾回收日志 

[**GC** (Allocation Failure) [PSYoungGen: 1861K->488K(2560K)] 1861K->732K(9728K), 0.0084952 secs] [Times: user=0.00 sys=0.00, real=0.02 secs]   
[GC (Allocation Failure) [PSYoungGen: 488K->488K(2560K)] 732K->732K(9728K), 0.0012515 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]   
[**Full GC** (Allocation Failure) [PSYoungGen: 488K->0K(2560K)] [ParOldGen: 244K->632K(7168K)] 732K->632K(9728K), [Metaspace: 3358K->3358K(1056768K)], 0.0071535 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]     
[GC (Allocation Failure) [PSYoungGen: 0K->0K(2560K)] 632K->632K(9728K), 0.0003614 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]   
[Full GC (Allocation Failure) [PSYoungGen: 0K->0K(2560K)] [ParOldGen: 632K->615K(7168K)] 632K->615K(9728K), [Metaspace: 3358K->3358K(1056768K)], 0.0061335 secs] [Times: user=0.11 sys=0.00, real=0.01 secs]   
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space at interview.App.main(App.java:11)  
日志说明：  
[**GC (Allocation Failure) [PSYoungGen** GC类型 **：1861K** YoungGC前新生代内存占用**->488K** YoungGC后新生代内存占用**(2560K** 新生代总共大小**)] 1861K** YoungGC前JVM堆内存占用**->732K** YoungGC后JVM堆内存占用**(9728K** JVM堆总大小**), 0.0084952 secs** YoungGC耗时] [Times: **user=0.00** YoungGC用户耗时 **sys=0.00** YoungGC系统耗时, **real=0.02 secs** YoungGC实际耗时]    

[GC (Allocation Failure) [PSYoungGen: 1861K->488K(2560K)] 1861K->732K(9728K), 0.0084952 secs] [Times: user=0.00 sys=0.00, real=0.02 secs] 
   
**GC规律**：GC类型 GC前内存->GC后内存（该区总内存）

#### 17.3.7 -XX:SurvivorRatio
设置新生代中eden和S0/S1空间的比例  
默认  
-XX:SurvivorRatio=8,Eden:S0:S1=8:1:1  
例如：  
-XX:SurvivorRatio=4,Eden:S0:S1=4:1:1  
SurvivorRatio值设置eden区比例占多少，S0/S1相同

**Heap 新生代堆空间（1/3）老年代堆空间（2/3）**  
 **PSYoungGen total 2560K**, used 126K [0x00000000ffd00000, 0x0000000100000000, 0x0000000100000000)  
  **eden space 2048K**, 6% used [0x00000000ffd00000,0x00000000ffd1f9b0,0x00000000fff00000)  
  **from space 512K**, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)  
  **to   space 512K**, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)

 **ParOldGen       total 7168K**, used 615K [0x00000000ff600000, 0x00000000ffd00000, 0x00000000ffd00000)  
  object space 7168K, 8% used [0x00000000ff600000,0x00000000ff699e98,0x00000000ffd00000)  
 Metaspace       used 3461K, capacity 4496K, committed 4864K, reserved 1056768K  
  class space    used 379K, capacity 388K, committed 512K, reserved 1048576K

 
**MinorGC的过程( 复制->清空->互换) ** 
**1: eden、 SuryivorFrom复制到SuryivorTo， 年龄+1**  
首先，当Eden区满的时候会触发第一 次GC,把还活着的对象拷贝到SurvivorFrom区， 当Eden
区再次触发GC的时候会扫描Eden区和From区域,对这两个区域进行垃圾回收，经过这次回
收后还存活的对象,则直接复制到To区域(如果有对象的年龄已经达到了老年的标准，则赋
值到老年代区)，同时把这些对象的年龄+1  
**2:清空eden、SurvivorFrom**  
然后，清空Eden和SurvivorFrom中的对象，也即复制之后有交换，谁空谁是to  
**3: SurvivorTo和 SurvivorFrom 互换**  
最后，SurvivorTo和SurvivorFrom互换，原SurvivorTo成为 下-次GC时的SurvivorFrom区。部分对象会在From和To区域中复制来复制去，如此交换15次(由JVM参数axTenuringThreshold决定.这个参数默认是151.最终如果还是存活.就在入到老年代

#### 17.3.8 -XX:NewRatio
配置年轻代与老年代在堆结构的占比  
默认  
-XX:NewRatio=2新生代占1，老年代2，年轻代占整个堆的1/3  
例如  
-XX:NewRatjo=4新生代占1,老年代4，年轻代占整个堆的1/5  
NewRatio值就是设置老年代的占比，剩下的1给新生代

#### 17.3.9 -XX:MaxTenuringThreshold 
设置垃圾最大年龄：-XX:MaxTenuringThreshold=0；默认:15，值为0-15  
如果设置为0的话，则年轻代对象不经过Survivor区，直接进入年老代。对于年老代比较多的应用，可以提高效率。如果将此值设置为-一个较大值，则年轻代对象会在Survivor区进行多次复制，这样可以增加对象再年轻代的存活时间，增加在年轻代即被回收的概论。


## 18 引用
### 18.1 强引用Reference
Object obj = new Object();

当内存不足，JVM开始垃圾回收，对于强引用的对象，就算是出现了OOM也不会对该对象进行回收，死都不收。  
强引用是我们最常见的普通对象引用，**只要还有强引用指向一个对象，就能表明对象还“活着”，垃圾收集器不会碰这种对象**。在Java中最常见的就是强引用，把一个对象赋给一个引用变量， 这个引用变量就是- -一个强引用。当-一个对象被强引用变量引用时，它处于可达状态，它是不可能被垃圾回收机制回收的，即使该对象以后永远都不会被用到JVM也不会回收。因此强引用是造成Java内存泄漏的主要原因之一。  
对于一个普通的对象，如果没有其他的引用关系，只要超过了引用的作用域或者显式地将相应(强)引用赋值为null,
一般认为就是可以被垃圾收集的了(当然具体回收时机还是要看垃圾收集策略)。

### 18.2软引用SoftReference
**内存足够的时候不回收，内存不够的时候进行回收**  
软引用是一种相对强引用弱化了一些的引用，需要用 java.lang.ref.SoftReference类来实现，可以让对象豁免一些垃圾收集。  
软引用通常用在对内存敏感的程序中，比如高速缓存就有用到软引用，内存够用的时候就保留，不够用就回收!

### 18.3 弱引用WeakReference
只要GC就进行回收，用 java.lang.ref.WeakReference类来实现

    Object obj = new Object();
    WeakReference<Object> weakReference = new WeakReference(obj);
    System.out.println(obj);//java.lang.Object@4554617c
    System.out.println(weakReference.get());//java.lang.Object@4554617c
    System.out.println("----------");
    obj=null;
    System.gc();
    System.out.println(obj);//null
    System.out.println(weakReference.get());//null

### 18.4 虚引用PhantomReference
虛引用需要 java.lang.ref.PhantomReference 类来实现。  

顾名思义，就是形同虚设，与其他几种引用都不同，虚引用并不会决定对象的生命周期。  
如果一个对象仅持有虚引用，那么它就和没有任何引用一样，在任何时候都可能被垃圾回收器回收，它不能单独使用也不能通过它访问对象，虚引用必须和引用队列(ReferenceQueue)联合使用。
虚引用的主要作用是跟踪对象被垃圾回收的状态。仅仅是提供了一种确保对象被 finalize 以后，做某些事情的机制。  
PhantomReference 的get方法总是返回null,因此无法访问对应的引用对象。其意义在于说明-一个对 象已经进入 finalization 阶段，可以被gc回收， 用来实现比 fialization 机制更灵活的回收操作。
换句话说，设置虛引用关联的唯一-目的，就是在这个对象被收集器回收的时候收到一个系统通知或者后续添加进-一步的处理。  
Java技术允许使用finalize()方法在垃圾收集器将对象从内存中清除出去之前做必要的清理工作。

**GC 回收之前放到 ReferenceQueue 引用队列中**-虚引用通知机制


	Object obj = new Object();
    ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
    PhantomReference phantomReference = new PhantomReference(obj,referenceQueue);
    System.out.println("---GC前----");  
    System.out.println(obj);//java.lang.Object@4554617c
    System.out.println(phantomReference.get());//null
    System.out.println(referenceQueue.poll());//null

    System.out.println("---GC后----");
    obj = null;
    System.gc();
    System.out.println(obj);//null
    System.out.println(phantomReference.get());//null
    System.out.println(referenceQueue.poll());//java.lang.ref.PhantomReference@74a14482  

## 19 OOM

**java.lang.StackOverflowError **管运行   
**java.lang.OutOfMemoryError**: Java heap space 管存储  
**java.lang.OutOfMemoryError**: GC overhead limit exceeded  
-xx : MaxDirectMemorysize= 5m  
 
* GC回收时间过长时会抛出OutOfMemroyError，**超过98%的时间用来做GC并且回收了不到2%的堆内存**
* 连续多次GC都只回收了不到2%的极端情况下才会抛出。假如不抛出GC overhead limit 错误会发生什么停况呢?
* 那就是GC清理的这么点内存很快会再次填满，迫使GC再次执行.这样就形成恶性循环,
* CPU使用率-直是100%， 而GC 却没有任何成果

**java.lang.OutOfMemoryError**: Direct buffer memory    

* 导致原因:
 写NIO程序经常使ByteBuffer来读取或者写入数据， 这是一 一种基于通道(Channel)|与缓冲区(Buffer)的I/0方式,
它可以使用Native函数库直接分配堆外内存，然后通过一个 存储在Java雄里面的DirectByteBuffer对象作为这块内存的引用进行操作。
这样能在-些场景中显蓍提高性能，因为避免了在Java堆和Native堆中来回复制数据。  
ByteBuffer.allocate(capability)第-种方式是分配JVM堆内存，属于GC 管辖范围，由于需要拷贝所以速度相对较慢  
ByteBuffer.allocteDirect(capability)第一种方式是分配OS 本地内存，不属FGC管辖范围，由于不需要内存拷贝所以速度相对较快。  
* 但如果不断分配本地内存， 堆内存很少使用，那么JVM就不需要执行GC, DirectByteBuffer对象 们就不会被回收,这时候堆内存充足，但本地内存可能已经使用光了，再次尝试分配本地内存就会出现OutOfMemoryError,那程序就直接崩溃了。

-Xms5m -Xmx5m -XX:+PrintGCDetails -XX:MaxDirectMemorySize=5m  

	System.out.println("初始JVM最大内存："+VM.maxDirectMemory());
	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(10*1024*1024);
结果：  
[GC (Allocation Failure) [PSYoungGen: 1024K->488K(1536K)] 1024K->592K(5632K), 0.0007910 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]  
初始JVM最大内存：5242880  
[GC (System.gc()) [PSYoungGen: 1313K->488K(1536K)] 1417K->688K(5632K), 0.0008659 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]  
[Full GC (System.gc()) [PSYoungGen: 488K->0K(1536K)] [ParOldGen: 200K->635K(4096K)] 688K->635K(5632K), [**Metaspace: 3424K->3424K**(1056768K)], 0.0056662 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]  
Exception in thread "main" java.lang.OutOfMemoryError: Direct buffer memory

**java.lang.OutOfMemoryError**: unable to create new native thread  
**java.lang.OutOfMemoryError**: Metaspace  










写作规范参考：[《中文技术文档的写作规范》](https：//github.com/ruanyf/document-style-guide "中文技术文档的写作规范")


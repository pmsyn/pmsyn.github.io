
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

4.2 Unsafe

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
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
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
				
			} ， "线程"+i).start();
		}

## 10.队列

* **ArrayBlockingQueue** ：由数组结构组成的有界阻塞队列。
 
* **LinkedBlockingQueue**：由链表结构组成的有界(但大小默认值为Integer.MAX_VALUE)。

* PriorityBlockingQueue：支持优先级排序的无界阻塞队列。

* DelayQueue：使用优先级队列实现的延迟无界阻塞队列。

* **SynchronousQueue**：不存储元素的阻塞队列，也即单个元素的队列

* LinkedTransferQueue：由链麦结构组成的无界阻塞队列。
 
* LinkedBlockingDeque：由链表结构组成的双向阻塞队列。

多线程判断使用while循环防止虚假唤醒

## 11.Synchornized和Lock区别联系
* 1.原始构成

synchronized是关键字属于JVM层面，monitorenter(底层是通过tmoni tor对象来完成，其实wait/notify 等方法也依赖monitor对象只有在同步块或方法中才能调wait/notify等方法monitorexit 
 
Lock是具体类(java. util. concurrent. Locks. Lock)是api层面的锁

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

* 第一：降低资源消耗。通过重复利用己创建的线程降低线程创建和销毁造成的消耗。
* 第二：提高响应速度。当任务到达时，任务可以不需要的等到线程创建就能立即执行。
* 第三：提高线程的可管理性。线程是稀缺资源，如果无限制的创建，不仅会消耗系统资源，还会降低系统的稳定性，使用线程池可以进行统一的分配， 调优和监控。

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

--
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
			2，//corePoolSize
			5，//maximumPoolSize
			1，//keepAliveTime
			TimeUnit.SECONDS，//unit
			new LinkedBlockingQueue(3)，//workQueue
			Executors.defaultThreadFactory()，//threadFactory
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





写作规范参考：[《中文技术文档的写作规范》](https：//github.com/ruanyf/document-style-guide "中文技术文档的写作规范")


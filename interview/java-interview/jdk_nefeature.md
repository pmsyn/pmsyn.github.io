 <h1 style="text-align:center;">Java新特性</h1>
# 一、Java8

## 1.1 速度更快

1. HashMap底层采用红黑树

2. JVM放弃永久区使用元空间，区别元空间采用物理内存，元空间的回收机制？

## 1.2 lamda表达式

“->” 箭头操作符或lambda操作符

lambda表达式的两部分

左侧：参数

右侧：执行的功能，lambda体

### 1.2.1 语法格式：

- 无参数，无返回值

  () -> System.out.println('无参数，无返回值');

  ```java
  public void test() {
      Runnable r = () -> System.out.println("无参数，无返回值");
      r.run();
  }
  ```

- 有一个参数(e) ，参数可省略不写

  ```java
      public void test() {
          Consumer<Object> consumer = (e) -> System.out.println(e);
          consumer.accept("有参数无返回值");
  
          consumer = e -> System.out.println(e);
          consumer.accept("有参数无返回值");
      }
  ```

- 两个以上的参数，有返回值并且Lambda体中有多条语句

  ```java
  Comparator<Integer> comparator = (x, y) ->{
      return Integer.compare(x,y);
  };
  ```

- 两个以上的参数，Lambda体中只有一条语句,大括号可以省略

  ```java
  Comparator<Integer> comparator = (x, y) -> Integer.compare(x,y);
  ```

- Lambda表达式参数列表的数据类型可以不写。JVM编译器通过上下文推断出数据类型，即“**类型推断**”。

- Lambda表达式需要“**函数式接口**”的支持

  函数式接口：接口中只有一个抽象方法的接口，可以使用@FunctionInterface修饰进行函数式接口的检查。

### 1.2.2 内置核心函数接口：

#### 1.2.2.1 Consumer<T> :消费型接口

```
void accept(T t);
```

```java
public static void main(String[] args){
    testConsumer("abc",e -> System.out.println(e));

}

public static void testConsumer(String str,Consumer<String> consumer){
    consumer.accept(str);
}
```

#### 1.2.2.2 Supplier<T>:供给型接口

```java
/**
 * Gets a result.
 *
 * @return a result
 */
T get();
```

```java
public static void main(String[] args){
    Map<String, Object> map = new HashMap<>();
    map.put(String.valueOf(1), 1);
    List<Map<String, Object>> maps = testSupplier(() -> map);
}

public static List<Map<String,Object>>  testSupplier(Supplier<Map<String,Object>> supplier){
    List<Map<String,Object>> newArr = new ArrayList<>();
    newArr.add(supplier.get());
    return newArr;
}
```

#### 1.2.2.3 Function<T,R> :函数型接口

```java
/**
 * Applies this function to the given argument.
 *
 * @param t the function argument
 * @return the function result
 */
R apply(T t);
```

```java
public static void main(String[] args){
    testFuncation("zhangsan",s-> s.toUpperCase());
}

public static void  testFuncation(String str,Function<String,String> function){
    System.out.println(function.apply(str));
}
```

#### 1.2.2.4 Predicate<T>：断言型接口

```java
/**
 * Evaluates this predicate on the given argument.
 *
 * @param t the input argument
 * @return {@code true} if the input argument matches the predicate,
 * otherwise {@code false}
 */
boolean test(T t);
```

```java
public static void main(String[] args) {
    testPredicate(Arrays.asList("hello", "world"), e -> "hello".equals(e));
}

public static String testPredicate(List<String> arr, Predicate<String> predicate) {
    for (String str : arr) {
        if (predicate.test(str)) return str;
    }
    return null;
}
```

### 1.2.3 方法引用

如果lambda体中的内容已经有方法实现，我们可以使用方法引用。

注意：

* 被引用的**方法参数和返回值**要与**lambda函数的参数和返回值**一致

* 若Lambda 参数列表中的第一个参数是实例方法的调用者，第二个参数是实例方法的参数时可以使用类名::实例方法

```java
public static int compare(int x, int y) {
    return (x < y) ? -1 : ((x == y) ? 0 : 1);
}
int compare(T o1, T o2);
```

```java
Person person = new Person();
Supplier<Object> supplier = () -> person.getName();
//对象::实例方法名
Supplier<Object> supplier = person::getName;

//类::静态方法
Comparator<Integer> comparator = Integer::compare;

//类::实例方法
BiPredicate<String,String> bp = (x,y)->x.equals(y);
bp = String::equals;

```

### 1.2.4 构造器引用

构造器引用的参数列表需要与函数式接口抽象方法的参数列表保持一致

```java
//构造器引用
Supplier<Person> supplier = ()->new Person();
supplier = Person::new;
//一个参数构造器引用
Function<String,String> bp2 = String::new;
}
```

### 1.2.5 数组引用

```java
Function<Integer, String[]> arr = x -> new String[x];

arr = String[]::new;
```

## 1.3 StreamAPI

集合讲的是数据，流讲的是计算

注意：

1. Stream不会存储元素
2. Stream不会改变源对象，而是返回一个持有结果的新Stream。
3. Stream操作是延迟执行的，需要结果的时候才执行。

Stream的操作步骤:

### 1.3.1 创建Stream

一个数据源（集合、数组），获取一个流。

```java
//1. 通过Collection系列集合提供的stream()/parallelStream()
Stream<Integer> stream = Arrays.asList(1, 2, 3).stream();
stream = Arrays.asList(1, 2, 3).parallelStream();
//2.通过Arrays中的静态方法stream()获取
stream = Arrays.stream(new Integer[]{1, 2, 3});
//3.Stream类中的of方法
stream = Stream.of(1, 2, 3);
//4.创建无限流
//4.1 迭代
Stream.iterate(0, x -> x * x)
        .limit(10)//限制产生最大个数
        .forEach(System.out::println);
//4.2 生成
Stream.generate(() -> Math.random())
        .limit(10)
        .forEach(System.out::println);
```

### 1.3.2 中间操作

一个中间操作链，对数据源的数据进行处理。

**惰性求值**：多个中间操作可以连接起来形成一个流水线，除非流水线上触发终止操作，否则中间操作不会执行任何的处理，而在终止操作时一次性全部处理。

#### 1.3.2.1 筛选与切片

* filter：接收Lambda，从流中排除某些元素。

  ```java
  //中间操作不会产生结果
  Arrays.asList(1, 2, 3)
          .stream()
          .filter(e -> e>2)//中间操作
          .forEach(System.out::println);//终止操作，内部迭代
  ```

* limit(n)：截断流, 使其元素不超过给定数量。

  ```java
  Arrays.asList(1, 2, 3,4,5)
      .stream()
      .filter(x -> x > 1)
      .limit(2)
      .forEach(System.out::println);//返回 2,3
  
  ```

*  skip(n)：-跳过元素，返回一个扔掉了前n个元素的流。若流中元素不足n个，则返回一个空流。与limit(n)互补。

  ```java
  Arrays.asList(1, 2, 3,4,5)
          .stream()
          .filter(x -> x > 1)
          .skip(2)
          .forEach(System.out::println);//返回4,5
  ```

* distinct：筛选， 通过流所生成元素的hashCode()和equals()去除重复元素

  ```java
  Arrays.asList(1, 2, 4,3,4,3,5)
          .stream()
          .filter(x -> x > 1)
          .distinct()
          .forEach(System.out::println);
  ```

* map映射
  map:接收Lambda，将元素转换成其他形式或提取信息。接收一个函数作为参数，该函数会被应用到每个元素上，并将其映射成一个新的元素。
  
  ```java
  Arrays.asList(1, 2, 4,3,4,3,5)
          .stream()
          .map(x -> x*10)
          .forEach(System.out::println);
  ```
  
  ```java
  public class TestLambda {
      public static void main(String[] args) {
          Arrays.asList("hello", "world")
                  .stream()
                  .map(TestLambda::flatMap)
                  .forEach(x -> x.forEach(System.out::println));
      }
      
      public static Stream<Character> flatMap(String str) {
          List<Character> list = new ArrayList<>();
          for (char c : str.toCharArray()) {
              list.add(c);
          }
          return list.stream();
      }
  }
  ```
  
  
  flatMap:接收一个函数作为参数，将流中的每个值都换成另一个流，然后**把所有流连接成一个流**。
  
  ```java
  public class TestLambda {
      public static void main(String[] args) {
          Arrays.asList("hello", "world")
                  .stream()
                  .flatMap(TestLambda::flatMap)
                  .forEach(System.out::println);
      }
  
      public static Stream<Character> flatMap(String str) {
          List<Character> list = new ArrayList<>();
          for (char c : str.toCharArray()) {
              list.add(c);
          }
          return list.stream();
      }
  }
  ```
  
* 排序
    sorted()：自然排序

    ```java
    Arrays.asList("a","c", "b")
            .stream()
            .sorted()
            .forEach(System.out::println);
    ```

    

    sorted (Comparator com)： 定制排序

    ```java
    Arrays.asList("a", "c", "b")
            .stream()
            .sorted((x, y) -> x.compareToIgnoreCase(y))
            .forEach(System.out::println);
    ```

    allMatch：检查是否匹配所有元素

    ```java
    boolean flag =  Arrays.asList("a", "c", "b")
             .stream()
             .allMatch(x->x.equals("a"));
    ```

    anyMatch：检查是否至少匹配一个元素

    ```java
    boolean flag =  Arrays.asList("a", "c", "b")
             .stream()
             .anyMatch(x->x.equals("a"));
    ```

    noneMatch：检查是否没有匹配所有元素

    ```java
    boolean flag =  Arrays.asList("a", "c", "b")
             .stream()
             .noneMatch(x->x.equals("a"));
    ```

    findFirst：返回第一个元素Optional

    Optional：解决空指针异常

    ```java
    Optional<String> str = Arrays.asList("a", "c", "b")
              .stream()
              .findFirst();
      System.out.println(str.get());
    ```

    findAny：返回当前流中的任意元素
    count：返回流中元素的总个数
    max：返回流中最大值
    min：返回流中最小值

* 归约
    reduce (T identity, Binaryoperator) / reduce (Binaryoperator)一可以将流中元素反复结合起来，得到汇总结果。

    ```java
    List<Integer> arr = Arrays.asList(1, 2, 3, 4, 6);
    Integer sum = arr.stream()
            .reduce(0, (x, y) -> x + y);
    ```

    collect-将流转换为其他形式。接收-一个Collector接口的实现，用于给stream中元素做汇总的方法

    ```java
    List<Integer> arr = Arrays.asList(1, 2, 3, 4, 6);
    List<Integer> newArr = arr.stream()
            .map(x -> x * 3)
            .collect(Collectors.toList());
    ```


3. 终止操作

执行中间操作链，并产生结果



## 1.4 便于并行







## 1.5 最大化减少空指针异常 Optional

Optional<T>类(java. util. Optional)是一个容器类，代表一个值存在或不存在，原来用null 表示一个值不存在，现在Optional可以更好的表达这个概念。并且可以避免空指针异常。

常用方法:
Optional.of(T t) :创建一个Optional实例

```java
Optional<String> optional = Optional.of(new String());
System.out.println(optional.get());
```

Optional. empty() :创建-一个空的Optional 实例
Optional. ofNullable(T t):若t不为null,创建Optional实例,否则创建空实例
isPresent() :判断是否包含值
orElse(T t) :如果调用对 象包含值，返回该值，否则返回t
orElseGet (Supplier s) : 如果调用对象包含值，返回该值，否则返回s获取的值
map (Function f):如果有值对其处理，并返回处理后的Opt ional，否则返回Optional. empty()
flatMap (Function mapper):与 map 类似，要求返回值必须是0ptional

## 1.6 时间API

```java
System.out.println(LocalDate.now());//日期
System.out.println(LocalDateTime.now());//日期和时间
System.out.println(LocalTime.now());//时间
```

```java
LocalDate d1 = LocalDate.of(2020,1,1);
LocalDate d2 = LocalDate.now();
//获取两个日期之间的间隔
Period period = Period.between(d1, d2);
System.out.println(period.getDays());
```

```java
LocalTime t1 = LocalTime.now();
LocalTime t2 = LocalTime.now();
//获取两个时间之间的间隔
Duration duration = Duration.between(t1, t2);
System.out.println(duration.getSeconds());
```

### 1.6.2.时间校正器

* TemporalAdjuster :时间校正器。有时我们可能需要获取。

  例如:将日期调整到“下个周日”等操作。

  ```
  LocalDateTime t1 = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
  System.out.println(t1);
  ```

* TemporalAdjusters :该类通过静态方法提供了大量的常用TemporalAdjuster的实现。
    例如获取下个周一:
    
    ```java
    LocalDateTime t1 = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    System.out.println(t1);
    ```

### 1.6.3 时间格式化

* 默认时间格式

```java
LocalDateTime t1 = LocalDateTime.now();
DateTimeFormatter df = DateTimeFormatter.ISO_DATE;
String t = df.format(t1);
System.out.println(t);
```

* 自定义时间格式

```java
LocalDateTime t1 = LocalDateTime.now();
DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
String t = df.format(t1);
System.out.println(t);
```

### 1.6.4 时区

```java
LocalDateTime.now(ZoneId.of("Asia/Chungking"))
```

## 1.7 定义重复注解

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface MyAnnotations {
    MyAnnotation[] value();
}
```

```java
@Repeatable(MyAnnotations.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface MyAnnotation {
    String value() default "annotation";
}
```

```java
public class Test {
    public static void main(String[] args) throws NoSuchMethodException {
        Class<Test> test = Test.class;
        Method show = test.getMethod("show");
        Arrays.stream(
                show.getAnnotationsByType(MyAnnotation.class))
                .forEach(x -> System.out.println(x.value())
                );//HELLO,WORLD
    }

    @MyAnnotation("HELLO")
    @MyAnnotation("WORLD")
    public void show() {

    }
}
```
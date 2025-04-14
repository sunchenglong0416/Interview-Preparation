### String 类特性
#### 1. 不可变性
`String` 对象一旦被创建，其内容就不能被改变。若对 `String` 对象进行拼接、替换等操作，实际上是创建了一个新的 `String` 对象。例如：
```java
String str = "Hello";
str = str + " World";
```
在上述代码里，`str` 初始指向内容为 `"Hello"` 的 `String` 对象，执行拼接操作后，`str` 指向了一个新的 `String` 对象 `"Hello World"`，而原来的 `"Hello"` 对象并未改变。

#### 2. 常量池
Java 为了提高性能和减少内存开销，设有 `String` 常量池。当创建 `String` 常量时，JVM 会先在常量池中查找是否已存在相同内容的 `String` 对象。若存在，就直接返回该对象的引用；若不存在，才会创建新的 `String` 对象并将其放入常量池。示例如下：
```java
String str1 = "Hello";
String str2 = "Hello";
System.out.println(str1 == str2); // 输出 true
```
在这段代码中，`str1` 和 `str2` 引用的是常量池中同一个 `"Hello"` 对象。

#### 3. 继承自 `Object` 类
`String` 类继承自 `Object` 类，并重写了 `equals()`、`hashCode()`、`toString()` 等方法。`equals()` 方法用于比较两个 `String` 对象的内容是否相等，`hashCode()` 方法根据 `String` 对象的内容计算哈希码。例如：
```java
String str1 = "Hello";
String str2 = new String("Hello");
System.out.println(str1.equals(str2)); // 输出 true
```

#### 4. 实现 `Serializable`、`Comparable<String>` 和 `CharSequence` 接口
- `Serializable` 接口表明 `String` 对象可被序列化，能在网络传输或保存到文件时使用。
- `Comparable<String>` 接口让 `String` 对象可以进行比较，实现了 `compareTo()` 方法，用于按字典顺序比较两个 `String` 对象。
- `CharSequence` 接口定义了对字符序列的基本操作，`String` 类实现该接口以提供对字符序列的支持。

### String 类设计成不可变的原因
#### 1. 安全性
在多线程环境中，不可变对象是线程安全的。由于 `String` 对象不可变，多个线程可以同时访问和使用同一个 `String` 对象，无需担心数据被修改导致的线程安全问题。例如，在网络连接、数据库连接等场景中，`String` 常被用作参数，若 `String` 可变，可能会引发安全漏洞。

#### 2. 缓存哈希码
`String` 类重写了 `hashCode()` 方法，并且会缓存计算得到的哈希码。因为 `String` 对象不可变，所以其哈希码在对象创建时就可以确定，并且在对象的整个生命周期内保持不变。这样，在使用 `String` 作为哈希表（如 `HashMap`、`HashSet`）的键时，能提高哈希表的性能。例如：
```java
String str = "Hello";
int hashCode = str.hashCode(); // 计算并缓存哈希码
```

#### 3. 常量池的使用
`String` 常量池的实现依赖于 `String` 对象的不可变性。若 `String` 对象可变，当多个引用指向常量池中的同一个 `String` 对象时，一个引用对其内容的修改会影响其他引用，这会破坏常量池的设计初衷。

#### 4. 性能优化
不可变的 `String` 对象可以在很多场景下进行性能优化。例如，在字符串拼接时，编译器可以对其进行优化，使用 `StringBuilder` 或 `StringBuffer` 来提高拼接效率。同时，由于 `String` 对象不可变，在进行字符串比较时，只需要比较引用是否相等或者内容是否相等，而无需担心内容被修改。 
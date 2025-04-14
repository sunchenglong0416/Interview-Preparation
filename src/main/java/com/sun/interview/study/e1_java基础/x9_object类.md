### 1. Object 类的属性

`Object` 类本身没有定义任何实例变量（属性）。所有类都继承自 `Object` 类，因此它们可以使用 `Object` 类的方法。

### 2. Object 类的方法

`Object` 类提供了以下常用的方法：

- **`toString()`**
- **`equals(Object obj)`**
- **`hashCode()`**
- **`getClass()`**
- **`clone()`**
- **`finalize()`**
- **`wait()`**
- **`notify()`**
- **`notifyAll()`**

#### 2.1 `toString()`

**定义**：
返回对象的字符串表示。

**默认实现**：
```java
public String toString() {
    return getClass().getName() + "@" + Integer.toHexString(hashCode());
}
```


**重写示例**：
```java
public class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }

    public static void main(String[] args) {
        Person person = new Person("Alice", 30);
        System.out.println(person); // 输出: Person{name='Alice', age=30}
    }
}
```


#### 2.2 `equals(Object obj)`

**定义**：
比较两个对象是否相等。

**默认实现**：
```java
public boolean equals(Object obj) {
    return (this == obj);
}
```


**重写示例**：
```java
public class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return age == person.age && name.equals(person.name);
    }

    public static void main(String[] args) {
        Person person1 = new Person("Alice", 30);
        Person person2 = new Person("Alice", 30);
        System.out.println(person1.equals(person2)); // 输出: true
    }
}
```


#### 2.3 `hashCode()`

**定义**：
返回对象的哈希码值。

**默认实现**：
```java
public native int hashCode();
```


**重写示例**：
```java
import java.util.Objects;

public class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return age == person.age && name.equals(person.name);
    }

    public static void main(String[] args) {
        Person person1 = new Person("Alice", 30);
        Person person2 = new Person("Alice", 30);
        System.out.println(person1.hashCode()); // 输出: 哈希码值
        System.out.println(person2.hashCode()); // 输出: 相同的哈希码值
    }
}
```


#### 2.4 `getClass()`

**定义**：
返回对象的运行时类。

**示例**：
```java
public class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public static void main(String[] args) {
        Person person = new Person("Alice", 30);
        System.out.println(person.getClass()); // 输出: class Person
    }
}
```


#### 2.5 `clone()`

**定义**：
创建并返回对象的一个副本。

**默认实现**：
```java
protected native Object clone() throws CloneNotSupportedException;
```


**重写示例**：
```java
public class Person implements Cloneable {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }

    public static void main(String[] args) {
        try {
            Person person1 = new Person("Alice", 30);
            Person person2 = (Person) person1.clone();
            System.out.println(person1); // 输出: Person{name='Alice', age=30}
            System.out.println(person2); // 输出: Person{name='Alice', age=30}
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
```


#### 2.6 `finalize()`

**定义**：
在垃圾回收器回收对象之前调用的方法。

**默认实现**：
```java
protected void finalize() throws Throwable { }
```


**示例**：
```java
public class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("Finalizing " + this);
        super.finalize();
    }

    public static void main(String[] args) {
        Person person = new Person("Alice", 30);
        person = null;
        System.gc(); // 建议垃圾回收
    }
}
```


**解释**：
- `finalize` 方法在对象被垃圾回收之前调用。
- 不推荐使用 `finalize` 方法，因为它的调用时间不确定。

#### 2.7 `wait()`, `notify()`, `notifyAll()`

**定义**：
用于线程间的通信和同步。

- **`wait()`**：使当前线程等待，直到其他线程调用 `notify()` 或 `notifyAll()`。
- **`notify()`**：唤醒一个等待的线程。
- **`notifyAll()`**：唤醒所有等待的线程。

**示例代码**：
```java
public class WaitNotifyExample {
    private static final Object lock = new Object();
    private static boolean ready = false;

    public static void main(String[] args) {
        Thread waiter = new Thread(() -> {
            synchronized (lock) {
                while (!ready) {
                    try {
                        System.out.println("Waiter is waiting...");
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Waiter is notified and continues.");
            }
        });

        Thread notifier = new Thread(() -> {
            synchronized (lock) {
                ready = true;
                System.out.println("Notifier is notifying...");
                lock.notify();
            }
        });

        waiter.start();
        notifier.start();
    }
}
```


**解释**：
- `waiter` 线程等待 `ready` 变量变为 `true`。
- `notifier` 线程将 `ready` 变量设置为 `true` 并通知 `waiter` 线程。

### 3. 常见面试题

#### 3.1 `toString()` 方法的作用是什么？

**答案**：
`toString()` 方法返回对象的字符串表示。默认实现返回类名和哈希码值。通常重写该方法以便更好地表示对象的内容。

**示例**：
```java
@Override
public String toString() {
    return "Person{name='" + name + "', age=" + age + "}";
}
```


#### 3.2 `equals()` 方法的作用是什么？

**答案**：
`equals()` 方法比较两个对象是否相等。默认实现比较对象的引用（即是否是同一个对象）。通常重写该方法以便比较对象的内容。

**示例**：
```java
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Person person = (Person) obj;
    return age == person.age && name.equals(person.name);
}
```


#### 3.3 `hashCode()` 方法的作用是什么？

**答案**：
`hashCode()` 方法返回对象的哈希码值。默认实现返回对象的内存地址的整数表示。通常重写该方法以便返回对象内容的哈希码值，以确保 `equals()` 和 `hashCode()` 一致性。

**示例**：
```java
import java.util.Objects;

@Override
public int hashCode() {
    return Objects.hash(name, age);
}
```


#### 3.4 `getClass()` 方法的作用是什么？

**答案**：
`getClass()` 方法返回对象的运行时类。

**示例**：
```java
System.out.println(person.getClass()); // 输出: class Person
```


#### 3.5 `clone()` 方法的作用是什么？

**答案**：
`clone()` 方法创建并返回对象的一个副本。默认实现是浅拷贝。通常重写该方法以便实现深拷贝。

**示例**：
```java
public class Person implements Cloneable {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }

    public static void main(String[] args) {
        try {
            Person person1 = new Person("Alice", 30);
            Person person2 = (Person) person1.clone();
            System.out.println(person1); // 输出: Person{name='Alice', age=30}
            System.out.println(person2); // 输出: Person{name='Alice', age=30}
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
```


#### 3.6 `finalize()` 方法的作用是什么？

**答案**：
`finalize()` 方法在垃圾回收器回收对象之前调用。默认实现为空。不推荐使用该方法，因为它调用时间不确定。

**示例**：
```java
@Override
protected void finalize() throws Throwable {
    System.out.println("Finalizing " + this);
    super.finalize();
}
```


#### 3.7 `wait()`, `notify()`, `notifyAll()` 方法的作用是什么？

**答案**：
- **`wait()`**：使当前线程等待，直到其他线程调用 `notify()` 或 `notifyAll()`。
- **`notify()`**：唤醒一个等待的线程。
- **`notifyAll()`**：唤醒所有等待的线程。


### 1. `equals` 和 `==` 的区别

#### 1.1 `==` 操作符

**定义**：
`==` 操作符用于比较两个对象的引用（即内存地址）是否相同。

**使用场景**：
- **基本数据类型**：比较数值是否相等。
- **引用类型**：比较对象的内存地址是否相同。

**示例代码**：

```java
public class EqualsVsEqualsExample {
    public static void main(String[] args) {
        int a = 10;
        int b = 10;
        System.out.println(a == b); // 输出: true

        String str1 = new String("Hello");
        String str2 = new String("Hello");
        System.out.println(str1 == str2); // 输出: false

        String str3 = "Hello";
        String str4 = "Hello";
        System.out.println(str3 == str4); // 输出: true
    }
}
```


**解释**：
- 对于基本数据类型（如 `int`），`==` 比较数值是否相等。
- 对于引用类型（如 `String`），`==` 比较对象的内存地址是否相同。
- `str1` 和 `str2` 是不同的对象，因此 `str1 == str2` 返回 `false`。
- `str3` 和 `str4` 指向字符串常量池中的同一个对象，因此 `str3 == str4` 返回 `true`。

#### 1.2 `equals` 方法

**定义**：
`equals` 方法用于比较两个对象的内容是否相等。默认实现继承自 `Object` 类，比较对象的引用（即内存地址）是否相同。

**使用场景**：
- **引用类型**：比较对象的内容是否相等。

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


**解释**：
- `equals` 方法默认比较对象的引用（即内存地址）是否相同。
- 重写 `equals` 方法以便比较对象的内容是否相等。

### 2. 为什么需要重写 `equals` 和 `hashCode` 方法

#### 2.1 重写 `equals` 方法

**原因**：
- **内容比较**：默认的 `equals` 方法比较对象的引用（即内存地址）是否相同。通常需要比较对象的内容是否相等。
- **集合类**：在使用 `HashSet`、`HashMap` 等集合类时，需要确保对象的内容相等时，集合类能够正确识别这些对象。

**示例代码**：

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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return age == person.age && Objects.equals(name, person.name);
    }

    public static void main(String[] args) {
        Person person1 = new Person("Alice", 30);
        Person person2 = new Person("Alice", 30);
        System.out.println(person1.equals(person2)); // 输出: true
    }
}
```


**解释**：
- 重写 `equals` 方法以便比较对象的内容是否相等。
- 使用 `Objects.equals` 方法比较字符串内容。

#### 2.2 重写 `hashCode` 方法

**原因**：
- **一致性**：如果两个对象通过 `equals` 方法比较相等，它们的 `hashCode` 值必须相同。
- **集合类**：在使用 `HashSet`、`HashMap` 等集合类时，需要确保对象的内容相等时，集合类能够正确识别这些对象。

**示例代码**：

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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    public static void main(String[] args) {
        Person person1 = new Person("Alice", 30);
        Person person2 = new Person("Alice", 30);
        System.out.println(person1.equals(person2)); // 输出: true
        System.out.println(person1.hashCode()); // 输出: 哈希码值
        System.out.println(person2.hashCode()); // 输出: 相同的哈希码值
    }
}
```


**解释**：
- 重写 `equals` 方法以便比较对象的内容是否相等。
- 重写 `hashCode` 方法以便返回对象内容的哈希码值，确保 `equals` 和 `hashCode` 一致性。

### 3. `equals` 和 `hashCode` 的一致性

**定义**：
- 如果两个对象通过 `equals` 方法比较相等，它们的 `hashCode` 值必须相同。
- 如果两个对象的 `hashCode` 值相同，它们不一定通过 `equals` 方法比较相等。

**原因**：
- **集合类**：在 `HashSet` 和 `HashMap` 等集合类中，`hashCode` 值用于快速查找对象。
- **一致性**：确保对象的内容相等时，集合类能够正确识别这些对象。

**示例代码**：

```java
import java.util.Objects;
import java.util.HashSet;

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
        return age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    public static void main(String[] args) {
        Person person1 = new Person("Alice", 30);
        Person person2 = new Person("Alice", 30);

        HashSet<Person> set = new HashSet<>();
        set.add(person1);
        set.add(person2);

        System.out.println(set.size()); // 输出: 1
    }
}
```


**解释**：
- 重写 `equals` 方法以便比较对象的内容是否相等。
- 重写 `hashCode` 方法以便返回对象内容的哈希码值，确保 `equals` 和 `hashCode` 一致性。
- `HashSet` 使用 `hashCode` 值来快速查找对象，确保对象的内容相等时，集合类能够正确识别这些对象。

### 4. 重写 `equals` 和 `hashCode` 的最佳实践

#### 4.1 重写 `equals` 方法的最佳实践

1. **自反性**：对于任何非空引用值 `x`，`x.equals(x)` 应该返回 `true`。
2. **对称性**：对于任何非空引用值 `x` 和 `y`，如果 `x.equals(y)` 返回 `true`，则 `y.equals(x)` 也应该返回 `true`。
3. **传递性**：对于任何非空引用值 `x`、`y` 和 `z`，如果 `x.equals(y)` 返回 `true` 并且 `y.equals(z)` 返回 `true`，则 `x.equals(z)` 也应该返回 `true`。
4. **一致性**：对于任何非空引用值 `x` 和 `y`，多次调用 `x.equals(y)` 应该始终返回相同的值，前提是对象上的信息没有被修改。
5. **非空性**：对于任何非空引用值 `x`，`x.equals(null)` 应该返回 `false`。

**示例代码**：

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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    public static void main(String[] args) {
        Person person1 = new Person("Alice", 30);
        Person person2 = new Person("Alice", 30);
        System.out.println(person1.equals(person2)); // 输出: true
    }
}
```


#### 4.2 重写 `hashCode` 方法的最佳实践

1. **一致性**：对于任何非空引用值 `x`，多次调用 `x.hashCode()` 应该始终返回相同的值，前提是对象上的信息没有被修改。
2. **相等性**：如果两个对象通过 `equals` 方法比较相等，它们的 `hashCode` 值必须相同。
3. **不相等性**：如果两个对象通过 `equals` 方法比较不相等，它们的 `hashCode` 值不一定不同，但应该尽可能不同以提高哈希表的性能。

**示例代码**：

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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    public static void main(String[] args) {
        Person person1 = new Person("Alice", 30);
        Person person2 = new Person("Alice", 30);

        HashSet<Person> set = new HashSet<>();
        set.add(person1);
        set.add(person2);

        System.out.println(set.size()); // 输出: 1
    }
}
```


**解释**：
- 重写 `equals` 方法以便比较对象的内容是否相等。
- 重写 `hashCode` 方法以便返回对象内容的哈希码值，确保 `equals` 和 `hashCode` 一致性。
- `HashSet` 使用 `hashCode` 值来快速查找对象，确保对象的内容相等时，集合类能够正确识别这些对象。

### 5. 为什么需要同时重写 `equals` 和 `hashCode`

**原因**：
- **集合类**：在 `HashSet` 和 `HashMap` 等集合类中，`hashCode` 值用于快速查找对象。
- **一致性**：确保对象的内容相等时，集合类能够正确识别这些对象。

**示例代码**：

```java
import java.util.Objects;
import java.util.HashSet;

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
        return age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    public static void main(String[] args) {
        Person person1 = new Person("Alice", 30);
        Person person2 = new Person("Alice", 30);

        HashSet<Person> set = new HashSet<>();
        set.add(person1);
        set.add(person2);

        System.out.println(set.size()); // 输出: 1
    }
}
```


**解释**：
- 重写 `equals` 方法以便比较对象的内容是否相等。
- 重写 `hashCode` 方法以便返回对象内容的哈希码值，确保 `equals` 和 `hashCode` 一致性。
- `HashSet` 使用 `hashCode` 值来快速查找对象，确保对象的内容相等时，集合类能够正确识别这些对象。

### 6. 常见面试题

#### 6.1 `==` 和 `equals` 的区别

**答案**：
- **`==` 操作符**：
  - 对于基本数据类型，比较数值是否相等。
  - 对于引用类型，比较对象的内存地址是否相同。
- **`equals` 方法**：
  - 默认实现继承自 `Object` 类，比较对象的内存地址是否相同。
  - 通常重写该方法以便比较对象的内容是否相等。

**示例**：
```java
public class EqualsVsEqualsExample {
    public static void main(String[] args) {
        int a = 10;
        int b = 10;
        System.out.println(a == b); // 输出: true

        String str1 = new String("Hello");
        String str2 = new String("Hello");
        System.out.println(str1 == str2); // 输出: false

        String str3 = "Hello";
        String str4 = "Hello";
        System.out.println(str3 == str4); // 输出: true
    }
}
```


#### 6.2 为什么需要重写 `equals` 方法？

**答案**：
- **内容比较**：默认的 `equals` 方法比较对象的引用（即内存地址）是否相同。通常需要比较对象的内容是否相等。
- **集合类**：在使用 `HashSet`、`HashMap` 等集合类时，需要确保对象的内容相等时，集合类能够正确识别这些对象。

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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return age == person.age && Objects.equals(name, person.name);
    }

    public static void main(String[] args) {
        Person person1 = new Person("Alice", 30);
        Person person2 = new Person("Alice", 30);
        System.out.println(person1.equals(person2)); // 输出: true
    }
}
```


#### 6.3 为什么需要重写 `hashCode` 方法？

**答案**：
- **一致性**：如果两个对象通过 `equals` 方法比较相等，它们的 `hashCode` 值必须相同。
- **集合类**：在 `HashSet` 和 `HashMap` 等集合类中，`hashCode` 值用于快速查找对象。
- **性能**：确保对象的内容相等时，集合类能够正确识别这些对象。

**示例**：
```java
import java.util.Objects;
import java.util.HashSet;

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
        return age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    public static void main(String[] args) {
        Person person1 = new Person("Alice", 30);
        Person person2 = new Person("Alice", 30);

        HashSet<Person> set = new HashSet<>();
        set.add(person1);
        set.add(person2);

        System.out.println(set.size()); // 输出: 1
    }
}
```


#### 6.4 `equals` 和 `hashCode` 的一致性

**答案**：
- **一致性**：如果两个对象通过 `equals` 方法比较相等，它们的 `hashCode` 值必须相同。
- **原因**：在 `HashSet` 和 `HashMap` 等集合类中，`hashCode` 值用于快速查找对象。
- **示例**：
  ```java
  import java.util.Objects;
  import java.util.HashSet;

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
          return age == person.age && Objects.equals(name, person.name);
      }

      @Override
      public int hashCode() {
          return Objects.hash(name, age);
```

# 两个不同的对象，hashcode有可能一样
哈希函数的基本原理
哈希函数：哈希函数将任意长度的数据映射为固定长度的值（哈希码）。
有限范围：哈希码通常是整数（int 类型），其取值范围是有限的（-2^31 到 2^31-1，即 -2147483648 到 2147483647）。
无限可能的对象：理论上，Java中可以创建无限数量的对象。
由于哈希码的范围是有限的，而对象的数量是无限的，因此根据鸽巢原理（Pigeonhole Principle），至少有两个不同的对象会映射到相同的哈希码。  
在 Java 虚拟机（JVM）中，`hashCode` 的生成方式并不是由 JVM 直接规定，而是由具体的类来实现。每个类可以重写 `hashCode` 方法来定义其对象的哈希码生成方式。然而，Java 提供了一些默认的哈希码生成方式，特别是在 `Object` 类中。

### 1. 默认的 `hashCode` 生成方式

#### 1.1 `Object` 类的默认 `hashCode` 方法

**定义**：
在 `Object` 类中，`hashCode` 方法的默认实现是基于对象的内存地址生成哈希码。

**实现**：
```java
public native int hashCode();
```


**特点**：
- **基于内存地址**：默认的 `hashCode` 方法返回对象的内存地址的哈希值。
- **唯一性**：对于不同的对象，内存地址通常是唯一的，因此默认的哈希码也是唯一的。
- **非确定性**：对象的内存地址在不同的 JVM 实例中可能不同，因此默认的哈希码在不同的 JVM 实例中也可能不同。

**适用场景**：
- **默认实现**：适用于不需要特定哈希码的类。
- **唯一标识**：适用于需要唯一标识对象的场景。

**示例**：
```java
public class DefaultHashCodeExample {
    public static void main(String[] args) {
        Object obj1 = new Object();
        Object obj2 = new Object();
        
        System.out.println("obj1 hashCode: " + obj1.hashCode()); // 输出 obj1 的哈希码
        System.out.println("obj2 hashCode: " + obj2.hashCode()); // 输出 obj2 的哈希码
    }
}
```


### 2. 常见的 `hashCode` 生成方式

虽然 `hashCode` 的生成方式由具体的类定义，但 Java 提供了一些常见的生成方式，特别是在 `java.util` 包中的类（如 `String`、`Integer`、`HashMap` 等）。

#### 2.1 `String` 类的 `hashCode` 方法

**定义**：
`String` 类的 `hashCode` 方法基于字符串的内容生成哈希码。

**实现**：
```java
public int hashCode() {
    int h = hash;
    if (h == 0 && value.length > 0) {
        char val[] = value;
        for (int i = 0; i < value.length; i++) {
            h = 31 * h + val[i];
        }
        hash = h;
    }
    return h;
}
```


**特点**：
- **基于内容**：哈希码基于字符串的内容生成。
- **一致性**：相同的字符串内容生成相同的哈希码。
- **高效性**：使用简单的数学运算生成哈希码，效率高。

**适用场景**：
- **字符串哈希**：适用于需要基于字符串内容生成哈希码的场景。
- **哈希表**：适用于需要将字符串作为键存储在哈希表中的场景。

**示例**：
```java
public class StringHashCodeExample {
    public static void main(String[] args) {
        String str1 = "Hello";
        String str2 = "Hello";
        String str3 = "World";
        
        System.out.println("str1 hashCode: " + str1.hashCode()); // 输出 69609650
        System.out.println("str2 hashCode: " + str2.hashCode()); // 输出 69609650
        System.out.println("str3 hashCode: " + str3.hashCode()); // 输出 891559843
    }
}
```


#### 2.2 `Integer` 类的 `hashCode` 方法

**定义**：
`Integer` 类的 `hashCode` 方法返回整数值本身作为哈希码。

**实现**：
```java
public int hashCode() {
    return Integer.hashCode(value);
}

public static int hashCode(int value) {
    return value;
}
```


**特点**：
- **基于整数值**：哈希码基于整数值本身生成。
- **一致性**：相同的整数值生成相同的哈希码。
- **高效性**：直接返回整数值，效率高。

**适用场景**：
- **整数哈希**：适用于需要基于整数值生成哈希码的场景。
- **哈希表**：适用于需要将整数作为键存储在哈希表中的场景。

**示例**：
```java
public class IntegerHashCodeExample {
    public static void main(String[] args) {
        Integer int1 = 123;
        Integer int2 = 123;
        Integer int3 = 456;
        
        System.out.println("int1 hashCode: " + int1.hashCode()); // 输出 123
        System.out.println("int2 hashCode: " + int2.hashCode()); // 输出 123
        System.out.println("int3 hashCode: " + int3.hashCode()); // 输出 456
    }
}
```


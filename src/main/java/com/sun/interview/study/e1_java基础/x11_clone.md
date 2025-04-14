在Java中，克隆用于创建对象副本，根据复制深度分为深克隆和浅克隆，区别如下：
### 定义与实现方式
- **浅克隆**：创建新对象，将原对象非静态字段复制进去。对于基本数据类型（如`int`、`double` 等）和`String`类型字段，直接复制值；对于引用类型字段，仅复制引用地址，新旧对象共享引用对象。实现时需类实现`Cloneable`接口，重写`Object`类的`clone()`方法 。示例：
```java
class Address {
    String city;
    public Address(String city) {
        this.city = city;
    }
}
class Person implements Cloneable {
    String name;
    Address address;
    public Person(String name, Address address) {
        this.name = name;
        this.address = address;
    }
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
```
- **深克隆**：创建新对象，递归复制原对象所有字段及其引用对象，新对象与原始对象完全独立。实现方式有：
    - 重写`clone()`方法，对引用类型成员变量也进行递归克隆。
    - 利用序列化和反序列化，类需实现`Serializable`接口。例如：
```java
import java.io.*;
class DeepAddress implements Serializable {
    String city;
    public DeepAddress(String city) {
        this.city = city;
    }
}
class DeepPerson implements Serializable {
    String name;
    DeepAddress address;
    public DeepPerson(String name, DeepAddress address) {
        this.name = name;
        this.address = address;
    }
    public Object deepClone() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject();
    }
}
```

### 内存与数据独立性
- **浅克隆**：在内存使用上，仅复制对象本身及基本数据类型、`String`类型成员变量，引用类型成员变量共享内存，所以内存效率高。但因共享引用对象内存，修改克隆对象引用类型成员变量，会影响原始对象 。比如修改上述`Person`对象中`address`的`city`属性，`person1`和`person2`的`address`都会改变。
- **深克隆**：为所有引用类型成员变量分配独立内存，新对象与原始对象在内存中完全独立。虽然内存消耗大，但修改克隆对象数据不会影响原始对象 。如调用`DeepPerson`的`deepClone`方法得到新对象，修改新对象`address`的`city`属性，原始对象不受影响。

### 性能表现
- **浅克隆**：因只复制对象本身和少量成员变量，不递归复制引用对象，性能开销小，复制速度快 。
- **深克隆**：需递归复制所有引用成员，创建多个新对象并分配内存，处理大型对象或复杂引用关系对象时，性能开销大，速度较慢 。 
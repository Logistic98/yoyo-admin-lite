## 为什么选择 Lodash
[Lodash](https://www.lodashjs.com/) 通过降低 array、number、objects、string 等等的使用难度从而让 JavaScript 变得更简单。 Lodash 的模块化方法 非常适用于：
- 遍历 array、object 和 string
- 对值进行操作和检测
- 创建符合功能的函数

## 使用

```js
import * as _ from 'lodash';
```
### 深拷贝数组
```js
var objects = [{ 'a': 1 }, { 'b': 2 }];
 
var deep = _.cloneDeep(objects);
console.log(deep[0] === objects[0]);
// => false
```

### 数组切割
```js 
_.chunk(['a', 'b', 'c', 'd'], 2);
// => [['a', 'b'], ['c', 'd']]
 
_.chunk(['a', 'b', 'c', 'd'], 3);
// => [['a', 'b', 'c'], ['d']]

```

### 深比较来确定两者的值是否相等
```js
var object = { 'a': 1 };
var other = { 'a': 1 };
 
_.isEqual(object, other);
// => true
 
object === other;
// => false
```

### 两数相加
```js
_.add(6, 4);
// => 10
```

### 两数相减
```js
_.subtract(6, 4);
// => 2
```

### 两数相乘
```js
_.multiply(6, 4);
// => 24
```

### 两数相除
```js
_.divide(6, 4);
// => 1.5
```
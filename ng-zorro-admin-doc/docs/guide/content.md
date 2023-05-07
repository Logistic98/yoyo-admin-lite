# 内容投影
angular 有这么3个元素标签 `ng-container` `ng-content` `ng-template` , 开发经常用到，官网对他们的介绍却少之又少。
## ng-container
等同 微信小程序的`<block>`, vue 的`<template>` , 作为包装元素，不会在dom中产生标签，可以接收控制属性。
> `<template>` 是html5原生标签，理论上 angular 也可以用, 但是被angular官方优化掉了并且和`ng-template`不是一回事。

如下： 我们在做复杂的条件循环的时候，经常用到 `ng-container` 来执行 `*ngFor` 和 `*ngIf`的同时又不会产生额外的标签。
```html
<!-- 侧边栏部分代码 -->
 <ul nz-menu nzMode="inline" class="sider-menu scrollbar" >
   <ng-container *ngFor="let item of menuList">
       <li nz-submenu *ngIf="!item.nzIconfont" [nzTitle]="item.name" [nzIcon]="item.ico">
         <ul>
           <li *ngFor="let it of item.children" [routerLink]="it.link" nz-menu-item >
            {{it.name}}
           </li>
         </ul>
       </li>
   </ng-container>
 </ul>
```

## ng-content
等同于 vue的 `slot插槽`。 官方的解释是：内容投影是一种模式，你可以在其中插入或投影要在另一个组件中使用的内容。比如，你可能有一个子组件，它可以接受父级插入的内容。

::: tip
  `<ng-content>` 元素是一个占位符，它不会创建真正的 DOM 元素。`<ng-content>` 的那些自定义属性将被忽略。
:::

### 单插槽

假设应用场景是这样的，有一个组件展示班级个人信息。

```html
<!-- 使用组件 -->
<app-detail-info></app-detail-info>
```

```html
<!-- detail-info-component.html -->
<p>小明-1995年-男</p>
```

现在要统一加上学校信息，这时候可以利用内容投影

```html
<!-- 使用组件 -->
<app-detail-info>
  光明小学
</app-detail-info>
```
```html
<!-- detail-info-component.html -->
<ng-content></ng-content>
<p>小明-1995年-男</p>
```



```html
<!-- 结果 -->
 光明小学
 小明-1995年-男
```

### 多插槽

如果需要插入多段，我们也可以在对应的`<ng-content>`标签加上select
```html
<!-- 使用组件 -->
<app-detail-info>
  <div grade>一年级</div>
  <div class="school">光明小学</div>
  <div ngProjectAs="[cls]">二班</div>
  中国
</app-detail-info>
```

```html
<!-- detail-info-component.html -->
<ng-content></ng-content>
<ng-content select=".school"></ng-content>
<ng-content select="[grade]"></ng-content>
<ng-content select="[cls]"></ng-content>
<ng-content></ng-content>
<p>小明-1995年-男</p>
```

```html
<!-- 结果 -->
 中国
 光明小学
 一年级
 一班
 小明-1995年-男
```

::: tip
如果你的组件包含不带 `select` 属性的 `<ng-content>` 元素，则该实例将接收所有与其他 `<ng-content>` 元素都不匹配的投影组件。

在示例中，第一个 `<ng-content>` 就接收投影到组件中的匹配不到的其他内容。
:::

## ng-template

`<ng-template>`元素定义了一个默认不渲染的模板。常规用法如下

### 1. 当作 NgIf else的子句
```html
<div *NgIf="boolean" else elseBlock ></div>
<ng-template #elseBlock>...</ng-template>
```
其实 `*NgIf` 指令是一种简写
```html
<div *ngIf="hero" class="name">{{hero.name}}</div>

<!-- *ngIf变成属性型指令，并绑定在ng-template元素上; -->
<!-- ngIf的宿主元素被包含在ng-template元素内 -->
<ng-template [ngIf]="hero">
  <div class="name">{{hero.name}}</div>
</ng-template>
```

### 2.插入由模板创建的视图
```html
<ng-container *ngTemplateOutlet="tpl; context: myContext"></ng-container>
<ng-template #tpl let-nickname="name">
    <span>I am {{nickname}}</span>
</ng-template>
```
```js
myContext = {$implicit: 'bob', name: 'jone'};
```

::: tip 
`ng-template` 在 组件封装 和 结构指令封装 中非常常见，后续会在单独章节中说明。
::: 
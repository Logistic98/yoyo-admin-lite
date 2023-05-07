>原文参考：Exploring Angular DOM manipulation techniques using ViewContainerRef
>
>作者：Max Koretskyi
>
>如果想深入学习 Angular 如何使用 Renderer 和 View Containers 技术操作 DOM，可以查阅
>
>YouTube 视频 [my talk at NgVikings](https://www.youtube.com/watch?v=qWmqiYDrnDc&list=PLVI0Ut22uwY4UC1v5fUvi2RIU4R4jPkba)。
# Angular引用类型
看 Angular 官方文档发现会提到 `ElementRef`、`TemplateRef`、`ViewContainerRef` 等概念。尽管这些类在 Angular 官方文档会有提及，但很少会去描述整体思路，这可能就是 Angular 难学的原因吧。

Angular 中引用类型（reference type）包括：`ElementRef`、`TemplateRef`、`ComponentRef` 、`ViewRef`、 `ViewContainerRef`。


## @ViewChild

在探索 DOM 抽象类前，先了解下如何在组件/指令中获取这些抽象类。Angular 提供了一种叫做 DOM Query 的技术，主要来源于 `@ViewChild` 和 `@ViewChildren` 装饰器（decorators）。两者基本功能相同，唯一区别是 `@ViewChild` 返回单个引用，`@ViewChildren` 返回由 QueryList 对象包装好的多个引用。
用法类似 `vue` 的 `ref`, 比如：
```js
@Component({
    selector: 'sample',
    template: `
        <span #tref>I am span</span>
    `
})
export class SampleComponent implements AfterViewInit {
    @ViewChild("tref", {read: ElementRef}) tref: ElementRef;

    ngAfterViewInit(): void {
        // outputs `I am span`
        console.log(this.tref.nativeElement.textContent);
    }
}
```
ViewChild 装饰器基本语法是：

```js
@ViewChild([reference from template], {read: [reference type]});
```
上例中你可以看到，我把 tref 作为模板引用名称，并将 `ElementRef` 与该元素联系起来。第二个参数 read 是可选的，因为 Angular 会根据 DOM 元素的类型推断出该引用类型。例如，如果它（#tref）挂载的是 `span` 这种 html 元素，Angular 返回 `ElementRef`；如果它挂载的是 template 元素，Angular 返回 `TemplateRef`。一些引用类型如 `ViewContainerRef` 就不会被 Angular 推断出来，所以必须在 read 参数中显式申明。其他的如 `ViewRef` 不可以挂载在 DOM 元素中，所以必须手动在构造函数中编码构造出来。

## ElementRef
对视图中某个原生元素的包装器，ElementRef 的背后是一个可渲染的具体元素，提供一个`nativeElement`属性 可直接拿到背后的DOM元素。

使用 `@ViewChild` 装饰的 DOM 元素会返回 `ElementRef`，由于所有组件挂载于自定义 DOM 元素，所有指令作用于 DOM 元素，所以组件和指令都可以通过 DI（Dependency Injection）获取宿主元素的ElementRef 对象。比如：
```js
@Component({
    selector: 'sample',
    ...
export class SampleComponent{
	  constructor(private hostElement: ElementRef) {
          //outputs <sample>...</sample>
   		  console.log(this.hostElement.nativeElement.outerHTML);
	  }
	...
```
所以组件通过 DI（Dependency Injection）可以访问到它的宿主元素，因为指令没有视图模板，所以获取的是指令挂载的宿主元素。 `@ViewChild` 装饰器经常被用来获取模板视图中的 DOM 元素。

> 通过 `ElementRef.nativeElement` 即可拿到 DOM。

## TemplateRef
表示一个内嵌模板，用于实例化内嵌的视图。 

提供 `elementRef ` 属性，可以拿到宿主元素的引用。
```js
@Component({
    selector: 'sample',
    template: `
        <ng-template #tpl>
            <span>I am span in template</span>
        </ng-template>
    `
})
export class SampleComponent implements AfterViewInit {
    @ViewChild("tpl") tpl: TemplateRef<any>;

    ngAfterViewInit() {
        let elementRef = this.tpl.elementRef;
        // outputs `template bindings={}`
        console.log(elementRef.nativeElement);
    }
}

```

提供 `createEmbeddedView()` 方法，允许我们创建视图返回 `ViewRef`。

## ViewRef
该抽象类型表示一个 Angular 视图（View），在 Angular 世界里，视图（View）是构建应用中 UI 的基础单元。它是可以同时创建与销毁的最小元素组合。Angular 鼓励开发者把 UI 作为一堆视图（View）的组合，而不仅仅是 html 标签组成的树。

底层是视图树，最后渲染成DOM树，可能类似 vue 的虚拟dom？。

Angular 支持两种类型的视图：

* 嵌入视图（Embedded View），由 `Template` 提供

* 宿主视图（Host View），由 `Component` 提供

### 创建嵌入视图
```js
ngAfterViewInit() {
    let view = this.tpl.createEmbeddedView(null);
}
```

### 创建宿主视图
宿主视图是在动态实例化组件时创建的。可以使用 ComponentFactoryResolver 动态创建一个组件
```js
constructor(private injector: Injector,
            private r: ComponentFactoryResolver) {
    let factory = this.r.resolveComponentFactory(ColorComponent);
    let componentRef = factory.create(injector);
    let view = componentRef.hostView;
}
```
在 Angular 中，每一个组件绑定着一个注入器（Injector）实例，所以创建 `ColorComponent` 组件时传入当前组件（即 SampleComponent）的注入器。另外，别忘了，动态创建组件时需要在模块（module）或宿主组件的 [declarations](https://angular.io/guide/ngmodule-faq#!#q-entry-component-defined) 属性添加被创建的组件。

嵌入视图 和 宿主视图 被创建后，可以使用 `ViewContainer` 插入 DOM 树中。

## ViewContainerRef

表示可以将一个或多个视图附着到组件中的容器，任何 DOM 元素都可以作为视图容器，对于绑定 ViewContainer 的 DOM 元素，Angular 不会把视图插入该元素的内部，而是追加到该元素后面，这类似于 router-outlet 插入组件的方式，通常比较好的方式是把 ViewContainer 绑定在 ng-container 元素上，从而不会在 DOM 中引入多余的 html 元素。

###  element 属性，

指向 `ElementRef`, 可以通过 `element`属性拿到DOM元素的引用
```js
@Component({
    selector: 'sample',
    template: `
        <span>I am first span</span>
        <ng-container #vc></ng-container>
        <span>I am last span</span>
    `
})
export class SampleComponent implements AfterViewInit {
    @ViewChild("vc", {read: ViewContainerRef}) vc: ViewContainerRef;

    ngAfterViewInit(): void {
        // outputs `template bindings={}`
        console.log(this.vc.element.nativeElement.textContent);
    }
}
```
上面，绑定的是会被渲染为注释的 `ng-container` 元素，所以输出 `template bindings={}`。

### 操作视图
`ViewContainer` 提供了一些操作视图 [API](https://angular.cn/api/core/ViewContainerRef#instance-methods)：
```js
class ViewContainerRef {
    ...
    clear() : void
    insert(viewRef: ViewRef, index?: number) : ViewRef
    get(index: number) : ViewRef
    indexOf(viewRef: ViewRef) : number
    detach(index?: number) : ViewRef
    move(viewRef: ViewRef, currentIndex: number) : ViewRef
}

```
上面 `ViewRef` 中说创建完视图，可以通过 `ViewContainerRef` 的 `insert()` 插入DOM：

```js
@Component({
    selector: 'sample',
    template: `
        <span>I am first span</span>
        <ng-container #vc></ng-container>
        <span>I am last span</span>
        <ng-template #tpl>
            <span>I am span in template</span>
        </ng-template>
    `
})
export class SampleComponent implements AfterViewInit {
    @ViewChild("vc", {read: ViewContainerRef}) vc: ViewContainerRef;
    @ViewChild("tpl") tpl: TemplateRef<any>;

    ngAfterViewInit() {
        let view = this.tpl.createEmbeddedView(null); //创建视图
        this.vc.insert(view); //视图容器插入视图 == 插入DOM
    }
}
```
通过上面的实现，最后的 html 看起来是

```html
<sample>
    <span>I am first span</span>
    <!--template bindings={}-->
    <span>I am span in template</span>

    <span>I am last span</span>
    <!--template bindings={}-->
</sample>
```
也可以通过 `detach` 方法从视图中移除 DOM。

### createEmbeddedView()

上面通过 `insert()` 方法，插入创建好的视图。 

`ViewContainer`还提供传入 模板引用对象（TemplateRef）或 组件工厂对象（componentRef）来插入DOM。

[实例化一个内嵌视图，并把它插入到该容器中。](https://angular.cn/api/core/ViewContainerRef#createembeddedview)
```js
this.viewContainer.createEmbeddedView( 模版引用，上下文数据  );  //返回一个EmbeddedView
```

```js
@Component({
    selector: 'sample',
    template: `
        <span>I am first span</span>
        <ng-container #vc></ng-container>
        <span>I am last span</span>
        <ng-template #tpl>
            <span>I am span in template</span>
        </ng-template>
    `
})
export class SampleComponent implements AfterViewInit {
    @ViewChild("vc", {read: ViewContainerRef}) vc: ViewContainerRef;
    @ViewChild("tpl") tpl: TemplateRef<any>;

    ngAfterViewInit() {
        this.vc.createEmbeddedView(tpl)
    }
}
```

上面是Angular操作 DOM 的内部机制， angular4 之后提供了2个快捷方式 `ngTemplateOutlet` 和 `ngComponentOutlet`, 可以不需要写实例化视图的代码。

[实例化一个 Component 并把它的宿主视图插入到本容器的指定 index 处](https://angular.cn/api/core/ViewContainerRef#createcomponent)

```js
abstract createComponent<C>(componentType: Type<C>, options?: { index?: number; injector?: Injector; ngModuleRef?: NgModuleRef<unknown>; environmentInjector?: EnvironmentInjector | NgModuleRef<unknown>; projectableNodes?: Node[][]; }): ComponentRef<C>
```

## ngTemplateOutlet
NgTemplateOutlet： 它是结构指令，根据一个提前备好的 TemplateRef 插入一个内嵌视图。 你可以通过设置 `[ngTemplateOutletContext]` 来给 `EmbeddedViewRef` 附加一个上下文对象。 [ngTemplateOutletContext] 是一个对象，该对象的 key 可在模板中使用 let 语句进行绑定。

```html
<ng-container *ngTemplateOutlet="templateRefExp; context: contextExp"></ng-container>       
```
经过测试：`<ng-template>` `<ng-container>` 都可承载这个指令， 但不支持 `<div>` 这样的标签
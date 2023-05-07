# 新增样式

项目样式唯一入口文件 `src/styles.less` 

## 1.新增全局样式文件

 在 `src/styles/` 目录新建， 在 `src/styles.less` 中 `@import`的方式导入
 ```less
 // Custom Theming for NG-ZORRO
// For more information: https://ng.ant.design/docs/customize-theme/en
@import "../node_modules/ng-zorro-antd/ng-zorro-antd.less";

@layout-sider-background:rgb(48, 65, 86);
@menu-dark-bg:rgb(48, 65, 86);
@menu-dark-inline-submenu-bg:rgb(31, 45, 61);
@layout-header-height:50px;
/* You can add global styles to this file, and also import other style files */
@import "./styles/reset.css";
@import "./styles/scrollbar.less";
 ```

## 2.页面样式
业务组件的样式，写在组件`@Component` `styleUrls` 指向的 less文件即可，为了业务代码的简洁性，不建议以下方式直接把css代码写在ts中
```js
//不推荐
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styles: [
    `p{height:1200px}`
  ]
})
```

## 3. :host 和 ::ng-deep
在开发的时候，经常会遇到给页面单独修改第三方组件样式的需求，不要影响其他页面的样式。只要弄明白 `:host 和 ::ng-deep` 的用法，就会变得非常简单。

正常情况下页面级样式在控制台看,带有特殊属性选择器：类似 `vue` 的 `scoped`。
```css
div [ngcontent-cqs-c349] { color:#fff; }
```
如果加上 `:host`
```css
:host div { color:#000; }
/* 控制台 */
[ngcontent-cqs-c349] div  { color:#000; }
```
如果加上 `::ng-deep`

```css
::ng-deep div { color:#ddd; }
/* 控制台 */
 div  { color:#ddd; }
```

如果加上 `:host ::ng-deep`

```css
:host ::ng-deep .class{ color:#eee; }
/* 原控制台带有第三方组件的属性选择器 */
.class[ngcontent-aaa-123]  { color:#ddd; }
/* 控制台 */
[ngcontent-cqs-c349].class  { color:#eee; }
```

:::tip 总结
* :host  前面加本组件的动态属性的属性选择器，确保样式只生效于本组件和他的子组件
* ::ng-deep 会去掉选择器后面的属性选择器，这样就变成了全局样式
* :host ::ng-deep 结合使用，会单独作用当前组件的第三方样式
:::

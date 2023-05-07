可以设置返回后距顶部距离及自定义样式，无此需求也可使用ng-zorro的[返回组件](https://ng.ant.design/components/back-top/zh)
### 示例
```html
<app-back-to-top></app-back-to-top>
```

### API
| 参数        | 说明           | 类型  | 默认  |
| :-------: | :-------------: | :-----: | :-----: |
| [visibilityHeight]|	滚动高度到此值出现   | number | 400 |
| [backPosition]    | 回到顶部的高度      |  number | 0 |
| [customStyle]    | 自定义样式      |  样式对象{key:value} 或 样式字符串列表"width: 100px; height: 100px" | {} |
| [Template]     | 自定义内容,见示例      |  TemplateRef\<void\> | \- |

#### Template--自定义内容
```html
<app-back-to-top [backPosition]="80" [visibilityHeight]="100" [Template]="tpl" >
  <ng-template #tpl>
    <div class="back-top-inner">UP</div>
  </ng-template>
</app-back-to-top>
```
富文本是管理后台一个核心的功能，但同时又是一个有很多坑的地方。在选择富文本的过程中我也走了不少的弯路，市面上常见的富文本都基本用过了，最终权衡了一下选择了Tinymce。

这里在简述一下推荐使用 tinymce 的原因：tinymce 是一家老牌做富文本的公司(这里也推荐 ckeditor，也是一家一直做富文本的公司，新版本很不错)，它的产品经受了市场的认可，不管是文档还是配置的自由度都很好。在使用富文本的时候有一点也很关键就是复制格式化，tinymce 的去格式化相当的好，它还有一些增值服务(付费插件)，最好用的就是powerpaste，非常的强大，支持从 word 里面复制各种东西，而且还帮你顺带格式化了。富文本还有一点也很关键，就是拓展性。tinymce 学习成本和容易度都不错，很方便拓展。最后一点就是文档很完善，基本你想得到的配置项，它都有。tinymce 也支持按需加载，你可以通过它官方的 build 页定制自己需要的 plugins。

## 常见富文本
- [summernote](https://github.com/summernote/summernote) 先来说一个我绝对不推荐的富文本。这是一个韩国人开源的富文本(当然不推荐的理由不是因为这个)，它对很多富文本业界公认的默认行为理解是反其道而行的，而且只为用了一个 dialog 的功能，引入了 bootstrap，一堆人抗议就是不改。格式化也是差劲。。反正不要用！不要用！不要用！
- [ckeditor](https://github.com/galetahub/ckeditor) ckeditor 也是一家老牌做富文本的公司，楼主旧版后台用的就是这个，今年也出了 5.0 版本，ui 也变美观了不少，相当的不错，而且它号称是插件最丰富的富文本了。推荐大家也可以试用一下。
- [quill](https://github.com/quilljs/quill) 也是一个非常火的富文本，长相很不错。基于它写插件也很简单，api 设计也很简单。楼主不选择它的原因是它对图片的各种操作不友善，而且很难改。如果对图片没什么操作的用户，推荐使用。
- [medium-editor](https://github.com/yabwe/medium-editor )大名鼎鼎的 medium 的富文本(非官方出品)，但完成度还是很不错，拓展性也不错。不过我觉得大部分用户还是会不习惯 medium 这种写作方式的。
- [squire](https://github.com/neilj/Squire) 一个比较轻量的富文本，压缩完才 11.5kb，相对于其它的富文本来说是非常的小了，推荐功能不复杂的建议使用。
- [wangEditor](https://github.com/wangeditor-team/wangEditor) 一个国人写的富文本，用过感觉还是不错的。不过毕竟是个人的，不像专门公司做富文本的，配置型和丰富性不足。前端几大禁忌就有富文本 为什么都说富文本编辑器是天坑?，不过个人能做成这样子很不容易了。
- [百度 UEditor](https://github.com/fex-team/ueditor)  ui 真的不好看，不符合当今审美了，官方也已经很久没更新过了。
- [slate](https://github.com/ianstormtaylor/slate) 是一个 完全 可定制的富文本编辑框架。通过 Slate，你可以构建出类似 Medium、Dropbox Paper 或者 Canvas 这样使用直观、富交互、体验业已成为 Web 应用标杆的编辑器。同时，你也无需担心在代码实现上陷入复杂度的泥潭之中。看起来很酷，以后有机会我会在项目中实践试用一下。

上面列举了很多富文本并没有提供直接的angular的组件，，其实用 angular 封装组件很方便的，没必要去用人家封装的东西。像 vue-quill vue-editor 这种都只是简单包了一层，没什么难度。还不如自己来封装，灵活性可控性更强一点。还有一点基于 angular 真没什么好的富文本，不像 react 有 facebook 出的 draft-js，ory 出的 editor，这种大厂出的产品。

也有一些付费的富文本编辑器， [froala-editor](https://froala.com/wysiwyg-editor/) 这款编辑器。不管是美观和易用性都是不错的，专业版，一年 $349

## Tinymce
这里来简单讲一下在自己项目中使用 Tinymce 的方法。

> 没有采用npm安装的方式(方法比较复杂而且还有一些问题如编译时间长等) 采用异步加载 CDN 的引入方式进行封装

### 示例
`<app-tinymce height="300" width="800"></app-tinymce>`
### API

| 参数	| 说明 | 类型 |	默认值|
| :----:  |:---:|:---:| :---:|
|[tinymceId]|	组件唯一标识符，不用显式指定|	string|	ng-tinymce-时间戳|
|[content]|	富文本内容|	string|	/
|[menubar]|	菜单栏|	string|	'file edit insert view format table'
|[width]|	组件宽度|	string | number	|'auto'
|[height]	|组件高度|	string | number|	360
|[language]|	语言|	'zh_CN' / 'en' / 'es_MX'/  'ja'|	'zh_CN'
|(contentChange)|	change的回调,返回富文本内容|	EventEmitter\<string\>	|/|
### 方法
#### contentChange--回调示例

触发回调的事件有：NodeChange  Change  KeyUp  SetContent

```html
<app-tinymce (contentChange)="hanleChange($event)"></app-tinymce>
```
```js
hanleChange(e:any){
  console.log(e)
}
```
#### setContent--设置内容示例
直接调用内置方法
```html
<app-tinymce  #tinymce2 height="300" width="800" ></app-tinymce>
<button nz-button nzType="primary" (click)="tinymce2.setContent('11111')">设置内容</button>
```
通过@ViewChild调用子组件setContent()方法，设置内容
```html
<app-tinymce  #tinymce2 height="300" width="800" ></app-tinymce>
<button nz-button nzType="primary" (click)="setValue()">设置内容</button>
```
```js
import {ViewChild } from '@angular/core';
@ViewChild('tinymce2',{static:true})  tinymce2: any ;
setValue(){
  this.tinymce2.setContent('content')
}
```
#### getContent --获取富文本
通过@ViewChild调用子组件getContent()方法，获取内容

## CKEditor
CKEditor 4 通过 CKEditor 4 Angular 组件提供原生 Angular 集成。它提供了 CKEditor 4 和 Angular 的深度集成，让您可以在 Angular 组件中使用 WYSIWYG 编辑器的原生功能。CKEditor 4 Angular 组件与 Angular 5.0 及更高版本兼容。[详见文档](https://ckeditor.com/docs/ckeditor4/latest/guide/dev_angular.html)
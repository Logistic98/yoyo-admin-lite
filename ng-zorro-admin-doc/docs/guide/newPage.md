# 新增页面
如果熟悉 vue-router 的配置就很简单了。 [angular-route](https://angular.cn/api/router/Route) 的配置也类似。

不同点在于`angular-route ` 的 `简单配置` 和 `惰性加载`。以下介绍两种方式的如何选择：
## 路由简单配置
* 如果我们的项目较为轻便，没有模块的区分，只有几个页面简单组成，可以选择`简单配置`。
在`app/main.pages`目录新增页面组件，推荐采用[CLI命令](https://angular.cn/cli/generate#component)的方式自动将组件导入module，手动新建麻烦还很容易出错。
:::tip
  CLI命令创建 默认目录是 `src/app/`
:::
```bash
ng g c main/pages/[name] --skip-tests
```
再把页面组件配置在 `/src/app/main/main-routing.module.ts` children中
```js
const routes: Routes = [
  {
    path: '',
    component: LayoutTopSideComponent,
    // 所有页面直接写在 children 
    children: [
      {
        path: 'home',
        component: HomeComponent,
        data: {
          title: '首页',
        }
      },{
        path: 'excel',
        component: ExcelComponent,
        data: {
          title: 'excel',
        }
      }]
  }]
```
采用这种方式，页面没有模块的概念，所以 `main/pages` 中都是页面级组件。

<img :src="$withBase('/img/page1.png')" alt="">

## 路由惰性加载
* 如果是大型项目，有模块区分，那么选择  `惰性加载`。
在`app/main.pages`目录创建带路由的模块，再创建组件
```bash
ng generate module main.pages/[name] --routing

ng g c main/pages/[name] --skip-tests
```

结构如图：

<img :src="$withBase('/img/page.png')" alt="">

在模块的`-routing.module.ts` 中配置路由

```js
//dogs-manage-routing.module.ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DogsManageComponent } from './dogs-manage/dogs-manage.component';
import { BadgeManageComponent } from './badge-manage/badge-manage.component';
import { DogDetailComponent } from './dog-detail/dog-detail.component';

const routes: Routes = [{
  path:'',
  redirectTo: 'dogs-manage',
},{
  path:'dogs-manage',
  component:DogsManageComponent,
},{
  path:'badge-manage',
  component:BadgeManageComponent,
},{
  path:'dogs-manage/dog-detail',
  component:DogDetailComponent,
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DogsManageRoutingModule { }

```
这样就把页面组件封装成了模块包，然后把模块包懒加载到主路由模块
```js
const routes: Routes = [
  {
    path: '',
    component: LayoutTopSideComponent,
    children: [
      {
        path: '',
        redirectTo: 'dogs-manage',
      },
      {
        path: 'dogs-manage',
        loadChildren: () =>
         import('./pages/dogs-manage/dogs-manage.module').then((m)=>m.DogsManageModule)
      }
    ]
  }
]
```
:::tip
惰性加载通过将应用程序拆分为多个包并按需加载它们来加快应用程序加载时间。要使用惰性加载，请在 Route 对象中提供 loadChildren 属性，而不是 children 属性。
:::

## 新增服务

在 `src/@service` 目录中 创建对应模块的服务。 服务中编写模块页面用到的异步接口。通过`依赖注入`的方式将组件需要的服务注入进组件。

何为 [依赖注入](/guide/injectable.html) 后面章节单独介绍。

```bash
ng generate service ../@service/dog-manage  --skip-tests
```
```js
//dog-manage-service.ts
import { Inject, Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { HttpClient, HttpRequest, HttpResponse } from '@angular/common/http';
import { filter, map } from 'rxjs/operators';
@Injectable({
  providedIn: 'root'
})
export class DogManageService {

  constructor(private http: HttpClient, @Inject('BASE_CONFIG') private config:any) { }
  //犬只列表查询
  getDogsList(data:any): Observable<any> {
    const url = `${this.config.url}/dogs`;
    return this.http.get(url,{ params: data }).pipe(map((res) => res));
  }

  //更新(创建)徽章图片
  upDateBadges(data:any): Observable<any>{
    const url = `${this.config.url}/dogs/badges/images/${data.code}`;
    return this.http.post(url,data).pipe(map((res) => res));
  }
}
```

关于`rxjs` 的接口封装，会在后面单独章节介绍。

## 使用服务

```js
import { Component, OnInit } from '@angular/core';
import { DogManageService } from '@service/dog-manage.service'; //引入服务
@Component({
  selector: 'app-dogs-manage',
  templateUrl: './dogs-manage.component.html',
  styleUrls: ['./dogs-manage.component.less']
})
export class DogsManageComponent implements OnInit {
  
  //将服务传入构造函数，使组件继承来自服务中定义的属性和方法
  constructor(private service:DogManageService) {} 

  getList(){
    const data = {...}
    // 调用服务中的异步方法
    this.service.getDogsList(data).subscribe(res=>{
      console.log(res)
    })
  }
}

```

## 新增公共组件

`src/@components` 目录新建，采用 `路由简单配置` 的方式新增组件，要在页面里引用，记得在`components.module.ts`的 `@NgModule` 装饰器  `exports ` 导出
```js
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { TinymceComponent } from './tinymce/tinymce.component';
import { BackToTopComponent } from './back-to-top/back-to-top.component';
import { AlbumComponent } from './album/album.component';
import { JsonViewComponent } from './json-view/json-view.component';

@NgModule({
  declarations: [TinymceComponent, BackToTopComponent,AlbumComponent, JsonViewComponent],
  imports: [
    CommonModule,
    SharedModule
  ],
  exports:[TinymceComponent,BackToTopComponent,AlbumComponent,JsonViewComponent]
})
export class ComponentsModule { }
```

使用， 标签名由组件 `@Component` 装饰器的 `selector` 决定

```html
<app-tinymce></app-tinymce>
```

:::tip
个人写项目的习惯，在全局的 `src/@components` 只会写一些全局的组件。每个页面或者模块特定的业务组件则会写在当前页面或模块下面，用来减轻维护成本。

请记住拆分组件最大的好处不是公用而是可维护性！
:::


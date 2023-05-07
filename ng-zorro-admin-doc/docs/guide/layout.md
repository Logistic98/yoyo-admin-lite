# 布局

页面布局是一个产品最外层的框架结构，往往会包含导航、侧边栏、面包屑以及内容等。

## Layout

:::tip 位置

src/@Layout 

:::

项目目前提供2种`lauout`布局方案，后续会新增多种布局形式。


`ng-zorro-admin` 中 `main/pages` 页面都是基于这个 `layout` 的，除了个别页面如：`login` , `404`, 等页面没有使用该layout。如果你想在一个项目中有多种不同的`layout`，只要在一级路由那里选择不同的`layout`组件就行。
```js
import { LayoutTopSideComponent } from '@layout/layout-top-side/layout-top-side.component';
import { MainLayoutDefaultComponent } from '@layout/main-layout-default/main-layout-default.component';

const routes: Routes = [
  {
    path: '',
    // 你可以选择不同的layout组件
    component: LayoutTopSideComponent,
    // 这里开始对应的路由都会显示在 <router-outlet></router-outlet> 中
    children: [
      {
        path: 'home',
        component: HomeComponent,
        data: {
          title: '首页',
        }
      }]
  }]
     
```

这里使用了 `路由嵌套` , 所以一般情况下，你增加或者修改页面只会影响 `<router-outlet></router-outlet>` 这个主体区域。其它配置在 layout 中的内容如：侧边栏或者导航栏都是不会随着你主体页面变化而变化的。
```text
/foo                                    /bar
+------------------+                    +-----------------+
| layout            |                   | layout          |
| +--------------+  |                   | +-------------+ |
| | foo.component   | |  +------------> | | bar.component | |
| |               | |                   | |             | |
| +--------------+  |                   | +-------------+ |
+------------------+                    +-----------------+
```
当然你也可以一个项目里面使用多个不同的 layout，只要在你想作用的路由父级上引用它就可以了。

## 侧边栏

本项目侧边栏主要基于 `ng-zorro` 的 `ng-menu` 改造。
侧边栏是根据菜单目录生成的，开发时候我们需要手写菜单目录，为了多套布局共用将其单独提成文件，位置在 `@layout/menu-list.ts` , 结构为一个json数组如图
```js
export const menuList =  [
  {
    id: 1,
    name: '首页',
    ico: 'dashboard',
    link:'/web/home',
    children: []
  },{
    id: 2,
    name: '图表',
    ico: 'bar-chart',
    nzIconfont:'icon--yiliao-jiyin',
    children: [
      {
        id: 21,
        name:'折线图',
        link: '/web/echarts/line',
      },
      {
        id: 22,
        name:'饼图',
        link: '/web/echarts/pie',
      }
    ]
  }]
```
 * id： 唯一值
 * name： 菜单名称
 * ico： NG-ZORRO的icon图标
 * nzIconfont： 支持阿里图标，优先级大于ico
 * link：  路径

 <b>注意：若没有children 请设置为空数组，不要省略。针对有无子集和使用了何种图标的情况做了自动处理，渲染代码如下：</b>
 ```html
  <ul nz-menu nzMode="inline" class="sider-menu scrollbar" >
    <ng-container *ngFor="let item of menuList">
      <ng-container *ngIf="item.children.length!=0">
        <li nz-submenu *ngIf="!item.nzIconfont" [nzTitle]="item.name" [nzIcon]="item.ico">
          <ul>
            <li *ngFor="let it of item.children" [routerLink]="it.link" nz-menu-item nzMatchRouter>{{it.name}}</li>
          </ul>
        </li>
        <li nz-submenu *ngIf="item.nzIconfont" [nzTitle]="titleTpl">
          <ng-template #titleTpl> <i nz-icon [nzIconfont]="item.nzIconfont"></i><span>{{item.name}}</span></ng-template>
          <ul>
            <li *ngFor="let it of item.children" [routerLink]="it.link" nz-menu-item nzMatchRouter>{{it.name}}</li>
          </ul>
        </li>
      </ng-container>
      <ng-container *ngIf="item.children.length==0">
        <li  nz-menu-item *ngIf="!item.nzIconfont" [routerLink]="item.link" nzMatchRouter>
          <i nz-icon [nzType]="item.ico"></i>
          <span>{{item.name}}</span>
        </li>
        <li  nz-menu-item *ngIf="item.nzIconfont"  [routerLink]="item.link" nzMatchRouter>
          <i nz-icon [nzIconfont]="item.nzIconfont"></i><span>{{item.name}}</span>
        </li>
      </ng-container>
    </ng-container>
  </ul>
 ```

 :::danger  注意
  如果要设置菜单默认展开，只需给`nz-submenu`这层增加 `nzOpen`属性，但是官方组件样式`nz-submenu`的DOM默认带 `ant-menu-submenu-active`类目，导致子属性未选中，但是父级有状态色的bug，目前官方还未解决这个bug：[https://github.com/NG-ZORRO/ng-zorro-antd/issues/6542](https://github.com/NG-ZORRO/ng-zorro-antd/issues/6542)。

  如果遇到这种需求，可以暂时用样式覆写的方式，把`ant-menu-submenu-active`的样式颜色去掉。
 :::


:::tip 菜单权限
思路是通过 `role` 去获取当前角色可访问的菜单 `tree` , 然后 `filterMenu` 方法过滤 `menuList` , 拿到可以访问的菜单，具体会在 `权限验证` 一节中详细说明
:::

## 面包屑
`ng-zorro` 提供了根据routers自动生成面包屑组件 [Breadcrumb](https://ng-zorro.gitee.io/components/breadcrumb/zh)，但不建议使用他。
```html
<nz-breadcrumb [nzAutoGenerate]="true" [nzRouteLabel]="'title'"></nz-breadcrumb> 
```

因为可能某些页面的面包屑，不按常规规则来，比如tab页，是没办法把对应tab页的title自动生成到面包屑中的，推荐每个模板中单独手写面包屑，会更加灵活。
```html
<div class="bread">
  <nz-breadcrumb>
    <nz-breadcrumb-item>
       <a [routerLink]="['/']">首页</a>
    </nz-breadcrumb-item>
    <nz-breadcrumb-item>
      后台管理
    </nz-breadcrumb-item>
  </nz-breadcrumb>
</div>
```

## 快捷导航 (历史页签)
在 `MainLayoutDefaultComponent` 这个layout布局组件中，引用了 `快捷导航 (历史页签)` 功能，会在访问页面的时候，自动缓存成快捷导航。如图：

<img :src="$withBase('/img/tag.png')" alt="">

组件 封装在 `@lauout/widgets/tag-view`，可以直接引用
```html
 <app-tag-view></app-tag-view>
```
目前缓存在`sessionStorage`，也可以根据需求把缓存写入`localStorage`实现关闭浏览器页签不消失。

## 全屏
组件 封装在 @lauout/widgets/screnfull，可以直接引用
```html
 <app-screenfull></app-screenfull>
```

## 用户信息
<br>
<img :src="$withBase('/img/user.png')" alt="">

组件 封装在 @lauout/widgets/user，可以直接引用。
```html
 <app-user></app-user>
```

'管理员' 是动态获取的，思路是在 `路由守卫` 中，从接口请求当前账号的信息，存入一个全局的`authObservable`属性中,`authObservable`是一个 [BehaviorSubject](https://cn.rx.js.org/manual/overview.html#h26) ，观察者通过订阅，可以拿到最新的值。

```js
// @service/auth.guard.ts
export class AuthGuard implements CanActivate {

  constructor(private authSerice: AuthService,private router: Router){}
  
  authObservable = new BehaviorSubject(null);
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean  {

      const url = state.url
      if (url === '/passport/login') { 
        return  this.authSerice.logoutSystem().pipe(
              map(() => true),
              catchError(() => of(true))
        );
      } else {
        return this.authSerice.getCurrentAuth().pipe(
          map((authReulst: any) => {
            if (authReulst) {
              //权限数据发布到BehaviorSubject
              this.authObservable.next(authReulst);
              return true;
            }
            this.router.navigateByUrl('/passport/login');
            return false;
          }),
          catchError((error) => {
            this.router.navigateByUrl('/passport/login');
            return of(false);
          })
        )
        return true;
      }
  }
}
```

```js
// @layout/widgets/user/user.component.ts
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@service/auth.service';
import { AuthGuard } from '@service/auth.guard';
@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.less']
})
export class UserComponent implements OnInit {
  currentUser: any;
  constructor(private guard:AuthGuard) { }

  ngOnInit(): void {
    //订阅BehaviorSubject，得到权限数据
    this.guard.authObservable.subscribe((res:any)=>{
      this.currentUser = res?.name
    })
  }
}
```
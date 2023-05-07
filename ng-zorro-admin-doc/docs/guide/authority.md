# 权限验证

## 菜单权限
 
项目中菜单权限的实现思路是：本地写一份静态menulist，后端根据role返回一份动态menulist，两个列表比对从静态列表中筛出当前role的可访问列表，最终在 [侧边栏](/blog/guide/layout.html#侧边栏) 中显示。


* 为什么不直接使用接口返回的menulist？
> 1. 考虑到菜单权限配置 通常在开发完成后集中配置，前期权限部分可能没有开发或者接口不完善等，所以前端前期手写一份静态的menulist，保证除权限之外的工作正常进行。
> 2. 静态menulist 可在菜单录入配置的时候当作参考。
> 3. 从静态列表中筛查，为了保证录入的menulist 一定存在，避免出错。

 * 为什么要手写静态的menulist，而不是从路由树生成？
 > 确实路由中已经配置了，还要单独手写一份menulist 不是很聪明的样子。
 > vue 通过 `this.$router` 可以拿到全部路由对象，angular可能是由于安全考虑，没有暴露该方法，只能获取当前路由，所以无法拿到全部的menulist。如果有能拿到路由器的方法请告诉我。

 ## url 二次验证
 筛选后虽然没有访问权限的菜单不会出现在导航中，但是不排除在地址栏中手动输入url的行为。
 ```js
 checkUrlVisit(tree:any){
    let hasUrl = false
    const visitList = tree.filter((e:any)=>e.url)
    const _url = window.location.pathname
    visitList.forEach((e:any) => {
      if(_url === e.url){
        hasUrl = true
      }
    });
    if(!hasUrl){
      //导航时不要把当前状态记入历史, 防止点击浏览器后退按钮回去
      this.router.navigate([visitList[0].url], { replaceUrl: true });
    }
  }

 ```

 其实这段验证代码，放在导航守卫中更好，以后可以考虑优化一下

 ## 权限逻辑
项目中菜单权限的实现思路是：按钮也是一种菜单，在菜单管理中这样配置 `目录-菜单-按钮`，只不过新增按钮的时候可以给按钮赋值某些权限，后端根据权限来控制接口的访问权。
然后把菜单挂在角色上，因为按钮也是一种菜单，所以也可以挂在角色上。

接口通过role返回tree，这里面即有`菜单`也有`按钮`,数据如下：

```js
[
  {authorities:null, grade: 1, id: 11, menuType: 1, parent: null, title: "新生犬管理", url: null},
  {authorities: null,grade: 2,id: 12,menuType: 2,parent: {id: 11, title: '新生犬管理', …},title: "新生犬列表",url: "/app/newborn-manage/newborn-list"}
  {authorities: "dog_breed_list,dog_breed_detail",grade: 3,id: 59,menuType: 3,parent: {id: 12, title: '新生犬列表', …},title: "新生犬列表",url: null}
]
```
没错列表接口权限也被当成一种按钮，只有挂载了权限，角色才能请求相应接口。
项目通过`filterMenu` 和 `filterButtons` 方法，对tree 进行菜单和按钮的分类，具体实现详见@service/auth.guard.ts。

## 权限指令
封装了一个指令权限 `*permission`，能简单快速的实现按钮级别的权限判断。 用法类似 `*ngIf`

<b>使用</b>
```html
<button nz-button  (click)="showAddModal()" *permission="'menu_add'">
  <i nz-icon nzType="plus" nzTheme="outline"></i>新增
</button>
```
<b>推荐</b>

首选指令的方式，如果遇到特别复杂的业务，下面也介绍了使用`权限判断函数`的使用方法，来更加灵活的应对（权限指令内部也是封装自权限判断函数来处理的）。

## 权限判断函数
在某些情况下不适合使用指令 比如 Tabs 组件 控制tabt、tabc的关系 只能通过设置 `*ngIf` 来实现

引入权限判断函数并注入到构造函数中, html中使用 `asyncPipe`订阅结果。
```js
import { FuncsService } from '@service/funcs.service';

constructor( private funcs:FuncsService ) {}

checkPermission(permission:string){
  return this.funcs.checkPermission(permission)
}
```
```html
<button nz-button  (click)="showAddModal()" *ngIf="checkPermission('menu_add')|async">
  <i nz-icon nzType="plus" nzTheme="outline"></i>新增菜单
</button>
```
某些情况控制按钮显示之余还涉及其他业务，可以在`checkPermission`方法中灵活处理，权限判断函数是一个`Observable<boolean>`
```js
checkPermission(permission:string){
  const _show = this.funcs.checkPermission(permission).subscribe(e=>e)
  if(_show){
    ...业务代码
  }
  return _show
}
```
:::danger
 angular 模板表达式中应该尽量避免使用函数调用，变更检测策略会导致函数重复执行。<br>
 再次声明：首选指令的方式
:::
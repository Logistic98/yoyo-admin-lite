import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { BehaviorSubject, catchError, map, Observable, of } from 'rxjs';
import { AuthService } from './auth.service';
import { menuList } from '@layout/menu-list';
import * as _ from 'lodash';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  menuList:any = _.cloneDeep(menuList)
  authObservable = new BehaviorSubject({});

  constructor(private authService: AuthService,private router: Router){}


  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean  {

      const url = state.url
      if (url === '/passport/login') {
        return  this.authService.logoutSystem().pipe(
              map(() => true),
              catchError(() => of(true))
        );
      } else {
        /**获取当前用户信息*/
        return this.authService.getCurrentAuth().pipe(
          map((authReulst: any) => {
            if (authReulst) {
              //console.log(authReulst)
              const tree = authReulst.role.menus.filter((r:any)=>r.visible==='VISIBLE')
              this.authObservable.next({
                tree: this.filterMenu(this.menuList,tree),
                button: this.filterButtons(tree)
              });
              //路径检查
              //if(tree.findIndex((menu:any)=>menu.url===url) !== -1) return true;
              return true//需要路径检查要删掉这个
            }
            this.router.navigateByUrl('/passport/login');
            return false;
          }),
          catchError((error) => {
            this.router.navigateByUrl('/passport/login');
            return of(false);
          })
        )
      }
  }


  filterMenu(totalMenus:any,tree:any){
    if(tree.length===0) return [] ;
    //根据url遍历 菜单列表
    let newArr:any = []
    totalMenus.forEach((item:any)=>{
      item.children.forEach((child:any)=>{
        tree.forEach((t:any)=>{
          if(t.url===child.link && t.url!=null){
            child.show = true
            child.sort = t.sort
            item.sort = t.parentMenu.sort
             //判断是否添加过
            let isContain = newArr.find((v:any) => v.id===item.id)
            if(!isContain){
              newArr.push(item)
            }
          }
        })
      })
      item.children = item.children.filter((child:any)=>child.show)
    })
    this.treeForeach(newArr,(arr:any) =>{arr.sort((a:any,b:any)=> a.sort - b.sort)})
    // console.log(newArr)
    return newArr
  }

  //排序--递归遍历树
   treeForeach (tree:any, func:any) {
      func(tree)
      tree.forEach((data:any)=>{
        data.children && this.treeForeach(data.children, func)
      })
  }

  filterButtons(tree:any){
    let buttonAuthorities: any[] = []
    tree.forEach((e:any)=>{
      if(e.type==='BUTTON'&&e.permission){
        buttonAuthorities.push(e)
      }
    })
    return buttonAuthorities
  }


}

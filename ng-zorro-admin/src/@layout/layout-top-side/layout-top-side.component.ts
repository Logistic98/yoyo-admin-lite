import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NzIconService } from 'ng-zorro-antd/icon';
import { menuList } from '@layout/menu-list';
@Component({
  selector: 'app-layout-top-side',
  templateUrl: './layout-top-side.component.html',
  styleUrls: ['./layout-top-side.component.less']
})
export class LayoutTopSideComponent implements OnInit {
  showMenu:any = []
  menuList:any = menuList
  constructor(private iconService: NzIconService, private router: Router) {
    this.iconService.fetchFromIconfont({
      scriptUrl: '//at.alicdn.com/t/font_3306087_0i6mle8mhlid.js'
    });
   }

  ngOnInit(): void {
    /*
    * 思路：通过role去获取当前角色可访问的菜单tree,然后filterMenu方法过滤一下menuList
    * 实际开发解开下面代码,删除25行
    */

    this.showMenu = this.menuList

    //  this.guard.authObservable.subscribe((res:any)=>{
    //   console.log(res)
    //   //通过role去获取菜单
    //   this.sysService.getRoleTree(res.role.id).subscribe(tree=>{
    //     console.log('当前角色上的菜单:',tree)
    //     //this.showMenu = this.menuList
    //     this.showMenu = this.filterMenu(this.menuList,tree)

    //     //检测访问的url是否在菜单权限内
    //     this.checkUrlVisit(tree)

    //     //按钮权限
    //     console.log('按钮权限',this.filterButtons(tree))
    //     localStorage.setItem('buttonList',JSON.stringify(this.filterButtons(tree)))
    //   })
    // })


  }

  filterMenu(totalMenus:any,tree:any){
    //根据url遍历 菜单列表
    let newArr:any = []
    totalMenus.forEach((item:any)=>{
      item.children.forEach((child:any)=>{
        tree.forEach((t:any)=>{
          if(t.url===child.link && t.url!=null){
            child.show = true
             //判断是否添加过
            let isContain = newArr.find((v:any) => v.id===item.id)
            //debugger
            if(!isContain){
              newArr.push(item)
            }
          }
        })
      })
      item.children = item.children.filter((child:any)=>child.show)
    })
    //console.log(newArr)  
    return newArr
  }

  checkUrlVisit(tree:any){
    let hasUrl = false
    const visitList = tree.filter((e:any)=>e.url)
    const _url = window.location.pathname
    // console.log('_url:',_url)
    visitList.forEach((e:any) => {
      if(_url === e.url){
        hasUrl = true
      }
    });
    if(!hasUrl){
      // this.router.navigateByUrl(visitList[0].url);
      this.router.navigate([visitList[0].url], { replaceUrl: true });
    }
  }

  filterButtons(tree:any){
    let buttonAuthorities: any[] = []
    tree.forEach((e:any)=>{
      if(e.menuType===3&&e.authorities){
        buttonAuthorities.push(e.title)
      }
    })
    return buttonAuthorities
  }

}

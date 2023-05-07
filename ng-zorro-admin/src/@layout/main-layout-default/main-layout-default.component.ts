import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NzIconService } from 'ng-zorro-antd/icon';
import { menuList } from '@layout/menu-list';
import { SystemManageService } from '@service/system-manage.service';
import { AuthGuard } from '@service/auth.guard';


@Component({
  selector: 'app-main-layout-default',
  templateUrl: './main-layout-default.component.html',
  styleUrls: ['./main-layout-default.components.less'],
})
export class MainLayoutDefaultComponent implements OnInit {
  isCollapsed = false;
  showMenu:any = []
  menuList:any = menuList



  constructor(private iconService: NzIconService, private router: Router,private guard:AuthGuard, private sysService:SystemManageService) {
    this.iconService.fetchFromIconfont({
      scriptUrl: '//at.alicdn.com/t/font_3306087_0i6mle8mhlid.js'
    });
   }

  ngOnInit(): void {
    this.guard.authObservable.subscribe((res:any)=>{
      console.log('subject:',res)
      this.showMenu = this.menuList
     
      //检测访问的url是否在菜单权限内
      // this.checkUrlVisit(tree)
    
    })
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



}

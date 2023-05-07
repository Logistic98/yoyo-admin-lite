import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd,ActivatedRoute} from '@angular/router';
import { filter, map , mergeMap } from 'rxjs/operators';


@Component({
  selector: 'app-tag-view',
  templateUrl: './tag-view.component.html',
  styleUrls: ['./tag-view.component.less']
})
export class TagViewComponent implements OnInit {
  visitedViews:any = JSON.parse(sessionStorage.getItem('visitedViews') || '[]') 
  title:string = ''
  currentUrl:string = ''
 
  constructor( private router:Router, private activatedRoute:ActivatedRoute) { 
    /**
    * 监听路由变化
    * 访问过的页面存到sessionStorage
    * 获取最后一级title, 赋值给NavigationEnd的路由结果
    */

     this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      map(() => {
        return this.activatedRoute
      }),
      map(route => {
        while (route.firstChild) route = route.firstChild;
        return route;
      }),
      filter(route => route.outlet === 'primary'),
      mergeMap(route => route.data)
      ).subscribe((d)=>{
        this.title = d['title']
       
      })

    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        //console.log('url',event);
        //console.log(this.activatedRoute)
        this.currentUrl = event.urlAfterRedirects
        if(this.currentUrl==='/passport/login'){
          return 
        }
        if(this.visitedViews.some((v:any)=> v.urlAfterRedirects === event.urlAfterRedirects )) return 

        this.visitedViews.push(
          Object.assign({},event,{
            title:this.title
          })
        )
        sessionStorage.setItem('visitedViews',JSON.stringify(this.visitedViews))
        //console.log(this.visitedViews) 
      }
    })

  }

  
  ngOnInit(): void {
   
  }


  onClose(e:Event,v:any): void{
    e.preventDefault();
    e.stopPropagation();
    //console.log(v)
    if(this.currentUrl === v.url){
      //如果删除的是当前页,则跳到上一个view
      this.visitedViews = this.visitedViews.filter( (view:any) =>{
        return view.url != v.url
      })
      if(this.visitedViews.length != 0){
        const url = this.visitedViews.slice(-1)[0].url
        sessionStorage.setItem('visitedViews',JSON.stringify(this.visitedViews))
        this.router.navigateByUrl(url)
      }
     
      
    }else{
      this.visitedViews = this.visitedViews.filter( (view:any) =>{
        return view.url != v.url
      })
      sessionStorage.setItem('visitedViews',JSON.stringify(this.visitedViews))
    }
    
   
  }

}

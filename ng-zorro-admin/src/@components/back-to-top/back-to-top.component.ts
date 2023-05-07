import { Component, OnInit,Input, TemplateRef } from '@angular/core';
import { fromEvent,Subject } from 'rxjs';
import {takeUntil} from 'rxjs/operators';

@Component({
  selector: 'app-back-to-top',
  templateUrl: './back-to-top.component.html',
  styleUrls: ['./back-to-top.component.less']
})
export class BackToTopComponent implements OnInit {

  @Input() visibilityHeight:number = 400;//默认滚动400px显示
  @Input() backPosition:number = 0; //默认滚动到顶部
  @Input() Template?:TemplateRef<any>;
  @Input() customStyle:any = {}
  visible:boolean = false;
  interval:any = null;
  isMoving:boolean = false
  private $destory = new Subject<boolean>();
  constructor() { }

  ngOnInit(): void {
  //window.addEventListener('scroll', this.handleScroll)
  fromEvent(window, 'scroll').pipe( takeUntil(this.$destory) )
  .subscribe((event) => {
    this.handleScroll()
  });

  }
  ngOnDestroy():void{
    this.$destory.next(true);
    this.$destory.unsubscribe();
    if (this.interval) {
      clearInterval(this.interval)
    }
  }


  backToTop(){
    if (this.isMoving) return
    const start = window.pageYOffset
    let i = 0
    this.isMoving = true
    this.interval = setInterval(() => {
      const next = Math.floor(this.easeInOutQuad(10 * i, start, -start, 500))
      if (next <= this.backPosition) {
        window.scrollTo(0, this.backPosition)
        clearInterval(this.interval)
        this.isMoving = false
      } else {
        window.scrollTo(0, next)
      }
      i++
    }, 16.7)
  }

  easeInOutQuad(t: number, b: number, c: number, d: number) {
    if ((t /= d / 2) < 1) return c / 2 * t * t + b
    return -c / 2 * (--t * (t - 2) - 1) + b
  }

  handleScroll() {
   
    this.visible = window.pageYOffset > this.visibilityHeight
  }

}

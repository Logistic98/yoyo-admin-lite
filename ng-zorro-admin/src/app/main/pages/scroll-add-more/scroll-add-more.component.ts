import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { debounceTime, fromEvent,Subject, timeInterval } from 'rxjs';
@Component({
  selector: 'app-scroll-add-more',
  templateUrl: './scroll-add-more.component.html',
  styleUrls: ['./scroll-add-more.component.less']
})
export class ScrollAddMoreComponent implements OnInit {

  loading = false
  list = [1,2,3,4,5,6,7,8,9,10]
  conH = 0
  offsetTop = 0

  @ViewChild('container')
  container!: ElementRef; 

  constructor() { }

  ngOnInit(): void {
   
  }

  ngAfterViewInit() {
    this.conH = this.container.nativeElement.clientHeight
    this.offsetTop = this.container.nativeElement.offsetTop
    console.log('Height',this.conH)
    console.log('offsetTop',this.offsetTop)

    fromEvent(window, 'scroll').pipe(debounceTime(200))
    .subscribe((event) => {
      this.handleScroll(event)
    });
}

  handleScroll(event: Event){
    console.log('scrollY',window.scrollY)
    console.log('innerHeight',window.innerHeight)
    this.conH = this.container.nativeElement.clientHeight
    this.offsetTop = this.container.nativeElement.offsetTop
    
    if(window.scrollY+window.innerHeight>this.conH+this.offsetTop){
      console.log('adddddd')
      this.loading = true

      setTimeout(()=>{
        this.loading = false
        this.list = [...this.list,1,2,3,4,5,6,7,8,9,10]
      },3000)
    }
  }

}

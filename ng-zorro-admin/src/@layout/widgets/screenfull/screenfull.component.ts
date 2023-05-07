import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-screenfull',
  templateUrl: './screenfull.component.html',
  styleUrls: ['./screenfull.component.less']
})
export class ScreenfullComponent implements OnInit {
  isfullscreen = false
  constructor() { }

  ngOnInit(): void {
  }

  click(){
    if(!this.isfullscreen){
      this.full()
    } else{
      this.exit()
    }
    this.isfullscreen = !this.isfullscreen
    
  }

  full(ele = document.documentElement  as HTMLElement & {
    mozRequestFullScreen(): Promise<void>;
    webkitRequestFullscreen(): Promise<void>;
    msRequestFullscreen(): Promise<void>;
  }) {
    if (ele.requestFullscreen) {
        ele.requestFullscreen();
    } else if (ele.mozRequestFullScreen) {
        ele.mozRequestFullScreen();
    } else if (ele.webkitRequestFullscreen) {
        ele.webkitRequestFullscreen();
    } else if (ele.msRequestFullscreen) {
        ele.msRequestFullscreen();
    }
  }

  exit() {
    const _document = document as Document & {
      mozCancelFullScreen(): Promise<void>;
      webkitExitFullscreen(): Promise<void>;
      msExitFullscreen(): Promise<void>;
    }
    if(_document.exitFullscreen) {
        _document.exitFullscreen();
    } else if(_document.mozCancelFullScreen) {
        _document.mozCancelFullScreen();
    } else if(_document.webkitExitFullscreen) {
        _document.webkitExitFullscreen();
    } else if(_document.msExitFullscreen) {
        _document.msExitFullscreen();
    }
}
}

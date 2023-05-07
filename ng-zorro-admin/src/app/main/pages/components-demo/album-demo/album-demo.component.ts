import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-album-demo',
  templateUrl: './album-demo.component.html',
  styleUrls: ['./album-demo.component.less']
})
export class AlbumDemoComponent implements OnInit {
  albumConfig:any = {
    visible:false
  }
  constructor() { }

  ngOnInit(): void {
  }

  view(){
    this.albumConfig  = {
      visible:true,
      data:[{
        img:'./assets/ng.png',
        title:''
      },{
        img:'./assets/ng.png',
        title:'标题2'
      }],
      index: 1,
      download:true
    }
  }

}

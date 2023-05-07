import { Component, OnInit } from '@angular/core';
declare const AMap:any;

@Component({
  selector: 'app-base-map',
  templateUrl: './base-map.component.html',
  styleUrls: ['./base-map.component.less']
})
export class BaseMapComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
    this.loadMap();
  }
  loadMap(){
    // 异步加载地图资源
    const existingScript = document.getElementById('gd')
    if (!existingScript) {
      var url = 'https://webapi.amap.com/maps?v=1.4.15&key=f2c11639586ad820afb6fd04dec83474&callback=init';
      var jsapi = document.createElement('script');
      jsapi.src = url;
      jsapi.id = 'gd'
      document.head.appendChild(jsapi);
      (window as any).init = ()=>{ this.mapInit()}
    }else{
      this.mapInit()
    }
   
    
  }
  mapInit(){
    //业务代码
    var map = new AMap.Map('container', {
      center:[117.000923,36.675807],
      zoom:11,
      mapStyle: 'amap://styles/darkblue', //设置地图的显示样式
   });
   AMap.plugin(['AMap.ToolBar'],function(){//异步加载插件
     var toolbar = new AMap.ToolBar();
     map.addControl(toolbar);
   });
  }
}

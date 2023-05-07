import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-album',
  templateUrl: './album.component.html',
  styleUrls: ['./album.component.less']
})
export class AlbumComponent implements OnInit {
  _config  = {
    visible: false,
    data:[],
    index:0,
    download:false,
    showPrev:false,
    showNext:false
  }

  @Input() config :{
    visible: boolean,
    data:Array<any>,
    index:number,
    download:boolean,
    showPrev:false,
    showNext:false
  } | any

  index = 0
  curImg = ''
  curTitle = ''

  constructor() { }

  ngOnInit(): void {

  }

  ngOnChanges(){
    this.config = Object.assign(this._config,this.config)
    console.log(this.config)
    this.index = this.config.index
    this.curImg = this.config.data[this.index]?.img
    this.curTitle = this.config.data[this.index]?.title
    if(this.config.data.length>1){
      if(this.index<this.config.data.length-1){
        this.config.showNext = true
      }else if(this.index>0){
        this.config.showPrev = true
      }

    }
  }

  close(){
    this.config!.visible = false
  }
  prev(){
    if(this.index === 0){
      this.config.showPrev = false
    }else{
     this.index--
     if(this.index === 0){
       this.config.showPrev = false
      }
     this.config.showNext = true
    }
    this.curImg = this.config.data[this.index].img
    this.curTitle = this.config.data[this.index].title
  }
  next(){
    const _length = this.config.data.length
    if(this.index < _length){
      this.index++
      this.config.showPrev = true
      if( this.index === _length-1){
        this.config.showNext = false
      }
    }else{
      this.config.showNext = false
    }
    this.curImg = this.config.data[this.index].img
    this.curTitle = this.config.data[this.index].title

  }
}

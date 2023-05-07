import { Component, OnInit, Input , Output, EventEmitter } from '@angular/core';
import { timer } from 'rxjs';
import plugins from './plugins'
import toolbar from './toolbar'
import load from './dynamicLoadScript'
const tinymceCDN = 'https://cdn.jsdelivr.net/npm/tinymce-all-in-one@4.9.3/tinymce.min.js'
@Component({
  selector: 'app-tinymce',
  templateUrl: './tinymce.component.html',
  styleUrls: ['./tinymce.component.less']
})
export class TinymceComponent implements OnInit {
  @Input() tinymceId:string = 'ng-tinymce-' + +new Date() + ((Math.random() * 1000).toFixed(0) + '')
  @Input() content:string = ''
  @Input() menubar:string = 'file edit insert view format table'
  @Input() width:number|string = 'auto'
  @Input() height:number|string = 360
  @Input() language:string = 'zh_CN'
  @Output() contentChange = new EventEmitter() 

  fullscreen =  false
  hasInit = false
  hasChange = false
  constructor() { }

  ngOnInit(): void {
    timer(100).subscribe(()=>{
      this.init()
    })
  }

  ngOnDestory():void {
    this.destroyTinymce()
  }

  init() {
    // dynamic load tinymce from cdn
    load(tinymceCDN, (err:any) => {
      if (err) {
        console.log('error',err.message)
        return
      }
     
      this.initTinymce()
    })
  }

  initTinymce() {
    const _this = this;
    (window as any).tinymce.init({
      language: _this.language,
      selector: `#${_this.tinymceId}`,
      width: _this.width,
      height: _this.height,
      body_class: 'panel-body ',
      object_resizing: false,
      toolbar:  toolbar,
      menubar: _this.menubar,
      plugins: plugins,
      end_container_on_empty_block: true,
      powerpaste_word_import: 'clean',
      code_dialog_height: 450,
      code_dialog_width: 1000,
      advlist_bullet_styles: 'square',
      advlist_number_styles: 'default',
      imagetools_cors_hosts: ['www.tinymce.com', 'codepen.io'],
      default_link_target: '_blank',
      link_title: false,
      nonbreaking_force_tab: true, // inserting nonbreaking space &nbsp; need Nonbreaking Space Plugin
      init_instance_callback: (editor:any) => {
        if (_this.content) {
          editor.setContent(_this.content)
        }
        _this.hasInit = true
        editor.on('NodeChange Change KeyUp SetContent', () => {
          _this.hasChange = true
          _this.contentChange.emit(editor.getContent())
        })
      },
      setup(editor:any) {
        editor.on('FullscreenStateChanged', (e:any) => {
          _this.fullscreen = e.state
          console.log(_this.fullscreen)
        })
      },
      convert_urls: false

    })
  }

  destroyTinymce() {
    const tinymce = (window as any).tinymce.get(this.tinymceId)
    if (this.fullscreen) {
      tinymce.execCommand('mceFullScreen')
    }
    if (tinymce) {
      tinymce.destroy()
    }
  }

  setContent(value:string) {
    (window as any).tinymce.get(this.tinymceId).setContent(value)
  }

  getContent() {
    return (window as any).tinymce.get(this.tinymceId).getContent()
  }




}

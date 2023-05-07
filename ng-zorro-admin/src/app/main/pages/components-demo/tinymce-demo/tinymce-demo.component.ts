import { Component, OnInit, ViewChild } from '@angular/core';

@Component({
  selector: 'app-tinymce-demo',
  templateUrl: './tinymce-demo.component.html',
  styleUrls: ['./tinymce-demo.component.less']
})
export class TinymceDemoComponent implements OnInit {
  @ViewChild('tinymce2',{static:true})  tinymce2: any ;
  content:string =  `<h1 style="text-align: center;">Welcome to the TinyMCE demo!</h1><p style="text-align: center; font-size: 15px;"><img title="TinyMCE Logo" src="https://angular.cn/assets/images/logos/angular/logo-nav@2x.png" alt="TinyMCE Logo" width="270" height="72" /><ul>
  <li>Our <a href="//www.tinymce.com/docs/">documentation</a> is a great resource for learning how to configure TinyMCE.</li><li>Have a specific question? Visit the <a href="https://community.tinymce.com/forum/">Community Forum</a>.</li><li>We also offer enterprise grade support as part of <a href="https://tinymce.com/pricing">TinyMCE premium subscriptions</a>.</li></ul>`;
  content2:string = ''
  
  constructor() { }

  ngOnInit(): void {
  }

  hanleChange(e:any){
    //console.log(e)
  }

  setValue(){
    console.log('set')
    this.tinymce2.setContent(this.content)
  }

  getValue(){
    this.content2 = this.tinymce2.getContent()
  }

}

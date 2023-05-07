import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-go-top',
  templateUrl: './go-top.component.html',
  styleUrls: ['./go-top.component.less']
})
export class GoTopComponent implements OnInit {
  myBackToTopStyle:any = { right: '0px',bottom: '50px'}
  constructor() { }
  ngOnInit(): void {
  }

}

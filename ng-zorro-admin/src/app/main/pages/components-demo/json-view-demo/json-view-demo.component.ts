import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-json-view-demo',
  templateUrl: './json-view-demo.component.html',
  styleUrls: ['./json-view-demo.component.less']
})
export class JsonViewComponent implements OnInit {
  jsonObj = {
    "角色": {
        "名称": "管理员"
    },
    "菜单": [
        {
            "标题": "部门管理"
        },
        {
            "标题": "用户管理"
        },
        {
            "标题": "角色管理"
        },
        {
            "标题": "菜单管理"
        },
        {
            "标题": "日志管理"
        },
    ]
}
  constructor() { }

  ngOnInit(): void {
  }

}

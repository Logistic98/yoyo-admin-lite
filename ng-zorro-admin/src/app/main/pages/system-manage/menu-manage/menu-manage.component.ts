import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzModalService } from 'ng-zorro-antd/modal';
import { SystemManageService } from '@service/system-manage.service';
import { FuncsService } from '@service/funcs.service';
import * as _ from 'lodash';


export interface TreeNodeInterface {
  id: number;
  key: string;
  link: string;
  age?: number;
  level?: number;
  expand?: boolean;
  address?: string;
  children: TreeNodeInterface[];
  parent?: TreeNodeInterface;
  description?: string;
  permission?: string;
  sort: number;
  type?: string;
  url?: string;
  visible?: string;
  icon?: string;
  createTime?: string;
  name?: string;
  title?: string;
  showChild?:any;
}

@Component({
  selector: 'app-menu-manage',
  templateUrl: './menu-manage.component.html',
  styleUrls: ['./menu-manage.component.less']
})

export class MenuManageComponent implements OnInit {
  loading = false;
  listOfData: any = [];
  originMenuList:any = []//菜单list
  //modal
  modalForm!: FormGroup;
  isAddVisible = false;

  parentMenuId: any 
  isEdit = false
  menuTypes = [
    {value: 'MAIN', label: '目录'},
    {value: 'CHILD', label: '菜单'},
    {value: 'BUTTON', label: '按钮'}
  ]

  

  
  //树形数据
  mapOfExpandedData: { [key: string]: TreeNodeInterface[] } = {};
  collapse(array: TreeNodeInterface[], data: TreeNodeInterface, $event: boolean): void {
    if (!$event) {
      if (data.children) {
        data.children.forEach(d => {
          const target = array.find(a => a.key === d.key)!;
          target.expand = false;
          this.collapse(array, target, false);
        });
      } else {
        return;
      }
    }
  }
  convertTreeToList(root: TreeNodeInterface): TreeNodeInterface[] {
    const stack: TreeNodeInterface[] = [];
    const array: TreeNodeInterface[] = [];
    const hashMap = {};
    stack.push({ ...root, level: 0, expand: false });

    while (stack.length !== 0) {
      const node = stack.pop()!;
      this.visitNode(node, hashMap, array);
      if (node.children) {
        for (let i = node.children.length - 1; i >= 0; i--) {
          stack.push({ ...node.children[i], level: node.level! + 1, expand: false, parent: node });
        }
      }
    }

    return array;
  }
  visitNode(node: TreeNodeInterface, hashMap: { [key: string]: boolean }, array: TreeNodeInterface[]): void {
    if (!hashMap[node.key]) {
      hashMap[node.key] = true;
      array.push(node);
    }
  }
  search(): void {
    this.getTableList()
  }

 
  constructor(private fb: FormBuilder,
    private msg: NzMessageService,
    private modalService: NzModalService,
    private service:SystemManageService,
    private funcs:FuncsService
    ) {}
  

  ngOnInit(): void {
    this.modalForm = this.fb.group({
      id: [null],
      name: [null, [Validators.required,Validators.maxLength(15)]],
      desc: [null, [Validators.maxLength(15)]],
      type: ['MAIN', [Validators.required]], //MAIN分组 CHILD菜单 BUTTON按钮
      parentMenu: [null],
      url: [null],
      permission: [null],
      visible:[null],
      sort:[null],
      icon:[null]
    });
    this.getTableList()
   
  }
  checkPermission(permission:string){
    return this.funcs.checkPermission(permission)
  }



  getTableList() {
    this.loading = true
    this.service.getMenus().subscribe({
      next: list=>{
        this.originMenuList = _.cloneDeep(list)
        this.rootMenuList = [];
        this.searchRootMenu(list);
        this.searchChildren(list, this.rootMenuList);
        this.listOfData = this.rootMenuList;
        this.listOfData.forEach((list:any) => {
          this.mapOfExpandedData[list.id] = this.convertTreeToList(list);
        });
        this.findLeaf(this.listOfData);
        this.loading = false
        console.log(this.listOfData)
      },
      error: error=>{
        this.msg.error(error.message)
        this.loading = false
      }
    })
  }

  showAddModal(): void {
    this.isEdit = false
    this.modalForm.reset()
    this.modalForm.get("type")?.setValue('MAIN'); 
    this.modalForm.get("visible")?.setValue(true); 
    this.parentMenuId = null; 
    this.isAddVisible = true;
  }
  //编辑
  showEditModal(info:any): void {
    console.log(info)
    this.isEdit = true
    this.modalForm.get("id")?.setValue(info.id)
    this.modalForm.get("name")?.setValue(info.name)
    this.modalForm.get("desc")?.setValue(info.description)
    this.modalForm.get("type")?.setValue(info.type)
    this.modalForm.get("parentMenu")?.setValue(info.parentMenu)
    this.parentMenuId = info.parent?.id || null
    this.modalForm.get("url")?.setValue(info.url)
    this.modalForm.get("permission")?.setValue(info.permission)
    this.modalForm.get("visible")?.setValue(info.visible==='VISIBLE'?true:false)
    this.modalForm.get("sort")?.setValue(info.sort)
    this.isAddVisible = true;
  }

  //删除
  delBasement(id: string): void {
    this.modalService.confirm({
      nzTitle: '确定删除所选条目?',
      nzContent: '',
      nzOkText: '是',
      nzOkDanger:true,
      nzOnOk: () => this.delConfig(id),
      nzCancelText: '否',
      nzOnCancel: () => ''
    });
  }
  delConfig(id: string) {
    this.service.deleteMenus(id).subscribe(val => {   
        this.msg.success("删除成功");
        this.getTableList();
      }, error => {
          this.msg.error(error.error.message)
      });     
  }

  handleAddOk(): void {
   
    const modalForm = this.modalForm.value;
    if(modalForm.name?.trim()==''){
      this.msg.error('必填项不能为空');
      return;
    }
   
    const info:any = {
      type: modalForm.type,
      name: modalForm.name,
      description:modalForm.desc,
      parentMenu:null,
      url: modalForm.url,
      permission:modalForm.permission,
      visible:modalForm.visible?'VISIBLE':'HIDE',
      sort:modalForm.sort
    }
    if(modalForm.parentMenu) {
      info.parentMenu = modalForm.parentMenu;
    }
    if (this.isEdit) {
      info['id'] = modalForm.id;
      this.service.updateMenus(info).subscribe({
        next: item => {
          this.msg.success("保存成功");
          this.getTableList()
          this.isAddVisible = false;
        },
        error: error => {
          this.msg.error(error.error.message)
        }
      })
    } else {
      this.service.createMenus(info).subscribe({
        next: item => {
          this.msg.success("保存成功");
          this.getTableList()
          this.isAddVisible = false;
        },
        error: error => {
          this.msg.error(error.error.message)
        }
      })
    }
    
  }

  handleAddCancel(): void {
    this.isAddVisible = false;
  }


  /* 
  * 数据格式拼接
  * 判断是否为叶子节点leaf
  * key=id
  * 根据orderNum排序
  * 循环parent拼接children
  */
  // 查找根菜单
  rootMenuList:any[]=[];
  searchRootMenu(list:any) {
    list.forEach((it:any) => {
      if(!it||(typeof it) !='object') {
        return;
      }
      if (it.children===''||it.children==null) {
        it.children = [];
        it.isLeaf = true;
      }
      it.key = it.id;
      it.title = it.name;
      // console.log(it.icon)
      it.icon = it.icon
      // it.icon = it.icon?it.icon.value:'';
      if ((typeof it.parentMenu)!='object'||it.parentMenu==null) {
        let isContains = false;
        this.rootMenuList.forEach((i:any) => {
         if (i.id == it.id) {
            isContains = true;
          }
        })
        if (!isContains) {
          this.rootMenuList.push(it);
        }
      } else {
        this.searchRootMenu([it.parentMenu]);
      }
    })
  }

 

  // 循环插入children
  searchChildren(list:any, menuList:any) {
    if (menuList==='') {
      return;
    }
    list.forEach((item:any) => {
      // if(item.type == 'BUTTON') {
      //   return;
      // }
      if (item.parentMenu!=='') {
        menuList.forEach((it:any) => {
          it.key = it.id;
          it.title = it.name;
          // it.icon = it.icon?it.icon.value:'';
          if ((typeof item.parentMenu)!="object"||item.parentMenu==null) {
            return
          }
          if (it.id === item.parentMenu.id) {
            if (it.children!=='') {
              let isCt = false;
              it.children.forEach((i:any) => {
                if (i.id === item.id) {
                  isCt = true;
                }
              })
              if (!isCt) {
                // 根据排序插入
                it.children.push(item)
              }
            } else {
              it.children = [item];
            }
          }
        })
      }
    })
    menuList.forEach((it:any) => {
      this.searchChildren(list, it.children);
    })
  }

   
  // 判断是否为叶子节点
  findLeaf(list:any) {
    list.forEach((item:any) => {
      item.key = item.id;
      // item.icon = item.icon?item.icon.value:''
      if (!item.children){
        item.children = [];
        item.isLeaf = true;
        return;
      }
      if (item.children.length<=0) {
        item.children = [];
        item.isLeaf = true;
      } else {
        item.isLeaf = false;
        this.findLeaf(item.children);
      }
    })
  }
  /* =========================数据拼接完成========================== */

  selectParent(event:any) {
    //id=>对象
    const parentMenu = this.originMenuList.filter((e:any)=>e.id===event)[0]
    this.modalForm.get('parentMenu')?.setValue(parentMenu)
    console.log(parentMenu)
  }

  switchChange(event:any){
    console.log(event?'VISIBLE':"HIDE")  
  }







}

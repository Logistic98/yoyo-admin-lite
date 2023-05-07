import { filter } from 'rxjs/operators';
import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzModalService } from 'ng-zorro-antd/modal';
import { NzFormatEmitEvent, NzTreeComponent, NzTreeNodeOptions } from 'ng-zorro-antd/tree';
import { SystemManageService } from '@service/system-manage.service';
import * as _ from 'lodash';

export interface TreeNodeInterface {
 key: string;
 title: string;
 age?: number;
 level?: number;
 expand?: boolean;
 address?: string;
 children?: TreeNodeInterface[];
 parent?: TreeNodeInterface;
 type?: any;
 url?: string;
 createTime?: string;
 name?: string;
}
@Component({
  selector: 'app-role-manage',
  templateUrl: './role-manage.component.html',
  styleUrls: ['./role-manage.component.less']
})
export class RoleManageComponent implements OnInit {
  modalForm!: FormGroup;
  isAddVisible = false;
  loading = false;
  listOfData: any

  pageIndex = 1;
  pageSize = 10;
  total = 0;

  isEdit = false
  readonly = false //修改的时候角色不允许编辑
  defaultCheckedKeys: any[]=[]
  selectMenus: any = [];
  menuList: any[]=[]//菜单列表
  originMenuList:any = []

  listOfOption: Array<{ value: string; label: string }> = [];
  listOfSelectedValue = [''];


  @ViewChild('nzTreeComponent', { static: false }) nzTreeComponent!: NzTreeComponent;
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

  constructor(private fb: FormBuilder,
    private msg: NzMessageService,
    private modalService: NzModalService,
    private service:SystemManageService) { }

  ngOnInit(): void {
    this.modalForm = this.fb.group({
      id: [null],
      name: [null, [Validators.required,Validators.maxLength(15)]],
      listOfSelectedValue:[null]
    });
    this.getTableList()
    this.getMenuList()
   
   
  }


 
 getTableList() {
   this.loading = true
   const info = {
     page: this.pageIndex,
     pageSize: this.pageSize,
     roleName: ''
   }
   this.service.getRoles(info).subscribe(res => {
     console.log(res)
     this.listOfData = res.content
     this.loading = false
   }, error => {
     this.msg.error(error.message)
     this.loading = false
   })
 }


 showAddModal(): void {
   this.isEdit = false
   this.modalForm.reset()
   this.isAddVisible = true;
   this.selectMenus = [];
   this.defaultCheckedKeys =[];
 }


 /*获取菜单&变形 */
 getMenuList() {
    this.service.getMenus().subscribe(list => {
      console.log('allmenu:',list)
      this.originMenuList = _.cloneDeep(list)
      this.rootMenuList = [];
      this.searchRootMenu(list);
      this.searchChildren(list, this.rootMenuList);
      this.menuList = this.rootMenuList;
      this.menuList.forEach((item:any) => {
        this.mapOfExpandedData[item.id] = this.convertTreeToList(item);
      });
      this.findLeaf(this.menuList);
      console.log('rootMenuList',this.rootMenuList)
      this.loading = false
    }, error => {
      this.msg.error(error.message)
      this.loading = false
    })
 }


 showEditModal(info:any): void {
  this.isEdit = true
  this.readonly = true
  this.modalForm.get('name')?.setValue(info.name)
  this.modalForm.get('id')?.setValue(info.id);
  this.isAddVisible = true;
  console.log(info)
  
  //  //角色已经绑定的菜单
  const trees = this.listOfData.filter((item:any)=>item.id===info.id)[0].menus;
  let keys:any = []
  trees.map((t:any)=>{
    if(t.parentMenu&&t.url){
     keys.push(t.id)
    }
    if(t.parentMenu&&t.type==='BUTTON'){
     keys.push(t.id)
    }
   /*
   * fixed 因为按钮和菜单没有强关联关系，所以取消了目录和菜单的关联nzCheckStrictly
   */ 
   keys.push(t.id)
   
  })
  this.defaultCheckedKeys = keys
  this.selectMenus = keys
  console.log(this.defaultCheckedKeys)
  

 }

 handleAddOk(): void {
   this.isAddVisible = false;
   const modalForm = this.modalForm.value
   
   if(modalForm.name.trim()==''){
     this.msg.error('必填项不能为空');
     return;
   }
   //因接口需要menus对象，所以[id]=>[对象]
   const menus = this.originMenuList.filter((m:any)=> this.defaultCheckedKeys.includes(m.id) )
   //console.log(menus)
   
   if (this.isEdit) {
    let info:any = {
      id: modalForm.id,
      name: modalForm.name,
      menus: menus, 
    }
     this.service.updateRoleMenus(info).subscribe(item => {
       this.msg.success("保存成功");
       this.getTableList()
     }, error => {
       this.msg.error(error.error.message)
     })
    
   } else {
    let info:any = {
      name: modalForm.name,
      menus:menus
    }
     this.service.createRoles(info).subscribe(item => {
       this.msg.success("保存成功");
       this.getTableList()
     }, error => {
       this.msg.error(error.error.message)
     })
   }
 }

 handleAddCancel(): void {
   this.isAddVisible = false;
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
   this.service.deleteRole(id).subscribe(val => {   
       this.msg.success("删除成功");
       this.getTableList();
     }, error => {
         this.msg.error(error.error.message)
     });     
 }






/* 
 * 先筛出根菜单 && key=id
 * 再筛出子菜单
 * 判断是否为叶子节点leaf
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
     }
     it.key = it.id;
     it.title = it.name;
     if (it.parentMenu==null) {
       //父级
       //避免重复push
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
       //子级的父级已经筛过了没必要筛一遍
      //  this.searchRootMenu([it.parent]);
     }
   })
 }



 /**
  * 循环插入children
  * {list:所有menu, menuList:根菜单}
  */
 
 searchChildren(list:any, menuList:any) {
  list.forEach((item:any) => {
    if (item.parentMenu===null) {
      return
    }
    menuList.forEach((root:any) => {
      if (item.parentMenu.id === root.id) {
          let isContains = false;
          root.children.forEach((i:any) => {
            if (i.id === item.id) {
              isContains = true;
            }
          })
          if (!isContains) {
            root.children.push(item)
          }
      }
    })
  })
  //递归 判断子菜单是否还有子集
  menuList.forEach((it:any) => {
    this.searchChildren(list, it.children);
  })
 }

  
 // 判断是否为叶子节点
 //  list:变形完的menuList
 findLeaf(list:any) {
   list.forEach((item:any) => {
     if (item.children.length===0){
       item.isLeaf = true;
     } else {
       item.isLeaf = false;
       this.findLeaf(item.children);
     }
   })
 }
 /* =========================数据拼接完成========================== */

 // 树形菜单选择

 nzEvent(event:any): void {
  //有强关联关系时候的点击回调事件 *** 目前已废用
   console.log(event.keys)
   this.selectMenus = [...event.keys];
  //后端只要子菜单id，前端根据父级筛出子集
   event.keys.forEach((sid:any,i:any) => {
     this.menuList.forEach((root)=>{
       //根id 判断选中sid是不是根
      if(sid === root.id){ 
          let NewArr: any[] = []
          root.children.forEach((child:any) => {
            if(child.isLeaf===true){
              NewArr.push(child.id)
            }else{
              child.children.forEach((c:any)=>{
                if(c.isLeaf===true){
                  NewArr.push(c.id)
                }
              })
            }
          });
          const index = this.selectMenus.indexOf(sid)
          this.selectMenus.splice(index,1,...NewArr)
          
      }
     })
   });
   console.log(this.selectMenus)
 }

 nzEvent2(event:any): void {
  //按钮和菜单没有强关联关系，但是目录和菜单有，组件无法单独设置某层级是否有关联，所以全部去掉关联。目录和菜单关联关系自己写
  //console.log(event)
  const key = event.node.key
  const keyObj = event.node.origin
  let keys = event.keys
  // console.log('defaultCheckedKeys',this.defaultCheckedKeys)

  this.menuList.forEach((root)=>{
    //根据点击key判断点击是不是目录,把子菜单勾选或取消
    if(key === root.id){
      if(this.defaultCheckedKeys.includes(key)){
        //父级取消
        root.children.forEach((child:any) => {
          if(child.type==='CHILD'&&child.url){
            keys =  keys.filter((k:any)=> k!=child.id)
          }
        })
      }else{
        //父级选中
        root.children.forEach((child:any) => {
          if(child.type==='CHILD'&&child.url){
            keys = [...new Set([...keys,child.id])]
          }
        })
      }
    }else{
      //如果不是目录，是菜单的话，判目录下菜单是否全部不存在，自动取消目录勾选状态
      //判断目录是否已全选，自动勾选父级
      //TODO 半选状态
      if(keyObj.type==='CHILD'){
        const parentId = keyObj.parentMenu.id
        if(parentId === root.id){
          //检测目录所有子菜单没有选中的
          let allChildNoChecked = root.children.every((child:any) => {
            return child.type==='CHILD'&&child.url&& !keys.includes(child.id)
          })
          //console.log(allChildNoChecked)
          if(allChildNoChecked){
            keys =  keys.filter((k:any)=> k!= parentId)
          }
          //检测所有子菜单全被选中，自动勾选父级
          let allChildChecked = root.children.every((child:any) => {
            return child.type==='CHILD'&&child.url&& keys.includes(child.id)
          })
          //console.log(allChildChecked)
          if(allChildChecked){
            keys =  [...new Set([...keys,parentId])]
          }else{
             //检测子菜单没有全选中，取消父级的勾选
            keys =  keys.filter((k:any)=> k!= parentId)
          } 
          //检测至少有一个子菜单是选中的，设置半选状态

          
        }
      }
    }
  })
  console.log(keys) //TODO 结果中除掉目录id，后端好像也没验证这个
  this.defaultCheckedKeys = keys
 
}




 selectChange(e:any){
  console.log(this.modalForm.get('listOfSelectedValue')!.value)
 }



}

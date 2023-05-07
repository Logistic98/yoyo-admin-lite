import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzModalService } from 'ng-zorro-antd/modal';
import { NzFormatEmitEvent, NzTreeNode } from 'ng-zorro-antd/tree';
import { SystemManageService } from '@service/system-manage.service';
export interface TreeNodeInterface {
  id: number;
  key: string;
  level?: number;
  expand?: boolean;
  address?: string;
  children: TreeNodeInterface[];
  parent?: TreeNodeInterface;
  description?: string;
  sort: number;
  status?: number;
  visible?: string;
  createTime?: string;
  name?: string;
  title?: string;
  showChild?: any;
}
@Component({
  selector: 'app-dept-manage',
  templateUrl: './dept-manage.component.html',
  styleUrls: ['./dept-manage.component.less']
})
export class DeptManageComponent implements OnInit {


  modalForm!: FormGroup;

  isAddVisible = false;
  loading = false;
  switchLoading = false;
  listOfData: any = [];
  parentMenuId: any
  existingNames = new Set()


  isEdit = false
  isEditingName = ''

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

  constructor(private fb: FormBuilder,
    private msg: NzMessageService,
    private modalService: NzModalService,
    private service: SystemManageService) { }

  ngOnInit(): void {
    this.modalForm = this.fb.group({
      id: [null],
      name: [null, [Validators.required, Validators.maxLength(15), this.forbidRepeat.bind(this)]],
      parentMenu: [null],
    });
    this.getTableList()



  }

  forbidRepeat(control: FormControl) {
    //新增的时候校验 所有名字 不能重复
    if (!this.isEdit) {
      const _has = this.existingNames.has(control.value)
      return _has ? { 'hasExist': true } : null
    } else {
      //修改的时候校验 除当前的名字之外 不能重复
      this.existingNames.delete(this.isEditingName)
      const _exceptSelf = [... this.existingNames]
      const _has = _exceptSelf.includes(control.value)
      return _has ? { 'hasExist': true } : null
    }

  }



  getTableList() {
    this.loading = true
    this.service.getDepartment().subscribe(res => {
      this.listOfData = res
      this.listOfData.forEach((item: any) => {
        this.mapOfExpandedData[item.id] = this.convertTreeToList(item);
      });
      this.findLeaf(this.listOfData);
      this.loading = false
      console.log(this.listOfData)
      //已有名字
      // res.forEach((e:any)=>{
      //   this.existingNames.add(e.name);
      // })
      // console.log(this.existingNames)
    }, error => {
      this.msg.error(error.message)
      this.loading = false
    })
  }

  showAddModal(): void {
    this.isEdit = false
    this.modalForm.reset()
    this.isAddVisible = true;
  }

  showEditModal(info: any): void {
    this.isEdit = true
    this.isEditingName = info.name
    this.isAddVisible = true;
    console.log(info)
    this.modalForm.get('name')?.setValue(info.name)
    this.modalForm.get('id')?.setValue(info.id)
    this.modalForm.get('parentMenu')?.setValue(info.parentDept)
    this.parentMenuId = info.parentDept.id

  }

  //删除
  delBasement(id: number): void {
    this.modalService.confirm({
      nzTitle: '确定删除所选条目?',
      nzContent: '',
      nzOkText: '是',
      nzOkDanger: true,
      nzOnOk: () => this.delConfig(id),
      nzCancelText: '否',
      nzOnCancel: () => ''
    });
  }
  delConfig(id: number) {
    this.service.deleteDepartment(id).subscribe(val => {
      this.msg.success("删除成功");
      this.getTableList();
    }, error => {
      this.msg.error(error.error.message)
    });
  }


  handleAddOk(): void {

    const modalForm = this.modalForm.value

    if (this.isEdit) {
      let info: any = {
        id: modalForm.id,
        name: modalForm.name,
        parentDept: modalForm.parentMenu
      }
      this.service.updateDepartment(info).subscribe(item => {
        this.msg.success("保存成功");
        this.getTableList()
        this.isAddVisible = false;
      }, error => {
        this.msg.error(error.error.message)
      })

    } else {
      let info: any = {
        name: modalForm.name,
        parentDept: modalForm.parentMenu
      }
      this.service.createDepartment(info).subscribe(item => {
        this.msg.success("保存成功");
        this.getTableList()
        this.isAddVisible = false;
      }, error => {
        this.msg.error(error.error.message)
      })
    }
  }

  handleAddCancel(): void {
    this.isAddVisible = false;
  }

  // 判断是否为叶子节点
  findLeaf(list: any) {
    list.forEach((item: any) => {
      item.key = item.id;
      // item.icon = item.icon?item.icon.value:''
      if (!item.children) {
        item.children = [];
        item.isLeaf = true;
        return;
      }
      if (item.children.length <= 0) {
        item.children = [];
        item.isLeaf = true;
      } else {
        item.isLeaf = false;
        this.findLeaf(item.children);
      }
    })
  }

  //树转列表
  treeToList(tree: [], result: any = [], level = 0) {
    tree.forEach((node: any) => {
      result.push(node)
      node.level = level + 1
      node.children && this.treeToList(node.children, result, level + 1)
    })
    return result
  }

  selectParent(event: any) {
    //根据id筛对象
    const lists = this.treeToList(this.listOfData)
    const obj = lists.filter((e: any) => e.id === event)[0]
    this.modalForm.get('parentMenu')?.setValue(obj)
    console.log(this.modalForm.value.parentMenu)

  }


  clickSwitch(item:any) {
    this.switchLoading = true
    if(!item.status){
      //启用
      this.service.onDepartment(item.id).subscribe(res=>{
        this.switchLoading = false
        this.msg.success("启用成功");
        item.status = true
      })
    }else{
      //停用
      this.service.offDepartment(item.id).subscribe(res=>{
        this.switchLoading = false
        this.msg.success("停用成功");
        item.status = false
      })
    }
  }




}

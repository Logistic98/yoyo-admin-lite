import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzModalService } from 'ng-zorro-antd/modal';
import { SystemManageService } from '@service/system-manage.service';
import { CryptoService } from "@service/crypto.service";

@Component({
  selector: 'app-user-manage',
  templateUrl: './user-manage.component.html',
  styleUrls: ['./user-manage.component.less']
})
export class UserManageComponent implements OnInit {
  modalForm!: FormGroup;
  changePassWordForm!:FormGroup;
  isAddVisible = false;
  isPassWordVisible = false;
  loading = true;
  listOfData: any;
  deptTree:any;//部门树
  deptIdObj!: {};//选中的部门对象
  page = 1
  pageSize = 20
  total:any = 0


  roles:any //角色下拉数据
  genders = [{value:1,label:'男'},{value:2,label:'女'}]

  isEdit = false
  id = ''//编辑的账号id

  //搜索
  searchName = ''
  constructor(
    private fb: FormBuilder,
    private msg: NzMessageService,
    private modalService: NzModalService,
    private service:SystemManageService,
    private cryptoService: CryptoService,
    ) { }

  ngOnInit(): void {
    this.modalForm = this.fb.group({
      name:[null,[Validators.required,Validators.maxLength(10)]],
      gender:[null,[Validators.required]],
      username: [null, [Validators.required,Validators.minLength(3)]],
      password:[null,Validators.required],
      roleId:[null,Validators.required],
      deptId:[null,Validators.required],
      phoneNumber:['',[Validators.required,Validators.pattern(/^(?:(?:\+|00)86)?1[3-9]\d{9}$/)]]

    });

    this.changePassWordForm = this.fb.group({
      oldPassword:[null,Validators.required],
      newPassword:[null,Validators.required]
    })

    this.getRolesList()
    this.getUserList()
    this.getDeptList()
  }

  getUserList(){
    this.loading = true;
    const data = {
      name: this.searchName,
      page: this.page,
      pageSize: this.pageSize
    }
    this.service.getUser(data).subscribe(res => {
      console.log('用户列表--',res)
      this.loading = false;
      this.listOfData = res.content
      this.total = res.totalElements
    }, error => {
      this.msg.error(error.message)
    })
  }

  getDeptList() {
    this.service.getDepartment().subscribe(res => {
      this.deptTree = res
    })
  }

  search(){
    this.getUserList()
  }


  getRolesList() {
    this.service.getRoles().subscribe(res => {
      console.log('角色列表--',res)
      this.roles = res.content
    }, error => {
      this.msg.error(error.message)
    })
  }

  showAddModal(): void {
    this.isEdit = false
    this.modalForm.reset()
    this.isAddVisible = true;

  }

  showEditModal(info:any): void {
     this.isEdit = true
     this.isAddVisible = true;
     console.log(info)
     this.id = info.id
     this.modalForm.get('name')?.setValue(info.name)
     this.modalForm.get('roleId')?.setValue(info.role.id)
     this.modalForm.get('deptId')?.setValue(info.dept?.id)
     this.modalForm.get('gender')?.setValue(info.gender)
     this.modalForm.get('phoneNumber')?.setValue(info.phoneNumber)
    //编辑隐藏用户名&密码，并清除验证
    this.modalForm.get('username')!.clearValidators()
    this.modalForm.get('username')!.markAsPristine()
    this.modalForm.get('username')!.updateValueAndValidity()
    this.modalForm.get('password')!.clearValidators()
    this.modalForm.get('password')!.markAsPristine()
    this.modalForm.get('password')!.updateValueAndValidity()
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
    this.service.deleteUser(id).subscribe(val => {
      this.msg.success("删除成功");
      this.getUserList();
    }, error => {
        this.msg.error(error.error.message)
    });
  }


  handleAddOk(): void {
    this.isAddVisible = false;
    const modalForm = this.modalForm.value
    if (this.isEdit) {

      let info:any = {
        id: this.id,
        role: this.roles.filter((r:any)=>r.id===modalForm.roleId)[0],
        dept: this.deptIdObj,
        phoneNumber: modalForm.phoneNumber,
        name:modalForm.name,
        gender:modalForm.gender,

      }
      this.service.updateUser(info).subscribe(item => {
        this.msg.success("保存成功");
        this.getUserList()
      }, error => {
        this.msg.error(error.error.message)
      })

    } else {
      console.log(modalForm)
      const aesBody = this.cryptoService.encryptByEnAES(JSON.stringify(modalForm));
      this.service.createUser(aesBody).subscribe(item => {
        this.msg.success("保存成功");
        this.getUserList()
      }, error => {
        this.msg.error(error.error.message)
      })
    }
  }

  handleAddCancel(): void {
    this.isAddVisible = false;
  }
  changePassWordCancle(){
    this.isPassWordVisible = false
  }

  pageIndexChange(e:any){
    console.log(e)
    this.page = e
    this.getUserList()
  }
  pageSizeChange(e:any){
    console.log(e)
    this.pageSize = e
    this.getUserList()
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

  selectDept(event: any) {
    console.log(event)
    this.deptIdObj = this.treeToList(this.deptTree).filter((e:any)=>e.id===event)[0]
  }

  showChangePassword(){
    this.isPassWordVisible = true
  }

  changePassWordOk(){
    const changePassWordForm = this.changePassWordForm.value
    console.log(changePassWordForm)
    const aesBody = this.cryptoService.encryptByEnAES(JSON.stringify(changePassWordForm));
    this.service.passwordChange(aesBody).subscribe({
      next:res=>{
        this.msg.success("修改成功");
      },
      error:error=>{
        this.msg.error(error.error.message)
      }
    })
  }

}

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MenuManageComponent } from './menu-manage/menu-manage.component';
import { RoleManageComponent } from './role-manage/role-manage.component';
import { UserManageComponent } from './user-manage/user-manage.component';
import { DeptManageComponent } from './dept-manage/dept-manage.component';

const routes: Routes = [{
  path:'',
  redirectTo: 'menu-manage',
},{
  path:'menu-manage',
  data: {title: '菜单管理'},
  component:MenuManageComponent,
},{
  path:'role-manage',
  data: {title: '角色管理'},
  component:RoleManageComponent,
},{
  path:'dept-manage',
  data: {title: '部门管理'},
  component:DeptManageComponent,
},{
  path:'user-manage',
  data: {title: '用户管理'},
  component:UserManageComponent,
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SystemManageRoutingModule { }

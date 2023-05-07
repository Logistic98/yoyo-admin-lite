import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { SystemManageRoutingModule } from './system-manage-routing.module';
import { MenuManageComponent } from './menu-manage/menu-manage.component';
import { RoleManageComponent } from './role-manage/role-manage.component';
import { UserManageComponent } from './user-manage/user-manage.component';
import { DeptManageComponent } from './dept-manage/dept-manage.component';
import { directiveModule } from 'src/@directive/directive.module';

@NgModule({
  declarations: [
    MenuManageComponent,
    RoleManageComponent,
    UserManageComponent,
    DeptManageComponent
    
  ],
  imports: [
    CommonModule,
    SharedModule,
    SystemManageRoutingModule,
    directiveModule
  ]
})
export class SystemManageModule { }

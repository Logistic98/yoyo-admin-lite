import { Inject,Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { HttpClient, HttpRequest, HttpResponse } from '@angular/common/http';
import { filter, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SystemManageService {

  constructor(private http: HttpClient, @Inject('BASE_CONFIG') private config:any) { }

  //获取当前用户菜单列表
  getMenus(): Observable<any> {
    const url = `${this.config.url}/web/user/current/menus`;
    return this.http.get(url).pipe(map((res) => res));
  }

  //菜单创建
   createMenus(data:any): Observable<any> {
    const url = `${this.config.url}/web/menu/add`;
    return this.http.post(url,data).pipe(map((res) => res));
  }


  //菜单删除
  deleteMenus(id:any): Observable<any> {
    const url = `${this.config.url}/web/menu/delete/${id}`;
    return this.http.get(url).pipe(map((res) => res));
  }

  //菜单更新
  updateMenus(data:any): Observable<any> {
    const url = `${this.config.url}/web/menu/update`;
    return this.http.post(url,data).pipe(map((res) => res));
  }

  //角色列表
  getRoles(data?:any): Observable<any> {
    const url = `${this.config.url}/web/role/page`;
    return this.http.get(url,{params:data}).pipe(map((res) => res));
  }

   //创建角色
   createRoles(data: any): Observable<any> {
    const url = `${this.config.url}/web/role/add`;
    return this.http.post(url,data).pipe(map((res) => res));
  }

  //获取角色绑定的菜单
  //此后台无单独接口，在角色列表中拿

  //角色修改
  updateRoleMenus(data:any): Observable<any> {
    const url = `${this.config.url}/web/role/update`;
    return this.http.post(url,data).pipe(map((res) => res));
  }

  //角色删除
  deleteRole(id:any): Observable<any> {
    const url = `${this.config.url}/web/role/delete/${id}`;
    return this.http.get(url).pipe(map((res) => res));
  }

  //创建部门
  createDepartment(data: any): Observable<any> {
    const url = `${this.config.url}/web/dept/add`;
    return this.http.post(url,data).pipe(map((res) => res));
  }

  //部门列表
  getDepartment(): Observable<any> {
    const url = `${this.config.url}/web/dept/list`;
    return this.http.get(url).pipe(map((res) => res));
  }


  //更新部门
  updateDepartment(data:any): Observable<any> {
    const url = `${this.config.url}/web/dept/update`;
    return this.http.post(url,data).pipe(map((res) => res));
  }

  //删除部门
  deleteDepartment(id:any): Observable<any> {
    const url = `${this.config.url}/web/dept/delete/${id}`;
    return this.http.get(url).pipe(map((res) => res));
  }

  //启用部门
  onDepartment(id:any): Observable<any> {
    const url = `${this.config.url}/web/dept/enable/${id}`;
    return this.http.get(url).pipe(map((res) => res));
  }

  //禁用部门
  offDepartment(id:any): Observable<any> {
    const url = `${this.config.url}/web/dept/disable/${id}`;
    return this.http.get(url).pipe(map((res) => res));
  }

  //创建用户
  createUser(data: any): Observable<any> {
    const url = `${this.config.url}/web/user/add`;
    return this.http.post(url,data).pipe(map((res) => res));
  }

  //用户列表
  getUser(data: any): Observable<any> {
    const url = `${this.config.url}/web/user/page`;
    return this.http.get(url,{params:data}).pipe(map((res) => res));
  }
  //根据角色查用户
  getUserByRole(data: any): Observable<any> {
    const url = `${this.config.url}/web/user/list`;
    return this.http.get(url,{params:data}).pipe(map((res) => res));
  }

   //更新用户
   updateUser(data:any): Observable<any> {
    const url = `${this.config.url}/web/user/update`;
    return this.http.post(url,data).pipe(map((res) => res));
  }

  //删除用户
  deleteUser(id:any): Observable<any> {
    const url = `${this.config.url}/web/user/delete/${id}`;
    return this.http.get(url).pipe(map((res) => res));
  }

  //修改密码
  passwordChange(obj:any): Observable<any> {
    const url = `${this.config.url}/web/user/password/update`;
    return this.http.post(url,obj).pipe(map((res) => res));
  }

}

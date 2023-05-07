import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
@Injectable({
    providedIn: 'root'
})
export class AuthService {
    constructor(private http: HttpClient) {}

    /**
     * 获取用户的当前登录信息
     * @returns Observable<any>
     */
    getCurrentAuth(): Observable<any> {
        return this.http.get(`/api/web/user/current`);
    }

    /**
     * 登录系统
     * @params formdata
     * @returns Observable<any>
     */
     loginSystem(formdata: any): Observable<any> {
        return this.http.post(`/auth/login`, formdata);
      }
   
    /**
     * 退出系统
     * @returns Observable<any>
     */
     logoutSystem(): Observable<any> {
        return this.http.get(`/auth/logout`);
      }
   
}

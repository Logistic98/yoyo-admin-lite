# 登录

项目设计层面，登录状态完全由后台维护，前端不需要维护token状态，登录状态没有在拦截器中判断，而是路由守卫通过后端的一个接口来获取。

## 路由配置
项目遵循 [路由惰性加载](blog/guide/newPage.html#路由惰性加载) 的方式已经建好了 `passport模块`，`登录/注册`等相关业务组件可以直接在此模块中创建，`src/app/passport/passport-components.ts` 相当于 登录页面的 [layout](/blog/guide/layout.html#layout), `登录/注册`等业务组件可以按照`路由简单配置`的方式，直接在`passport-routing.moudle.ts`中配置。

```js
//passport-routing.moudle.ts

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { PassportComponent } from './passport.component';

const routes: Routes = [
  {
    path:'',
    component:PassportComponent,
    children: [
      {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
      },
      {
        path:"login",
        component: LoginComponent,
        data: { title: '登录界面'}
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PassportRoutingModule { }
```

## 加密算法

采用 [crypto-js加密库](https://cryptojs.gitbook.io/docs/) ,本项目与后端约定 AES加密方式 。

```bash
npm install crypto-js
```

把加密算法封装成服务
```js
//src/@service/crypto.service.ts

import { Injectable } from '@angular/core';
import { AES, enc, mode, pad } from 'crypto-js';

@Injectable({
  providedIn:'root'
})
export class CryptoService {
  private key: string = '1234567890dhhdhhdhdhh……'; //32位 需要是16的倍数
  constructor() {
  }
  /**
   * AES加密
   */
  encryptByEnAES(data: string): string {
    let Key = enc.Utf8.parse(this.key);
    let tmpAES = AES.encrypt(enc.Utf8.parse(data), Key, {
      mode: mode.ECB,
      padding: pad.Pkcs7
    });
    return tmpAES.toString();
  }

  /**
   * AES解密
   */
  encryptByDeAES(data: string): string {
    let Key = enc.Utf8.parse(this.key);
    let tmpDeAES = AES.decrypt(data, Key, {
      mode: mode.ECB,
      padding: pad.Pkcs7
    });
    return tmpDeAES.toString(enc.Utf8);
  }
}
```

使用

把账号密码对象转成 json字符串 进行加密

```js
// login.component.ts

import { CryptoService } from "@service/crypto.service";
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.less']
})
export class LoginComponent implements OnInit {

  constructor( private cryptoService: CryptoService ) {}

  submit() {
    const info = {
      username: 'userName',
      password: 'password'
    }
    const aesBody = this.cryptoService.encryptByEnAES(JSON.stringify(info));
    this.service.loginSystem(aesBody).subscribe((v: any) => {
      this.loading = true;
      this.router.navigateByUrl("/web");
    })

  }
}
```
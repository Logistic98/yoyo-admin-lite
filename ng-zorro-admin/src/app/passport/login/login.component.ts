import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { FormGroup, FormBuilder, Validators } from "@angular/forms";
import { NzMessageService } from "ng-zorro-antd/message";
import { NzModalService } from 'ng-zorro-antd/modal';

import { AuthService } from "@service/auth.service";
import { CryptoService } from "@service/crypto.service";



@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.less']
})
export class LoginComponent implements OnInit {
  error = '';
  form: FormGroup;
  loading = false;
  current: any;

  constructor(
    fb: FormBuilder,
    private router: Router,
    public msg: NzMessageService,
    private modalSrv: NzModalService,
    private service: AuthService,
    private cryptoService: CryptoService,
  ) {
    this.form = fb.group({
      userName: ['', [Validators.required, Validators.minLength(3), Validators.pattern(/^[^\u4e00-\u9fa5]+$/) ]],//10000000000
      password: ['', Validators.required],//10000000000
      remember: [true]
    });
    modalSrv.closeAll();
  }



  submit() {
    if (this.form.valid) {
      //console.log('submit', this.form.value);
      const info = {
        username: this.form.value.userName,
        password: this.form.value.password
      }
      const aesBody = this.cryptoService.encryptByEnAES(JSON.stringify(info));
      this.service.loginSystem(aesBody).subscribe({
        next:(v: any) => {
          this.loading = true;
          this.router.navigateByUrl("/web")
        },
        error: (error) => {
          this.error = error.error.message
          console.log(this.error)
        }
      },)

    } else {
      Object.values(this.form.controls).forEach(control => {
        if (control.invalid) {
          control.markAsDirty();
          control.updateValueAndValidity({ onlySelf: true });
        }
      })
    }

  }

  ngOnInit(): void {

  }

  ngOnDestroy(): void { }

}

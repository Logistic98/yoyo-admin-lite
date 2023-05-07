import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from 'src/@shared/shared.module';

import { PassportRoutingModule } from './passport-routing.module';
import { LoginComponent } from './login/login.component';
import { PassportComponent } from './passport.component';



@NgModule({
  declarations: [
    LoginComponent,
    PassportComponent,
  ],
  imports: [
    CommonModule,
    PassportRoutingModule,
    SharedModule
  ]
})
export class PassportModule { }

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from'@angular/forms';
import { RouterModule } from'@angular/router';
import { SHARED_ZORRO_MODULES } from './shared-zorro-module';
import { SHARED_DELON_MODULES } from './shared-delon-module';



@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    ...SHARED_ZORRO_MODULES,
    ...SHARED_DELON_MODULES
  ],
  exports:[
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    ...SHARED_ZORRO_MODULES,
    ...SHARED_DELON_MODULES
  ]
})
export class SharedModule { }

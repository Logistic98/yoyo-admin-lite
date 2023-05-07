import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomIfDirective } from './custom-if.directive';
import { PermissionDirective } from './permission.directive';


@NgModule({
  declarations: [
    CustomIfDirective,
    PermissionDirective
  ],
  imports: [
    CommonModule
  ],
  exports: [
    CustomIfDirective,
    PermissionDirective

  ]
})
export class directiveModule { }

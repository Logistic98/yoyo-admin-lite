import { Directive, forwardRef, Input } from '@angular/core';
import { Validator, AbstractControl, NG_VALIDATORS, ValidationErrors } from '@angular/forms';
@Directive({
  selector: '[appNoWhitespaceValidator]',
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => NoWhitespaceValidatorDirective),
      multi: true
    }
  ]
})
export class NoWhitespaceValidatorDirective {

  constructor() { }
  validate(control: AbstractControl): ValidationErrors | null {
    // console.log(control)
    if(control.value && control.value.trim()===''){
      //验证全是空格的话返回erros对象
      return { whitespace : true };
    }else{
      return null
    }
   
  }

}

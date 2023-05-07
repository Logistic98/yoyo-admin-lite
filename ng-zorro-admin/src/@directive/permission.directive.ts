import { Directive,Input, TemplateRef, ViewContainerRef } from '@angular/core';
import { FuncsService } from '@service/funcs.service'; 

@Directive({
  selector: '[permission]'
})
export class PermissionDirective {
  @Input() 
  set permission(value:string){
    this.checkPermission(value)
  }
  constructor(
    private funcs:FuncsService,
    private templateRef: TemplateRef<unknown>,
    private vcr: ViewContainerRef
  ) { }

  checkPermission(permission:string){
    this.funcs.checkPermission(permission).subscribe(boolean=>{
      if(boolean){
        this.vcr.createEmbeddedView(this.templateRef);
      }else{
        this.vcr.clear();
      }
    })
    
  }

}

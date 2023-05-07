import { Directive, Input } from '@angular/core';
import { TemplateRef, ViewContainerRef } from '@angular/core';


@Directive({
  selector: '[customIf]'
})
export class CustomIfDirective {
  @Input() set customIf(value: boolean) {
    this._renderTemplate(value)
  }
  constructor( 
    private templateRef: TemplateRef<unknown>,
    private vcr: ViewContainerRef) { }
  
  _renderTemplate(show: boolean) {
    this.vcr.clear();
    if (show) {
      this.vcr.createEmbeddedView(this.templateRef);
    }
  }

}

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { TinymceComponent } from './tinymce/tinymce.component';
import { BackToTopComponent } from './back-to-top/back-to-top.component';
import { AlbumComponent } from './album/album.component';
import { JsonViewComponent } from './json-view/json-view.component';


@NgModule({
  declarations: [TinymceComponent, BackToTopComponent,AlbumComponent, JsonViewComponent],
  imports: [
    CommonModule,
    SharedModule
  ],
  exports:[TinymceComponent,BackToTopComponent,AlbumComponent,JsonViewComponent]
})
export class ComponentsModule { }

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from 'src/@shared/shared.module';
import { IconsProviderModule } from 'src/app/icons-provider.module';
import { LayoutTopSideComponent } from './layout-top-side/layout-top-side.component';
import { MainLayoutDefaultComponent } from './main-layout-default/main-layout-default.component';
import { ScreenfullComponent } from './widgets/screenfull/screenfull.component';
import { UserComponent } from './widgets/user/user.component';
import { TagViewComponent } from './widgets/tag-view/tag-view.component';


@NgModule({
  declarations: [
    MainLayoutDefaultComponent,
    LayoutTopSideComponent,
    ScreenfullComponent,
    UserComponent,
    TagViewComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    IconsProviderModule,
  ],
  exports: [ MainLayoutDefaultComponent,LayoutTopSideComponent ],
})
export class layoutModule { }

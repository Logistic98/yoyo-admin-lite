import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from 'src/@shared/shared.module';
import { MainRoutingModule } from './main-routing.module';
import { layoutModule } from 'src/@layout/layout.module';
import { ComponentsModule } from 'src/@components/components.module';
import { HomeComponent } from './pages/home/home.component';
import { XlsxComponent } from './pages/delon/xlsx/xlsx.component';
import { LineChartComponent } from './pages/echarts/line-chart/line-chart.component';
import { PieChartComponent } from './pages/echarts/pie-chart/pie-chart.component';
import { TableEditComponent } from './pages/table/table-edit/table-edit.component';
import { TinymceDemoComponent } from './pages/components-demo/tinymce-demo/tinymce-demo.component';
import { GoTopComponent } from './pages/components-demo/go-top/go-top.component';
import { ScrollAddMoreComponent } from './pages/scroll-add-more/scroll-add-more.component';
import { TableComplexComponent } from './pages/table/table-complex/table-complex.component';
import { AlbumDemoComponent } from './pages/components-demo/album-demo/album-demo.component';
import { JsonViewComponent } from './pages/components-demo/json-view-demo/json-view-demo.component';
import { BaseMapComponent } from './pages/maps/base-map/base-map.component';
import { directiveModule } from 'src/@directive/directive.module';


@NgModule({
  declarations: [
    HomeComponent,
    XlsxComponent,
    LineChartComponent,
    PieChartComponent,
    TableEditComponent,
    TinymceDemoComponent,
    GoTopComponent,
    ScrollAddMoreComponent,
    TableComplexComponent,
    AlbumDemoComponent,
    JsonViewComponent,
    BaseMapComponent
  ],
  imports: [
    CommonModule,
    MainRoutingModule,
    layoutModule,
    SharedModule,
    ComponentsModule,
    directiveModule
  ],
  providers:[
   
  ]
})
export class MainModule { }

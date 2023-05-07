import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainLayoutDefaultComponent } from '@layout/main-layout-default/main-layout-default.component';
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

const routes: Routes = [
  {
    path: '',
    component: MainLayoutDefaultComponent,
    children: [
      {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
      },
      {
        path: 'home',
        component: HomeComponent,
        data: {
          title: '首页',
        }
      },
      {
        path: 'echarts',
        data: {title: '图表'},
        children: [
          {
            path: '',
            redirectTo: 'line',
            pathMatch: 'full'
          },
          {
            path: 'line',
            component:LineChartComponent,
            data: {title: '折线图'}
          },
          {
            path: 'pie',
            component:PieChartComponent,
            data: {title: '饼图'}
          },
        ]
      },
      {
        path: 'table',
        data: {title: 'table'},
        children:[
          {
            path: '',
            redirectTo: 'table-edit',
            pathMatch: 'full'
          },
          {
            path: 'table-edit',
            component:TableEditComponent,
            data: {title: '表格内编辑'}
          },
          {
            path: 'table-complex',
            component:TableComplexComponent,
            data: {title: '综合表格'}
          }

        ]
      },
      {
        path: 'xlsx',
        component: XlsxComponent,
        data: {title: 'Excel'}
      },
      {
        path: 'components-demo',
        data: {title: '组件'},
        children: [
          {
            path: '',
            redirectTo: 'tinymce',
            pathMatch: 'full'
          },
          {
            path: 'tinymce',
            component: TinymceDemoComponent,
            data: {title: '富文本'}
          },
          {
            path: 'goTop',
            component: GoTopComponent,
            data: {title: '返回顶部'}
          },
          {
            path: 'album',
            component: AlbumDemoComponent,
            data: {title: '相册'}
          },
          {
            path: 'json-view',
            component: JsonViewComponent,
            data: {title: 'josn可视化'}
          }
        ]

      },
      {
        path:'map',
        data:{title:'高德地图'},
        children:[
          {
            path:'baseMap',
            component:BaseMapComponent,
            data:{title:'基础'}
          }
        ]
      },
      {
        path:'other',
        data:{title:'其他'},
        children:[
          {
            path:'',
            redirectTo:'scrollAddMore',
            pathMatch:'full'
          },{
            path:'scrollAddMore',
            component:ScrollAddMoreComponent,
            data:{title:'滚动加载更多'}
          }
        ]
      },
      {
        path: 'system-manage', // 系统管理
        loadChildren: () =>
          import('./pages/system-manage/system-manage.module').then((m) => m.SystemManageModule),
      },

    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MainRoutingModule { }

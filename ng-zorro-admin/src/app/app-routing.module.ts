import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuard } from '@service/auth.guard';
const routes: Routes = [
  { 
    path: 'web', 
    loadChildren: () => import('./main/main.module').then(m => m.MainModule),
    data: {
      title: '',
    },
    canActivate:[AuthGuard]
  },
  { path: 'passport',
   loadChildren: () => import('./passport/passport.module').then(m => m.PassportModule),
   canActivate:[AuthGuard]
  },
  {
    path: '',
    redirectTo: 'web',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

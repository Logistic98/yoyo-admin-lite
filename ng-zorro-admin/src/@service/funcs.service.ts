import { Injectable } from '@angular/core';
import { AuthGuard } from '@service/auth.guard';
import { Observable, of,map,catchError, share, take, last } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FuncsService {
  constructor(private guard:AuthGuard) { }

  checkPermission (permission:string) :Observable<boolean> {
    if(!permission) return of(false);
    return this.guard.authObservable.pipe(
      share(),
      map( (res:any)=>{
        const _permissions = res.button.map((btn:any)=> btn.permission)
         //console.log(_permissions.includes(permission))
        return  _permissions.includes(permission)
      }),

      catchError(() => {
        return of(false);
      })
    )
  }
}

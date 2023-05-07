import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@service/auth.service';
import { AuthGuard } from '@service/auth.guard';
@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.less']
})
export class UserComponent implements OnInit {
  currentUser: any;
  constructor(private router: Router, private service: AuthService, private guard:AuthGuard) { }

  ngOnInit(): void {
    this.guard.authObservable.subscribe((res:any)=>{
      this.currentUser = res?.name
    })
  }

  logout(){
    this.router.navigateByUrl('/passport/login');
  }

}

import { Injectable, Injector } from '@angular/core';
import { Router } from '@angular/router';
import {
    HttpInterceptor,
    HttpRequest,
    HttpHandler,
    HttpErrorResponse,
    HttpSentEvent,
    HttpHeaderResponse,
    HttpProgressEvent,
    HttpResponse,
    HttpUserEvent
} from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { mergeMap, catchError, map, tap } from 'rxjs/operators';
import { NzMessageService } from 'ng-zorro-antd/message';
import { environment } from 'src/environments/environment';


/**
 * 默认HTTP拦截器，其注册细节见 `app.module.ts`
 */
@Injectable()
export class DefaultInterceptor implements HttpInterceptor {
    constructor(private injector: Injector) {}

    get msg(): NzMessageService {
        return this.injector.get(NzMessageService);
    }

   // tslint:disable-next-line:typedef
   goTo(url: string) {
        setTimeout(() => this.injector.get(Router).navigateByUrl(url));
    }

    private handleData(event: HttpResponse<any> | HttpErrorResponse): any {
        // 可能会因为 `throw` 导出无法执行 `_HttpClient` 的 `end()` 操作
        // 业务处理：一些通用操作

        switch (event.status) {
            case 200:
                break;
            // case 400: //用户名密码错误
            // console.log(400)
            //     break;
            case 401: // 未登录状态码
                //console.log(401)
                break;
            case 403:
                //console.log(403)
               this.goTo('/passport/login');
               break;
            case 404:
                //console.log(404)
                // debugger
                break;
            case 500:
                //console.log(500)
                // this.goTo(`/${event.status}`);
                break;
            default:
                //console.log('未知错误')
                break;
        }

        return event;
    }

    intercept(
        req: HttpRequest<any>,
        next: HttpHandler
    ): Observable<
        | HttpSentEvent
        | HttpHeaderResponse
        | HttpProgressEvent
        | HttpResponse<any>
        | HttpUserEvent<any>
    > {
        // 统一加上服务端前缀
        //const url = req.url;
        // if (!url.startsWith('https://') && !url.startsWith('http://')) {
        //   url = environment.SERVER_URL + url;
        // }
        // const newReq = req.clone({
        //     url
        // });

        return next.handle(req).pipe(
            mergeMap((event: any) => {
                //console.log('no-err')
                // 只是为了捕获200状态码
                if (event instanceof HttpResponse && event.status===200) {
                    return of(this.handleData(event));
                }
                // 若一切都正常，则后续操作
                return of(event);
            }),
            catchError((err: HttpErrorResponse) => {
                //console.log('error')
                return throwError(this.handleData(err));
            })
        );
    }
}

import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree} from '@angular/router';
import {map, Observable} from 'rxjs';
import {OidcSecurityService} from "angular-auth-oidc-client";

@Injectable({
  providedIn: 'root'
})
export class NameGraphqlGuard implements CanActivate {

  constructor(private oidcSecurityService: OidcSecurityService) {
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.oidcSecurityService.checkAuth()
    .pipe(
      map(a => {
        console.log("canActivate[NameGraphqlGuard] => isAuthenticated", a.isAuthenticated)
        return a.isAuthenticated
      })
    )
  }

}

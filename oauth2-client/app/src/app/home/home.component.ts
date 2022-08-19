import {Component, OnInit} from '@angular/core';
import {EMPTY, Observable} from "rxjs";
import {
  OidcClientNotification,
  OidcSecurityService,
  OpenIdConfiguration,
  UserDataResult
} from "angular-auth-oidc-client";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  configuration$: Observable<OpenIdConfiguration> = EMPTY;
  userDataChanged$: Observable<OidcClientNotification<any>> = EMPTY;
  userData$: Observable<UserDataResult> = EMPTY;
  isAuthenticated = false;

  constructor(public oidcSecurityService: OidcSecurityService) {
  }

  ngOnInit() {
    this.configuration$ = this.oidcSecurityService.getConfiguration();
    this.userData$ = this.oidcSecurityService.userData$;

    this.oidcSecurityService.isAuthenticated$.subscribe(({isAuthenticated}) => {
      this.isAuthenticated = isAuthenticated;

      console.warn('authenticated: ', isAuthenticated);
    });
  }

  login() {
    this.oidcSecurityService.authorize();
  }

  refreshSession() {
    this.oidcSecurityService.forceRefreshSession().subscribe((result) => console.log(result));
  }

  logout() {
    this.oidcSecurityService.logoff();
  }

  logoffAndRevokeTokens() {
    this.oidcSecurityService.logoffAndRevokeTokens().subscribe((result) => console.log(result));
  }

  revokeRefreshToken() {
    this.oidcSecurityService.revokeRefreshToken().subscribe((result) => console.log(result));
  }

  revokeAccessToken() {
    this.oidcSecurityService.revokeAccessToken().subscribe((result) => console.log(result));
  }

}

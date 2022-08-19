import {Component, OnInit} from '@angular/core';
import {OidcSecurityService} from "angular-auth-oidc-client";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'app';

  constructor(public oidcSecurityService: OidcSecurityService) {
  }

  ngOnInit() {
    this.oidcSecurityService.checkAuth().subscribe(({
                                                      isAuthenticated,
                                                      userData,
                                                      accessToken,
                                                      idToken
                                                    }) => {
      console.log("isAuthenticated", isAuthenticated)
      console.log("userData", userData)
      console.log("accessToken", accessToken)
      console.log("idToken", idToken)
      /*...*/
    });
  }

}

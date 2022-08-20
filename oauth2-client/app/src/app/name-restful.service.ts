import {Injectable} from '@angular/core';
import {Name} from "./name";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {map, mergeMap, Observable} from "rxjs";
import {OidcSecurityService} from "angular-auth-oidc-client";

@Injectable({
  providedIn: 'root'
})
export class NameRestfulService {

  url = 'http://localhost:8080/name'

  constructor(
    private http: HttpClient,
    private oidcSecurityService: OidcSecurityService
  ) {
  }

  getNames(): Observable<Name[]> {
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

    return this.oidcSecurityService.getIdToken()
    .pipe(
      map((token) =>
        new HttpHeaders({
          Authorization: 'Bearer ' + token
        })
      )
    )
    .pipe(
      mergeMap(
        headers => this.http.get<Name[]>(
          this.url, {headers: headers})
      )
    )
      ;
  }
}

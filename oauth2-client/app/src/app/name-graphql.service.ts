import {Injectable} from '@angular/core';
import {Apollo, ApolloBase} from "apollo-angular";
import {map, Observable} from "rxjs";
import {OidcSecurityService} from "angular-auth-oidc-client";
import {NamesGQL, NamesQuery} from "../generated/graphql";

@Injectable({
  providedIn: 'root'
})
export class NameGraphqlService {

  private apollo: ApolloBase;

  constructor(
    private apolloProvider: Apollo,
    private namesGQL: NamesGQL,
    private oidcSecurityService: OidcSecurityService
  ) {
    this.apollo = this.apolloProvider
  }

  getData(): Observable<NamesQuery['names']> {
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

    this.oidcSecurityService.getIdToken()
    .subscribe(token => {
      localStorage.setItem('token', token);
      console.log("localStorage[token].set", token)
    });

    return this.namesGQL.watch()
    .valueChanges.pipe(map(result => result.data.names))
      ;
  }
}

import {Injectable} from '@angular/core';
import {Apollo, ApolloBase, gql} from "apollo-angular";
import {Observable} from "rxjs";
import {Name} from "./name";
import type {ApolloQueryResult} from "@apollo/client/core";
import {OidcSecurityService} from "angular-auth-oidc-client";

const NAME_QUERY = gql`
  {
    names {
      id
      firstName
      lastName
    }
  }
`;

@Injectable({
  providedIn: 'root'
})
export class NameGraphqlService {

  private apollo: ApolloBase;

  constructor(
    private apolloProvider: Apollo,
    private oidcSecurityService: OidcSecurityService
  ) {
    this.apollo = this.apolloProvider
  }

  getData(): Observable<ApolloQueryResult<NameQueryResult>> {
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

    return this.apollo
    .watchQuery<NameQueryResult>({query: NAME_QUERY})
      .valueChanges
      ;
  }
}

export interface NameQueryResult {
  names: Name[]
}

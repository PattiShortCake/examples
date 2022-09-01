import {NgModule} from '@angular/core';
import {APOLLO_OPTIONS, ApolloModule} from 'apollo-angular';
import {ApolloClientOptions, ApolloLink, InMemoryCache} from '@apollo/client/core';
import {HttpLink} from 'apollo-angular/http';
import {setContext} from "@apollo/client/link/context";
import {OidcSecurityService} from "angular-auth-oidc-client";

const uri = 'http://localhost:4200/graphql'; // <-- add the URL of the GraphQL server here
export function createApollo(
  httpLink: HttpLink,
  oidcSecurityService: OidcSecurityService
): ApolloClientOptions<any> {

  const basic = setContext((operation, context) => ({
    headers: {
      Accept: 'charset=utf-8'
    }
  }));

  const auth = setContext((operation, context) => {
    const token = localStorage.getItem('token');

    console.log("localStorage[token].get", token)
    if (token === null) {
      console.log("No headers")
      return {};
    } else {
      return {
        headers: {
          Authorization: `Bearer ${token}`
        }
      };
    }
  });

  const link = ApolloLink.from([basic, auth, httpLink.create({uri})]);
  const cache = new InMemoryCache();

  return {
    link: link,
    cache: cache,
  };
}

@NgModule({
  exports: [ApolloModule],
  providers: [
    {
      provide: APOLLO_OPTIONS,
      useFactory: createApollo,
      deps: [HttpLink, OidcSecurityService],
    },
  ],
})
export class GraphQLModule {
}

import {Injectable} from '@angular/core';
import {Apollo, ApolloBase, gql} from "apollo-angular";
import {Observable} from "rxjs";
import {Name} from "./name";
import type {ApolloQueryResult} from "@apollo/client/core";

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

  constructor(private apolloProvider: Apollo) {
    this.apollo = this.apolloProvider
  }

  getData(): Observable<ApolloQueryResult<NameQueryResult>> {
    return this.apollo
    .watchQuery<NameQueryResult>({query: NAME_QUERY})
      .valueChanges
      ;
  }
}

export interface NameQueryResult {
  names: Name[]
}

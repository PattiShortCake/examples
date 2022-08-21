import * as Apollo from 'apollo-angular';
import {gql} from 'apollo-angular';
import {Injectable} from '@angular/core';

export type Maybe<T> = T | null;
export type InputMaybe<T> = Maybe<T>;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
export type MakeOptional<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]?: Maybe<T[SubKey]> };
export type MakeMaybe<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]: Maybe<T[SubKey]> };
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: string;
  String: string;
  Boolean: boolean;
  Int: number;
  Float: number;
};

export type Name = {
  __typename?: 'Name';
  firstName?: Maybe<Scalars['String']>;
  id: Scalars['ID'];
  lastName?: Maybe<Scalars['String']>;
};

export type Query = {
  __typename?: 'Query';
  names: Array<Name>;
};

export type NamesQueryVariables = Exact<{ [key: string]: never; }>;


export type NamesQuery = { __typename?: 'Query', names: Array<{ __typename?: 'Name', id: string, firstName?: string | null, lastName?: string | null }> };

export const NamesDocument = gql`
  query Names {
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
export class NamesGQL extends Apollo.Query<NamesQuery, NamesQueryVariables> {
  override document = NamesDocument;

  constructor(apollo: Apollo.Apollo) {
    super(apollo);
  }
}

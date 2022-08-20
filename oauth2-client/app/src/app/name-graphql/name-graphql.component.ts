import {Component, OnInit} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {Name} from "../name";
import {OidcSecurityService} from "angular-auth-oidc-client";
import {NameGraphqlService} from "../name-graphql.service";

@Component({
  selector: 'app-name-graphql',
  templateUrl: './name-graphql.component.html',
  styleUrls: ['./name-graphql.component.css']
})
export class NameGraphqlComponent implements OnInit {

  displayedColumns: string[] = ['id', 'first-name', 'last-name'];
  names: MatTableDataSource<Name>;

  constructor(
    private nameGraphqlService: NameGraphqlService,
    private oidcSecurityService: OidcSecurityService
  ) {
    this.names = new MatTableDataSource<Name>()
  }

  ngOnInit(): void {
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

    this.nameGraphqlService.getData().subscribe(result => {
        let initialData = result.data;
        console.log("namesQueryResult", initialData)
        let nameResults = initialData.names;
        console.log("names", nameResults)
        this.names = new MatTableDataSource<Name>(nameResults)
      }
    );
  }

}

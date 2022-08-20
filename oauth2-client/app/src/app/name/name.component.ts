import {Component, OnInit} from '@angular/core';
import {NameRestfulService} from "../name-restful.service";
import {Name} from "../name";
import {MatTableDataSource} from "@angular/material/table";
import {OidcSecurityService} from "angular-auth-oidc-client";

@Component({
  selector: 'app-name',
  templateUrl: './name.component.html',
  styleUrls: ['./name.component.css']
})
export class NameComponent implements OnInit {

  displayedColumns: string[] = ['id', 'first-name', 'last-name'];
  names: MatTableDataSource<Name>;

  constructor(
    private nameRestfulService: NameRestfulService,
    public oidcSecurityService: OidcSecurityService
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

    this.nameRestfulService.getNames().subscribe(response => {
      this.names = new MatTableDataSource<Name>(response);
    })
  }

}

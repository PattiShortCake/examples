import {Component, OnInit} from '@angular/core';
import {NameRestfulService} from "../name-restful.service";
import {Name} from "../name";
import {MatTableDataSource} from "@angular/material/table";
import {OidcSecurityService} from "angular-auth-oidc-client";
import {ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree} from "@angular/router";
import {map, Observable} from "rxjs";

@Component({
  selector: 'app-name',
  templateUrl: './name.component.html',
  styleUrls: ['./name.component.css']
})
export class NameComponent implements OnInit, CanActivate {

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

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.oidcSecurityService.checkAuth()
    .pipe(
      map(a => {
        console.log("canActivate => isAuthenticated", a.isAuthenticated)
        return a.isAuthenticated
      })
    )
  }

}

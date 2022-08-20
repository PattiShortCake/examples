import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NameComponent} from './name/name.component';
import {MatIconModule} from "@angular/material/icon";
import {MatTableModule} from "@angular/material/table";
import {HttpClientModule} from "@angular/common/http";
import {AuthConfigModule} from './auth/auth-config.module';
import {HomeComponent} from './home/home.component';
import {UnauthorizedComponent} from './unauthorized/unauthorized.component';
import {GraphQLModule} from './graphql.module';
import {NameGraphqlComponent} from './name-graphql/name-graphql.component';

@NgModule({
  declarations: [
    AppComponent,
    NameComponent,
    HomeComponent,
    UnauthorizedComponent,
    NameGraphqlComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MatIconModule,
    MatTableModule,
    AuthConfigModule,
    GraphQLModule,
  ],
  providers: [NameComponent],
  bootstrap: [AppComponent]
})
export class AppModule { }

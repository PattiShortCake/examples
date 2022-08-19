import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {NameComponent} from "./name/name.component";
import {HomeComponent} from "./home/home.component";
import {UnauthorizedComponent} from "./unauthorized/unauthorized.component";

const routes: Routes = [
  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {path: 'home', component: HomeComponent},
  {path: 'forbidden', component: UnauthorizedComponent},
  {path: 'unauthorized', component: UnauthorizedComponent},
  {path: 'name', component: NameComponent, canActivate: [NameComponent]}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

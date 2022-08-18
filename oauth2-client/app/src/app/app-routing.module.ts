import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {NameComponent} from "./name/name.component";

const routes: Routes = [
  {
    path: 'name', component: NameComponent
  }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

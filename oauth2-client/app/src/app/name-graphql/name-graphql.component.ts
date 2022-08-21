import {Component, OnInit} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {NameGraphqlService} from "../name-graphql.service";
import {Name} from "../../generated/graphql";

@Component({
  selector: 'app-name-graphql',
  templateUrl: './name-graphql.component.html',
  styleUrls: ['./name-graphql.component.css']
})
export class NameGraphqlComponent implements OnInit {

  displayedColumns: string[] = ['id', 'first-name', 'last-name'];
  names: MatTableDataSource<Name>;

  constructor(
    private nameGraphqlService: NameGraphqlService
  ) {
    this.names = new MatTableDataSource<Name>()
  }

  ngOnInit(): void {
    this.nameGraphqlService.getData().subscribe(result => {
      this.names = new MatTableDataSource<Name>(result)
      }
    );
  }

}

import {Component, OnInit} from '@angular/core';
import {NameRestfulService} from "../name-restful.service";
import {Name} from "../name";
import {MatTableDataSource} from "@angular/material/table";

@Component({
  selector: 'app-name',
  templateUrl: './name.component.html',
  styleUrls: ['./name.component.css']
})
export class NameComponent implements OnInit {

  displayedColumns: string[] = ['id', 'first-name', 'last-name'];
  names: MatTableDataSource<Name>;

  constructor(private nameRestfulService: NameRestfulService) {
    this.names = new MatTableDataSource<Name>()
  }

  ngOnInit(): void {
    this.nameRestfulService.getNames().subscribe(response => {
      this.names = new MatTableDataSource<Name>(response);
    })
  }

}

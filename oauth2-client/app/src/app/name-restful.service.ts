import {Injectable} from '@angular/core';
import {Name} from "./name";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class NameRestfulService {

  url = 'http://localhost:8080/name'

  constructor(private http: HttpClient) {
  }

  getNames(): Observable<Name[]> {
    return this.http.get<Name[]>(this.url);
  }
}

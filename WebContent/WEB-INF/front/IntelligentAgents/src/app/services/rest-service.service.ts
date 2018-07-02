import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';

import { Observable } from 'rxjs';

import { Agent } from '../shared/model/agent';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class RestServiceService {
  private SERVER_URL = 'http://localhost:8082/Inteligent_Agents/rest/app';

  constructor(private http: HttpClient) {}

  getRunningAgents () {
    return this.http.get<Agent[]>(this.SERVER_URL + '/agents/running');
  }
}

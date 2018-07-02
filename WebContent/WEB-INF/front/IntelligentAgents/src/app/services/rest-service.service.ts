import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';

import { Observable } from 'rxjs';

import { AgentTypeDTO } from '../shared/model/agent-type-dto';
import { Aid } from '../shared/model/aid';

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

  getAllAgentTypes () {
    return this.http.get<AgentTypeDTO[]>(this.SERVER_URL + '/agents/classes');
  }

  getRunningAgents () {
    return this.http.get<Aid[]>(this.SERVER_URL + '/agents/running');
  }
}

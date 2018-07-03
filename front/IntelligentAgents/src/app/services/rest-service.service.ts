import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';

import { Observable } from 'rxjs';

import { AgentTypeDTO } from '../model/agent-type-dto';
import { Aid } from '../model/aid';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class RestServiceService {
  private SERVER_URL = 'rest/';

  constructor(private http: HttpClient) {}

  getAllAgentTypes () {
    return this.http.get<AgentTypeDTO[]>(this.SERVER_URL + 'handshake/agents/classes');
  }

  getRunningAgents () {
    return this.http.get<Aid[]>(this.SERVER_URL + 'app/agents/running');
  }

  startAgent(aid: Aid, agentName: string) {
    return this.http.put(this.SERVER_URL + 'app/agents/running/' + aid.name + '/' + agentName, null);
  }
}

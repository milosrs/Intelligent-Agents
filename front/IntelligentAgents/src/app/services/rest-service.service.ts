import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';

import { Observable } from 'rxjs';

import { AgentTypeDTO } from '../model/agent-type-dto';
import { Aid } from '../model/aid';
import { AclMessage } from '../model/acl-message';

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

  startAgent(agent: AgentTypeDTO, agentName: string) {
    const url = "http://" + agent.hostAddress + "/Inteligent_Agents/"+ this.SERVER_URL + 'app/agents/running/' + agent.name + '/' + agentName;
    return this.http.put(url , null);
  }

  sendAclMessage(aclMessage : AclMessage){
    return this.http.post(this.SERVER_URL + 'app/messages', aclMessage);
  }
  
  getPerformatives(){
    return this.http.get(this.SERVER_URL + 'app/messages');
  }

  deleteRunningAgent (aid: Aid) {
    return this.http.delete(this.SERVER_URL + 'app/agents/running/' + aid.name + '__' + aid.host.hostAddress + '__'
    + aid.host.alias + '__' + aid.type.name, httpOptions);
  }
}

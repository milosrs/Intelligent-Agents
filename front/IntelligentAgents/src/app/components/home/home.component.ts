import { Component, OnInit, ViewChild } from '@angular/core';
import { ListItem } from '../../shared/model/list-item';
import { HelperFunctions } from '../../shared/util/helper-functions';
import { HttpClient } from '@angular/common/http';
import { AgentTypeDTO } from '../../model/agent-type-dto';
import { Aid } from '../../model/aid';
import { RestServiceService } from '../../services/rest-service.service';
import { ToolboxComponent } from '../toolbox/toolbox.component';
import { SocketService } from '../../services/socket.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  private agentTypes: AgentTypeDTO[];
  private runningAgents: Aid[] = [];
  @ViewChild(ToolboxComponent) toolboxComp: ToolboxComponent;

  constructor(private http: HttpClient, private restService: RestServiceService, private socketService : SocketService) { }

  ngOnInit() {
    this.getAgentTypes();
    this.getRunningAgents();
    this.startSocket();
  }

  getAgentTypes(): void {
    this.restService.getAllAgentTypes()
      .subscribe(agentTypes => {console.log(agentTypes); this.agentTypes = agentTypes; });
  }

  getRunningAgents(): void {
    this.restService.getRunningAgents()
      .subscribe(runningAgents => {console.log(runningAgents); this.runningAgents = runningAgents; });
  }

  allAgentsListItem() {
    return HelperFunctions.createListItems(this.agentTypes, null, ['name', 'hostAddress']);
  }

  runningAgentsToList() {
    return HelperFunctions.createListItems(this.runningAgents, null, ['name']);
  }

  selectRunningAgent(agent: any) {
    this.toolboxComp.setSelection(agent, true);
  }

  selectNewAgent(agent: any) {
    this.toolboxComp.setSelection(agent, false);
  }

  startAgentEvent(agent: Aid) {
    this.runningAgents.push(agent);
  }

  stopAgentEvent(agent : Aid){
    var index = this.containsAid(agent);
    if(index!=-1){
      this.runningAgents.splice(index,1);
    }
  }

  startSocket(){
    this.socketService.initSocket();

    this.socketService.getSocket().onmessage = (event) => { 
      var resp = JSON.parse(event.data);
      if(resp.messageType==="startAgent"){
        this.startAgentEvent(JSON.parse(resp.content));
      }else if(resp.messageType==="stopAgent"){
        this.stopAgentEvent(JSON.parse(resp.content));
      }
    }
  }

  containsAid(aid:Aid):number{
    var index = 0;
    for(let listAid of this.runningAgents){
      if(listAid.name===aid.name && listAid.type.name===aid.type.name && listAid.type.module===aid.type.module && listAid.host.hostAddress===aid.host.hostAddress && listAid.host.alias===aid.host.alias){
        return index;
      }
      index++;
    }
    return -1;
  }

}

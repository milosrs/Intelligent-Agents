import { Component, OnInit, ViewChild } from '@angular/core';
import { ListItem } from '../../shared/model/list-item';
import { HelperFunctions } from '../../shared/util/helper-functions';
import { HttpClient } from '@angular/common/http';
import { AgentTypeDTO } from '../../model/agent-type-dto';
import { Aid } from '../../model/aid';
import { RestServiceService } from '../../services/rest-service.service';
import { ToolboxComponent } from '../toolbox/toolbox.component';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  private agentTypes: AgentTypeDTO[];
  private runningAgents: Aid[];
  @ViewChild(ToolboxComponent) toolboxComp: ToolboxComponent;

  constructor(private http: HttpClient, private restService: RestServiceService) { }

  ngOnInit() {
    this.getAgentTypes();
    this.getRunningAgents();
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
}

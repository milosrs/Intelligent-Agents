import { Component, OnInit } from '@angular/core';
import { ListItem } from '../../model/list-item';
import { HelperFunctions } from '../../util/helper-functions';
import { HttpClient } from '@angular/common/http';
import { AgentTypeDTO } from '../../model/agent-type-dto';
import { Aid } from '../../model/aid';
import { RestServiceService } from '../../../services/rest-service.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  // private mockItems: ListItem[];

  // private dummyObj =  {'name' : 'Djes bracooo!'};

  private agentTypes: AgentTypeDTO[];

  private runningAgents: Aid[];

  constructor(private http: HttpClient, private restService: RestServiceService) { }

  ngOnInit() {
    // this.mockItems = HelperFunctions.createDummyTest(this.dummyObj);
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
}

import { Component, OnInit } from '@angular/core';
import { ListItem } from '../../model/list-item';
import { HelperFunctions } from '../../util/helper-functions';
import { HttpClient } from '@angular/common/http';
import { Agent } from '../../model/agent';
import { RestServiceService } from '../../../services/rest-service.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  private mockItems: ListItem[];

  private runningAgents: Agent[];

  private dummyObj =  {'name' : 'Djes bracooo!'};

  constructor(private http: HttpClient, private restService: RestServiceService) { }

  ngOnInit() {
    this.mockItems = HelperFunctions.createDummyTest(this.dummyObj);
    this.getRunningAgents();
  }

  getRunningAgents(): void {
    this.restService.getRunningAgents()
      .subscribe(runningAgents => {console.log(runningAgents); this.runningAgents = runningAgents; });
  }
}

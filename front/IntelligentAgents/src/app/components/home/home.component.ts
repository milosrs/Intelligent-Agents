import { Component, OnInit } from '@angular/core';
import { ListItem } from '../../shared/model/list-item';
import { HelperFunctions } from '../../shared/util/helper-functions';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  private SERVER_URL = 'http://localhost:8082/Inteligent_Agents/rest/app';

  private mockItems: ListItem[];

  private runningAgents: ListItem[];

  private dummyObj =  {'name' : 'Djes bracooo!'};

  constructor(private http: HttpClient) { }

  ngOnInit() {
    this.mockItems = HelperFunctions.createDummyTest(this.dummyObj);

    this.http.get(this.SERVER_URL + '/agents/running');

  }


}

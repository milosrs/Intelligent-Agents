import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RestServiceService } from '../../services/rest-service.service';
import { HelperFunctions } from '../../shared/util/helper-functions';


@Component({
  selector: 'app-toolbox',
  templateUrl: './toolbox.component.html',
  styleUrls: ['./toolbox.component.css']
})
export class ToolboxComponent implements OnInit {

  private isRunningAgent: boolean;
  private selectedObject: any;
  private agentName: string;
  @Output() onStartAgentEvent: EventEmitter<any> = new EventEmitter();
  @Output() onDeleteRunningAgentEvent: EventEmitter<any> = new EventEmitter();

  constructor(protected service: RestServiceService) { }

  ngOnInit() {
    this.isRunningAgent = false;
  }

  setSelection(selection: any, isRunningAgent: boolean) {
    this.selectedObject = selection;
    this.isRunningAgent = isRunningAgent;
  }

  startAgentEvent(event) {
    this.service.startAgent(this.selectedObject, this.agentName)
        .subscribe(resp => {
          console.log(resp);
          if (!HelperFunctions.isEmptyValue(resp)) {
            this.onStartAgentEvent.emit(resp);
            this.agentName = undefined;
          } else {
            alert('Vracen null iz nekog razloga.');
          }
        });
  }

  deleteRunningAgentEvent(event) {
    this.service.deleteRunningAgent(this.selectedObject)
    .subscribe(resp => {
      console.log(resp);
      if (!HelperFunctions.isEmptyValue(resp)) {
        this.onDeleteRunningAgentEvent.emit(resp);
      } else {
        alert('Vracen null iz nekog razloga.');
      }
    });
  }
}

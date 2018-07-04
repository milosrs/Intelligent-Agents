import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
import { RestServiceService } from '../../services/rest-service.service';
import { HelperFunctions } from '../../shared/util/helper-functions';
import { AclMessage } from '../../model/acl-message';
import { Aid } from '../../model/aid';


@Component({
  selector: 'app-toolbox',
  templateUrl: './toolbox.component.html',
  styleUrls: ['./toolbox.component.css']
})
export class ToolboxComponent implements OnInit {

  @Input() runningAgents;
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
    // this.aclMessage = new AclMessage();
    // this.aclMessage.sender = this.selectedObject;
  }

  startAgentEvent(event) {
    this.service.startAgent(this.selectedObject, this.agentName)
        .subscribe(resp => {
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

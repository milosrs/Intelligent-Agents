import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
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
  private aclMessage;
  private expanded : boolean = false;
  @Output() onStartAgentEvent: EventEmitter<any> = new EventEmitter();
  @Output() onDeleteRunningAgentEvent: EventEmitter<any> = new EventEmitter();

  constructor(protected service: RestServiceService) { }

  ngOnInit() {
    this.isRunningAgent = false;
  }

  setSelection(selection: any, isRunningAgent: boolean) {
    this.selectedObject = selection;
    this.isRunningAgent = isRunningAgent;
    this.aclMessage = new AclMessage();
    this.aclMessage.sender = this.selectedObject;
  }

  startAgentEvent(event) {
    this.service.startAgent(this.selectedObject, this.agentName)
        .subscribe(resp => {
        });
  }

  showCheckboxes() {
  var checkboxes = document.getElementById("checkboxes");
    if (!this.expanded) {
      checkboxes.style.display = "block";
      this.expanded = true;
    } else {
      checkboxes.style.display = "none";
      this.expanded = false;
    }
}

bindReceiver(aid:Aid){
  var index = this.containsAid(aid);
  if(index==-1){
    this.aclMessage.receivers.push(aid);
  }else{
    this.aclMessage.receivers.splice(index,1);
  }
}

containsAid(aid:Aid):number{
  var index = 0;
  for(let listAid of this.aclMessage.receivers){
    if(listAid.name===aid.name && listAid.type.name===aid.type.name && listAid.type.module===aid.type.module && listAid.host.hostAddress===aid.host.hostAddress && listAid.host.alias===aid.host.alias){
      return index;
    }
    index++;
  }
  return -1;
}

  sendMessage(){
    console.log(this.aclMessage);
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

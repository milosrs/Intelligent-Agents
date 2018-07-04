import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
import { RestServiceService } from '../../services/rest-service.service';
import { AclMessage } from '../../model/acl-message';
import { Aid } from '../../model/aid';

@Component({
  selector: 'app-messaging',
  templateUrl: './messaging.component.html',
  styleUrls: ['./messaging.component.css']
})
export class MessagingComponent implements OnInit {

  @Input() runningAgents;
  private aclMessage;
  private expanded = false;

  constructor(protected service: RestServiceService) { }

  ngOnInit() {
    this.aclMessage = new AclMessage();
  }

  showCheckboxes() {
    const checkboxes = document.getElementById('checkboxes');
      if (!this.expanded) {
        checkboxes.style.display = 'block';
        this.expanded = true;
      } else {
        checkboxes.style.display = 'none';
        this.expanded = false;
      }
  }

  bindReceiver(aid: Aid) {
    const index = this.containsAid(aid);
    if (index === -1) {
      this.aclMessage.receivers.push(aid);
    } else {
      this.aclMessage.receivers.splice(index, 1);
    }
  }

  containsAid(aid: Aid): number {
    let index = 0;
    for (const listAid of this.aclMessage.receivers) {
      if (listAid.name === aid.name && listAid.type.name === aid.type.name
        && listAid.type.module === aid.type.module
        && listAid.host.hostAddress === aid.host.hostAddress
        && listAid.host.alias === aid.host.alias) {
        return index;
      }
      index++;
    }
    return -1;
  }

  sendMessage() {
    console.log(this.aclMessage);
  }
}

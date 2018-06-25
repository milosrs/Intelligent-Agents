import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild, ViewChildren, QueryList, NgModule} from '@angular/core';
import {HelperFunctions} from '../../util/helper-functions';
import {Constants} from '../../constants/constants';
import {ListItem} from '../../model/list-item';
import { RequestComponent } from '../request/request.component';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class ListComponent implements OnInit {

  private listType = Constants.ListType;
  private requestType = Constants.RequestType;
  
  @Input() public header: string;
  @Input() public items: any;
  @Input() public type: string;
  @Input() public selectedRequestType: string;
  @Input() public dynamicStyle: string;
  @Input() public additionalPassParam: string;

  @Output() onElementClickEvent: EventEmitter<any> = new EventEmitter();
  @Output() acceptClickEvent: EventEmitter<any> = new EventEmitter<any>();
  @Output() declineClickEvent: EventEmitter<any> = new EventEmitter<any>();
  @Output() addRemoveClickEvent: EventEmitter<any> = new EventEmitter<any>();
  
  @ViewChildren(RequestComponent) reqComps: QueryList<RequestComponent>;

  constructor() { }

  ngOnInit() { }

  elementClicked(item: ListItem) {
    if(!HelperFunctions.containsEmptyValues(item.relatedItem)){
      if(!HelperFunctions.containsEmptyValues(this.additionalPassParam)){
        this.onElementClickEvent.emit({item: item.relatedItem, additional: this.additionalPassParam});
      } else {
        this.onElementClickEvent.emit(item.relatedItem);
      }
    } else {
      if(!HelperFunctions.containsEmptyValues(this.additionalPassParam)){
        this.onElementClickEvent.emit({item: item, additional: this.additionalPassParam});
      } else {
        this.onElementClickEvent.emit(item);
      }
    }
  }

  accept(object) {
    this.acceptClickEvent.emit(object);
  }

  decline(object) {
    this.declineClickEvent.emit(object);
  }

  addRemove(object) {
    this.addRemoveClickEvent.emit(object);
  }

  resetChildren() {
    this.reqComps.forEach(c =>{
      console.log(c);
      c.resetButtons();
    } );
  }
}

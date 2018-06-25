import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { AbstractService } from '../../../services/abstract.service';
import { ListComponent } from '../list/list.component';
import { HelperFunctions } from '../../util/helper-functions';
import $ from 'jquery'
import { CrudInterfaceObject } from '../../model/crudInterfaceObject';
declare var $: $

@Component({
  selector: 'CRUD',
  templateUrl: './crud-interface.component.html',
  styleUrls: ['./crud-interface.component.css']
})
export class CrudInterfaceComponent implements OnInit {

  @Input() public header: string;
  @Input() public CRUD: string;
  @Input() public items: any[];
  @Input() public displayNames: string[];
  @Input() public keysToEdit: string[];

  @Output() public onCreateClickEvent: EventEmitter<any> = new EventEmitter<any>();
  @Output() public onUpdateClickEvent: EventEmitter<any> = new EventEmitter<any>();
  @Output() public onDeleteClickEvent: EventEmitter<any> = new EventEmitter<any>(); 
  
  private activeItem: any;
  private createdItems: any[];
  private listitems;
  private entityName;
  private typesList;
  private keyList;
  private activeOperation;
  private objectToSend = {};

  constructor() {}

  ngOnInit() {
    if(!HelperFunctions.containsEmptyValues(this.items) && !HelperFunctions.isEmptyValue(this.items)) {
      this.listitems = HelperFunctions.createListItems(this.items, null, ['name']);
      this.typesList = HelperFunctions.getObjectTypesList(this.items[0]);
      this.keyList = Object.keys(this.items[0]);
    } else {
      this.items = [];
      this.keyList = this.keysToEdit;
    }

    for(let i = 0; i < this.keyList.length; i++) {
      if(this.keysToEdit.indexOf(this.keyList[i]) > -1) {
        this.objectToSend[this.keyList[i]] = null;
      } else {
        this.keyList.splice(i, 1);
      }
    }
  }

  elclick(item) {
    this.activeOperation = item['additional'];
    this.activeItem = item['item'];
  
    if(this.activeOperation == 'Update') {
      $('#exampleModal').modal('show');
    } else if(this.activeOperation == 'Delete') {
      this.onDeleteClickEvent.emit(new CrudInterfaceObject(this.objectToSend, this.activeItem, null));
    }
  }

  getListStyle() {
    return {
      "max-height" : "154px"
    };
  }

  getObjectKeyType(key: string) {
    if(!HelperFunctions.isEmptyValue(this.items)){
      return typeof this.items[0][key];
    } else {
      return 'string';
    }
  }

  onOperationClick(operation) {
    this.activeOperation = operation;
  }

  onOkClick() {
    if(this.activeOperation === 'Update') {
      const send = new CrudInterfaceObject(this.objectToSend, this.activeItem, null);
      this.onUpdateClickEvent.emit(send);
    } else if (this.activeOperation === 'Create') {
      const send = new CrudInterfaceObject(this.objectToSend, this.activeItem, null);
      this.onCreateClickEvent.emit(send);
    }
  }

  addToArray(item) {
    this.items.push(item);
    this.updateListItems();
  }

  updateFromArray(item, keyName) {
    for(let i=0; i<this.items.length; i++) {
      if(this.items[i][keyName] === item[keyName]) {
        this.items[i] = item;
        break;
      }
    }
    this.updateListItems();
  }

  deleteFromArray(keyName, key) {
    let index;
  
    for(let i=0; i<this.items.length; i++) {
      if(this.items[i][keyName] === key) {
        index = i;
        break;
      }
    }

    this.items.splice(index, 1);
    this.updateListItems();
  }

  updateListItems() {
    this.listitems = HelperFunctions.createListItems(this.items, null, ['name']);
  }
}

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-toolbox',
  templateUrl: './toolbox.component.html',
  styleUrls: ['./toolbox.component.css']
})
export class ToolboxComponent implements OnInit {

  private isRunningAgent: boolean;

  constructor() { }

  ngOnInit() {
    this.isRunningAgent = false;
  }

}

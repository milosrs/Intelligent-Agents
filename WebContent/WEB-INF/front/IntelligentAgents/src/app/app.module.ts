import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { NgForm } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AppRoutingModule } from './app-routing/app-routing.module';
import { HttpClientModule } from '@angular/common/http';

import { CrudInterfaceComponent } from './shared/components/crud-interface/crud-interface.component';
import { AppComponent } from './app.component';

import { AbstractService } from './services/abstract.service';

import { ListComponent } from './shared/components/list/list.component';
import { RequestComponent } from './shared/components/request/request.component';
import { SearchComponent } from './shared/components/search/search.component';

@NgModule({
  declarations: [
    AppComponent,
    CrudInterfaceComponent,
    ListComponent,
    RequestComponent,
    SearchComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    CommonModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

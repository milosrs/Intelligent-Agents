import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { NgForm } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AppRoutingModule } from './app-routing.module';
import { HttpClientModule } from '@angular/common/http';

import { CrudInterfaceComponent } from './shared/components/crud-interface/crud-interface.component';
import { AppComponent } from './app.component';

import { ListComponent } from './shared/components/list/list.component';
import { RequestComponent } from './shared/components/request/request.component';
import { SearchComponent } from './shared/components/search/search.component';
import { HomeComponent } from './components/home/home.component';
import { ToolboxComponent } from './components/toolbox/toolbox.component';
import { ConsoleComponent } from './components/console/console.component';
import { SocketService } from './services/socket.service';
import { MessagingComponent } from './components/messaging/messaging.component';

@NgModule({
  declarations: [
    AppComponent,
    CrudInterfaceComponent,
    ListComponent,
    RequestComponent,
    SearchComponent,
    HomeComponent,
    ToolboxComponent,
    ConsoleComponent,
    MessagingComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    CommonModule,
    HttpClientModule,
    AppRoutingModule,
  ],
  exports: [
  ],
  providers: [ SocketService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

import { Injectable } from '@angular/core';

@Injectable()
export class SocketService {

  private socket: WebSocket;

  constructor() { }

  public getSocket() : WebSocket{
    return this.socket;
  }

  public initSocket(): void {
    this.socket = new WebSocket('ws://192.168.0.17:8080/Inteligent_Agents/websocket');
  }

  public closeSocket(): void {
    this.socket.close();
  }

  public send(message: any): void {
    this.socket.send(JSON.stringify(message));
  }

}
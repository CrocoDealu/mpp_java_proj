import { Injectable } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class GameWebSocketService {
  private stompClient!: Client;
  private connected = false;
  private gameEventsSubject = new Subject<any>();

  connect(): void {
    if (this.connected) return;
    this.stompClient = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      connectHeaders: {},
      debug: function (str) {
        console.log(str);
      },
      reconnectDelay: 5000,
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      onConnect: () => {
        this.stompClient.subscribe('/topic/games', (message: IMessage) => {
          const event = JSON.parse(message.body);
          this.gameEventsSubject.next(event);
        });
      },
    });

    this.stompClient.activate();
    this.connected = true;
  }

  disconnect(): void {
    if (this.stompClient) {
      this.stompClient.deactivate();
    }
  }

  getGameEvents(): Observable<any> {
    return this.gameEventsSubject.asObservable();
  }
}

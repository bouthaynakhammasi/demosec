# 📡 WebSocket Real-Time Notifications - Frontend Integration

## 🎯 Objectif
Chaque utilisateur (Patient, Pharmacien, etc.) reçoit UNIQUEMENT ses propres notifications en temps réel, pas celles des autres utilisateurs du même rôle.

## 🔑 Comment ça fonctionne maintenant

### Avant (❌ PROBLEM)
```javascript
// Tous les utilisateurs connectés à /topic/notifications
// recevaient les MÊMES messages
const stompClient = new StompClient();
stompClient.subscribe('/topic/notifications', (msg) => {
    console.log(msg); // ❌ Reçoit les notifications de TOUS
});
```

### Après (✅ SOLUTION)
```javascript
// Chaque utilisateur reçoit UNIQUEMENT ses notifications
// via /user/{userId}/queue/notifications
const stompClient = new StompClient();
stompClient.subscribe('/user/{userId}/queue/notifications', (msg) => {
    console.log(msg); // ✅ Reçoit UNIQUEMENT ses propres notifications
});
```

---

## 🚀 Configuration Frontend (Angular/TypeScript)

### 1️⃣ Installer les dépendances
```bash
npm install stompjs sockjs-client
# ou
npm install @stomp/stompjs sockjs-client
```

### 2️⃣ Créer un service WebSocket

Créer `src/app/services/websocket.service.ts` :

```typescript
import { Injectable } from '@angular/core';
import { Client, StompSubscription, Frame } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Subject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private stompClient: Client;
  private notificationSubject$ = new Subject<any>();
  private orderUpdatesSubject$ = new Subject<any>();
  private paymentSubject$ = new Subject<any>();
  private healthSubject$ = new Subject<any>();

  private userId: string | null = null;
  private token: string | null = null;

  public notifications$: Observable<any> = this.notificationSubject$.asObservable();
  public orderUpdates$: Observable<any> = this.orderUpdatesSubject$.asObservable();
  public payments$: Observable<any> = this.paymentSubject$.asObservable();
  public health$: Observable<any> = this.healthSubject$.asObservable();

  constructor() {
    this.initializeStompClient();
  }

  /**
   * Initialiser le client STOMP (ne pas se connecter automatiquement)
   */
  private initializeStompClient(): void {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8081/springsecurity/ws'),
      connectHeaders: {},
      debug: (str) => console.log('🔵 STOMP:', str),
      onConnect: () => this.handleConnect(),
      onDisconnect: () => this.handleDisconnect(),
      onStompError: (frame: Frame) => this.handleError(frame),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });
  }

  /**
   * Se connecter au WebSocket avec le JWT token
   * @param token Le JWT token obtenu lors du login
   * @param userId L'ID de l'utilisateur
   */
  public connect(token: string, userId: string): Promise<Frame> {
    this.token = token;
    this.userId = userId;

    return new Promise((resolve, reject) => {
      // Mettre à jour les headers avec le JWT token
      this.stompClient.connectHeaders = {
        'Authorization': `Bearer ${token}`
      };

      this.stompClient.onConnect = () => {
        this.handleConnect();
        resolve(new Frame('CONNECTED'));
      };

      this.stompClient.onStompError = (frame: Frame) => {
        this.handleError(frame);
        reject(frame);
      };

      this.stompClient.activate();
    });
  }

  /**
   * Gérer la connexion établie
   */
  private handleConnect(): void {
    console.log('✅ Connected to WebSocket');

    if (!this.userId) {
      console.error('❌ userId is not set');
      return;
    }

    // 🎯 S'abonner aux notifications PERSONNELLES
    // Destination: /user/{userId}/queue/notifications
    this.stompClient.subscribe(
      `/user/${this.userId}/queue/notifications`,
      (message) => {
        try {
          const notification = JSON.parse(message.body);
          console.log('📬 Notification reçue:', notification);
          this.notificationSubject$.next(notification);
        } catch (e) {
          console.error('Erreur parsing notification:', e);
        }
      }
    );

    // 🎯 S'abonner aux mises à jour de commande
    this.stompClient.subscribe(
      `/user/${this.userId}/queue/order-updates`,
      (message) => {
        try {
          const update = JSON.parse(message.body);
          console.log('📋 Order update reçue:', update);
          this.orderUpdatesSubject$.next(update);
        } catch (e) {
          console.error('Erreur parsing order update:', e);
        }
      }
    );

    // 🎯 S'abonner aux notifications de paiement
    this.stompClient.subscribe(
      `/user/${this.userId}/queue/payments`,
      (message) => {
        try {
          const payment = JSON.parse(message.body);
          console.log('💰 Payment notification reçue:', payment);
          this.paymentSubject$.next(payment);
        } catch (e) {
          console.error('Erreur parsing payment:', e);
        }
      }
    );

    // Test de connexion
    this.sendHealthCheck();
  }

  /**
   * Gérer la déconnexion
   */
  private handleDisconnect(): void {
    console.log('⚠️  Disconnected from WebSocket');
  }

  /**
   * Gérer les erreurs WebSocket
   */
  private handleError(frame: Frame): void {
    console.error('❌ WebSocket Error:', frame);
  }

  /**
   * Envoyer un health check au serveur
   */
  public sendHealthCheck(): void {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.publish({
        destination: '/app/health',
        body: JSON.stringify({ timestamp: new Date() })
      });
      console.log('💓 Health check sent');
    }
  }

  /**
   * Déconnecter du WebSocket
   */
  public disconnect(): Promise<void> {
    return new Promise((resolve) => {
      if (this.stompClient && this.stompClient.active) {
        this.stompClient.deactivate().then(() => {
          console.log('❌ Disconnected');
          resolve();
        });
      } else {
        resolve();
      }
    });
  }

  /**
   * Vérifier si on est connecté
   */
  public isConnected(): boolean {
    return this.stompClient?.connected ?? false;
  }
}
```

### 3️⃣ Utiliser le service dans un composant

```typescript
import { Component, OnInit, OnDestroy } from '@angular/core';
import { WebSocketService } from './services/websocket.service';
import { AuthService } from './services/auth.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css']
})
export class NotificationsComponent implements OnInit, OnDestroy {
  notifications: any[] = [];
  private destroy$ = new Subject<void>();

  constructor(
    private wsService: WebSocketService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Récupérer le token et l'ID utilisateur depuis le service auth
    const token = this.authService.getToken();
    const userId = this.authService.getUserId();

    if (token && userId) {
      // Connexion au WebSocket
      this.wsService.connect(token, userId).then(() => {
        console.log('✅ WebSocket connected');

        // Écouter les notifications en temps réel
        this.wsService.notifications$
          .pipe(takeUntil(this.destroy$))
          .subscribe((notification) => {
            console.log('🔔 Nouvelle notification:', notification);
            this.notifications.unshift(notification);
            
            // Afficher une toast, badge, etc.
            this.showNotificationAlert(notification);
          });

        // Écouter les mises à jour de commande
        this.wsService.orderUpdates$
          .pipe(takeUntil(this.destroy$))
          .subscribe((update) => {
            console.log('📦 Mise à jour de commande:', update);
            // Mettre à jour l'interface utilisateur
          });

        // Écouter les notifications de paiement
        this.wsService.payments$
          .pipe(takeUntil(this.destroy$))
          .subscribe((payment) => {
            console.log('💳 Notification de paiement:', payment);
            // Mettre à jour l'interface utilisateur
          });
      }).catch((error) => {
        console.error('❌ Erreur de connexion WebSocket:', error);
      });
    }
  }

  private showNotificationAlert(notification: any): void {
    // Utiliser Toast, Snackbar, ou Modal selon votre UI library
    console.log(`🔔 ${notification.title}: ${notification.message}`);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.wsService.disconnect();
  }
}
```

### 4️⃣ Template HTML

```html
<div class="notifications-container">
  <h3>Notifications Temps Réel 📬</h3>
  
  <div *ngIf="notifications.length === 0" class="no-notifications">
    Aucune notification
  </div>

  <div *ngFor="let notif of notifications" class="notification-item">
    <strong>{{ notif.title }}</strong>
    <p>{{ notif.message }}</p>
    <small>{{ notif.createdAt | date:'short' }}</small>
  </div>
</div>
```

---

## 🔐 Points clés à retenir

| Point | Explication |
|-------|-------------|
| **JWT Token** | Doit être envoyé dans l'header `Authorization: Bearer {token}` |
| **UserID** | Chaque utilisateur doit avoir un ID unique |
| `/user/{userId}/queue/notifications` | Chaque utilisateur reçoit UNIQUEMENT ses messages |
| `convertAndSendToUser()` | Backend envoie UNIQUEMENT au user spécifique |
| Pas de broadcast général | Les pharmaciens ne voient que leurs commandes |
| Les patients ne voient que leurs commandes | Chacun isole ses données |

---

## 🐛 Dépannage

### Le WebSocket ne se connecte pas
```javascript
// Vérifier:
1. Le token JWT est valide
2. Le userId est correct
3. Le serveur WebSocket est accessible (ws://localhost:8081/springsecurity/ws)
4. Pas de CORS issues
```

### Les notifications ne sont pas reçues
```javascript
// Vérifier:
1. L'authentification WebSocket est réussie (check console)
2. Vous êtes abonnés à la bonne destination (/user/{userId}/queue/notifications)
3. Le serveur envoie les notifications via convertAndSendToUser()
```

### Différentes notifications pour différents utilisateurs
```javascript
// ✅ C'est normal! Chaque utilisateur reçoit uniquement:
// - Ses propres commandes
// - Ses propres notifications
// - Ses propres mises à jour de paiement
```

---

## 📝 Résumé

✅ **Avant la fix**: Tous les utilisateurs du même rôle recevaient les mêmes notifications  
❌ **Après la fix**: Chaque utilisateur reçoit UNIQUEMENT ses notifications  

**La solution**: Utiliser `convertAndSendToUser()` au lieu de `convertAndSend()`


# 🚀 PROMPT COMPLET POUR INTÉGRATION PAIEMENTS FRONTEND ANGULAR

**À passer au Chat de votre Frontend Team (Angular)**

---

## 📋 CONTEXTE

Vous avez maintenant un **backend Spring Boot complet avec paiements réels Stripe/D17 et WebSocket**. 

Le backend est **100% prêt** et expose les endpoints suivants:

```
POST   /api/pharmacy/payments              → Initier un paiement
GET    /api/pharmacy/payments/order/{id}   → Récupérer le statut de paiement

WebSocket STOMP:
/topic/payment/{patientId}/completed   → Paiement réussi ✓
/topic/payment/{patientId}/failed      → Paiement échoué ✗
/topic/payment/{patientId}/processing  → En traitement...
/topic/pharmacy/{pharmacyId}/payments  → Tous les paiements (pour pharmacie)
/topic/pharmacy/{pharmacyId}/orders/ready → Commandes prêtes à préparer
```

---

## 🎯 OBJECTIF FRONTEND

Créer une **expérience de paiement complète** avec:
- ✅ Formulaire de paiement (4 méthodes: Stripe Card, PayPal, D17 Wallet, Bank Transfer)
- ✅ Intégration Stripe.js + D17 SDK côté client
- ✅ Validation en temps réel avec formatage automatique
- ✅ Notifications WebSocket pour statut en temps réel
- ✅ Toast notifications (succès/erreur/processing)
- ✅ Gestion d'erreurs robuste
- ✅ Orchestration complète du flux de commande

**Temps estimé:** 4-5 jours pour senior Angular dev

---

## 📦 ARCHITECTURE REQUISE

```
src/app/
├── components/
│   ├── payment/
│   │   ├── payment-checkout/
│   │   │   ├── payment-checkout.component.ts
│   │   │   ├── payment-checkout.component.html
│   │   │   ├── payment-checkout.component.css
│   │   │   └── payment-checkout.model.ts
│   │   └── payment-status/
│   │       ├── payment-status.component.ts (optional - pour affichage statut)
│   │       └── payment-status.component.html
│   │
│   └── notifications/
│       ├── payment-toast/
│       │   ├── payment-toast.component.ts
│       │   ├── payment-toast.component.html
│       │   └── payment-toast.component.css
│       └── payment-alerts/
│           └── payment-alerts.component.ts (Pharmacy alerts)
│
├── services/
│   ├── payment.service.ts           ← CRITICAL
│   ├── websocket-payment.service.ts ← CRITICAL
│   └── stripe.service.ts            (optional - wrapper Stripe.js)
│
├── models/
│   ├── payment.model.ts
│   └── payment-response.model.ts
│
├── interceptors/
│   └── payment-error.interceptor.ts (optional)
│
└── config/
    └── payment.config.ts (optional - API endpoints)
```

---

## 🔑 SERVICES À DÉVELOPPER

### 1️⃣ **PaymentService** (CRITICAL)

```typescript
// services/payment.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { PaymentRequest, PaymentResponse, PaymentStatus } from '../models/payment.model';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private apiUrl = '/api/pharmacy/payments';
  
  // State management
  currentPayment$ = new BehaviorSubject<PaymentResponse | null>(null);
  isProcessing$ = new BehaviorSubject<boolean>(false);
  paymentError$ = new BehaviorSubject<string | null>(null);

  constructor(private http: HttpClient) {}

  /**
   * Initiate payment
   * @param request PaymentRequest with orderId, method, token, email, phone
   * @returns Observable<PaymentResponse> with transaction details
   */
  initiatePayment(request: PaymentRequest): Observable<PaymentResponse> {
    this.isProcessing$.next(true);
    this.paymentError$.next(null);

    return this.http.post<PaymentResponse>(this.apiUrl, request, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${this.getJwtToken()}`
      })
    });
  }

  /**
   * Get payment status by order ID
   */
  getPaymentByOrderId(orderId: number): Observable<PaymentResponse> {
    return this.http.get<PaymentResponse>(`${this.apiUrl}/order/${orderId}`);
  }

  /**
   * Get JWT token from localStorage/sessionStorage
   */
  private getJwtToken(): string {
    return localStorage.getItem('auth_token') || '';
  }

  /**
   * Update local state on successful payment
   */
  setPaymentSuccess(response: PaymentResponse): void {
    this.currentPayment$.next(response);
    this.isProcessing$.next(false);
  }

  /**
   * Update local state on payment failure
   */
  setPaymentError(error: string): void {
    this.paymentError$.next(error);
    this.isProcessing$.next(false);
  }
}
```

---

### 2️⃣ **WebSocketPaymentService** (CRITICAL)

```typescript
// services/websocket-payment.service.ts

import { Injectable } from '@angular/core';
import { Client, StompSubscription } from '@stomp/stompjs';
import { BehaviorSubject, Subject } from 'rxjs';
import { AuthService } from './auth.service'; // You have this

interface PaymentMessage {
  event: string;
  status: string;
  transactionId: string;
  amount: number;
  timestamp: Date;
  reason?: string; // For failures
}

@Injectable({ providedIn: 'root' })
export class WebSocketPaymentService {
  private client: Client;
  private connected$ = new BehaviorSubject<boolean>(false);

  // Payment notifications
  paymentCompleted$ = new Subject<PaymentMessage>();
  paymentFailed$ = new Subject<PaymentMessage>();
  paymentProcessing$ = new Subject<PaymentMessage>();
  pharmacyPaymentAlert$ = new Subject<PaymentMessage>(); // For pharmacy staff

  private subscriptions: StompSubscription[] = [];

  constructor(private authService: AuthService) {
    this.initializeClient();
  }

  private initializeClient(): void {
    this.client = new Client({
      brokerURL: 'ws://localhost:8081/springsecurity/ws', // Update if needed
      reconnectDelay: 5000,
      debug: (str) => console.log('STOMP:', str),
    });

    this.client.onConnect = () => {
      console.log('✓ WebSocket connected');
      this.connected$.next(true);
      this.subscribeToPaymentTopics();
    };

    this.client.onDisconnect = () => {
      console.log('✗ WebSocket disconnected');
      this.connected$.next(false);
    };

    this.client.onStompError = (error) => {
      console.error('STOMP error:', error);
      this.connected$.next(false);
    };
  }

  /**
   * Connect to WebSocket broker
   */
  connect(): void {
    if (this.client && !this.client.connected) {
      this.client.activate();
    }
  }

  /**
   * Disconnect from WebSocket
   */
  disconnect(): void {
    if (this.client && this.client.connected) {
      this.client.deactivate();
    }
  }

  /**
   * Subscribe to payment topics for current user
   */
  private subscribeToPaymentTopics(): void {
    const patientId = this.authService.getCurrentPatientId();
    const pharmacyId = this.authService.getCurrentPharmacyId();

    // Patient payment statuses
    if (patientId) {
      this.subscribeToTopic(
        `/topic/payment/${patientId}/completed`,
        (message) => this.paymentCompleted$.next(JSON.parse(message.body))
      );

      this.subscribeToTopic(
        `/topic/payment/${patientId}/failed`,
        (message) => this.paymentFailed$.next(JSON.parse(message.body))
      );

      this.subscribeToTopic(
        `/topic/payment/${patientId}/processing`,
        (message) => this.paymentProcessing$.next(JSON.parse(message.body))
      );
    }

    // Pharmacy payment alerts (if user is pharmacist)
    if (pharmacyId) {
      this.subscribeToTopic(
        `/topic/pharmacy/${pharmacyId}/payments`,
        (message) => this.pharmacyPaymentAlert$.next(JSON.parse(message.body))
      );
    }
  }

  /**
   * Subscribe to a topic
   */
  private subscribeToTopic(topic: string, callback: (message: any) => void): void {
    if (this.client && this.client.connected) {
      const subscription = this.client.subscribe(topic, callback);
      this.subscriptions.push(subscription);
      console.log(`Subscribed to ${topic}`);
    }
  }

  /**
   * Unsubscribe all
   */
  unsubscribeAll(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
    this.subscriptions = [];
  }

  /**
   * Check if connected
   */
  isConnected(): Observable<boolean> {
    return this.connected$.asObservable();
  }
}
```

---

## 🎨 COMPOSANTS À DÉVELOPPER

### 3️⃣ **PaymentCheckoutComponent** (CRITICAL)

```typescript
// components/payment/payment-checkout/payment-checkout.component.ts

import { Component, OnInit, OnDestroy, Output, EventEmitter, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { loadStripe, Stripe, StripeCardElement } from '@stripe/js';
import { PaymentService } from '../../../services/payment.service';
import { WebSocketPaymentService } from '../../../services/websocket-payment.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

interface PaymentFormData {
  orderId: number;
  method: 'STRIPE' | 'PAYPAL' | 'D17' | 'BANK_TRANSFER';
  token?: string;
  email: string;
  phone: string;
}

@Component({
  selector: 'app-payment-checkout',
  templateUrl: './payment-checkout.component.html',
  styleUrls: ['./payment-checkout.component.css']
})
export class PaymentCheckoutComponent implements OnInit, OnDestroy {
  @Input() orderId: number;
  @Input() orderAmount: number;
  @Input() orderNumber: string;
  
  @Output() paymentSuccess = new EventEmitter<any>();
  @Output() paymentFailed = new EventEmitter<any>();
  @Output() paymentProcessing = new EventEmitter<void>();

  paymentForm: FormGroup;
  selectedMethod: 'STRIPE' | 'PAYPAL' | 'D17' | 'BANK_TRANSFER' = 'STRIPE';
  
  isProcessing = false;
  errorMessage: string | null = null;
  showCardForm = true;

  // Stripe elements
  stripe: Stripe | null = null;
  cardElement: StripeCardElement | null = null;

  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private paymentService: PaymentService,
    private wsPaymentService: WebSocketPaymentService
  ) {
    this.paymentForm = this.fb.group({
      method: ['STRIPE', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      cardNumber: [''],
      cardExpiry: [''],
      cardCvc: ['']
    });
  }

  async ngOnInit(): Promise<void> {
    // Initialize Stripe
    this.stripe = await loadStripe('pk_test_YOUR_PUBLISHABLE_KEY'); // Get from env
    
    // Create card element
    if (this.stripe) {
      const elements = this.stripe.elements();
      this.cardElement = elements.create('card');
      this.cardElement.mount('#card-element');
    }

    // Listen to WebSocket payment confirmation
    this.wsPaymentService.paymentCompleted$
      .pipe(takeUntil(this.destroy$))
      .subscribe((message) => {
        console.log('✓ Payment confirmed via WebSocket:', message);
        this.isProcessing = false;
        this.paymentSuccess.emit(message);
      });

    // Listen to payment failure
    this.wsPaymentService.paymentFailed$
      .pipe(takeUntil(this.destroy$))
      .subscribe((message) => {
        console.log('✗ Payment failed:', message);
        this.isProcessing = false;
        this.errorMessage = message.reason || 'Payment failed. Please try again.';
        this.paymentFailed.emit(message);
      });

    // Listen to processing state
    this.wsPaymentService.paymentProcessing$
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.paymentProcessing.emit();
      });

    // Connect WebSocket
    this.wsPaymentService.connect();
  }

  async onPaymentSubmit(): Promise<void> {
    if (!this.paymentForm.valid) {
      this.errorMessage = 'Please fill all required fields';
      return;
    }

    this.isProcessing = true;
    this.paymentProcessing.emit();

    try {
      let token: string | null = null;

      // Get payment token based on method
      switch (this.selectedMethod) {
        case 'STRIPE':
          token = await this.getStripeToken();
          break;
        case 'PAYPAL':
          token = await this.getPayPalToken();
          break;
        case 'D17':
          token = await this.getD17Token();
          break;
        case 'BANK_TRANSFER':
          token = await this.getBankTransferToken();
          break;
      }

      if (!token) {
        throw new Error('Failed to generate payment token');
      }

      // Send to backend
      const request: PaymentFormData = {
        orderId: this.orderId,
        method: this.selectedMethod,
        token: token,
        email: this.paymentForm.get('email')?.value,
        phone: this.paymentForm.get('phone')?.value
      };

      this.paymentService.initiatePayment(request).subscribe({
        next: (response) => {
          console.log('Payment initiated:', response);
          // WebSocket will handle confirmation
        },
        error: (error) => {
          this.isProcessing = false;
          this.errorMessage = error.error?.message || 'Payment initiation failed';
          this.paymentFailed.emit(error);
        }
      });

    } catch (error: any) {
      this.isProcessing = false;
      this.errorMessage = error.message;
      this.paymentFailed.emit(error);
    }
  }

  /**
   * Get Stripe token via Stripe.js
   */
  private async getStripeToken(): Promise<string> {
    if (!this.stripe || !this.cardElement) {
      throw new Error('Stripe not initialized');
    }

    const { token, error } = await this.stripe.createToken(this.cardElement);

    if (error) {
      throw new Error(error.message);
    }

    return token?.id || '';
  }

  /**
   * Get PayPal token - requires PayPal SDK
   */
  private async getPayPalToken(): Promise<string> {
    // Implementation depends on PayPal SDK integration
    // For Stripe: use Stripe PayPal integration instead
    return 'paypal_token_placeholder';
  }

  /**
   * Get D17 wallet token
   * Requires D17 SDK or API call to get wallet token
   */
  private async getD17Token(): Promise<string> {
    // Call your backend endpoint to generate D17 payment link
    // Or use D17 SDK directly
    return 'd17_wallet_token_placeholder';
  }

  /**
   * Get bank transfer token
   */
  private async getBankTransferToken(): Promise<string> {
    // Bank transfer might use Stripe ACH/SEPA
    return 'bank_transfer_token_placeholder';
  }

  /**
   * Change payment method
   */
  onMethodChange(method: 'STRIPE' | 'PAYPAL' | 'D17' | 'BANK_TRANSFER'): void {
    this.selectedMethod = method;
    this.paymentForm.patchValue({ method });
    this.showCardForm = (method === 'STRIPE');
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.wsPaymentService.disconnect();
  }
}
```

---

### 4️⃣ **Payment Template HTML**

```html
<!-- components/payment/payment-checkout/payment-checkout.component.html -->

<div class="payment-checkout-container">
  <!-- Order Summary -->
  <div class="order-summary">
    <h2>Order {{ orderNumber }}</h2>
    <p class="amount">{{ orderAmount | currency:'TND':'symbol':'1.2-2' }}</p>
  </div>

  <!-- Payment Method Selection -->
  <div class="payment-methods">
    <button 
      *ngFor="let method of ['STRIPE', 'PAYPAL', 'D17', 'BANK_TRANSFER']"
      (click)="onMethodChange(method)"
      [class.active]="selectedMethod === method"
      class="method-button">
      <span class="icon">{{ getMethodIcon(method) }}</span>
      <span class="label">{{ getMethodLabel(method) }}</span>
    </button>
  </div>

  <!-- Payment Form -->
  <form [formGroup]="paymentForm" (ngSubmit)="onPaymentSubmit()" class="payment-form">
    
    <!-- Email & Phone (All methods) -->
    <div class="form-section">
      <mat-form-field>
        <mat-label>Email</mat-label>
        <input matInput formControlName="email" type="email">
      </mat-form-field>

      <mat-form-field>
        <mat-label>Phone</mat-label>
        <input matInput formControlName="phone" placeholder="+216...">
      </mat-form-field>
    </div>

    <!-- Stripe Card Form -->
    <div *ngIf="selectedMethod === 'STRIPE'" class="card-form">
      <label>Card Details</label>
      <div id="card-element"></div>
      <small *ngIf="errorMessage" class="error">{{ errorMessage }}</small>
    </div>

    <!-- D17 Wallet (if configured) -->
    <div *ngIf="selectedMethod === 'D17'" class="d17-form">
      <p>You will be redirected to D17 to complete payment</p>
    </div>

    <!-- PayPal (if configured) -->
    <div *ngIf="selectedMethod === 'PAYPAL'" class="paypal-form">
      <p>You will be redirected to PayPal to complete payment</p>
    </div>

    <!-- Bank Transfer -->
    <div *ngIf="selectedMethod === 'BANK_TRANSFER'" class="bank-form">
      <p>Bank transfer details will be provided after order confirmation</p>
    </div>

    <!-- Error Message -->
    <div *ngIf="errorMessage" class="error-message">
      <mat-icon>error</mat-icon>
      {{ errorMessage }}
    </div>

    <!-- Processing Indicator -->
    <div *ngIf="isProcessing" class="processing">
      <mat-spinner diameter="30"></mat-spinner>
      <span>Processing payment...</span>
    </div>

    <!-- Submit Button -->
    <button 
      type="submit" 
      [disabled]="isProcessing || !paymentForm.valid"
      class="submit-button">
      {{ isProcessing ? 'Processing...' : 'Pay ' + (orderAmount | currency:'TND':'symbol':'1.2-2') }}
    </button>
  </form>

  <!-- Security Badge -->
  <div class="security-info">
    <mat-icon>lock</mat-icon>
    <span>Secure payment powered by Stripe & D17</span>
  </div>
</div>
```

---

### 5️⃣ **Payment Toast Component**

```typescript
// components/notifications/payment-toast/payment-toast.component.ts

import { Component, OnInit, OnDestroy } from '@angular/core';
import { WebSocketPaymentService } from '../../../services/websocket-payment.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

interface Toast {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  title: string;
  message: string;
  duration?: number;
}

@Component({
  selector: 'app-payment-toast',
  templateUrl: './payment-toast.component.html',
  styleUrls: ['./payment-toast.component.css']
})
export class PaymentToastComponent implements OnInit, OnDestroy {
  toasts: Toast[] = [];
  private destroy$ = new Subject<void>();

  constructor(private wsPaymentService: WebSocketPaymentService) {}

  ngOnInit(): void {
    // Listen to payment events
    this.wsPaymentService.paymentCompleted$
      .pipe(takeUntil(this.destroy$))
      .subscribe((message) => {
        this.showToast('success', 'Payment Successful', 
          `Transaction: ${message.transactionId}`);
      });

    this.wsPaymentService.paymentFailed$
      .pipe(takeUntil(this.destroy$))
      .subscribe((message) => {
        this.showToast('error', 'Payment Failed', message.reason);
      });

    this.wsPaymentService.paymentProcessing$
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.showToast('info', 'Processing', 'Verifying payment...');
      });
  }

  showToast(type: 'success' | 'error' | 'warning' | 'info', 
            title: string, message: string): void {
    const id = Date.now().toString();
    this.toasts.push({ id, type, title, message, duration: 4000 });

    if (type !== 'error') {
      setTimeout(() => this.removeToast(id), 4000);
    }
  }

  removeToast(id: string): void {
    this.toasts = this.toasts.filter((t) => t.id !== id);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
```

---

## 🔌 INTÉGRATION ROUTING

```typescript
// app-routing.module.ts

const routes: Routes = [
  {
    path: 'patient',
    children: [
      {
        path: 'orders/:id/payment',
        component: PaymentCheckoutComponent,  // ← HERE
        canActivate: [AuthGuard]
      },
      // ... other routes
    ]
  }
];
```

---

## 📱 APP.MODULE IMPORTS

```typescript
// app.module.ts

import { PaymentCheckoutComponent } from './components/payment/payment-checkout/payment-checkout.component';
import { PaymentToastComponent } from './components/notifications/payment-toast/payment-toast.component';
import { PaymentService } from './services/payment.service';
import { WebSocketPaymentService } from './services/websocket-payment.service';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

@NgModule({
  declarations: [
    PaymentCheckoutComponent,
    PaymentToastComponent,
    // ... other components
  ],
  imports: [
    ReactiveFormsModule,
    FormsModule,
    MatFormFieldModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatInputModule,
    // ... other modules
  ],
  providers: [
    PaymentService,
    WebSocketPaymentService
  ]
})
export class AppModule { }
```

---

## 🛠️ DEPENDENCIES À INSTALLER

```bash
# Stripe
npm install @stripe/stripe-js --save

# STOMP (WebSocket)
npm install @stomp/stompjs@latest --save
npm install sockjs-client --save

# Material (optional but recommended)
ng add @angular/material

# RxJS (already installed)
```

---

## 🧪 TESTING CHECKLIST

- [ ] **1. Form Validation**
  ```
  ✓ Email validation
  ✓ Phone validation
  ✓ Card format (Stripe)
  ✓ All fields required
  ```

- [ ] **2. Stripe Integration**
  ```
  ✓ Card element renders
  ✓ Test card: 4111 1111 1111 1111
  ✓ Error card: 4000 0000 0000 0002
  ✓ 3DS card: 4000 2500 0000 3155
  ```

- [ ] **3. WebSocket**
  ```
  ✓ Connects on payment init
  ✓ Receives completion message
  ✓ Receives failure message
  ✓ Displays toast notifications
  ```

- [ ] **4. Complete Flow**
  ```
  ✓ Click "Pay" button
  ✓ See "Processing..." 
  ✓ Receive WebSocket confirmation
  ✓ See success toast
  ✓ Redirect to confirmation page
  ```

- [ ] **5. Error Handling**
  ```
  ✓ Invalid card shows error
  ✓ Network timeout handled
  ✓ Failed payment shows retry option
  ✓ User can change payment method
  ```

---

## 🔑 ENVIRONMENT VARIABLES

Create `environment.ts` and `environment.prod.ts`:

```typescript
// environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8081/springsecurity',
  wsUrl: 'ws://localhost:8081/springsecurity/ws',
  stripe: {
    publishableKey: 'pk_test_YOUR_KEY_HERE'
  },
  d17: {
    enabled: false, // Set to true when configured
    apiUrl: 'https://api.d17.tn/'
  }
};
```

---

## 🎯 WORKFLOW COMPLET (User Journey)

```
1. Patient clicks "Go to Checkout"
   ↓
2. Navigate to /patient/orders/{orderId}/payment
   ↓
3. PaymentCheckoutComponent loads
   - Stripe card element initialized
   - WebSocket connects
   ↓
4. Patient selects payment method (default: STRIPE)
   ↓
5. Patient enters:
   - Email
   - Phone
   - Card details (Stripe) or redirects to D17/PayPal
   ↓
6. Patient clicks "Pay {amount}"
   ↓
7. Frontend creates payment token via Stripe.js
   ↓
8. Frontend sends to backend:
   POST /api/pharmacy/payments
   {
     "orderId": 123,
     "method": "STRIPE",
     "token": "pm_1A1A1A1A...",
     "email": "patient@example.com",
     "phone": "+21698765432"
   }
   ↓
9. Backend:
   - Creates Payment entity (PENDING)
   - Sends to Stripe API
   - Broadcasts via WebSocket: PROCESSING
   ↓
10. Stripe responds (SUCCESS or FAILURE)
    ↓
11. Backend receives Stripe webhook
    ↓
12. Backend broadcasts via WebSocket:
    /topic/payment/{patientId}/completed ← Frontend gets this!
    ↓
13. Frontend:
    - Stops loading
    - Shows success toast
    - Updates Payment status component
    - Redirects to order confirmation
    ↓
14. Order status in database: PAID
    ↓
15. Pharmacy receives WebSocket alert:
    /topic/pharmacy/{pharmacyId}/orders/ready
    ↓
16. Pharmacist sees new order ready to prepare
```

---

## ✨ BONUS FEATURES (Optional)

- [ ] **Payment History**
  ```typescript
  // Show past payments
  paymentHistory$: Observable<PaymentResponse[]>
  ```

- [ ] **Refund Request**
  ```typescript
  // Allow customer to request refund
  requestRefund(paymentId: number): Observable<RefundResponse>
  ```

- [ ] **Payment Receipt**
  ```typescript
  // Generate and download receipt PDF
  downloadReceipt(paymentId: number): void
  ```

- [ ] **Multiple Payment Methods**
  ```
  Save multiple cards
  Default payment method
  Quick checkout
  ```

- [ ] **Payment Analytics**
  ```
  Track conversion rate
  Monitor failure reasons
  A/B test payment methods
  ```

---

## 📚 HELPFUL RESOURCES

- **Stripe Documentation:** https://stripe.com/docs/stripe-js
- **STOMP Client:** https://stomp-js.github.io/stomp-websocket/
- **Angular Reactive Forms:** https://angular.io/guide/reactive-forms
- **RxJS Documentation:** https://rxjs.dev/

---

## 🚀 SUMMARY

**What Backend Provides:**
- ✅ Real Stripe/D17 payment processing
- ✅ Webhook confirmations
- ✅ WebSocket real-time notifications
- ✅ Error handling & retries
- ✅ Transaction tracking

**What Frontend Must Implement:**
- ✅ Stripe.js integration (card element)
- ✅ Form validation & UX
- ✅ WebSocket subscription to payment topics
- ✅ Toast notifications
- ✅ Complete order workflow

**Integration Points:**
- HTTP: POST /api/pharmacy/payments
- WebSocket: /topic/payment/{patientId}/*
- Stripe SDK: Client-side payment tokenization

---

## ❓ QUESTIONS FOR FRONTEND TEAM

1. Do you have Stripe account setup already?
2. Should we support PayPal through Stripe or separately?
3. What's your current UI/design system?
4. Do you need payment history page?
5. Should pharmacy staff see payment dashboard?
6. Need invoice generation?

---

**BACKEND IS READY TO RECEIVE PAYMENT REQUESTS! 🎉**

Pass this prompt to your Angular team and they can start implementation immediately.
The backend has everything they need - no more waiting!

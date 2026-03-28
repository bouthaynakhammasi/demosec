# 🔗 GUIDE D'INTÉGRATION COMPLÈTE - FRONTEND + BACKEND PAYMENTS

**Pour synchroniser Frontend et Backend sur les paiements réels**

---

## 📊 ÉTAT ACTUEL

### ✅ BACKEND - 100% PRÊT

```
Payment Gateway Layer:
├── Stripe SDK (stripe-java 25.0.0) ✅
├── D17 API Integration ✅
├── Webhook Handlers (Stripe + D17) ✅
└── WebSocket Notifications ✅

Services:
├── PaymentService (enhanced) ✅
├── PaymentWebSocketService ✅
└── PaymentGatewayFactory ✅

Controllers:
├── /api/pharmacy/payments (POST) ✅
├── /api/pharmacy/payments/order/{id} (GET) ✅
└── /api/pharmacy/payments/webhook/* ✅

Configuration:
├── pom.xml (updated) ✅
├── application.properties (updated) ✅
└── Config Validators ✅
```

### ⏳ FRONTEND - À IMPLÉMENTER (4-5 jours)

```
Services:
├── payment.service.ts ⏐ ~200 lignes
├── websocket-payment.service.ts ⏐ ~250 lignes
└── stripe.service.ts (optional) ⏐ ~100 lignes

Components:
├── payment-checkout ⏐ ~400 lignes (TS+HTML+CSS)
├── payment-toast ⏐ ~150 lignes
├── payment-status ⏐ ~100 lignes
└── payment-alerts (pharmacy) ⏐ ~150 lignes

Models:
├── payment.model.ts ⏐ ~50 lignes
└── payment-response.model.ts ⏐ ~30 lignes

Total: ~1,430 lignes (sans third-party SDK)
```

---

## 🎯 SYNC POINTS - BACKEND & FRONTEND

### 1️⃣ **HTTP Request/Response**

**Backend Expects:**
```json
{
  "orderId": 123,
  "method": "STRIPE",  // STRIPE | PAYPAL | D17 | BANK_TRANSFER
  "token": "pm_1A1A1A...",  // From Stripe.js
  "email": "patient@example.com",
  "phone": "+216..."
}
```

**Backend Returns (201 Created):**
```json
{
  "id": 1,
  "orderId": 123,
  "method": "STRIPE",
  "status": "COMPLETED",  // PENDING | PROCESSING | COMPLETED | FAILED
  "amount": 149.99,
  "currency": "TND",
  "transactionId": "pi_1A1A1A1A...",
  "createdAt": "2026-03-21T10:30:00",
  "confirmedAt": "2026-03-21T10:32:15"
}
```

### 2️⃣ **WebSocket Topics**

**Frontend Subscribes To:**

```
/topic/payment/{patientId}/completed
↓
{
  "event": "PAYMENT_COMPLETED",
  "paymentId": 1,
  "orderId": 123,
  "transactionId": "pi_1A1A1A1A...",
  "amount": 149.99,
  "status": "COMPLETED",
  "confirmedAt": "2026-03-21T10:32:15",
  "timestamp": "2026-03-21T10:32:20"
}
```

```
/topic/payment/{patientId}/failed
↓
{
  "event": "PAYMENT_FAILED",
  "paymentId": 1,
  "orderId": 123,
  "amount": 149.99,
  "reason": "Card declined by issuer",
  "status": "FAILED",
  "timestamp": "2026-03-21T10:32:20"
}
```

```
/topic/payment/{patientId}/processing
↓
{
  "event": "PAYMENT_PROCESSING",
  "paymentId": 1,
  "orderId": 123,
  "status": "PROCESSING",
  "timestamp": "2026-03-21T10:31:05"
}
```

---

## 🔄 WORKFLOW DÉTAILLÉ

```
┌─────────────────────────────────────────────────────────────┐
│ FRONTEND (Angular)                                          │
│ Component: PaymentCheckoutComponent                         │
│                                                             │
│ 1. User fills payment form                                 │
│    - Email: patient@example.com                            │
│    - Phone: +21698765432                                   │
│    - Card (Stripe card element)                            │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
        ┌─────────────────────────────┐
        │ 2. Call Stripe.js           │
        │ stripe.createToken(card)    │
        │                             │
        │ Returns: pm_1A1A1A1A...    │
        └────────────┬────────────────┘
                     │
                     ▼
        ┌──────────────────────────────────────────┐
        │ 3. POST /api/pharmacy/payments           │
        │    {                                     │
        │      "orderId": 123,                     │
        │      "method": "STRIPE",                 │
        │      "token": "pm_1A1A1A1A...",         │
        │      "email": "...",                     │
        │      "phone": "..."                      │
        │    }                                     │
        └────────────┬─────────────────────────────┘
                     │
┌────────────────────▼──────────────────────────────┐
│ BACKEND (Spring Boot)                             │
│ PaymentServiceImpl.initiatePayment()              │
│                                                  │
│ 4. Create Payment entity (PENDING)               │
│ 5. Call Stripe API with token                    │
│    → StripePaymentProvider.processPayment()      │
│                                                  │
│ 6. Receive PaymentIntent from Stripe             │
│    - Status: PROCESSING or SUCCEEDED             │
│                                                  │
│ 7. If PROCESSING:                               │
│    - Save payment                                │
│    - WebSocket: /topic/payment/123/processing    │
│    - Return 201 with payment ID                  │
│                                                  │
│ 8. Stripe sends webhook after 2-30s:            │
│    POST /api/pharmacy/payments/webhook/stripe    │
│    Event: payment_intent.succeeded               │
│                                                  │
│ 9. PaymentWebhookController receives webhook     │
│    - Verifies signature (HMAC-SHA256)            │
│    - Finds payment by transactionId              │
│    - Updates status: COMPLETED                   │
│    - Updates order: PAID                         │
│                                                  │
│ 10. WebSocket broadcast:                         │
│     /topic/payment/123/completed                 │
│     {event, transactionId, amount, timestamp}    │
│                                                  │
│ 11. WebSocket broadcast to pharmacy:             │
│     /topic/pharmacy/456/orders/ready             │
│     {orderId, event, timestamp}                  │
└────────────────────┬─────────────────────────────┘
                     │
                     ▼
        ┌──────────────────────────────┐
        │ FRONTEND WebSocket Handler   │
        │                              │
        │ 12. Receive on               │
        │  /topic/payment/123/completed│
        │                              │
        │ 13. paymentCompleted$.next() │
        │                              │
        │ 14. Component detects and:   │
        │     - Stop loading spinner   │
        │     - Show success toast     │
        │     - Update UI              │
        │     - Redirect to confirm    │
        └──────────────────────────────┘
```

---

## 🛠️ BACKEND IMPLEMENTATION STEPS (5 min)

### Step 1: Copy Payment Files to Project
```bash
# Copy these directories:
src/main/java/com/aziz/demosec/
├── payment/gateway/          # Copy all
├── payment/PaymentWebSocketService.java
├── service/PaymentServiceImpl_Enhanced.java
├── controller/PaymentWebhookController.java
└── config/PaymentGatewayConfigValidator.java
```

### Step 2: Update PaymentService
```bash
# In src/main/java/com/aziz/demosec/service/

# Option A: Replace old with enhanced
mv PaymentServiceImpl.java PaymentServiceImpl_OLD.java
cp PaymentServiceImpl_Enhanced.java PaymentServiceImpl.java

# OR Option B: Merge enhanced features into existing
# Add the new methods to PaymentServiceImpl manually
```

### Step 3: Update PaymentRepository
```java
// In PaymentRepository, add:

Optional<Payment> findByTransactionId(String transactionId);
Optional<Payment> findByPharmacyOrder_Id(Long orderId);
```

### Step 4: Rebuild
```bash
cd c:\Users\LENOVO\IdeaProjects\demosecc
mvn clean package
```

### Step 5: Check Logs
```
Should see:
✓ [PaymentGatewayConfigValidator] Payment Gateway Configuration Validation
  Stripe: ✓ CONFIGURED (or ✗ NOT CONFIGURED for mock)
  D17: ✓ CONFIGURED (or ✗ NOT CONFIGURED for mock)
```

---

## 🎨 FRONTEND IMPLEMENTATION STEPS (4-5 days)

### Day 1: Setup & Services

**Step 1: Install Dependencies**
```bash
npm install @stripe/stripe-js
npm install @stomp/stompjs sockjs-client
ng add @angular/material
```

**Step 2: Create WebSocket Service**
```bash
ng generate service services/websocket-payment
```
→ Use code from `PROMPT_FRONTEND_PAYMENT_INTEGRATION.md`

**Step 3: Create Payment Service**
```bash
ng generate service services/payment
```
→ Use code from prompt

**Step 4: Create Models**
```bash
ng generate interface models/payment
```
Models needed:
- `PaymentRequest`
- `PaymentResponse`
- `PaymentStatus` (enum)

---

### Day 2: Component Structure

**Step 5: Create Payment Component**
```bash
ng generate component components/payment/payment-checkout
```

**Step 6: Implement Component TypeScript**
→ Copy PaymentCheckoutComponent from prompt
→ Integrate Stripe.js initialization
→ Add form validation

**Step 7: Create Component Template**
→ Copy HTML from prompt
→ Add Stripe card element binding
→ Add error message display

**Step 8: Style Component**
```bash
# Create payment-checkout.component.css
# Style the payment form with:
# - Grid layout for payment methods
# - Form field styling
# - Loading states
# - Error states
```

---

### Day 3: WebSocket Integration

**Step 9: Initialize WebSocket in App**
```typescript
// In app.component.ts constructor:
constructor(private wsPaymentService: WebSocketPaymentService) {
  wsPaymentService.connect();  // Connect on app init
}
```

**Step 10: Subscribe to Events**
→ Components subscribe to:
  - `paymentCompleted$`
  - `paymentFailed$`
  - `paymentProcessing$`

**Step 11: Create Toast Component**
```bash
ng generate component components/notifications/payment-toast
```
→ Show notifications on WebSocket events

---

### Day 4: Routing & Integration

**Step 12: Add Route**
```typescript
// app-routing.module.ts
{
  path: 'patient/orders/:id/payment',
  component: PaymentCheckoutComponent,
  canActivate: [AuthGuard]
}
```

**Step 13: Add to NavBar**
```html
<!-- navbar.component.html -->
<button (click)="goToPayment(orderId)">
  Complete Payment
</button>
```

**Step 14: Update Order Page**
```typescript
// order-details.component.ts
goToPayment(): void {
  this.router.navigate(['/patient/orders', this.orderId, 'payment']);
}
```

---

### Day 5: Testing & Refinement

**Step 15: Test Stripe Tokens**
```
1. Start ng serve
2. Go to /patient/orders/1/payment
3. Enter test card: 4111 1111 1111 1111
4. See card element working
5. Click Pay
6. Check console for token generation
```

**Step 16: Test Backend Request**
```
1. Check Network tab in browser console
2. POST /api/pharmacy/payments should succeed
3. Response should have paymentId and status
```

**Step 17: Test WebSocket**
```
1. Open browser console
2. Look for: "Subscribed to /topic/payment/{patientId}/completed"
3. Backend logs should show WebSocket messages
4. Frontend should receive message
5. Toast should appear
```

**Step 18: Test Complete Flow**
```
1. Select payment method
2. Enter form data
3. Submit payment
4. See processing UI
5. Receive WebSocket completion
6. See success toast
7. Redirect works
```

---

## 🔑 CONFIGURATION CHECKLIST

### BACKEND (application.properties)

```properties
# Default (mock mode - works immediately)
stripe.api.key=sk_test_YOUR_KEY_HERE
d17.api.key=d17_test_YOUR_KEY_HERE

# OR Environment variables (production recommended)
stripe.api.key=${STRIPE_API_KEY}
d17.api.key=${D17_API_KEY}
```

### FRONTEND (environment.ts)

```typescript
export const environment = {
  apiUrl: 'http://localhost:8081/springsecurity',
  wsUrl: 'ws://localhost:8081/springsecurity/ws',
  stripe: {
    publishableKey: 'pk_test_YOUR_KEY_HERE'
  }
};
```

---

## 🧪 INTEGRATION TESTS

### Test 1: Mock Mode (No Stripe Account)
```
✓ Backend accepts payment request
✓ Returns mock transaction ID
✓ WebSocket sends mock completion
✓ Frontend shows success
✓ Order status updates to PAID
Timeline: ~2 seconds
```

### Test 2: Real Stripe (With Account)
```
✓ Stripe token generated
✓ Stripe API called
✓ Real transaction ID returned
✓ Webhook arrives from Stripe
✓ Payment confirms via webhook
✓ Order updates to PAID
✓ Pharmacy staff alerted
Timeline: ~5-30 seconds
```

### Test 3: Error Handling
```
✓ Invalid card → Error shown
✓ Failed payment → Webhook failure → Error shown
✓ Network timeout → Retry option
✓ Missing fields → Form validation
```

### Test 4: WebSocket
```
✓ Connection established on app load
✓ Payment subscription active
✓ Receives completion message
✓ Receives failure message
✓ Toast notification displays
✓ UI updates automatically
```

---

## 📊 SUCCESS CRITERIA

Before going live, verify:

**Backend:**
- [ ] `/api/pharmacy/payments` endpoint responds
- [ ] Stripe SDK initialized (if configured)
- [ ] WebSocket broadcasts payment events
- [ ] Webhooks verify signatures
- [ ] Logs show payment flow
- [ ] Mock mode works (no keys needed)
- [ ] Real payment works (with keys)

**Frontend:**
- [ ] Payment form renders
- [ ] Stripe card element loads
- [ ] Form validation works
- [ ] Submits to backend
- [ ] WebSocket connects
- [ ] Receives events
- [ ] Toast shows
- [ ] Redirects after success
- [ ] Handles errors gracefully

**Integration:**
- [ ] Email confirms payment
- [ ] Order status: PAID
- [ ] Pharmacy sees alert
- [ ] Invoice generated (if implemented)
- [ ] Refund flow ready

---

## 📞 SUPPORT & RESOURCES

### Backend Issues

| Issue | Solution |
|-------|----------|
| "Stripe not configured" | Add STRIPE_API_KEY env var or set in application.properties |
| Webhook not received | Check Stripe webhook endpoint in dashboard |
| Payment fails | Check backend logs for Stripe error |
| Stock checking before payment | Add inventory validation in order creation |

### Frontend Issues

| Issue | Solution |
|-------|----------|
| Card element not loading | Check Stripe publishable key in environment |
| WebSocket won't connect | Check wsUrl in environment matches backend |
| No WebSocket message | Check subscription topic matches patientId |
| Form won't submit | Check validation errors in console |

### Testing Resources

- **Stripe Test Cards:** https://stripe.com/docs/testing
- **STOMP Client Docs:** https://stomp-js.github.io/guide/stompjs/
- **Angular Reactive Forms:** https://angular.io/guide/reactive-forms

---

## 🚀 GO-LIVE CHECKLIST

### 1 Week Before Launch
- [ ] Finalize payment methods (Stripe/D17/Others?)
- [ ] Configure production Stripe keys
- [ ] Test complete workflow
- [ ] Setup monitoring/alerting
- [ ] Write refund policy
- [ ] Train support team

### Launch Day
- [ ] Deploy backend with real keys
- [ ] Deploy frontend with real endpoints
- [ ] Monitor payment success rate
- [ ] Have rollback plan ready
- [ ] Customer support on standby

### Post-Launch
- [ ] Monitor for 24 hours
- [ ] Check payment success rate (target: >95%)
- [ ] Review failed payment reasons
- [ ] Optimize based on feedback
- [ ] Plan improvements (receipts, history, etc.)

---

## 💡 OPTIMIZATION IDEAS (After MVP)

1. **Saved Payment Methods**
   ```
   Allow customers to save cards
   Quick checkout for future payments
   ```

2. **Payment Analytics**
   ```
   Success rate by method
   Average transaction time
   Failed payment reasons
   ```

3. **Invoice System**
   ```
   Generate PDF invoices
   Email receipts
   Payment history
   ```

4. **Pharmacy Dashboard**
   ```
   Real-time payment alerts
   Daily revenue summary
   Top products by payment method
   ```

5. **Multi-Currency**
   ```
   Support USD, EUR, etc.
   Auto currency conversion
   Display in local currency
   ```

---

## 📝 SUMMARY

**Backend Status:** ✅ READY (All code provided)
**Frontend Status:** ⏳ TO IMPLEMENT (Prompt provided)
**Integration Time:** 5-7 days for complete implementation
**Testing Time:** 2-3 days for full QA
**Go-Live:** Ready after integration + testing

**Next Steps:**
1. Backend team: Copy payment files, run `mvn clean package`
2. Frontend team: Use `PROMPT_FRONTEND_PAYMENT_INTEGRATION.md`
3. Both teams: Run integration tests
4. Deploy to production

---

**VOUS ÊTES PRÊTS À ACCEPTER DES VRAIS PAIEMENTS! 🎉**

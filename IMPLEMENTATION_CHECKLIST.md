# ✅ IMPLEMENTATION CHECKLIST - PAIEMENTS RÉELS & WEBSOCKET

## 📋 **QUICK START (5-10 minutes)**

### Phase 1: Code Integration (2 min)

- [ ] **1. Copy Payment Gateway Files**
  ```
  Copy to: src/main/java/com/aziz/demosec/payment/gateway/
  ├── PaymentGatewayProvider.java
  ├── PaymentGatewayResponse.java
  ├── PaymentGatewayFactory.java
  └── impl/
      ├── StripePaymentProvider.java
      └── D17PaymentProvider.java
  ```

- [ ] **2. Copy WebSocket Service**
  ```
  Copy to: src/main/java/com/aziz/demosec/payment/
  └── PaymentWebSocketService.java
  ```

- [ ] **3. Replace Payment Service**
  ```
  1. Backup old: PaymentServiceImpl.java → PaymentServiceImpl_OLD.java
  2. Copy new: PaymentServiceImpl_Enhanced.java → PaymentServiceImpl.java
  ```

- [ ] **4. Add Webhook Controller**
  ```
  Copy to: src/main/java/com/aziz/demosec/controller/
  └── PaymentWebhookController.java
  ```

- [ ] **5. Add Config Validator**
  ```
  Copy to: src/main/java/com/aziz/demosec/config/
  └── PaymentGatewayConfigValidator.java
  ```

### Phase 2: Database (if needed)

- [ ] **6. Add Gateway Metadata Column** (Optional but recommended)
  ```sql
  ALTER TABLE payments ADD COLUMN gateway_metadata TEXT;
  ALTER TABLE payments ADD COLUMN confirmed_at DATETIME;
  ```

- [ ] **7. Add Transaction ID Index**
  ```sql
  ALTER TABLE payments ADD UNIQUE INDEX idx_transaction_id (transaction_id);
  ```

### Phase 3: Repository Update

- [ ] **8. Update PaymentRepository**
  ```java
  // Add this method to PaymentRepository:
  Optional<Payment> findByTransactionId(String transactionId);
  Optional<Payment> findByPharmacyOrder_Id(Long orderId);  // If missing
  ```

### Phase 4: Build & Test

- [ ] **9. Rebuild Project**
  ```bash
  mvn clean package
  ```
  Verify NO errors

- [ ] **10. Check Startup Logs**
  ```
  Should see:
  ============================================================
  PAYMENT GATEWAY CONFIGURATION VALIDATION
  ============================================================
  Stripe: ✗ NOT CONFIGURED
  D17: ✗ NOT CONFIGURED
  (This is OK - means mock mode enabled)
  ```

---

## 🔑 **STRIPE SETUP (Optional - For Production)**

### Get Stripe Keys

- [ ] **11. Create Stripe Account**
  - Go to: https://dashboard.stripe.com/
  - Sign up (free)
  - Verify email

- [ ] **12. Get Test Keys**
  - Dashboard → Developers → API Keys
  - Copy "Secret Key" (starts with `sk_test_`)
  - Copy "Publishable Key" (starts with `pk_test_`)

- [ ] **13. Get Webhook Secret**
  - Dashboard → Developers → Webhooks
  - Add Endpoint: `http://localhost:8081/springsecurity/api/pharmacy/payments/webhook/stripe`
  - Select events:
    - ✓ payment_intent.succeeded
    - ✓ payment_intent.payment_failed
    - ✓ charge.refunded
  - Copy "Signing Secret" (starts with `whsec_`)

### Configure Environment

- [ ] **14. Set Environment Variables** (Windows PowerShell)
  ```powershell
  $env:STRIPE_API_KEY = "sk_test_YOUR_KEY_HERE"
  $env:STRIPE_PUBLIC_KEY = "pk_test_YOUR_KEY_HERE"
  $env:STRIPE_WEBHOOK_SECRET = "whsec_YOUR_SECRET_HERE"
  ```

  Or add to `application.properties`:
  ```properties
  stripe.api.key=sk_test_YOUR_KEY_HERE
  stripe.publishable.key=pk_test_YOUR_KEY_HERE
  stripe.webhook.secret=whsec_YOUR_SECRET_HERE
  ```

- [ ] **15. Test Stripe Integration**
  ```
  1. Start backend
  2. POST /api/pharmacy/payments
     {
       "orderId": 1,
       "method": "STRIPE",
       "token": "pm_card_visa",  // Test token
       "email": "test@example.com",
       "phone": "+21698765432"
     }
  3. Should see in logs:
     "Stripe payment initiated - Transaction: ..."
  4. Check logs for success or failure
  ```

---

## 🇹🇳 **D17 SETUP (Optional - For Tunisia)**

### Get D17 Credentials

- [ ] **16. Contact D17**
  - Website: https://d17.tn/
  - Request merchant account & test credentials
  - You will receive:
    - API Key (d17_test_...)
    - Merchant ID
    - Webhook Secret

- [ ] **17. Configure D17**
  ```powershell
  $env:D17_API_KEY = "d17_test_YOUR_KEY_HERE"
  $env:D17_MERCHANT_ID = "YOUR_MERCHANT_ID_HERE"
  $env:D17_WEBHOOK_SECRET = "whsec_d17_YOUR_SECRET_HERE"
  ```

- [ ] **18. Test D17 Integration**
  ```
  POST /api/pharmacy/payments
  {
    "orderId": 1,
    "method": "D17",
    "token": "wallet_123...",
    "email": "patient@example.com",
    "phone": "+21698765432"
  }
  ```

---

## 🧪 **TESTING CHECKLIST**

### Basic Flow

- [ ] **19. Test Payment Creation**
  ```
  POST /api/pharmacy/payments
  {
    "orderId": 1,
    "method": "STRIPE",
    "token": "pm_card_visa",
    "email": "test@example.com",
    "phone": "+216XXXXXXXX"
  }
  
  Expected Response:
  {
    "id": 1,
    "status": "COMPLETED",  // or PROCESSING
    "transactionId": "pi_1A1A1A1A...",
    "amount": 49.99
  }
  ```

- [ ] **20. Check Payment Status**
  ```
  GET /api/pharmacy/payments/order/1
  
  Expected: status should be COMPLETED
  ```

- [ ] **21. Verify Order Updated**
  ```
  GET /api/pharmacy/orders/1
  
  Expected: status should be "PAID"
  ```

- [ ] **22. Test WebSocket Notifications**
  ```
  Open browser console:
  stompClient.subscribe('/topic/payment/123/completed', msg => {
    console.log('Payment confirmed!', JSON.parse(msg.body));
  });
  
  Should receive notification when payment completes
  ```

### Error Cases

- [ ] **23. Test Invalid Token**
  ```
  POST with invalid token
  Expected: status = FAILED, error message in response
  ```

- [ ] **24. Test Missing Order**
  ```
  POST with orderId = 9999
  Expected: 404 error "Order not found"
  ```

- [ ] **25. Test Webhook Signature**
  ```
  Send webhook with wrong signature
  Expected: 403 Forbidden
  ```

### Pharmacy Dashboard

- [ ] **26. Test Pharmacy Sees New Payments**
  ```
  Pharmacy user subscribes to:
  /topic/pharmacy/{id}/payments
  
  When payment arrives, should see notification
  ```

- [ ] **27. Test Order Readiness Alert**
  ```
  When payment confirmed, check:
  /topic/pharmacy/{id}/orders/ready
  
  Should receive order ready notification
  ```

---

## 📱 **FRONTEND INTEGRATION**

### Update Angular Services

- [ ] **28. Update payment.service.ts**
  ```typescript
  initiatePayment(request: PaymentRequest): Observable<PaymentResponse> {
    return this.http.post<PaymentResponse>(
      '/api/pharmacy/payments',
      request
    );
  }
  
  // Subscribe to WebSocket updates
  subscribeToPaymentStatus(patientId: number) {
    this.stompClient.subscribe(
      `/topic/payment/${patientId}/completed`,
      msg => this.paymentSubject$.next(JSON.parse(msg.body))
    );
  }
  ```

- [ ] **29. Update Payment Component**
  ```typescript
  onPaymentSubmit() {
    // Show "Processing..." UI
    this.paymentService.initiatePayment(this.form.value)
      .subscribe({
        next: (response) => {
          // Webhook will update status
          // Just show "Verifying payment..."
        },
        error: (err) => {
          // Show error to user
        }
      });
  }
  ```

- [ ] **30. Add WebSocket Listeners**
  ```typescript
  ngOnInit() {
    // Listen for payment completion
    this.paymentService.paymentCompleted$
      .subscribe(payment => {
        this.showSuccess('Payment confirmed!');
        this.router.navigate(['/confirmation']);
      });
    
    // Listen for payment failure
    this.paymentService.paymentFailed$
      .subscribe(error => {
        this.showError('Payment failed: ' + error.reason);
      });
  }
  ```

---

## 🔒 **SECURITY CHECKLIST**

- [ ] **31. HTTPS Only** (Production)
  ```
  Enforce HTTPS; webhook URLs must be HTTPS
  ```

- [ ] **32. Verify Webhook Signatures**
  ```
  PaymentWebhookController already does this ✓
  Stripe signature verified: HMAC-SHA256
  ```

- [ ] **33. No Card Storage**
  ```
  ✓ Using Stripe/D17 tokens only
  ✓ Cards never stored in database
  ✓ Payment details never logged
  ```

- [ ] **34. Rate Limiting** (Optional)
  ```
  Consider adding rate limit on /api/pharmacy/payments
  Max 5 requests per minute per user
  ```

- [ ] **35. Monitor Webhooks**
  ```
  Setup alerts for:
  - Failed webhook deliveries
  - Unusual payment patterns
  - Multiple failures from same customer
  ```

---

## 📊 **MONITORING & OPERATIONS**

### Logs to Check

- [ ] **36. Check Payment Service Logs**
  ```
  Look for patterns:
  "Stripe payment initiated"
  "Payment completed"
  "Payment failed"
  ```

- [ ] **37. Monitor Webhook Failures**
  ```
  In application logs, search for:
  "Error processing Stripe webhook"
  "Error processing D17 webhook"
  
  If found, check:
  - Network connectivity
  - Webhook secrets are correct
  - API rate limits not exceeded
  ```

- [ ] **38. Database Audit**
  ```
  SELECT * FROM payments WHERE status = 'FAILED'
  ORDER BY created_at DESC;
  
  Review and follow up with customers
  ```

### Alerts to Setup

- [ ] **39. Payment Gateway Unavailable**
  - Alert when provider returns errors
  - Fallback to manual review

- [ ] **40. High Failure Rate**
  - Alert if >5% of payments fail in 1 hour
  - May indicate gateway issues

- [ ] **41. Webhook Delivery Issues**
  - Alert if webhooks not received for 30 sec
  - May need manual verification

---

## 🚀 **DEPLOYMENT CHECKLIST**

### Before Going Live

- [ ] **42. Final Code Review**
  ```
  Check:
  ✓ No hardcoded keys
  ✓ All error handling in place
  ✓ Logging for audit trail
  ✓ WebSocket properly configured
  ```

- [ ] **43. Load Testing**
  ```
  Simulate:
  - 100 concurrent payments
  - Network latency
  - Gateway timeouts
  Ensure system remains responsive
  ```

- [ ] **44. Webhook Testing**
  ```
  - Send test webhooks from Stripe
  - Verify they update payments
  - Check WebSocket notifications sent
  ```

- [ ] **45. Cutover Plan**
  ```
  When switching from mock to real:
  1. Backup database
  2. Enable real keys (env vars)
  3. Monitor logs closely
  4. Have rollback plan ready
  ```

### Production Configuration

- [ ] **46. Use Production Keys**
  ```
  Switch from sk_test_ → sk_live_
  In secure environment variables only
  NEVER in version control
  ```

- [ ] **47. Enable Webhooks Verification**
  ```
  ✓ Stripe signature check (already done)
  ✓ D17 signature check (included)
  ✓ Rate limiting enabled
  ✓ Webhook retries configured
  ```

- [ ] **48. Setup Monitoring**
  ```
  Monitor:
  - Payment success rate
  - Webhook latency
  - Gateway response times
  - Error rates by type
  ```

- [ ] **49. Document Escalation**
  ```
  Create runbook for:
  - Failed payment customer support
  - Gateway outage recovery
  - Webhook delivery issues
  - Refund process
  ```

---

## 📞 **SUPPORT CONTACTS**

- [ ] **50. Save Support Numbers**
  ```
  Stripe Support: https://stripe.com/support
  Stripe API: https://stripe.com/docs/api
  
  D17 Support: https://d17.tn/contact
  You can also contact via merchant dashboard
  ```

---

## ✨ **SUMMARY**

**Total Steps:** 50  
**Estimated Time:** 
- Installation: 5-10 min
- Setup (Stripe): 10-15 min
- Setup (D17): 10-15 min (if using)
- Testing: 15-20 min
- **Total: 40-60 min for full production-ready setup**

**Can go live with mock mode immediately** - no external keys needed!

---

## 🎯 **POST-IMPLEMENTATION**

Once everything is working:

1. ✅ Monitor payment processing
2. ✅ Check WebSocket notifications in production
3. ✅ Review webhook delivery success
4. ✅ Collect feedback from pharmacy staff
5. ✅ Improve based on real usage

**Questions?** Refer to:
- `PAYMENT_INTEGRATION_COMPLETE.md` - Full guide
- `PAYMENT_WEBSOCKET_ANSWERS.md` - Q&A
- Code comments in payment gateway files

**YOU'RE READY TO PROCESS REAL PAYMENTS! 🚀**

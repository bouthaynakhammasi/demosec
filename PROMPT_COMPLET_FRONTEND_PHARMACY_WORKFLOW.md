# 🏥 PROMPT COMPLET FRONTEND - PHARMACY ORDER MANAGEMENT WORKFLOW

## 📖 INTRODUCTION & CONTEXTE GLOBAL

Tu vas développer l'interface frontend **COMPLÈTE** (Angular) pour **Medicare AI** - une plateforme de gestion des services de pharmacie.

### Architecture du Système:
```
       Patient (User)
           |
           v
    [Frontend Angular]
           |
    +------+-------+
    |      |       |
    v      v       v
   Patient App | Pharmacist Dashboard | Admin Panel
    |      |       |
    +------v-------+
           |
    [Backend API Spring Boot]
    http://localhost:8081/springsecurity
           |
    +------+-------+------+
    |      |       |      |
    v      v       v      v
  MySQL  WebSocket PDF   Email
  DB      (Real-time)  Gen   Notif
```

### Les 4 Acteurs Principaux:
1. **PATIENT** - Commande médicaments, suit livraison
2. **PHARMACIST** - Valide ordonnances, gère stock
3. **DELIVERY_COMPANY** - Effectue la livraison (via WebSocket)
4. **ADMIN** - Supervise transactions et qualité de service

---

## 🔐 AUTHENTIFICATION & SÉCURITÉ

### JWT Configuration
```typescript
// Base URL
const BASE_URL = 'http://localhost:8081/springsecurity/api';
const WS_URL = 'http://localhost:8081/springsecurity/ws';

// Auth Header
headers: {
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json'
}
```

### Rôles & Accès

| Rôle | Routes Accessibles | Permissions |
|------|-------------------|-----------|
| USER | `/patient/**` | Créer commande, voir ses commandes, tracker livraison |
| PHARMACIST | `/pharmacist/**` | Valider commandes, gérer stock, voir stats |
| ADMIN | `/admin/**` | Dashboard global, statistiques, gestion utilisateurs |

### Test Credentials
```
Patient: (auto après registration)
Email: patient@example.com / patient123

Pharmacist:
Email: ahmed.hassan@pharmacy.com
Password: (same as registration)

Pharmacist 2:
Email: fatima.zahra@pharmacy.com
Password: (same as registration)

Admin:
Email: admin@pharmacy.com
Password: (same as registration)
```

---

## 🌊 WORKFLOW COMPLET (STEP BY STEP)

### PHASE 1️⃣: PATIENT CRÉE UNE COMMANDE

#### 1.1 Patient Upload Prescription (Image/PDF)
```
Frontend:
  POST /api/upload
  Body: FormData { 'file': File }
  
Response:
  {
    "prescriptionImageUrl": "/uploads/prescriptions/UUID_filename.png"
  }

Action: 
  - File input accepte: .jpg, .png, .pdf
  - Afficher preview image après upload
  - Stocker URL en variable locale avant submit commande
```

#### 1.2 Patient Sélectionne Pharmacie
```
GET /api/pharmacy/pharmacies
Response: [
  {
    "id": 1,
    "name": "Pharmacie Centrale Casablanca",
    "address": "123 Boulevard Mohammed V",
    "locationLat": 33.5731,
    "locationLng": -7.5898,
    "phoneNumber": "0522123456",
    "email": "central@pharmacy.com"
  }
]

Action:
  - Liste pharmacies avec map (Google Maps ou Leaflet)
  - Filtrer par ville/proximité
  - Clicker pour voir détails + stocks disponibles
```

#### 1.3 Vérifier Disponibilité des Médicaments
```
POST /api/pharmacy/orders/stock/search
Query Params:
  - productId: 1
  - minQty: 2 (minimum 2 unités demandées)

Response: [
  {
    "id": 1,
    "pharmacyId": 1,
    "pharmacyName": "Pharmacie Centrale",
    "productId": 1,
    "productName": "Paracétamol 500mg",
    "totalQuantity": 450,
    "unitPrice": 2.50,
    "minQuantityThreshold": 50,
    "status": "IN_STOCK"
  }
]

Action:
  - Afficher stocks disponibles
  - Alert si "OUT_OF_STOCK" ou quantité insuffisante
  - Suggérer alternatives (#Alternative Medicines)
```

#### 1.4 Patient Confirme Adresse Livraison
```
Form Inputs:
  - deliveryAddress: "100 Rue du Patient, Casablanca" (text)
  - scheduledDeliveryDate: "2026-03-25" (date picker)
  - deliveryType: "HOME_DELIVERY" | "PICKUP" (radio)

Action:
  - Validation adresse (non-vide, min 10 caractères)
  - Date >= aujourd'hui
  - Si HOME_DELIVERY: afficher estimation frais
```

#### 1.5 Patient Crée la Commande
```
POST /api/pharmacy/orders
Authorization: Bearer {token}
Content-Type: application/json

{
  "patientId": 3,
  "pharmacyId": 1,
  "prescriptionImageUrl": "/uploads/prescriptions/UUID_file.png",
  "deliveryAddress": "100 Rue du Patient, Casablanca",
  "deliveryType": "HOME_DELIVERY",
  "scheduledDeliveryDate": "2026-03-25",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}

Response: (201 CREATED)
{
  "id": 1,
  "patientId": 3,
  "pharmacyId": 1,
  "status": "PENDING",
  "totalPrice": 14.50,
  "items": [
    {
      "productId": 1,
      "productName": "Paracétamol 500mg",
      "quantity": 2,
      "price": 5.00
    }
  ],
  "createdAt": "2026-03-20T10:00:00"
}

Action:
  - Afficher confirmation "Commande créée avec succès #1"
  - Stocker orderId en localStorage/sessionStorage
  - Rediriger vers page tracking
  - Notification: "Votre commande est aux validations du pharmacien"
```

### PHASE 2️⃣: PHARMACIST VALIDE LA COMMANDE

#### 2.1 Pharmacist Voit Nouvelle Commande
```
Dashboard Notification:
  [🔔] Nouvelle commande #1 - Patient: "Ali Ahmed"
       Montant: 14.50 DT | Médicaments: 3

Action:
  - Click notification → ouvre détails commande
  - Ou aller à: /pharmacist/orders?status=PENDING
```

#### 2.2 Pharmacist Consulte Détails Commande
```
GET /api/pharmacy/orders/{orderId}
Authorization: Bearer {pharmaToken}

Response:
{
  "id": 1,
  "patientId": 3,
  "patientName": "Ali Ahmed",
  "patientPhone": "0612345678",
  "pharmacyId": 1,
  "status": "PENDING",
  "totalPrice": 14.50,
  "deliveryAddress": "100 Rue du Patient, Casablanca",
  "deliveryType": "HOME_DELIVERY",
  "scheduledDeliveryDate": "2026-03-25",
  "prescriptionImageUrl": "/uploads/prescriptions/UUID_file.png",
  "items": [
    {
      "productId": 1,
      "productName": "Paracétamol 500mg",
      "quantity": 2,
      "price": 5.00,
      "dosage": "500mg",
      "duration": "5 jours"
    }
  ],
  "createdAt": "2026-03-20T10:00:00"
}

Action:
  - Afficher image ordonnance en overlay
  - Vérifier disponibilité stock (déjà fait à la création)
  - Lire dosages et durées recommandées
```

#### 2.3 Pharmacist Valide Commande
```
PATCH /api/pharmacy/orders/{orderId}/status
Authorization: Bearer {pharmaToken}
Content-Type: application/json

{
  "status": "VALIDATED",
  "note": "Commande vérifiée ✓ Préparer la livraison",
  "changedBy": "PHARMACIST",
  "deliveryType": "HOME_DELIVERY"
}

Response: (200 OK)
{
  "id": 1,
  "status": "VALIDATED",
  "pharmacistNote": "Commande vérifiée ✓ Préparer la livraison",
  "updatedAt": "2026-03-20T10:30:00"
}

Backend Behavior:
  ✅ Stock décrémenté (ex: Paracétamol: 450 → 448)
  ✅ OrderTracking entry créée
  ✅ Patient notifié: "Votre commande est validée ✓ Choisissez mode réception"

Action Frontend:
  - Afficher toast success "Commande validée"
  - Mettre à jour UI: status badge → VALIDATED
  - Bouton action change: "Rejeter" → "Marquer prête"
```

#### 2.4 Alternative Scenario: Pharmacist Rejette Commande
```
PATCH /api/pharmacy/orders/{orderId}/reject
Authorization: Bearer {pharmaToken}
Content-Type: application/json

{
  "reason": "Stock insuffisant pour Paracétamol 500mg. Nous avons seulement 1 unité.",
  "changedBy": "PHARMACIST"
}

Response: (200 OK)
{
  "id": 1,
  "status": "REJECTED",
  "updatedAt": "2026-03-20T10:30:00"
}

Backend Behavior:
  ✅ Status → REJECTED
  ✅ Patient notifié avec raison du rejet
  ✅ Stock REST INTACT

Action Frontend:
  - Afficher toast warning "Commande rejetée"
  - Afficher raison rejet au pharmacist dans snapshot
  - Patient reçoit notification avec alternative medicines
```

### PHASE 3️⃣: PATIENT CONFIRME PAIEMENT & SÉLECTIONNE LIVRAISON

#### 3.1 Patient Reçoit Notification "Commande Validée"
```
NotificationData:
{
  "id": 1,
  "recipientId": 3,
  "type": "DELIVERY_CHOICE_REQUIRED",
  "title": "Commande validée ✅",
  "message": "Votre commande #1 est validée. Choisissez votre mode de réception.",
  "orderId": 1,
  "createdAt": "2026-03-20T10:30:00"
}

Frontend:
  - Afficher notification push (via WebSocket ou polling)
  - Click → rediriger vers /patient/orders/{orderId}
```

#### 3.2 Patient Sélectionne Mode Livraison
```
Page: /patient/orders/{orderId}

Options:
  1️⃣ HOME_DELIVERY (Livraison à domicile)
     - Cost: +2.50 DT
     - Delivery in: 1-2 jours
     
  2️⃣ PICKUP (Venir chercher à la pharmacie)
     - Cost: 0 DT
     - Pickup today/tomorrow

Action:
  - Cliquer sur option → sauvegarder choix
  - Mettre à jour via PATCH /api/pharmacy/orders/{orderId}/status
```

#### 3.3 Patient Paie la Commande
```
POST /api/payment/process
Body:
{
  "orderId": 1,
  "amount": 14.50,
  "paymentMethod": "CARD" | "PAYPAL" | "WALLET",
  "currency": "TND"
}

Response (Simulated):
{
  "status": "SUCCESS",
  "transactionId": "TXN-2026-03-20-001",
  "orderId": 1
}

Backend Behavior:
  ✅ Order status → PAID
  ✅ Payment record créée
  ✅ Pharmacist notifié: "Paiement reçu 💰"
  ✅ Delivery assigné (si HOME_DELIVERY)
```

### PHASE 4️⃣: COMMANDE EST PRÊTE & LIVRAISON COMMENCE

#### 4.1 Pharmacist Marque Commande "Prête"
```
PATCH /api/pharmacy/orders/{orderId}/status
Body:
{
  "status": "READY_FOR_PICKUP",
  "note": "Commande préparée et prête pour livraison",
  "changedBy": "PHARMACIST"
}

Backend Behavior (si HOME_DELIVERY):
  ✅ DeliveryGateway.createDelivery() appelé
  ✅ Livraison externa assignée
  ✅ Tracking ID généré: "DLVR-TN-20260320-001"
  ✅ Patient notifié avec URL tracking
```

#### 4.2 Système Lance Livraison Externe
```
Interface: DeliveryGateway

Request au TunisianDeliveryGateway:
{
  "pickupAddress": "123 Boulevard Mohammed V, Casablanca",
  "dropoffAddress": "100 Rue du Patient, Casablanca",
  "packageDescription": "Médicaments (Paracétamol 500mg x2, Amoxicilline 500mg x1)",
  "externalOrderRef": 1,
  "pickupLat": 33.5731,
  "pickupLng": -7.5898,
  "dropoffLat": 33.5820,
  "dropoffLng": -7.5900
}

Response:
{
  "trackingId": "DLVR-TN-20260320-001",
  "trackingUrl": "https://tunisiandelivery.tn/track/DLVR-TN-20260320-001",
  "estimatedArrival": "2026-03-21T14:30:00",
  "agencyName": "Fast Delivery TN"
}

Backend Behavior:
  ✅ Delivery record créée avec trackingId
  ✅ OrderTracking entry: "SHIPPED" + "Livraison assignée"
  ✅ Patient notifié: "Votre commande est en cours de livraison"
```

#### 4.3 Patient Suit la Livraison en Temps Réel (WebSocket)

**WebSocket Subscription:**
```typescript
// Frontend Angular
this.stompClient.subscribe(
  `/topic/delivery/${orderId}`,
  (message) => {
    const deliveryUpdate = JSON.parse(message.body);
    // {
    //   trackingId: "DLVR-TN-20260320-001",
    //   latitude: 33.5745,
    //   longitude: -7.5891,
    //   status: "IN_TRANSIT",
    //   eta: "2026-03-21T14:30:00",
    //   estimatedMinutes: 25
    // }
    this.updateMap(deliveryUpdate);
  }
);
```

**Map Real-Time Display:**
- 🚚 Pin livreur sur carte (GPS)
- ⏱️ ETA dynamique (calculée via Haversine)
- 📍 Route depuis pharmacie vers adresse patient
- 💬 Chat avec livreur (optionnel)

#### 4.4 Livreur Met à Jour Status (Webhook Mock)

**Backend reçoit updates via:**
```
POST /api/delivery/webhook
Body:
{
  "trackingId": "DLVR-TN-20260320-001",
  "latitude": 33.5745,
  "longitude": -7.5891,
  "status": "IN_TRANSIT"
}

Backend Behavior:
  ✅ DeliveryService.updateLocation() appelé
  ✅ OrderTracking update créé
  ✅ WebSocket broadcast à /topic/delivery/{orderId}
```

**Frontend reçoit update et:**
- 🟢 Update pin sur carte
- ⏱️ Recalculer ETA (Haversine)
- 🔔 Toast notification
- 📲 Timeline: "Livreur en route... (25 min)"

---

## 📡 ENDPOINTS COMPLÈTE - RÉFÉRENCE RAPIDE

### 📋 ORDERS (Patient & Pharmacist)

| Opération | Méthode | Endpoint | Acteur | Req | Resp |
|-----------|---------|----------|--------|-----|------|
| Créer commande | POST | `/pharmacy/orders` | PATIENT | OrderCreateDTO | OrderResponseDTO |
| Liste commandes Patient | GET | `/pharmacy/orders/patient/{id}` | PATIENT | - | List[OrderDTO] |
| Liste commandes Pharmacie | GET | `/pharmacy/orders/pharmacy/{id}` | PHARMACIST | - | List[OrderDTO] |
| Filtrer par statut | GET | `/pharmacy/orders/pharmacy/{id}/filter?status=X` | PHARMACIST | - | List[OrderDTO] |
| Détails commande | GET | `/pharmacy/orders/{id}` | PATIENT/PHARMA | - | OrderDetailDTO |
| Mettre à jour statut | PATCH | `/pharmacy/orders/{id}/status` | PHARMACIST | UpdateStatusDTO | OrderDTO |
| Rejeter commande | PATCH | `/pharmacy/orders/{id}/reject` | PHARMACIST | RejectDTO | OrderDTO |
| Annuler commande | PATCH | `/pharmacy/orders/{id}/cancel` | PATIENT | CancelDTO | OrderDTO |
| Suivi & tracking | GET | `/pharmacy/orders/{id}/tracking` | PATIENT/PHARMA | - | List[TrackingDTO] |
| Facture PDF | GET | `/pharmacy/orders/{id}/invoice` | PATIENT/PHARMA | - | PDF (blob) |
| Statistiques | GET | `/pharmacy/orders/pharmacy/{id}/stats` | PHARMACIST | - | StatsDTO |

### 📦 STOCKS

| Opération | Méthode | Endpoint | Acteur | Req | Resp |
|-----------|---------|----------|--------|-----|------|
| Tous stocks | GET | `/pharmacy/stocks/pharmacy/{id}` | PHARMACIST | - | List[StockDTO] |
| Détails stock | GET | `/pharmacy/stocks/{id}` | PHARMACIST | - | StockDTO |
| Ajouter stock | POST | `/pharmacy/stocks` | PHARMACIST | StockCreateDTO | StockDTO |
| Modifier stock | PUT | `/pharmacy/stocks/{id}` | PHARMACIST | StockUpdateDTO | StockDTO |
| Supprimer stock | DELETE | `/pharmacy/stocks/{id}` | PHARMACIST | - | 204 |
| Disponibilité produit | GET | `/pharmacy/stocks/availability/{id}` | PATIENT | - | List[StockDTO] |
| Rechercher par product+qty | POST | `/pharmacy/orders/stock/search?productId=X&minQty=Y` | PATIENT | - | List[AvailabilityDTO] |

### 🏪 PHARMACIES

| Opération | Méthode | Endpoint | Acteur | Req | Resp |
|-----------|---------|----------|--------|-----|------|
| Liste toutes | GET | `/pharmacy/pharmacies` | PUBLIC | - | List[PharmacyDTO] |
| Détails | GET | `/pharmacy/pharmacies/{id}` | PUBLIC | - | PharmacyDTO |
| Rechercher | GET | `/pharmacy/pharmacies/search?name=X` | PUBLIC | - | List[PharmacyDTO] |

### 📁 UPLOAD & FILES

| Opération | Méthode | Endpoint | Acteur | Req | Resp |
|-----------|---------|----------|--------|-----|------|
| Upload prescription | POST | `/upload` | PATIENT | FormData(file) | { prescriptionImageUrl } |

### 🚚 DELIVERY

| Opération | Méthode | Endpoint | Acteur | Req | Resp |
|-----------|---------|----------|--------|-----|------|
| Delivery info | GET | `/delivery/{orderId}` | PATIENT/PHARMA | - | DeliveryDTO |
| Webhook update | POST | `/delivery/webhook` | EXTERNAL | WebhookPayload | OK |
| Mock update | POST | `/delivery/mock-update` | DEV | MockPayload | DeliveryDTO |

### 💳 PAYMENT

| Opération | Méthode | Endpoint | Acteur | Req | Resp |
|-----------|---------|----------|--------|-----|------|
| Traiter paiement | POST | `/payment/process` | PATIENT | PaymentDTO | PaymentResultDTO |

### 🔔 NOTIFICATIONS (WebSocket + REST)

**WebSocket Subscription (STOMP):**
```
/topic/delivery/{orderId}       → Delivery GPS updates
/topic/orders/{patientId}       → Order status changes
/topic/pharmacist/{pharmacyId}  → New orders for pharmacist
```

**REST Endpoints:**
```
GET  /api/notifications          → Toutes notifications patient
POST /api/notifications/{id}/read → Marquer lue
```

---

## 🎨 INTERFACES UTILISATEUR À DÉVELOPPER

### 👤 PATIENT MODULE

#### Page 1: Home Dashboard (après login)
```
URL: /patient/dashboard

Layout:
┌─────────────────────────────────────┐
│ 🏥 Medicare AI - Mon Espace Patient  │
├─────────────────────────────────────┤
│                                     │
│ 📊 Statistiques Rapides             │
│ ┌─────────┬─────────┬────────────┐ │
│ │ Commandes│ En route│ Livrées   │ │
│ │    8    │    1    │     7      │ │
│ └─────────┴─────────┴────────────┘ │
│                                     │
│ 🎯 Actions Rapides                  │
│ [+ Nouvelle Commande] [📦 Tracking] │
│                                     │
│ 📋 Dernières Commandes              │
│  ┌─────┬────────┬───────┬────────┐ │
│  │ ID  │ Montant│ Statut│ Actions│ │
│  ├─────┼────────┼───────┼────────┤ │
│  │ #1  │ 14.50  │PAID   │ Voir   │ │
│  │ #2  │ 25.00  │SHIPPED│ Tracker│ │
│  │ #3  │ 8.75   │PENDING│ Annuler│ │
│  └─────┴────────┴───────┴────────┘ │
│                                     │
└─────────────────────────────────────┘
```

**Éléments:**
- Statistiques: total commandes, montant dépensé, livrées
- Boutons d'action: Créer commande, voir toutes, notifications
- Tableau commandes récentes avec filtres rapides
- Badge notifications en haut

---

#### Page 2: Créer Nouvelle Commande (Formulaire Multi-étapes)
```
URL: /patient/orders/new
Wizard: Step 1/4 → Step 4/4

STEP 1️⃣: UPLOAD PRESCRIPTION
┌──────────────────────────────┐
│ 📄 Télécharger Ordonnance    │
├──────────────────────────────┤
│                              │
│ 📸 [Cliquer pour uploader]  │
│    ou drag & drop           │
│                              │
│ Formats: .jpg, .png, .pdf   │
│ Max: 10MB                   │
│                              │
│ Preview: [Image affichée]   │
│                              │
│ [Retour] [Suivant →]        │
└──────────────────────────────┘

Action:
- File input avec image preview
- POST /api/upload on change
- Store prescriptionImageUrl
```

**STEP 2️⃣: SÉLECTIONNER PHARMACIE**
```
┌──────────────────────────────┐
│ 🏪 Choisir une Pharmacie     │
├──────────────────────────────┤
│                              │
│ 🔍 Rechercher: [________]   │
│    Ville:     [Dropdown]    │
│                              │
│ Résultats:                   │
│ ┌──────────────────────────┐│
│ │ Pharmacie Centrale       ││
│ │ 📍 Casablanca            ││
│ │ 📞 0522123456            ││
│ │ ⭐ 4.8 (120 avis)         ││
│ │ [Consulter Stocks]       ││
│ └──────────────────────────┘│
│                              │
│ ┌──────────────────────────┐│
│ │ Pharmacie Santé Plus     ││
│ │ 📍 Fez - 50km            ││
│ │ 📞 0535234567            ││
│ │ ⭐ 4.5 (95 avis)          ││
│ │ [Consulter Stocks]       ││
│ └──────────────────────────┘│
│                              │
│ [Retour] [Suivant →]        │
└──────────────────────────────┘

Action:
- GET /api/pharmacy/pharmacies & afficher liste
- Filtrer par ville (dropdown avec villes depuis DB)
- Map optionnel avec pharmacies
- Click "Consulter Stocks" → Modal avec products
```

**STEP 3️⃣: VÉRIFIER STOCKS & SÉLECTIONNER PRODUITS**
```
┌──────────────────────────────┐
│ 💊 Vérifier Disponibilité    │
│    (Pharmacie: Centrale)     │
├──────────────────────────────┤
│                              │
│ Produits Détectés Ordonnance:│
│ ┌──────────────────────────┐│
│ │ ☑ Paracétamol 500mg      ││
│ │   💾 En stock (450 unités)││
│ │   Quantité: [2▼]         ││
│ │   Prix unit: 2.50 TND    ││
│ │   Sous-total: 5.00 TND   ││
│ │   Dosage: 500mg          ││
│ │   Durée: 5 jours         ││
│ └──────────────────────────┘│
│                              │
│ ┌──────────────────────────┐│
│ │ ☑ Amoxicilline 500mg     ││
│ │   🟡 Stock bas (15 unités) ││
│ │   Quantité: [1▼]         ││
│ │   Prix unit: 3.75 TND    ││
│ │   Sous-total: 3.75 TND   ││
│ │ [Voir alternatives ↓]    ││
│ └──────────────────────────┘│
│                              │
│ 💰 Total Commande: 14.50 TND│
│                              │
│ [Retour] [Suivant →]        │
└──────────────────────────────┘

Action:
- POST /api/pharmacy/orders/stock/search pour chaque produit
- Afficher disponibilité par couleur (🟢 EN STOCK | 🟡 BAS | 🔴 RUPTURE)
- Si OUT_OF_STOCK: [Voir alternatives] → afficher autres pharmacies avec le produit
- Input quantité avec validation (min 1, max totalQuantity)
- Calcul automatique sous-total et total
```

**STEP 4️⃣: ADRESSE LIVRAISON & CONFIRMATION**
```
┌──────────────────────────────┐
│ 📍 Adresse & Mode Livraison  │
├──────────────────────────────┤
│                              │
│ Adresse Livraison:           │
│ [________________________________]
│  (min 10 caractères)         │
│                              │
│ Date Prévue Livraison:       │
│ [_____________]              │
│  (min date = aujourd'hui)     │
│                              │
│ Mode Livraison:              │
│ ○ 🏠 À domicile    (+2.50 DT)│
│       Livraison 1-2 jours   │
│ ○ 📦 À la pharmacie (0 DT)   │
│       Disponible aujourd'hui │
│                              │
│ RÉCAPITULATIF:               │
│ ┌──────────────────────────┐│
│ │ Paracétamol x2: 5.00 DT  ││
│ │ Amoxicilline x1: 3.75 DT ││
│ │ Frais livraison: 2.50 DT ││
│ │ ─────────────────────────││
│ │ TOTAL: 14.50 TND         ││
│ └──────────────────────────┘│
│                              │
│ [Retour] [Créer Commande]   │
└──────────────────────────────┘

Action:
- Validation formulaire avant submit
- POST /api/pharmacy/orders avec toutes données
- Afficher toast success et redirect
```

---

#### Page 3: Suivi Commande (Tracking Temps Réel)
```
URL: /patient/orders/{id}/tracking

┌─────────────────────────────────────┐
│ 📦 Suivi Commande #1                │
├─────────────────────────────────────┤
│                                     │
│ Status Badge: [SHIPPED 🚚]          │
│ Commande: Paracétamol x2 + Amoxicil│
│ Montant: 14.50 TND                  │
│                                     │
│ 🗺️  CARTE TEMPO RÉEL               │
│ ┌───────────────────────────────┐  │
│ │                               │  │
│ │  +   -                        │  │
│ │  Pharmacie Centrale           │  │
│ │  🚚 (GPS en route)            │  │
│ │  📍 Adresse Patient           │  │
│ │                               │  │
│ └───────────────────────────────┘  │
│ ⏱️  ETA: 25 minutes                │
│ 📍 Localisation: Rue Mohammed V    │
│                                     │
│ 📅 TIMELINE D'ÉVÉNEMENTS           │
│                                     │
│ ✅ 10:00 - Commande créée          │
│    Par: Patient Ali Ahmed           │
│                                     │
│ ✅ 10:30 - Commande validée        │
│    Par: Pharmacist Ahmed            │
│    Note: "Préparer pour livraison" │
│                                     │
│ ✅ 11:00 - Paiement confirmé       │
│    Montant: 14.50 TND              │
│                                     │
│ ⏳ 12:00 - En cours de livraison   │
│    Tracking: DLVR-TN-20260320-001 │
│    Via: Fast Delivery TN            │
│                                     │
│ ⭕ --:-- - Livré (en attente)      │
│    ETA: 14:30                       │
│                                     │
│ [📥 Télécharger Facture]            │
│                                     │
└─────────────────────────────────────┘

Action:
- GET /api/pharmacy/orders/{id}/tracking → Timeline
- WebSocket /topic/delivery/{orderId} → Real-time map updates
- Haversine calcul ETA automatique
- GET map data (latitude, longitude) et afficher Leaflet carte
- Download facture: GET /api/pharmacy/orders/{id}/invoice
```

---

#### Page 4: Historique Commandes
```
URL: /patient/orders

┌──────────────────────────────────────┐
│ 📋 Mes Commandes                     │
├──────────────────────────────────────┤
│                                      │
│ Filtres:                             │
│ [All] [PENDING] [PAID] [SHIPPED]    │
│ [DELIVERED] [REJECTED] [CANCELLED]  │
│                                      │
│ Recherche: [_________________]       │
│                                      │
│ Tableau:                             │
│ ┌───┬──────────┬────────┬──────────┬─────────┐
│ │ID │ Montant  │Status  │Date      │Actions  │
│ ├───┼──────────┼────────┼──────────┼─────────┤
│ │#1 │ 14.50 DT │SHIPPED │20/03/2026│[Tracker]│
│ │#2 │ 25.00 DT │PAID    │18/03/2026│[Détails]│
│ │#3 │  8.75 DT │PENDING │17/03/2026│[Annuler]│
│ │#4 │ 12.30 DT │DELIVERED│15/03/2026│[Facture]│
│ └───┴──────────┴────────┴──────────┴─────────┘
│                                      │
└──────────────────────────────────────┘

Action:
- GET /api/pharmacy/orders/patient/{patientId}
- Filtrer côté frontend avec dropdown
- Pagination 10 commandes/page
- Click ID → goto détails
- Click Tracker → goto tracking page
```

---

### 💊 PHARMACIST MODULE

#### Page 1: Dashboard Pharmacien
```
URL: /pharmacist/dashboard

┌─────────────────────────────────────┐
│ 👨‍⚕️ Dashboard Pharmacien - Ahmed Hassan│
├─────────────────────────────────────┤
│                                     │
│ 🔴 [5] Commandes en attente        │
│     Cliquer pour voir → /orders    │
│                                     │
│ 📊 STATISTIQUES CE MOIS             │
│ ┌──────┬──────┬──────┬──────────┐ │
│ │Total │Payées│Prêtes│Rejetées  │ │
│ │ 45   │  30  │  8   │    2      │ │
│ └──────┴──────┴──────┴──────────┘ │
│                                     │
│ 💰 CHIFFRE D'AFFAIRES               │
│ ┌────────────────────────────────┐ │
│ │ 1,250.75 TND (ce mois)         │ │
│ │ 850.50 TND (mois dernier)      │ │
│ │ Croissance: +47% 📈            │ │
│ └────────────────────────────────┘ │
│                                     │
│ ⚠️  ALERTES STOCK                   │
│ 🟡 Paracétamol 500mg: 35 unités   │
│    (Seuil min: 50)                 │
│ 🔴 Vitamine C 1000mg: 0 unités    │
│    (Rupture de stock)              │
│                                     │
│ [Gérer Stocks →]                   │
│                                     │
│ 🎯 TOP 5 PRODUITS VENDUS           │
│ 1. Paracétamol 500mg - 120 unités │
│ 2. Amoxicilline 500mg - 87 unités │
│ 3. Vitamine C 1000mg - 65 unités  │
│ 4. Losartan 50mg - 42 unités      │
│ 5. Atorvastatine 20mg - 38 unités │
│                                     │
└─────────────────────────────────────┘

Action:
- GET /api/pharmacy/orders/pharmacy/{pharmacyId}/stats
- GET /api/pharmacy/stocks/pharmacy/{pharmacyId} pour alertes
- Update stats toutes les 10 secondes
- Click badge [5] → redirect /pharmacist/orders?status=PENDING
```

---

#### Page 2: Gestion Commandes
```
URL: /pharmacist/orders

┌──────────────────────────────────────┐
│ 📦 Gestion Commandes Pharmacie #1   │
├──────────────────────────────────────┤
│                                      │
│ Filtres:                             │
│ [All] [PENDING⚠️] [VALIDATED✓]      │
│ [PAID💰] [READY_FOR_PICKUP🎁]       │
│ [REJECTED❌] [CANCELLED]             │
│                                      │
│ Tableau:                             │
│ ┌───┬─────────────┬────────┬────────────┬──────────┐
│ │ID │ Patient     │ Montant│ Status     │ Actions  │
│ ├───┼─────────────┼────────┼────────────┼──────────┤
│ │#1 │ Ali Ahmed   │14.50 DT│PENDING ⚠️ │ [Voir]   │
│ │#2 │ Fatima M.   │25.00 DT│VALIDATED✓ │ [Prête]  │
│ │#3 │ Mohamed A.  │8.75 DT │PAID💰     │ [Ready]  │
│ │#4 │ Aïcha K.    │12.30 DT│READY🎁    │ [Tracker]│
│ │#5 │ Salem H.    │18.50 DT│REJECTED❌  │ [Détails]│
│ └───┴─────────────┴────────┴────────────┴──────────┘
│                                      │
│ [Exporter CSV]                       │
│                                      │
└──────────────────────────────────────┘

Action:
- GET /api/pharmacy/orders/pharmacy/{pharmacyId}
- Filter par status & sort
- Pagination 15 par page
- Click ID/Patient → goto détails commande
```

---

#### Page 3: Détails Commande (Modal/Page)
```
URL: /pharmacist/orders/{id} ou Modal overlay

┌────────────────────────────────────────┐
│ 📋 DÉTAILS COMMANDE #1                 │
├────────────────────────────────────────┤
│                                        │
│ SECTION 1: INFO COMMANDE               │
│ Status: [PENDING ⚠️] (Button dropdown)│
│ ID: #1                                 │
│ Date: 20/03/2026 10:00                │
│ Patient: Ali Ahmed (0612345678)       │
│                                        │
│ Adresse Livraison:                     │
│ 100 Rue du Patient, Casablanca         │
│ Mode Livraison: 🏠 À DOMICILE          │
│ Date Prévue: 25/03/2026                │
│                                        │
│ SECTION 2: ORDONNANCE (IMAGE/PDF)      │
│ 📄 [Voir Ordonnance] [Télécharger]     │
│ Preview: [Image 200x300px affichée]   │
│                                        │
│ SECTION 3: ARTICLES COMMANDÉS          │
│ ┌──────────────────────────────────┐  │
│ │ Paracétamol 500mg               │  │
│ │   Quantité: 2 | Prix: 2.50 DT   │  │
│ │   Dosage: 500mg | Durée: 5j     │  │
│ │   Sous-total: 5.00 DT           │  │
│ └──────────────────────────────────┘  │
│                                        │
│ ┌──────────────────────────────────┐  │
│ │ Amoxicilline 500mg               │  │
│ │   Quantité: 1 | Prix: 3.75 DT    │  │
│ │   Dosage: 500mg | Durée: 7j     │  │
│ │   Sous-total: 3.75 DT            │  │
│ └──────────────────────────────────┘  │
│                                        │
│ MONTANT TOTAL: 14.50 TND               │
│                                        │
│ SECTION 4: NOTE PHARMACIEN             │
│ [Ajouter/Éditer Note]                 │
│ [_________________________________]   │
│  Message: "Prendre avec de la nourriture"
│                                        │
│ SECTION 5: ACTIONS                     │
│ ┌───────────────┬──────────────────┐  │
│ │ [Valider ✓]   │ [Rejeter ❌]    │  │
│ │               │                  │  │
│ │ [Marquer Prête] [Annuler Cmd]    │  │
│ └───────────────┴──────────────────┘  │
│                                        │
│ SECTION 6: HISTORIQUE (Timeline)       │
│ ✅ 10:00 - Commande créée              │
│    Patient Ali Ahmed                   │
│ ⏳ En attente de validation             │
│                                        │
└────────────────────────────────────────┘

Action (Si PENDING):
  1. Valider: PATCH /api/pharmacy/orders/{id}/status + "VALIDATED"
  2. Rejeter: Modal "Raison rejet?" → PATCH /reject
  3. Ajouter Note: PATCH /status avec "note"

Action (Si VALIDATED):
  4. Marquer Prête: PATCH /status + "READY_FOR_PICKUP"
     → Livraison lancée automatiquement si HOME_DELIVERY
```

**Modal Valider Commande:**
```
┌──────────────────────────────┐
│ ✅ Valider Commande #1        │
├──────────────────────────────┤
│                              │
│ Commande:                    │
│ • Paracétamol x2             │
│ • Amoxicilline x1            │
│ Montant: 14.50 TND           │
│                              │
│ ✅ Stock vérifié (OK)        │
│                              │
│ Note (optionnel):            │
│ [_____________________]      │
│                              │
│ Mode Livraison:              │
│ [Garder: HOME_DELIVERY ▼]   │
│                              │
│ [Annuler] [VALIDER]         │
└──────────────────────────────┘
```

**Modal Rejeter Commande:**
```
┌──────────────────────────────┐
│ ❌ Rejeter Commande #1        │
├──────────────────────────────┤
│                              │
│ Raison du rejet (requis):    │
│ [_____________________]      │
│ Min 10 caractères            │
│                              │
│ Exemples:                    │
│ • Stock insuffisant          │
│ • Ordonnance invalide        │
│ • Produit non disponible     │
│                              │
│ [Annuler] [REJETER]         │
└──────────────────────────────┘

Action:
- PATCH /api/pharmacy/orders/{id}/reject
- Stock RESTE INTACT (pas décrémenté)
- Patient reçoit notification + raison
```

---

#### Page 4: Gestion Stocks
```
URL: /pharmacist/stocks

┌──────────────────────────────────────┐
│ 📦 Gestion Stocks Pharmacie #1       │
├──────────────────────────────────────┤
│                                      │
│ [+ Ajouter Produit] [📊 Rapport]    │
│                                      │
│ Recherche: [____________]  [Filter ▼]
│                                      │
│ Tableau:                             │
│ ┌─────────┬──────┬──────┬────────┐  │
│ │ Produit │ Qty  │ Seuil│Actions │  │
│ ├─────────┼──────┼──────┼────────┤  │
│ │Paracétol│ 450  │  50  │ [Edit] │  │
│ │ 500mg   │ 🟢   │      │[Delete]│  │
│ ├─────────┼──────┼──────┼────────┤  │
│ │Amoxicil │ 200  │  30  │ [Edit] │  │
│ │ 500mg   │ 🟢   │      │[Delete]│  │
│ ├─────────┼──────┼──────┼────────┤  │
│ │Vitamine │  35  │  50  │ [Edit] │  │
│ │ C 1000mg│ 🟡   │      │[Delete]│  │
│ ├─────────┼──────┼──────┼────────┤  │
│ │Losartan │   0  │  40  │ [Edit] │  │
│ │  50mg   │ 🔴   │      │[Delete]│  │
│ └─────────┴──────┴──────┴────────┘  │
│                                      │
│ 🟢 En stock   🟡 Stock bas  🔴 Rupture│
│                                      │

Action:
- GET /api/pharmacy/stocks/pharmacy/{pharmacyId}
- Color code: 🟢 qty > threshold | 🟡 0 < qty ≤ threshold | 🔴 qty = 0
- Click [Edit] → Modal modifier
- Click [Delete] → Confirmation → DELETE /api/pharmacy/stocks/{id}
- Button [+ Ajouter] → Modal créer
```

**Modal Ajouter/Modifier Stock:**
```
┌────────────────────────────┐
│ ➕ Ajouter Produit         │
├────────────────────────────┤
│                            │
│ Produit (dropdown):        │
│ [Sélectionner ▼] (search) │
│  └ Paracétamol 500mg      │
│  └ Amoxicilline 500mg     │
│  └ Vitamine C 1000mg      │
│                            │
│ Quantité:                  │
│ [_______] (max 9999)       │
│                            │
│ Prix Unitaire (TND):       │
│ [_______] (ex: 2.50)       │
│                            │
│ Seuil Minimum:             │
│ [_______] (alerte stock)   │
│                            │
│ [Annuler] [ENREGISTRER]    │
└────────────────────────────┘

Action (Créer):
- POST /api/pharmacy/stocks
- Body: { pharmacyId, productId, totalQuantity, unitPrice, minQuantityThreshold }

Action (Modifier):
- PUT /api/pharmacy/stocks/{stockId}
- Pré-remplir champs avec valeurs actuelles
```

---

### 👨‍💼 ADMIN MODULE (Bonus)

#### Page 1: Dashboard Global
```
URL: /admin/dashboard

Metrics:
- Total patients actifs
- Total pharmacies
- Total commandes ce mois
- Total revenus
- Commandes en retard
- Taux de satisfaction
- Top pharmacies par revenu
- Top produits nationaux
```

---

## 🔌 INTÉGRATION WEBSOCKET (TEMPS RÉEL)

### Configuration STOMP (Angular):
```typescript
// stomp.service.ts
import * as SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';

export class StompService {
  private stompClient: Stomp.Client;
  
  connect(): Observable<void> {
    return new Observable(observer => {
      const socket = new SockJS('http://localhost:8081/springsecurity/ws');
      this.stompClient = Stomp.over(socket);
      
      this.stompClient.connect({}, 
        () => observer.next(),
        (error) => observer.error(error)
      );
    });
  }
  
  subscribe(topic: string, callback: (msg: any) => void): void {
    this.stompClient.subscribe(topic, (message) => {
      callback(JSON.parse(message.body));
    });
  }
  
  send(destination: string, headers: any, body: any): void {
    this.stompClient.send(destination, headers, JSON.stringify(body));
  }
  
  disconnect(): void {
    if (this.stompClient) {
      this.stompClient.disconnect(() => console.log('Disconnected'));
    }
  }
}
```

### Topics à Subscribe:

**1. Delivery GPS Updates (Patient):**
```typescript
this.stomp.subscribe(`/topic/delivery/${orderId}`, (update) => {
  // {
  //   trackingId: "DLVR-TN-20260320-001",
  //   latitude: 33.5745,
  //   longitude: -7.5891,
  //   status: "IN_TRANSIT",
  //   eta: "2026-03-21T14:30:00"
  // }
  this.mapService.updateDeliveryPin(update.latitude, update.longitude);
  this.updateETA(update.eta);
});
```

**2. Order Status Changes (Patient):**
```typescript
this.stomp.subscribe(`/topic/orders/${patientId}`, (update) => {
  // {
  //   orderId: 1,
  //   status: "VALIDATED",
  //   message: "Commande validée ✓"
  // }
  this.notificationService.show(update.message);
  this.refreshOrder(update.orderId);
});
```

**3. New Orders (Pharmacist):**
```typescript
this.stomp.subscribe(`/topic/pharmacist/${pharmacyId}`, (update) => {
  // {
  //   orderId: 1,
  //   patientName: "Ali Ahmed",
  //   amount: 14.50
  // }
  this.soundService.play('notification');
  this.refreshOrdersCount();
});
```

---

## 💾 SERVICES ANGULAR À CRÉER

```typescript
// pharmacy.service.ts
export class PharmacyService {
  // Pharmacies
  getAllPharmacies(): Observable<PharmacyDTO[]>
  searchPharmacies(name: string): Observable<PharmacyDTO[]>
  getPharmacyById(id: number): Observable<PharmacyDTO>
}

// order.service.ts
export class OrderService {
  // Patient
  createOrder(dto: CreateOrderDTO): Observable<OrderDTO>
  getPatientOrders(patientId: number): Observable<OrderDTO[]>
  getOrderDetails(orderId: number): Observable<OrderDetailDTO>
  cancelOrder(orderId: number, reason: string): Observable<OrderDTO>
  
  // Pharmacist
  getPharmacyOrders(pharmacyId: number): Observable<OrderDTO[]>
  getOrdersByStatus(pharmacyId: number, status: string): Observable<OrderDTO[]>
  validateOrder(orderId: number, dto: ValidateDTO): Observable<OrderDTO>
  rejectOrder(orderId: number, reason: string): Observable<OrderDTO>
  updateOrderStatus(orderId: number, dto: UpdateStatusDTO): Observable<OrderDTO>
  getOrderTracking(orderId: number): Observable<TrackingDTO[]>
  downloadInvoice(orderId: number): Observable<Blob>
  getPharmacyStats(pharmacyId: number): Observable<StatsDTO>
}

// stock.service.ts
export class StockService {
  getStocksByPharmacy(pharmacyId: number): Observable<StockDTO[]>
  getStockById(stockId: number): Observable<StockDTO>
  createStock(dto: CreateStockDTO): Observable<StockDTO>
  updateStock(stockId: number, dto: UpdateStockDTO): Observable<StockDTO>
  deleteStock(stockId: number): Observable<void>
  findAvailability(productId: number): Observable<StockDTO[]>
  searchByProductAndQty(productId: number, minQty: number): Observable<AvailabilityDTO[]>
}

// delivery.service.ts
export class DeliveryService {
  getDeliveryInfo(orderId: number): Observable<DeliveryDTO>
  updateLocation(trackingId: string, lat: number, lng: number, status: string): Observable<DeliveryDTO>
}

// upload.service.ts
export class UploadService {
  uploadPrescription(file: File): Observable<{ prescriptionImageUrl: string }>
}

// notification.service.ts
export class NotificationService {
  getAllNotifications(): Observable<NotificationDTO[]>
  markAsRead(notificationId: number): Observable<void>
  show(message: string, type: 'success' | 'error' | 'warning' | 'info'): void
}

// payment.service.ts
export class PaymentService {
  processPayment(dto: PaymentDTO): Observable<PaymentResultDTO>
}
```

---

## 🛠️ TECHNOLOGIES & TOOLS

- **Framework**: Angular 17+
- **HTTP Client**: HttpClientModule (interceptors pour JWT)
- **WebSocket**: STOMP + SockJS
- **Maps**: Leaflet (Haversine ETA)
- **PDF Download**: FileSaver.js
- **UI Components**: Material Design / PrimeNG
- **State Management**: RxJS Observables (ou NgRx optionnel)
- **Authentication**: JWT Token (httponly cookies)
- **Notifications**: Toastr / ngx-toastr

---

## ✅ CHECKLIST IMPLÉMENTATION

### Phase 1: Setup & Auth
- [ ] Login/Register page
- [ ] JWT token management (interceptor)
- [ ] Route guards (CanActivate par rôle)
- [ ] Refresh token logic

### Phase 2: Patient - Créer Commande
- [ ] Page wizard création commande (4 étapes)
- [ ] Upload prescription image
- [ ] Sélection pharmacie (liste + map)
- [ ] Vérification stocks (avec alert alternatives)
- [ ] Confirmation adresse & livraison
- [ ] Création commande via API

### Phase 3: Pharmacist - Valider Commande
- [ ] Dashboard pharmacien avec stats
- [ ] Liste commandes par statut
- [ ] Détails commande + image ordonnance
- [ ] Valider/Rejeter avec modals
- [ ] Gestion stocks avec CRUD
- [ ] Alertes stock bas

### Phase 4: Patient - Suivi Livraison
- [ ] Page tracking avec map temps réel (WebSocket)
- [ ] Timeline d'événements
- [ ] ETA dynamique (Haversine)
- [ ] Notification push
- [ ] Download facture PDF

### Phase 5: WebSocket & Real-time
- [ ] STOMP connection
- [ ] Delivery GPS broadcast
- [ ] Order status updates
- [ ] Pharmacist notifications
- [ ] Sound alerts

### Phase 6: Admin Dashboard (Bonus)
- [ ] Vue globale transactions
- [ ] Statistiques par pharmacie
- [ ] Gérer utilisateurs
- [ ] Export rapports

---

## 📝 ERREURS COURANTES & SOLUTIONS

| Problème | Solution |
|----------|————————|
| 401 Unauthorized | Refresh token expiré → redirect login |
| 404 Order not found | Vérifier orderId en param |
| CORS error | Backend allow-origins configuré |
| WebSocket disconnect | Reconnect auto avec exponential backoff |
| Image ordonnance chargée court | Compresser image côté client avant upload |
| Paiement fail | Afficher raison erreur backend |
| Stock décrémenté 2x | Transaction isolation (backend gère) |

---

## 🚀 DÉPLOIEMENT

**Développement:**
- Frontend: `ng serve --port 4200`
- Backend: `java -jar demosec-0.0.1-SNAPSHOT.jar`
- WebSocket: `ws://localhost:8081/springsecurity/ws`

**Production:**
- Build: `ng build --configuration production`
- HTTPS obligatoire (WebSocket)
- CORS: Allow production domain uniquement
- JWT secret en env variables

---

## 📞 RÉSUMÉ POUR CHAT FRONTEND

**Copier-coller ce prompt complet dans ChatGPT/Claude et ajouter:**

> "Je veux développer le frontend Angular complet pour Medicare AI Pharmacy Order Management. 
> Tu auras les API Spring Boot à http://localhost:8081/springsecurity/api.
> 
> Les 4 acteurs sont: Patient (crée commande), Pharmacist (valide), Delivery (livraison temps réel), Admin (supervision).
> 
> Utilise Angular + STOMP WebSocket + Leaflet maps + Material Design.
> 
> Commence par les services, puis les composants Patient (créer/tracker), Pharmacist (valider/stocks), et WebSocket real-time delivery."
```

---

**Notes:**
- Tous les endpoints sont testés ✅
- Backend WebSocket activé ✅
- Haversine ETA calculée ✅
- Invoice PDF généré ✅
- Stock décrémenté au VALIDATED ✅

Prêt à développer ? 🚀

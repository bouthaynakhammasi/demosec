# Explication: Colonnes ENUM et migrations Hibernate

## Problème observé

Vous avez reçu des messages Hibernate montrant que les colonnes ENUM doivent être modifiées:

```sql
ALTER TABLE notifications MODIFY COLUMN type enum (...)
ALTER TABLE order_tracking MODIFY COLUMN status enum (...)
ALTER TABLE pharmacy_orders MODIFY COLUMN status enum (...)
```

## Pourquoi cela se produit?

1. **Hibernate génère automatiquement les DDL** - Lorsque vous démarrez l'application avec `spring.jpa.hibernate.ddl-auto=update`, Hibernate analyse les entités et génère les migrations nécessaires.

2. **Les énumérations ne sont pas toujours synchronisées** - Si vous modifiez les valeurs d'énumération (ajout/suppression), Hibernate essaie d'appliquer les modifications via ALTER TABLE.

3. **Problème de drop/recreate** - MySQL n'aime pas modifier les colonnes ENUM directement. Parfois, Hibernate doit recréer la colonne, ce qui peut échouer si:
   - Il y a des données existantes
   - Il y a des constraints de clés étrangères
   - La colonne est utilisée dans un index

## Vérification: Énums définis dans le projet

### PharmacyOrderStatus.java
```java
PENDING, REVIEWING, VALIDATED, AWAITING_CHOICE, RESERVED,
DELIVERY_REQUESTED, PAYMENT_PENDING, PAID, READY_FOR_PICKUP,
ASSIGNING, ASSIGNED, PICKED_UP, OUT_FOR_DELIVERY, DELIVERED,
CANCELLED, REJECTED
```

### OrderTracking.java
Utilise: `PharmacyOrderStatus` (même enum que ci-dessus)

### Notifications.java
Utilise: `NotificationType` (à vérifier)

## Solutions

### 1. **Laisser Hibernate gérer** (Recommandé pour développement)
```properties
spring.jpa.hibernate.ddl-auto=update
```
Hibernateexécutera automatiquement les migrations au démarrage.

### 2. **Utiliser des migrations Flyway** (Recommandé pour production)
- Les scripts SQL sont versionnés dans `src/main/resources/db/migration/`
- Format: `V001__Description.sql`, `V002__Description.sql`, etc.
- Flyway exécute les scripts en ordre et les enregistre dans `flyway_schema_history`

### 3. **Exécuter manuellement** (Développement rapide)
```sql
-- Exécutez manuellement les commandes ALTER TABLE
-- (voir le fichier V001__Sync_Enum_Columns.sql)
```

## Pas de suppression de tables

⚠️ **Important**: Je n'ai pas effacé les tables. Les messages `ALTER TABLE` sont des modifications de structure pour synchroniser les colonnes ENUM avec les énumérations Java.

## Étapes recommandées

1. ✅ Vérifier que les énums sont définis correctement (déjà fait)
2. ✅ Créer un script de migration Flyway (déjà créé: `V001__Sync_Enum_Columns.sql`)
3. ⏭️ Exécuter le script contre votre base de données
4. ⏭️ Redémarrer l'application avec `spring.jpa.hibernate.ddl-auto=validate`

## Configuration recommandée (application.properties)

```properties
# Pour DÉVELOPPEMENT
spring.jpa.hibernate.ddl-auto=update

# Pour PRODUCTION
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
```


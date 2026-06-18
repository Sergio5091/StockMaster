# StockMaster MVP — Plan de développement

> Stack : Vue.js 3 · Spring Boot 3 · MySQL 8  
> Version MVP 1.0

---

## PHASE 1 — Fondations (Semaines 1-2)

### ✅ TÂCHE 1 — Setup projet & infrastructure

- [x] **1.1 Backend Spring Boot**
  - [x] Vérifier la structure des packages (`core`, `user`, `category`, `product`, `stock`, `supplier`, `warehouse`, `zone`)
  - [x] Configurer `application.properties` (DB, JWT, port)
  - [x] Tester le démarrage avec `mvn spring-boot:run`
  - [x] Vérifier Swagger sur `http://localhost:8080/swagger-ui.html`

- [x] **1.2 Base de données MySQL**
  - [x] Créer la base `stockmaster_db`
  - [x] Vérifier que Hibernate génère les tables au démarrage (`ddl-auto=update`)
  - [x] Créer un script SQL de données initiales → `src/main/resources/init-data.sql`

- [x] **1.3 Frontend Vue.js 3**
  - [x] Initialiser le projet avec Vite (`npm create vue@latest`)
  - [x] Installer les dépendances : Pinia, Vue Router, Axios
  - [x] Installer Tailwind CSS
  - [x] Installer PrimeVue
  - [x] Configurer la structure des dossiers (`views`, `stores`, `services`, `composables`, `layouts`, `components`)
  - [x] Créer `api.ts` avec intercepteur Axios (ajout du JWT dans les headers)
  - [x] Configurer le proxy Vite vers `http://localhost:8080`

- [ ] **1.4 Docker (optionnel pour le développement)**
  - [ ] Créer `docker-compose.yml` avec MySQL 8
  - [ ] Ajouter un service pour le backend Spring Boot
  - [ ] Tester le lancement complet avec `docker-compose up`

---

### ✅ TÂCHE 2 — Module Auth (JWT)

- [x] **2.1 Backend**
  - [x] Entité `User` : id, username, password, email, fullName, role, actif
  - [x] Entité `RefreshToken` : token, user, expiresAt, revoked
  - [x] `UserRepository` (JPA)
  - [x] `RefreshTokenRepository`
  - [x] `JwtTokenProvider` : générer, valider, extraire le token
  - [x] `JwtAuthenticationFilter` : filtre Spring Security
  - [x] `SecurityConfig` : routes publiques/protégées, CSRF désactivé, stateless
  - [x] `AuthController` :
    - [x] `POST /api/auth/login` → retourne `{token, refreshToken, expiresIn, user}`
    - [x] `POST /api/auth/refresh` → renouveler le token
    - [x] `POST /api/auth/logout`
    - [x] `GET /api/auth/me` → profil connecté
  - [x] `RefreshTokenService` : créer, valider, révoquer
  - [x] `@EnableJpaAuditing` activé sur `StockmasterApplication`
  - [x] `UnauthorizedException` corrigée (constructeurs ajoutés)
  - [ ] Gestion des tentatives échouées (compteur, blocage 5 échecs / 15 min)
  - [ ] `PUT /api/users/me/password` — changer son propre mot de passe

- [ ] **2.2 Frontend**
  - [x] `AuthLayout.vue` (layout page de login)
  - [x] `LoginView.vue` : formulaire username + password
  - [x] `auth.store.ts` (Pinia) : état user, token, role
  - [x] `auth.service.ts` : appels API login/logout/refresh
  - [x] Stocker le JWT dans `localStorage`
  - [x] Guard de navigation `beforeEach` dans `router/index.ts`
  - [x] Composable `usePermissions.ts` : `can()`, `canAccess()`
  - [ ] Page "Changer mot de passe" si `force_password_change = true`

---

### ✅ TÂCHE 3 — Module Utilisateurs

- [x] **3.1 Backend**
  - [x] `UserApplicationService` : CRUD complet (createUser, updateUser, deactivateUser, activateUser, changePassword, findAll, findById)
  - [x] `UserController` :
    - [x] `GET /api/users` (paginé)
    - [x] `POST /api/users` (via `RegisterRequest` dans AuthController)
    - [x] `GET /api/users/{id}`
    - [x] `PUT /api/users/{id}`
    - [x] `PATCH /api/users/{id}/deactivate`
    - [x] `PATCH /api/users/{id}/activate`
    - [x] `POST /api/users/{id}/reset-password`
  - [ ] Filtres sur GET /api/users (rôle, statut, recherche nom/email)
  - [ ] Envoi d'email (mot de passe temporaire) via Spring Mail
  - [ ] Validation : impossible de modifier son propre rôle

- [ ] **3.2 Frontend**
  - [ ] `UsersView.vue` : tableau paginé avec filtres
  - [ ] `UserFormModal.vue` : création / modification
  - [ ] Boutons Activer / Désactiver
  - [ ] Bouton "Réinitialiser mot de passe"
  - [ ] `users.service.ts`

---

### ✅ TÂCHE 4 — Module Entrepôts

- [ ] **4.1 Backend**
  - [ ] Entité `Warehouse` : id, code (ENT-001), nom, adresse, ville, pays, responsable_id, capacite_totale, capacite_utilisee, actif, telephone, email — ⚠️ stub vide
  - [ ] Génération automatique du code (ENT-001, ENT-002…)
  - [ ] `WarehouseRepository`, `WarehouseService`, `WarehouseController`
  - [ ] Endpoints :
    - [ ] `GET /api/warehouses` (filtré par rôle)
    - [ ] `POST /api/warehouses`
    - [ ] `GET /api/warehouses/{id}`
    - [ ] `PUT /api/warehouses/{id}`b                                                                                                                                                                                                                 
    - [ ] `PATCH /api/warehouses/{id}/deactivate` (vérifier stock actif)
    - [ ] `GET /api/warehouses/{id}/stats`
  - [ ] Règle métier : impossible de désactiver si stock actif

- [ ] **4.2 Frontend**
  - [ ] `WarehousesView.vue` : liste avec filtres (ville, statut, responsable)
  - [ ] `WarehouseDetailView.vue` : fiche + jauge de capacité + liste zones
  - [ ] `WarehouseFormModal.vue`
  - [ ] `warehouses.service.js`

---

### ✅ TÂCHE 5 — Module Catégories & Produits

- [ ] **5.1 Backend — Catégories**
  - [ ] Entité `Category` : id, nom, description, categorie_parente_id, actif (✅ existe déjà)
  - [ ] Vérifier `CategoryController`, `CategoryService`, `CategoryRepository`
  - [ ] Règle : max 2 niveaux de hiérarchie
  - [ ] Désactivation logique si des produits existent

- [ ] **5.2 Backend — Produits**
  - [ ] Entité `Product` : id, reference, code_barres, nom, description, categorie_id, fournisseur_principal_id, prix_achat, prix_vente, poids_kg, volume_m3, stock_minimum, stock_maximum, unite_mesure, actif, image_url
  - [ ] Génération automatique de la référence (PRD-XXXXXX)
  - [ ] `ProductRepository`, `ProductService`, `ProductController`
  - [ ] Endpoints :
    - [ ] `GET /api/products` (paginé, filtres)
    - [ ] `POST /api/products`
    - [ ] `GET /api/products/{id}`
    - [ ] `PUT /api/products/{id}`
    - [ ] `PATCH /api/products/{id}/deactivate`
    - [ ] `GET /api/products/{id}/stock`
    - [ ] `GET /api/products/{id}/movements`
    - [ ] `GET /api/products/search?q=`
    - [ ] `GET /api/products/barcode/{code}`

- [ ] **5.3 Frontend**
  - [ ] `CategoriesView.vue` : arbre hiérarchique + CRUD
  - [ ] `ProductsView.vue` : tableau paginé avec indicateurs stock (🔴🟠🟢)
  - [ ] `ProductDetailView.vue` : fiche produit + stock par entrepôt + historique
  - [ ] `ProductFormModal.vue`
  - [ ] `categories.service.js`, `products.service.js`

---

## PHASE 2 — Stock de base (Semaines 3-4)

### ✅ TÂCHE 6 — Module Fournisseurs

- [ ] **6.1 Backend**
  - [ ] Entité `Supplier` : id, code, nom, adresse, ville, pays, telephone, email, contacts, delai_livraison_jours, actif
  - [ ] Génération code (SUP-001)
  - [ ] `SupplierRepository`, `SupplierService`, `SupplierController`
  - [ ] Endpoints :
    - [ ] `GET /api/suppliers`
    - [ ] `POST /api/suppliers`
    - [ ] `GET /api/suppliers/{id}`
    - [ ] `PUT /api/suppliers/{id}`
    - [ ] `PATCH /api/suppliers/{id}/deactivate`
    - [ ] `GET /api/suppliers/{id}/orders`

- [ ] **6.2 Frontend**
  - [ ] `SuppliersView.vue` : tableau + filtres
  - [ ] `SupplierDetailView.vue` : fiche + historique commandes
  - [ ] `SupplierFormModal.vue`
  - [ ] `suppliers.service.js`

---

### ✅ TÂCHE 7 — Module Zones & Emplacements

- [ ] **7.1 Backend — Zones**
  - [ ] Entité `Zone` : id, code, nom, type (ENUM), entrepot_id, capacite_m3, occupation_m3, actif
  - [ ] Règle : somme des capacités zones ≤ capacité entrepôt
  - [ ] `ZoneController` :
    - [ ] `GET /api/warehouses/{warehouseId}/zones`
    - [ ] `POST /api/warehouses/{warehouseId}/zones`
    - [ ] `GET /api/zones/{id}`
    - [ ] `PUT /api/zones/{id}`
    - [ ] `DELETE /api/zones/{id}` (soft delete)
    - [ ] `GET /api/zones/{id}/occupancy`

- [ ] **7.2 Backend — Emplacements**
  - [ ] Entité `Location` : id, code (généré auto), zone_id, rayon, etagere, position, capacite_kg, poids_actuel, statut (ENUM), qr_code_url
  - [ ] Génération automatique du code (ENT001-ZONEA-R03-E02-P12)
  - [ ] Génération du QR Code à la création
  - [ ] `LocationController` :
    - [ ] `GET /api/zones/{zoneId}/locations`
    - [ ] `POST /api/zones/{zoneId}/locations`
    - [ ] `GET /api/locations/{id}`
    - [ ] `PUT /api/locations/{id}`
    - [ ] `GET /api/locations/{id}/content`
    - [ ] `GET /api/locations/scan/{qrCode}`

- [ ] **7.3 Frontend**
  - [ ] `ZonesView.vue` : liste des zones d'un entrepôt + taux d'occupation
  - [ ] `ZoneFormModal.vue`
  - [ ] `LocationsView.vue` : liste des emplacements d'une zone
  - [ ] `LocationDetailView.vue` : contenu + QR Code
  - [ ] `zones.service.js`, `locations.service.js`

---

### ✅ TÂCHE 8 — Module Stocks (consultation)

- [ ] **8.1 Backend**
  - [ ] Entité `Stock` : id, produit_id, entrepot_id, zone_id, emplacement_id, quantite_disponible, quantite_reservee, quantite_en_transit
  - [ ] Entité `StockMovement` : id, type, produit_id, entrepot_source_id, entrepot_destination_id, emplacement_source_id, emplacement_destination_id, quantite, quantite_avant, quantite_apres, reference_document, utilisateur_id, note, created_at
  - [ ] `StockRepository`, `StockMovementRepository`
  - [ ] `StockService` : méthodes de consultation + mise à jour
  - [ ] `StockController` :
    - [ ] `GET /api/stocks`
    - [ ] `GET /api/stocks/{produitId}/{entrepotId}`
    - [ ] `GET /api/stocks/movements`
  - [ ] Filtrage par rôle (MANAGER/OPERATOR → seulement leurs entrepôts)

- [ ] **8.2 Frontend**
  - [ ] `StocksView.vue` : vue globale avec indicateurs critiques/normal/excédentaire
  - [ ] Vue par entrepôt, vue par produit
  - [ ] `StockMovementsView.vue` : historique des mouvements avec filtres
  - [ ] Composant `StockIndicator.vue` (🔴🟠🟢)
  - [ ] `stocks.service.js`

---

### ✅ TÂCHE 9 — Module Entrées de stock (Bon de réception)

- [ ] **9.1 Backend**
  - [ ] Entité `GoodsReceipt` : id, numero (BR-2024-0001), fournisseur_id, entrepot_id, commande_fournisseur_id, statut, date_reception, note, cree_par, valide_par, date_validation
  - [ ] Entité `GoodsReceiptLine` : id, bon_reception_id, produit_id, emplacement_id, quantite_attendue, quantite_recue, qualite_ok, note_qualite
  - [ ] `GoodsReceiptService` : workflow BROUILLON → EN_ATTENTE_VALIDATION → VALIDE/REJETE
  - [ ] Mise à jour automatique du stock à la validation
  - [ ] Gestion quarantaine (qualite_ok = false)
  - [ ] `GoodsReceiptController` :
    - [ ] `GET /api/receipts`
    - [ ] `POST /api/receipts`
    - [ ] `GET /api/receipts/{id}`
    - [ ] `PUT /api/receipts/{id}`
    - [ ] `POST /api/receipts/{id}/submit`
    - [ ] `POST /api/receipts/{id}/validate`
    - [ ] `POST /api/receipts/{id}/reject`

- [ ] **9.2 Frontend**
  - [ ] `ReceiptsView.vue` : liste avec filtres (entrepôt, statut, dates)
  - [ ] `ReceiptFormView.vue` : création / saisie des lignes
  - [ ] `ReceiptDetailView.vue` : détail + actions (soumettre, valider, rejeter)
  - [ ] `receipts.service.js`

---

### ✅ TÂCHE 10 — Module Sorties de stock (Bon de sortie)

- [ ] **10.1 Backend**
  - [ ] Entité `GoodsIssue` : id, numero (BS-2024-0001), entrepot_id, motif (ENUM), client_nom, client_reference, statut, date_sortie, note, cree_par, valide_par
  - [ ] Entité `GoodsIssueLine` : id, bon_sortie_id, produit_id, emplacement_id, quantite_demandee, quantite_sortie
  - [ ] Vérification stock suffisant (alerte bloquante si insuffisant)
  - [ ] `GoodsIssueService` : workflow BROUILLON → EN_ATTENTE_VALIDATION → VALIDE/ANNULE
  - [ ] Génération PDF du bordereau de sortie
  - [ ] `GoodsIssueController` :
    - [ ] `GET /api/issues`
    - [ ] `POST /api/issues`
    - [ ] `GET /api/issues/{id}`
    - [ ] `PUT /api/issues/{id}`
    - [ ] `POST /api/issues/{id}/validate`
    - [ ] `POST /api/issues/{id}/cancel`
    - [ ] `GET /api/issues/{id}/pdf`

- [ ] **10.2 Frontend**
  - [ ] `IssuesView.vue` : liste avec filtres
  - [ ] `IssueFormView.vue` : création + vérification stock temps réel
  - [ ] `IssueDetailView.vue` : détail + actions + bouton PDF
  - [ ] `issues.service.js`

---

## PHASE 3 — Opérations avancées (Semaines 5-6)

### ✅ TÂCHE 11 — Module Transferts entre entrepôts

- [ ] **11.1 Backend**
  - [ ] Entité `Transfer` : id, numero (TRF-2024-0001), entrepot_source_id, entrepot_destination_id, statut (ENUM), date_expedition, date_reception, cree_par, expedie_par, recu_par
  - [ ] Entité `TransferLine` : id, transfert_id, produit_id, emplacement_source_id, emplacement_destination_id, quantite_demandee, quantite_recue
  - [ ] `TransferService` : workflow BROUILLON → EXPEDIE → RECU / ANNULE
  - [ ] Mise à jour stock : source (disponible -=, en_transit +=) puis destination (en_transit -=, disponible +=)
  - [ ] Enregistrement des écarts
  - [ ] `TransferController` :
    - [ ] `GET /api/transfers`
    - [ ] `POST /api/transfers`
    - [ ] `GET /api/transfers/{id}`
    - [ ] `PUT /api/transfers/{id}`
    - [ ] `POST /api/transfers/{id}/ship`
    - [ ] `POST /api/transfers/{id}/receive`
    - [ ] `POST /api/transfers/{id}/cancel`

- [ ] **11.2 Frontend**
  - [ ] `TransfersView.vue` : liste
  - [ ] `TransferFormView.vue` : création + vérification stock
  - [ ] `TransferDetailView.vue` : détail + actions (expédier, réceptionner, annuler)
  - [ ] `transfers.service.js`

---

### ✅ TÂCHE 12 — Module Commandes fournisseurs

- [ ] **12.1 Backend**
  - [ ] Entité `PurchaseOrder` : id, numero (CF-2024-0001), fournisseur_id, entrepot_destination_id, statut (ENUM), dates, montant_total, cree_par, valide_par
  - [ ] Entité `PurchaseOrderLine` : id, commande_id, produit_id, quantite_commandee, quantite_recue, prix_unitaire, montant_ligne
  - [ ] `PurchaseOrderService` : workflow complet
  - [ ] Mise à jour automatique depuis les bons de réception liés
  - [ ] `PurchaseOrderController` :
    - [ ] `GET /api/purchase-orders`
    - [ ] `POST /api/purchase-orders`
    - [ ] `GET /api/purchase-orders/{id}`
    - [ ] `PUT /api/purchase-orders/{id}`
    - [ ] `POST /api/purchase-orders/{id}/validate`
    - [ ] `POST /api/purchase-orders/{id}/send`
    - [ ] `POST /api/purchase-orders/{id}/cancel`

- [ ] **12.2 Frontend**
  - [ ] `PurchaseOrdersView.vue` : liste avec filtres
  - [ ] `PurchaseOrderFormView.vue` : création avec lignes produits
  - [ ] `PurchaseOrderDetailView.vue` : détail + actions
  - [ ] `purchaseOrders.service.js`

---

### ✅ TÂCHE 13 — Module Inventaires

- [ ] **13.1 Backend**
  - [ ] Entité `Inventory` : id, numero (INV-2024-001), type (ENUM), entrepot_id, zone_id, categorie_id, statut (ENUM), dates, cree_par
  - [ ] Entité `InventoryLine` : id, inventaire_id, produit_id, emplacement_id, quantite_theorique, quantite_comptee, ecart, note
  - [ ] `InventoryService` : snapshot stock au démarrage, workflow PLANIFIE → EN_COURS → TERMINE/ANNULE
  - [ ] Ajustement automatique du stock à la clôture (mouvement AJUSTEMENT_INVENTAIRE)
  - [ ] `InventoryController` :
    - [ ] `GET /api/inventories`
    - [ ] `POST /api/inventories`
    - [ ] `GET /api/inventories/{id}`
    - [ ] `POST /api/inventories/{id}/start`
    - [ ] `PUT /api/inventories/{id}/lines/{lineId}`
    - [ ] `POST /api/inventories/{id}/close`
    - [ ] `GET /api/inventories/{id}/report`

- [ ] **13.2 Frontend**
  - [ ] `InventoriesView.vue` : liste
  - [ ] `InventoryFormView.vue` : planification (type, périmètre)
  - [ ] `InventorySessionView.vue` : saisie des quantités comptées
  - [ ] `InventoryReportView.vue` : rapport des écarts
  - [ ] `inventories.service.js`

---

### ✅ TÂCHE 14 — Alertes automatiques

- [ ] **14.1 Backend**
  - [ ] Entité `Alert` : id, type, message, entite, entite_id, traitee, created_at
  - [ ] Entité `AlertConfig` : type, actif, seuil, emails_supplementaires, entrepot_id
  - [ ] `AlertScheduler` : job Spring Scheduler toutes les heures
    - [ ] Vérifier stock critique (< stock_minimum)
    - [ ] Vérifier stock excédentaire (> stock_maximum)
    - [ ] Vérifier zones saturées (> 90%)
    - [ ] Vérifier commandes en retard
  - [ ] `AlertService` : envoi email via Spring Mail + éviter doublons dans la journée
  - [ ] `AlertController` :
    - [ ] `GET /api/alerts`
    - [ ] `GET /api/alerts/history`
    - [ ] `PUT /api/alerts/config`
    - [ ] `POST /api/alerts/{id}/acknowledge`

- [ ] **14.2 Frontend**
  - [ ] `AlertsView.vue` : liste des alertes actives + historique
  - [ ] `AlertConfigView.vue` : configuration des seuils
  - [ ] Badge de notification dans le header
  - [ ] `alerts.service.js`

---

## PHASE 4 — Finalisation (Semaines 7-8)

### ✅ TÂCHE 15 — Tableau de bord + WebSocket

- [ ] **15.1 Backend — Dashboard**
  - [ ] `DashboardService` : agrégation des KPIs
  - [ ] `DashboardController` :
    - [ ] `GET /api/dashboard/summary`
    - [ ] `GET /api/dashboard/stock-evolution`
    - [ ] `GET /api/dashboard/top-products`
    - [ ] `GET /api/dashboard/alerts-summary`

- [ ] **15.2 Backend — WebSocket**
  - [ ] Configurer STOMP sur `/ws`
  - [ ] Topics :
    - [ ] `/topic/stock/{warehouseId}` — mise à jour stock
    - [ ] `/topic/alerts/{userId}` — alertes personnelles
    - [ ] `/topic/dashboard` — mise à jour tableau de bord
  - [ ] Publier sur le bon topic après chaque validation de mouvement

- [ ] **15.3 Frontend**
  - [ ] `DashboardView.vue` : KPIs + graphiques (Chart.js ou ApexCharts)
    - [ ] Vue ADMIN : global (entrepôts, produits, valeur stock, top 5, graphiques)
    - [ ] Vue MANAGER : filtré sur ses entrepôts + taux d'occupation zones
    - [ ] Vue OPERATOR : stock entrepôt + bons à traiter + alertes
  - [ ] Connexion WebSocket avec `@stomp/stompjs`
  - [ ] `notifications.store.js` (Pinia) : gérer les alertes temps réel
  - [ ] Toast de notification à la réception d'une alerte
  - [ ] `dashboard.service.js`

---

### ✅ TÂCHE 16 — Reporting & Exports

- [ ] **16.1 Backend**
  - [ ] Dépendance iText ou JasperReports pour PDF
  - [ ] Dépendance Apache POI pour Excel
  - [ ] `ReportService` :
    - [ ] Rapport des mouvements (PDF, Excel, CSV)
    - [ ] Rapport d'inventaire (PDF, Excel)
    - [ ] Rapport des fournisseurs (PDF, Excel)
    - [ ] Rapport de stock à une date donnée (PDF, Excel)
    - [ ] Bordereau de sortie (PDF)
  - [ ] `ReportController` :
    - [ ] `GET /api/reports/movements`
    - [ ] `GET /api/reports/inventory/{inventoryId}`
    - [ ] `GET /api/reports/suppliers`
    - [ ] `GET /api/reports/stock`
    - [ ] `GET /api/reports/issue/{issueId}/bordereau`

- [ ] **16.2 Frontend**
  - [ ] `ReportsView.vue` : formulaire de génération avec filtres
  - [ ] Boutons de téléchargement (PDF / Excel / CSV)
  - [ ] `reports.service.js`

---

### ✅ TÂCHE 17 — Audit Log & Traçabilité

- [ ] **17.1 Backend**
  - [ ] Entité `AuditLog` : id, entite, entite_id, action (ENUM), utilisateur_id, utilisateur_nom, ancienne_valeur (JSON), nouvelle_valeur (JSON), ip_address, created_at
  - [ ] Aspect Spring AOP `@Auditable` : intercepter les méthodes de service
  - [ ] `AuditLogRepository`, `AuditLogService`
  - [ ] `AuditLogController` :
    - [ ] `GET /api/audit-logs` (ADMIN, filtres)
    - [ ] `GET /api/audit-logs/{entite}/{entiteId}`

- [ ] **17.2 Frontend**
  - [ ] `AuditLogsView.vue` : tableau avec filtres (entité, utilisateur, action, période)
  - [ ] Vue détail d'un enregistrement : diff ancienne/nouvelle valeur
  - [ ] `auditLogs.service.js`

---

### ✅ TÂCHE 18 — Layout & Navigation

- [ ] **18.1 Frontend**
  - [ ] `DefaultLayout.vue` : sidebar + header + zone de contenu
  - [ ] Sidebar avec navigation par rôle (masquer les menus non autorisés)
  - [ ] Header : nom utilisateur, entrepôt actif, badge alertes, bouton logout
  - [ ] Composants réutilisables :
    - [ ] `DataTable.vue` : tableau générique paginé
    - [ ] `ConfirmDialog.vue` : modale de confirmation
    - [ ] `StatusBadge.vue` : badge coloré pour les statuts
    - [ ] `PageHeader.vue` : titre + boutons d'action
    - [ ] `EmptyState.vue` : état vide

---

### ✅ TÂCHE 19 — Tests

- [ ] **19.1 Backend**
  - [ ] Tests unitaires JUnit 5 + Mockito pour les services critiques :
    - [ ] `AuthService`
    - [ ] `StockService` (mise à jour stock)
    - [ ] `GoodsReceiptService` (workflow)
    - [ ] `GoodsIssueService` (vérification stock)
    - [ ] `TransferService`
  - [ ] Tests d'intégration pour les controllers principaux

- [ ] **19.2 Frontend**
  - [ ] Tests des composables (`usePermissions`, `usePagination`)
  - [ ] Tests des stores Pinia

---

### ✅ TÂCHE 20 — Docker & Déploiement

- [ ] **20.1 Containerisation**
  - [ ] `Dockerfile` pour le backend Spring Boot
  - [ ] `Dockerfile` pour le frontend Vue.js (Nginx)
  - [ ] `docker-compose.yml` final :
    - [ ] Service MySQL 8
    - [ ] Service backend (Spring Boot)
    - [ ] Service frontend (Nginx)
  - [ ] Variables d'environnement externalisées (`.env`)

- [ ] **20.2 Vérifications finales**
  - [ ] Tester le build complet `docker-compose up --build`
  - [ ] Vérifier toutes les routes Swagger
  - [ ] Vérifier les permissions par rôle
  - [ ] Vérifier les WebSockets en conditions réelles
  - [ ] Insérer des données de test (data fixtures)

---

## Récapitulatif des modules

| # | Module | Backend | Frontend | Statut |
|---|--------|---------|----------|--------|
| 1 | Setup & Infrastructure | ✅ Complet | ✅ Complet | ✅ |
| 2 | Auth (JWT) | ✅ Complet | ✅ Partiel | 🔄 |
| 3 | Utilisateurs | ✅ Partiel | ⬜ À faire | 🔄 |
| 4 | Entrepôts | ⬜ À faire | ⬜ À faire | ⬜ |
| 5 | Catégories & Produits | ⬜ À faire | ⬜ À faire | ⬜ |
| 6 | Fournisseurs | ⬜ À faire | ⬜ À faire | ⬜ |
| 7 | Zones & Emplacements | ⬜ À faire | ⬜ À faire | ⬜ |
| 8 | Stocks (consultation) | ⬜ À faire | ⬜ À faire | ⬜ |
| 9 | Entrées de stock | ⬜ À faire | ⬜ À faire | ⬜ |
| 10 | Sorties de stock | ⬜ À faire | ⬜ À faire | ⬜ |
| 11 | Transferts | ⬜ À faire | ⬜ À faire | ⬜ |
| 12 | Commandes fournisseurs | ⬜ À faire | ⬜ À faire | ⬜ |
| 13 | Inventaires | ⬜ À faire | ⬜ À faire | ⬜ |
| 14 | Alertes automatiques | ⬜ À faire | ⬜ À faire | ⬜ |
| 15 | Tableau de bord + WebSocket | ⬜ À faire | ⬜ À faire | ⬜ |
| 16 | Reporting & Exports | ⬜ À faire | ⬜ À faire | ⬜ |
| 17 | Audit Log | ⬜ À faire | ⬜ À faire | ⬜ |
| 18 | Layout & Navigation | — | ✅ Partiel | 🔄 |
| 19 | Tests | ⬜ À faire | ⬜ À faire | ⬜ |
| 20 | Docker & Déploiement | ⬜ À faire | — | ⬜ |

---

*Document généré le 15/06/2026 — StockMaster MVP v1.0*

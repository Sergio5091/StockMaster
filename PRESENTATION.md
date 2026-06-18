# 📦 StockMaster — Guide de Présentation & Flux Métier

## 🏗️ Architecture du système

```
Frontend (Vue.js 3 + Pinia)     Backend (Spring Boot 4)     Base de données
     localhost:5173          →       localhost:8080        →    MySQL :3306
                                                                stockmaster_db
```

---

## 🚀 Démarrage rapide

### 1. Démarrer le backend
```bash
cd backend/stockmaster
mvn spring-boot:run
```
> ✅ Le serveur démarre sur http://localhost:8080
> ✅ Les 4 utilisateurs sont créés automatiquement au premier démarrage

### 2. Démarrer le frontend
```bash
cd frontend
npm run dev
```
> ✅ L'application est accessible sur http://localhost:5173

---

## 👥 Comptes de démonstration

| Rôle | Email | Mot de passe | Accès |
|------|-------|--------------|-------|
| **Administrateur** | admin@stockmaster.com | Admin1234! | Accès complet |
| **Gestionnaire** | manager@stockmaster.com | Manager1234! | Gestion opérationnelle |
| **Magasinier** | operator@stockmaster.com | Operator1234! | Opérations de terrain |
| **Auditeur** | auditor@stockmaster.com | Auditor1234! | Lecture seule |

---

## 🎯 Flux de présentation recommandé (par ordre)

---

### 📋 SCÉNARIO 1 — Mise en place de la structure (Administrateur)

**Connectez-vous avec :** `admin@stockmaster.com` / `Admin1234!`

#### Étape 1.1 — Créer les catégories de produits
1. Aller dans **Catalogue → Catégories**
2. Créer une catégorie racine : `Informatique`
3. Créer une sous-catégorie : `Périphériques` (parent : Informatique)
4. Créer une autre catégorie : `Mobilier de bureau`

> 💡 **Ce que ça montre** : La hiérarchie parent/enfant des catégories, stockée en base MySQL

#### Étape 1.2 — Créer les entrepôts
1. Aller dans **Structure → Entrepôts**
2. Créer un entrepôt : `Entrepôt Paris-Nord`
   - Code : `ENT-001`
   - Ville : `Paris`
   - Capacité totale : `5000 m³`
3. Créer un deuxième entrepôt : `Entrepôt Lyon-Sud`
   - Code : `ENT-002`
   - Ville : `Lyon`
   - Capacité totale : `3500 m³`

> 💡 **Ce que ça montre** : Les données sont persistées en BDD, pas de mock data

#### Étape 1.3 — Créer les fournisseurs
1. Aller dans **Catalogue → Fournisseurs**
2. Créer un fournisseur : `TechSupply SA`
   - Email : `contact@techsupply.fr`
   - Délai livraison : `3 jours`
3. Créer un autre : `DigiMart Pro`

#### Étape 1.4 — Créer les produits
1. Aller dans **Catalogue → Produits**
2. Créer un produit :
   - Nom : `Clavier mécanique RGB`
   - Catégorie : `Périphériques` (chargé depuis l'API ✅)
   - Fournisseur : `TechSupply SA` (chargé depuis l'API ✅)
   - Prix achat : `45 €` / Prix vente : `89.99 €`
   - Stock minimum : `10`

> 💡 **Ce que ça montre** : Les listes déroulantes sont dynamiques (API), plus de données en dur

#### Étape 1.5 — Gérer les utilisateurs
1. Aller dans **Administration → Utilisateurs**
2. Voir les 4 utilisateurs créés par le seed
3. Modifier le profil d'un utilisateur

---

### 📥 SCÉNARIO 2 — Réception de marchandises (Magasinier)

**Connectez-vous avec :** `operator@stockmaster.com` / `Operator1234!`

#### Étape 2.1 — Créer un bon de réception
1. Aller dans **Opérations → Bons de réception**
2. Cliquer **Nouveau bon de réception**
3. Sélectionner le fournisseur : `TechSupply SA`
4. Sélectionner l'entrepôt : `Paris-Nord`
5. Ajouter une ligne : `Clavier mécanique RGB` x 50 unités
6. Sauvegarder en statut `BROUILLON`

#### Étape 2.2 — Soumettre pour validation
1. Ouvrir le bon de réception créé
2. Cliquer **Soumettre pour validation**
3. Le statut passe à `EN_ATTENTE_VALIDATION`

> 💡 **Ce que ça montre** : Le workflow de validation à plusieurs étapes

---

### ✅ SCÉNARIO 3 — Validation et gestion des stocks (Gestionnaire)

**Connectez-vous avec :** `manager@stockmaster.com` / `Manager1234!`

#### Étape 3.1 — Valider le bon de réception
1. Aller dans **Opérations → Bons de réception**
2. Voir le bon en attente soumis par le magasinier
3. Cliquer **Valider**
4. Le statut passe à `VALIDÉ` → le stock est mis à jour automatiquement

#### Étape 3.2 — Consulter les niveaux de stock
1. Aller dans **Stocks → Niveaux de stock**
2. Voir les 50 claviers ajoutés en stock
3. Filtrer par entrepôt `Paris-Nord`

#### Étape 3.3 — Créer un transfert inter-entrepôts
1. Aller dans **Opérations → Transferts**
2. Créer un transfert :
   - Source : `Paris-Nord`
   - Destination : `Lyon-Sud`
   - Produit : `Clavier mécanique RGB` x 20
3. Expédier puis réceptionner

> 💡 **Ce que ça montre** : La traçabilité des mouvements de stock entre sites

---

### 📤 SCÉNARIO 4 — Sortie de marchandises (Magasinier)

**Connectez-vous avec :** `operator@stockmaster.com` / `Operator1234!`

#### Étape 4.1 — Créer un bon de sortie
1. Aller dans **Opérations → Bons de sortie**
2. Créer un bon de sortie :
   - Motif : `Livraison client`
   - Entrepôt : `Paris-Nord`
   - Ajouter : `Clavier mécanique RGB` x 5
3. Soumettre pour validation

#### Étape 4.2 — Validation par le gestionnaire
1. Se reconnecter en tant que **Gestionnaire**
2. Valider le bon de sortie
3. Le stock diminue automatiquement

> 💡 **Ce que ça montre** : Le double contrôle magasinier → gestionnaire

---

### 📊 SCÉNARIO 5 — Supervision et audit (Auditeur)

**Connectez-vous avec :** `auditor@stockmaster.com` / `Auditor1234!`

#### Étape 5.1 — Consulter l'historique des mouvements
1. Aller dans **Stocks → Mouvements**
2. Voir toutes les entrées, sorties et transferts
3. Filtrer par produit ou par date

#### Étape 5.2 — Consulter le journal d'audit
1. Aller dans **Supervision → Audit**
2. Voir toutes les actions effectuées par les utilisateurs
3. Identifier qui a fait quoi et quand

#### Étape 5.3 — Consulter les rapports
1. Aller dans **Supervision → Rapports**
2. Voir les statistiques globales de stock

> 💡 **Ce que ça montre** : La traçabilité complète et le contrôle interne

---

### 🛒 SCÉNARIO 6 — Commandes fournisseurs (Gestionnaire)

**Connectez-vous avec :** `manager@stockmaster.com` / `Manager1234!`

#### Étape 6.1 — Créer une commande fournisseur
1. Aller dans **Opérations → Commandes**
2. Créer une nouvelle commande :
   - Fournisseur : `DigiMart Pro`
   - Date livraison prévue : dans 7 jours
   - Ajouter des lignes de commande
3. Valider et envoyer la commande

#### Étape 6.2 — Suivre la commande
1. Voir le statut `ENVOYÉE`
2. Lier la réception au bon de commande quand la livraison arrive

---

### 📋 SCÉNARIO 7 — Inventaire (Gestionnaire + Magasinier)

**Connectez-vous avec :** `manager@stockmaster.com` / `Manager1234!`

#### Étape 7.1 — Planifier un inventaire
1. Aller dans **Opérations → Inventaires**
2. Planifier un inventaire partiel :
   - Entrepôt : `Paris-Nord`
   - Type : `Partiel`
3. Lancer la session d'inventaire

#### Étape 7.2 — Saisir les quantités (Magasinier)
1. Se connecter en tant que **Magasinier**
2. Accéder à la session d'inventaire en cours
3. Saisir les quantités comptées physiquement
4. Valider les écarts

---

## 🔑 Points clés à mettre en avant

### Architecture technique
- **Frontend dynamique** : Toutes les données viennent de l'API REST, aucune donnée en dur
- **Backend Spring Boot** : API REST avec persistance MySQL via JPA/Hibernate
- **Sécurité** : Système de rôles (RBAC) avec 4 niveaux d'accès
- **Audit** : Toutes les actions sont tracées avec utilisateur + horodatage

### Workflow de validation
```
Magasinier          Gestionnaire          Système
    │                    │                   │
    ├── Crée bon ─────►  │                   │
    │   (BROUILLON)      │                   │
    │                    │                   │
    ├── Soumet ───────►  │                   │
    │   (EN_ATTENTE)     │                   │
    │                    │                   │
    │               Valide ──────────────►   │
    │               (VALIDÉ)            Mise à jour stock
```

### Données persistées en base MySQL
- ✅ Utilisateurs avec mots de passe BCrypt
- ✅ Entrepôts et zones de stockage
- ✅ Catégories hiérarchiques de produits
- ✅ Produits avec références et codes-barres
- ✅ Fournisseurs avec contacts
- ✅ Mouvements de stock tracés
- ✅ Bons de réception / sortie / transfert
- ✅ Commandes fournisseurs
- ✅ Sessions d'inventaire

---

## 🌐 Endpoints API disponibles

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | /api/warehouses | Liste des entrepôts |
| POST | /api/warehouses | Créer un entrepôt |
| GET | /api/categories | Liste des catégories |
| POST | /api/categories | Créer une catégorie |
| GET | /api/suppliers | Liste des fournisseurs |
| GET | /api/products | Liste des produits |
| GET | /api/stocks | Niveaux de stock |
| GET | /api/users | Liste des utilisateurs |

> 📖 Documentation Swagger disponible sur : http://localhost:8080/swagger-ui.html

---

## ⚠️ État actuel du projet

| Module | Statut | Notes |
|--------|--------|-------|
| Authentification | ✅ Fonctionnel | Via DEMO_USERS (frontend) |
| Entrepôts | ✅ Fonctionnel | CRUD complet |
| Catégories | ✅ Fonctionnel | CRUD complet |
| Fournisseurs | ✅ Fonctionnel | CRUD complet |
| Produits | 🔄 Partiel | Service à finaliser |
| Stocks | 🔄 Partiel | Lectures OK |
| Bons réception | 🔄 Partiel | UI OK, backend à finaliser |
| Bons sortie | 🔄 Partiel | UI OK, backend à finaliser |
| Transferts | 🔄 Partiel | UI OK, backend à finaliser |
| Commandes | 🔄 Partiel | UI OK |
| Inventaires | 🔄 Partiel | UI OK |
| Utilisateurs | ✅ Fonctionnel | Seed automatique |
| Audit | 🔄 Partiel | UI OK |
| Rapports | 🔄 Partiel | UI OK |

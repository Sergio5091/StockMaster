Absolument ! Voici le **Guide d'utilisation de Stockmaster**, organisé par **rôles fonctionnels**. Ce guide vous aidera à comprendre quelles actions sont à votre disposition en fonction de votre profil (Administrateur, Gestionnaire, Magasinier, Auditeur).

---

# 📘 Guide d'utilisation de Stockmaster

## 1. Introduction & Connexion

Stockmaster est une solution de gestion de stock complète couvrant l'approvisionnement, le stockage, la traçabilité et le pilotage.

**Comment se connecter ?**

1. Rendez-vous sur l'interface de connexion (front-end) ou utilisez l'API `/api/auth/login`.
2. Saisissez vos identifiants (nom d'utilisateur et mot de passe).
3. Si l'authentification réussit, vous recevez un **jeton JWT**. L'application l'utilise pour gérer vos droits d'accès.

> **Comptes par défaut (environnement de démonstration)** : Admin, Manager, Operator, Auditor (les mots de passe sont fournis par votre administrateur).

---

## 2. Matrice des permissions (par module)

| Module / Fonctionnalité | Auditeur | Magasinier | Gestionnaire | Administrateur |
| :--- | :---: | :---: | :---: | :---: |
| **Consultation Dashboard & KPIs** | ✅ | ✅ | ✅ | ✅ |
| **Consultation Stock / Produits / Entrepôts** | ✅ | ✅ | ✅ | ✅ |
| **Création Bons de Réception / Sortie / Transfert** | ❌ | ✅ | ✅ | ✅ |
| **Validation Bons (Approbation finale)** | ❌ | ❌ | ✅ | ✅ |
| **Gestion des Emplacements (création/modification)** | ❌ | ❌ | ✅ | ✅ |
| **Réalisation des comptages (Inventaire)** | ❌ | ✅ | ✅ | ✅ |
| **Validation des Inventaires (Ajustement stock)** | ❌ | ❌ | ✅ | ✅ |
| **Gestion des Utilisateurs (CRUD)** | ❌ | ❌ | ❌ | ✅ |
| **Consultation des Logs d'Audit** | ✅ | ❌ | ❌ | ✅ |
| **Génération des Rapports (PDF/CSV/Excel)** | ✅ | ❌ | ✅ | ✅ |
| **Marquage des Alertes comme "traitées"** | ❌ | ❌ | ✅ | ✅ |

---

## 3. Rôle 1 : L'Auditeur (Contrôle & Supervision)

*Votre rôle est de vérifier la conformité, l'intégrité des données et les performances globales sans intervenir sur les opérations.*

**🔍 Ce que vous pouvez faire :**

1.  **Tableau de bord** (`/api/dashboard`) : Consulter les KPI globaux (valeurs de stock, nombre de mouvements, alertes actives).
2.  **Audit & Traçabilité** (`/api/v1/audit`) : Consulter l'historique complet de qui a fait quoi (création, modification, validation) sur les différentes entités.
3.  **Consultation** : Visualiser tous les référentiels (Produits, Fournisseurs, Entrepôts, Commandes, Bons de réception/sortie) sans pouvoir les modifier.
4.  **Reporting** (`/api/v1/reports`) : Exporter les rapports de synthèse (Inventaires, Mouvements, Réceptions, Sorties) aux formats PDF, Excel ou CSV pour l'archivage.

> ⚠️ **Limitation** : Vous ne pouvez ni créer, ni modifier, ni valider aucun document opérationnel.

---

## 4. Rôle 2 : Le Magasinier (Opérations Terrain)

*Vous êtes l'acteur clé des opérations physiques : réception de marchandises, préparation des expéditions, rangement et comptages.*

**📦 Ce que vous pouvez faire :**

**A. Gérer les entrées et sorties physiques :**
- Créer un **Bon de Réception** (`POST /api/receipts`) lors de l'arrivée de marchandises (avec contrôle qualité).
- Créer un **Bon de Sortie** (`POST /api/issues`) pour préparer une expédition client ou une sortie interne.
- Créer un **Transfert** (`POST /api/transfers`) pour déplacer des produits entre entrepôts.

**B. Réceptionner les transferts :**
- Réceptionner physiquement un transfert arrivé (`POST /api/transfers/{id}/receive`) pour ajouter les produits dans le stock de destination.

**C. Réaliser les inventaires :**
- Saisir les **comptages physiques** (`POST /api/inventories/{id}/count`) sur les produits que vous avez comptés dans la zone dédiée.
- Démarrer un inventaire planifié (`POST /api/inventories/{id}/start`) pour visualiser la liste théorique des produits à compter.

**D. Gérer les Emplacements :**
- Changer le statut d'un emplacement (ex : `OCCUPE`, `LIBRE`, `RESERVE`) via `PATCH /api/locations/{id}/status` pour refléter l'occupation réelle du rack/étagère.

**E. Consulter et alerter :**
- Consulter les niveaux de stock en temps réel (`/api/stocks`).
- Visualiser les alertes (`/api/alerts`) pour savoir quels produits sont en rupture ou en surstock.

> ⚠️ **Limitation** : Vous ne pouvez **PAS** valider définitivement les bons (réception/expédition/inventaire). La validation nécessite un **Gestionnaire** (séparation des tâches pour éviter les fraudes/erreurs).

---

## 5. Rôle 3 : Le Gestionnaire (Supervision & Pilotage)

*Vous êtes le responsable opérationnel. Vous validez les actions des magasiniers, gérez les fournisseurs et les approvisionnements.*

**📊 Ce que vous pouvez faire (en plus des droits Magasinier) :**

**A. Valider les opérations (Approbation) :**
- **Valider les Bons de réception** (`POST /api/receipts/{id}/validate`) : Cela **ajoute** définitivement les produits en stock.
- **Valider les Bons de sortie** (`POST /api/issues/{id}/validate`) : Cela **déduit** définitivement les produits du stock.
- **Expédier les Transferts** (`POST /api/transfers/{id}/ship`) : Cela **déduit** du stock source.
- **Valider les Inventaires** (`POST /api/inventories/{id}/validate`) : Cela **ajuste automatiquement** les écarts constatés.

**B. Gérer les approvisionnements (Achats) :**
- Créer et gérer les **Commandes Fournisseurs** (`/api/purchase-orders`) : de la saisie du brouillon à l'envoi au fournisseur.
- Suivre les commandes pour déclencher les réceptions.

**C. Gérer les référentiels (hors sécurité) :**
- Créer/Modifier des **Produits**, **Catégories**, **Fournisseurs**, **Entrepôts**, **Zones** et **Emplacements**.
- Marquer un fournisseur ou un produit comme "inactif" si vous ne travaillez plus avec.

**D. Gérer les Alertes :**
- Marquer les alertes comme **"traitées"** (`POST /api/alerts/{id}/treat`) une fois que vous avez pris les mesures correctives (ex: réapprovisionnement).

**E. Pilotage stratégique :**
- Générer tous les rapports de gestion (performance fournisseurs, valeur des stocks, historiques).
- Vision complète du Dashboard KPI.

> ⚠️ **Limitation** : Vous ne pouvez pas gérer les comptes utilisateurs ni consulter les logs d'audit (réservé à l'Admin).

---

## 6. Rôle 4 : L'Administrateur (Paramétrage & Sécurité)

*Vous avez les clés du système. Vous configurez l'application et gérez les accès.*

**🔧 Ce que vous pouvez faire (exclusivement) :**

**A. Gestion des Utilisateurs (`/api/users`) :**
- Créer, modifier, activer ou désactiver des comptes.
- Réinitialiser les mots de passe.
- Assigner les rôles (Auditeur, Magasinier, Gestionnaire, Administrateur).

**B. Configuration technique :**
- Consulter les logs système et les traces d'audit (`/api/v1/audit`) pour détecter des anomalies ou suivre la conformité.
- Désactiver des modules ou des entrepôts (soft delete).

**C. Purge et Maintenance :**
- Lancer manuellement la génération des alertes (`POST /api/alerts/generate`).
- Nettoyer les alertes anciennes (automatisé, mais vous pouvez forcer si nécessaire).

**D. Accès complet :**
- Vous avez tous les droits d'un Gestionnaire + Auditeur, sans restriction.

---

## 7. Focus : Cycle de vie des documents (Workflows)

Pour bien comprendre votre rôle, voici les workflows standards :

### 🔄 Cycle d'une Réception (Entrée de stock)
1.  **Magasinier** : Crée le bon de réception (Brouillon) → Soumet pour validation.
2.  **Gestionnaire** : Valide la réception → Le stock est **augmenté** automatiquement.
3.  **Auditeur/Admin** : Peut consulter l'historique du document.

### 🔄 Cycle d'une Sortie (Expédition)
1.  **Magasinier** : Crée le bon de sortie (Brouillon) → Soumet pour validation.
2.  **Gestionnaire** : Valide la sortie → Le stock est **diminué** automatiquement.

### 🔄 Cycle d'un Inventaire (Comptage physique)
1.  **Gestionnaire** : Planifie l'inventaire (périmètre : entrepôt, zone, catégorie) → Statut *Planifié*.
2.  **Gestionnaire** : Démarre l'inventaire → Le système charge les quantités théoriques.
3.  **Magasinier** : Saisit les quantités comptées produit par produit.
4.  **Gestionnaire** : Valide l'inventaire → Le système **ajuste automatiquement** les écarts (ajout/retrait) et génère des mouvements de traçabilité.

### 🔄 Cycle d'un Transfert (inter-entrepôts)
1.  **Magasinier/Gestionnaire** : Crée le transfert (Brouillon).
2.  **Gestionnaire** : Expédie le transfert → Stock **source** diminue (en transit).
3.  **Magasinier** (destination) : Réceptionne le transfert → Stock **destination** augmente.

---

## 8. Guide pratique des actions rapides

| Si vous voulez... | Action | Rôle minimum |
| :--- | :--- | :--- |
| **Voir la valeur totale de mon stock** | Accéder au Dashboard `GET /api/dashboard/kpis` | Auditeur |
| **Ajouter un nouveau produit au catalogue** | `POST /api/products` | Gestionnaire |
| **Saisir que j'ai bien reçu 100 cartons** | Créer et soumettre un bon de réception | Magasinier |
| **Approuver la réception pour la mettre en stock** | Valider le bon de réception | Gestionnaire |
| **Savoir qui a supprimé un fournisseur** | Consulter les logs d'audit `GET /api/v1/audit/logs` | Administrateur |
| **Corriger un stock (trop perçu / casse)** | Créer un inventaire, compter le produit, puis valider l'inventaire | Gestionnaire |
| **Désactiver un collaborateur qui quitte l'entreprise** | `PATCH /api/users/{id}/deactivate` | Administrateur |

---

## 9. Conclusion et Recommandations

- **En tant que Magasinier** : Concentrez-vous sur la qualité de vos saisies de comptage et la vérification des quantités.
- **En tant que Gestionnaire** : Ne validez jamais un document sans vérifier les pièces justificatives (qualité des produits, quantité réelle).
- **En tant qu'Auditeur** : Utilisez les rapports CSV/Excel pour faire vos analyses mensuelles hors ligne.
- **En tant qu'Admin** : Assurez-vous que les tâches planifiées (Alertes / Nettoyage) tournent correctement pour éviter la saturation de la base de données.

N'hésitez pas à consulter la documentation Swagger de l'API (généralement accessible via `/swagger-ui.html`) pour la liste exhaustive des endpoints et leurs paramètres.
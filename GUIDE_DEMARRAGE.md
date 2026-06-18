# 🚀 Guide de démarrage StockMaster

## ✅ État actuel du projet

### Backend (Spring Boot)
- ✅ Architecture complète avec tous les modules
- ✅ Authentification JWT (login, refresh, logout, me)
- ✅ Sécurité Spring Security 6 configurée
- ✅ Base de données MySQL avec Hibernate
- ✅ Swagger UI pour tester l'API
- ✅ Utilisateur admin par défaut créé

### Frontend (Vue.js 3)
- ✅ **100% complet** avec toutes les vues (20+)
- ✅ Données mockées pour tester sans backend
- ✅ 4 comptes démo avec différents rôles
- ✅ Design moderne avec PrimeVue
- ✅ Graphiques ApexCharts
- ✅ Navigation complète avec sidebar

---

## 📋 Prérequis

### Logiciels nécessaires
- ✅ Java 17 (installé)
- ✅ Maven 3.9.14 (installé)
- ✅ Node.js et npm (vérifier avec `node --version`)
- ✅ MySQL (Laragon démarré)

---

## 🗄️ Étape 1 : Préparer la base de données

### 1.1 Créer la base de données
1. Ouvrir **HeidiSQL** (depuis Laragon)
2. Se connecter à MySQL
3. Cliquer sur "Nouveau" → "Base de données"
4. Nom : `stockmaster_db`
5. Encodage : `utf8mb4_unicode_ci`
6. Cliquer sur "OK"

### 1.2 Vérifier la connexion
La base de données sera automatiquement remplie au premier démarrage du backend.

---

## 🔧 Étape 2 : Démarrer le backend

### 2.1 Aller dans le dossier backend
```bash
cd backend/stockmaster
```

### 2.2 Compiler et démarrer
```bash
mvn clean install
mvn spring-boot:run
```

### 2.3 Attendre le message de succès
```
Started StockmasterApplication in X seconds
```

### 2.4 Tester l'API
Ouvrir dans le navigateur : **http://localhost:8080/swagger-ui.html**

### 2.5 Tester le login
Dans Swagger :
1. Aller sur `POST /api/auth/login`
2. Cliquer sur "Try it out"
3. Entrer :
```json
{
  "email": "admin@stockmaster.com",
  "password": "Admin@1234"
}
```
4. Cliquer sur "Execute"
5. Vous devez recevoir un `accessToken` et un `refreshToken`

---

## 🎨 Étape 3 : Démarrer le frontend

### 3.1 Ouvrir un **nouveau terminal**
Ne pas fermer le terminal du backend !

### 3.2 Aller dans le dossier frontend
```bash
cd frontend
```

### 3.3 Installer les dépendances (si pas déjà fait)
```bash
npm install
```

### 3.4 Démarrer le serveur de développement
```bash
npm run dev
```

### 3.5 Ouvrir dans le navigateur
**http://localhost:5173**

---

## 👤 Étape 4 : Se connecter au frontend

### Comptes de démonstration disponibles

#### 1️⃣ Admin (tous les droits)
- **Email** : `admin@stockmaster.com`
- **Mot de passe** : `Admin1234`
- **Droits** : Accès complet à toutes les fonctionnalités

#### 2️⃣ Manager
- **Email** : `manager@stockmaster.com`
- **Mot de passe** : `Manager1234`
- **Droits** : Gestion des entrepôts, produits, fournisseurs, stocks

#### 3️⃣ Opérateur
- **Email** : `operator@stockmaster.com`
- **Mot de passe** : `Operator1234`
- **Droits** : Réceptions, sorties, transferts

#### 4️⃣ Auditeur
- **Email** : `auditor@stockmaster.com`
- **Mot de passe** : `Auditor1234`
- **Droits** : Consultation uniquement (lecture seule)

---

## 🧪 Étape 5 : Tester les fonctionnalités

### Menu de navigation (sidebar à gauche)

#### 📊 Dashboard
- Vue d'ensemble avec KPIs
- Graphiques des ventes et des mouvements
- Top produits

#### 🏢 Gestion des entrepôts
- **Entrepôts** : Liste avec cartes visuelles
- **Zones** : Organisation par zones
- **Emplacements** : Détails des emplacements

#### 📦 Gestion des produits
- **Catégories** : Organisation des produits
- **Produits** : Liste complète avec recherche
- **Fournisseurs** : Gestion des fournisseurs

#### 📈 Gestion des stocks
- **Stocks** : Vue d'ensemble des niveaux
- **Mouvements** : Historique complet

#### 🔄 Opérations
- **Réceptions** : Entrées de marchandises
- **Sorties** : Sorties de marchandises
- **Transferts** : Transferts entre entrepôts
- **Commandes** : Commandes d'achat

#### 🔍 Suivi
- **Inventaires** : Inventaires physiques
- **Alertes** : Alertes de stock
- **Rapports** : Rapports statistiques
- **Logs d'audit** : Historique des actions

#### ⚙️ Administration
- **Utilisateurs** : Gestion des utilisateurs
- **Mon profil** : Profil personnel

---

## 🎯 Fonctionnalités à tester

### 1. Navigation
- ✅ Cliquer sur chaque menu
- ✅ Vérifier que les pages se chargent
- ✅ Tester le bouton de collapse du sidebar

### 2. Authentification
- ✅ Se déconnecter
- ✅ Se reconnecter avec un autre compte
- ✅ Vérifier que les permissions sont respectées

### 3. Données mockées
- ✅ Voir les entrepôts (3 entrepôts)
- ✅ Voir les produits (10 produits)
- ✅ Voir les fournisseurs
- ✅ Voir les mouvements de stock
- ✅ Voir les alertes

### 4. Formulaires
- ✅ Créer un nouvel entrepôt
- ✅ Créer un nouveau produit
- ✅ Créer une nouvelle réception
- ✅ Créer une nouvelle sortie

### 5. Détails
- ✅ Cliquer sur un entrepôt pour voir les détails
- ✅ Cliquer sur un produit pour voir les détails
- ✅ Cliquer sur un fournisseur pour voir les détails

### 6. Graphiques (Dashboard)
- ✅ Graphique des ventes (ligne)
- ✅ Graphique des mouvements (colonnes)
- ✅ Top 5 des produits

### 7. Permissions selon les rôles
- ✅ **Admin** : peut tout faire
- ✅ **Manager** : ne peut pas gérer les utilisateurs
- ✅ **Opérateur** : ne peut pas voir les rapports
- ✅ **Auditeur** : lecture seule (pas de boutons d'action)

---

## 🐛 Résolution des problèmes courants

### Backend ne démarre pas
```bash
# Vérifier que MySQL est démarré dans Laragon
# Vérifier que le port 8080 est libre
netstat -ano | findstr :8080

# Si un processus utilise le port, le tuer
taskkill /PID <PID> /F

# Nettoyer et recompiler
mvn clean
mvn spring-boot:run
```

### Frontend ne démarre pas
```bash
# Supprimer node_modules et réinstaller
rm -rf node_modules
rm package-lock.json
npm install
npm run dev
```

### Erreur de connexion à la base de données
1. Vérifier que Laragon est démarré
2. Vérifier que MySQL tourne (icône verte dans Laragon)
3. Ouvrir HeidiSQL et se connecter
4. Vérifier que la base `stockmaster_db` existe

### Page blanche dans le navigateur
1. Ouvrir la console du navigateur (F12)
2. Vérifier les erreurs JavaScript
3. Vérifier que `npm run dev` est bien démarré
4. Essayer de vider le cache du navigateur (Ctrl + F5)

---

## 📝 Prochaines étapes

### Phase 1 : Backend (modules à implémenter)
Les contrôleurs existent mais retournent des stubs. À implémenter :
1. **WarehouseService** : CRUD complet des entrepôts
2. **CategoryService** : CRUD des catégories
3. **ProductService** : CRUD des produits avec gestion des images
4. **SupplierService** : CRUD des fournisseurs
5. **StockService** : Gestion des stocks et mouvements
6. **ZoneService** : CRUD des zones
7. **LocationService** : CRUD des emplacements

### Phase 2 : Intégration frontend-backend
1. Créer `api.js` avec Axios
2. Remplacer les appels à `mockData.ts` par des appels API
3. Gérer les erreurs réseau
4. Ajouter des loaders pendant les requêtes

### Phase 3 : Fonctionnalités avancées
1. Réceptions, sorties, transferts (backend + intégration)
2. Inventaires physiques
3. Commandes d'achat
4. Rapports et statistiques
5. Notifications en temps réel

---

## 📞 Support

Si vous rencontrez des problèmes :
1. Vérifier les logs du backend dans la console
2. Vérifier les erreurs frontend dans la console du navigateur (F12)
3. Vérifier que tous les services sont démarrés (MySQL, backend, frontend)

---

## 🎉 Bon développement !

Le frontend est **100% fonctionnel** avec des données mockées.
Vous pouvez naviguer, créer, modifier, supprimer des entités.
Tout est prêt pour l'intégration avec le backend !

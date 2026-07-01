# 🧪 Guide de test StockMaster
## "Comprendre le projet en le testant comme un vrai entrepôt"

> **Connexion :** http://localhost:5173

---

## 👥 Les 4 rôles — qui fait quoi ?

| Rôle | Email | Mot de passe | Responsabilité |
|------|-------|-------------|----------------|
| **Administrateur** | admin@stockmaster.com | Admin1234 | Configure tout : entrepôts, produits, utilisateurs |
| **Manager** | manager@stockmaster.com | Manager1234 | Valide les bons, gère son entrepôt |
| **Opérateur** | operator@stockmaster.com | Operator1234 | Crée les bons de réception et de sortie |
| **Auditeur** | auditor@stockmaster.com | Auditor1234 | Consulte tout, ne modifie rien |

> 💡 **Règle de base :** l'opérateur fait le travail physique (recevoir, sortir). Le manager contrôle et valide. L'administrateur configure. L'auditeur surveille.

---

## 🗺️ Le flow complet dans l'ordre

```
ADMINISTRATEUR                    MANAGER                      OPÉRATEUR
      │                               │                             │
 [Tâche 1]                            │                             │
 Crée l'entrepôt                      │                             │
 Crée les zones                       │                             │
      │                               │                             │
 [Tâche 2]                            │                             │
 Crée les catégories                  │                             │
 Crée les fournisseurs                │                             │
 Crée les produits                    │                             │
      │                               │                             │
      │                          [Tâche 3]                         │
      │                     Passe une commande                      │
      │                     fournisseur (CF-)                       │
      │                               │                             │
      │                               │                        [Tâche 4]
      │                               │               Camion arrive → crée
      │                               │               bon de réception (BR-)
      │                               │                             │
      │                          [Tâche 5]                         │
      │                     Valide le bon → stock                   │
      │                          augmente ✅                        │
      │                               │                             │
      │                               │                        [Tâche 6]
      │                               │               Client commande → crée
      │                               │               bon de sortie (BS-)
      │                               │                             │
      │                          [Tâche 7]                         │
      │                     Valide la sortie → stock                │
      │                          diminue ✅                         │
      │                               │                             │
      │                          [Tâche 8]                         │
      │                     Transfère entre                         │
      │                     entrepôts (TRF-)                        │
      │                               │                             │
 [Tâche 9]                            │                        [Tâche 9]
 Planifie l'inventaire           Clôture                  Compte physiquement
                                 l'inventaire              les produits
```

---

## ✅ TÂCHE 1 — Créer la structure physique
### 👤 Qui : Administrateur
### Pourquoi en premier ?
Avant de stocker quoi que ce soit, il faut un endroit où le mettre. L'admin crée l'entrepôt et l'organise en zones.

**Se connecter avec :** admin@stockmaster.com / Admin1234

**Menu :** Entrepôts → **"Nouvel entrepôt"**

```
Nom           : Entrepôt Cotonou-Nord
Ville         : Cotonou
Pays          : Bénin
Adresse       : 12 Boulevard du Commerce
Téléphone     : +229 21 30 40 50
Email         : cotonou@techdistrib.com
Capacité      : 2000  (m³)
```

✅ Résultat : la carte de l'entrepôt apparaît dans la liste.

> 💡 **Pourquoi la capacité en m³ ?**
> Si tu mets 1900 m³ de produits dans 2000 m³, tu es à 95% — l'application t'alerte avant que tu sois bloqué.

**Voir les zones existantes :** clique sur **"Zones"** de ENT-001 Paris-Nord

> 💡 **Pourquoi des zones ?**
> Un entrepôt est découpé en espaces distincts :
> - **Réception** → là où les camions déchargent
> - **Stockage** → les rayons où tout est rangé
> - **Expédition** → là où on prépare les commandes clients
> - **Quarantaine** → les produits abîmés ou à vérifier
>
> Tu ne mélangés pas les marchandises qui arrivent avec celles qui partent.

**Voir un emplacement :** Zones → **"Voir les emplacements"** → clique sur l'œil 👁

> 💡 **Pourquoi un code comme `ENT001-ZONEA-R01-E01-P01` ?**
> Dans un bâtiment de 5000 m², un magasinier doit trouver un produit en 2 minutes.
> Ce code lui dit exactement : Entrepôt 001 → Zone A → Rayon 01 → Étagère 01 → Position 01.
> C'est l'adresse GPS d'une étagère. Il peut scanner le QR Code avec son téléphone.

---

## ✅ TÂCHE 2 — Créer le catalogue
### 👤 Qui : Administrateur
### Pourquoi ?
L'admin définit ce que l'entreprise vend, à quel prix, et les seuils d'alerte.

**2.1 — Créer une catégorie**

**Menu :** Catégories → **"Nouvelle catégorie"**

```
Nom    : Téléphonie
Parent : — (catégorie principale)
```

Puis une sous-catégorie :
```
Nom    : Smartphones
Parent : Téléphonie
```

> 💡 Maximum 2 niveaux de hiérarchie. L'application le bloque automatiquement.

**2.2 — Créer un fournisseur**

**Menu :** Fournisseurs → **"Nouveau fournisseur"**

```
Nom société       : MobileImport SARL
Ville             : Paris
Pays              : France
Email             : contact@mobileimport.fr
Téléphone         : +33 1 45 67 89 01
Prénom contact    : Jean
Nom contact       : Kouassi
Délai livraison   : 7  (jours)
```

> 💡 Le délai de livraison permet à l'application de te prévenir si la commande est en retard.

**2.3 — Créer un produit**

**Menu :** Produits → **"Nouveau produit"**

```
Nom              : Samsung Galaxy A55
Catégorie        : Smartphones
Fournisseur      : MobileImport SARL
Prix d'achat     : 280
Prix de vente    : 449
Stock minimum    : 5    ← alerte rouge si on descend en dessous
Stock maximum    : 50   ← alerte bleue si on dépasse
Unité            : UNITE
Poids            : 0.2  kg
Code-barres      : 8806094776756
```

✅ Résultat : le produit apparaît avec un indicateur 🔴 rouge (stock = 0, en dessous du minimum de 5).

> 💡 **Pourquoi rouge dès la création ?**
> Le produit existe dans le catalogue mais aucune marchandise n'est encore arrivée. C'est normal. La tâche suivante va corriger ça.

---

## ✅ TÂCHE 3 — Passer une commande au fournisseur
### 👤 Qui : Manager
### Pourquoi maintenant ?
Le manager voit que le Samsung Galaxy A55 est en rupture. Il décide de commander.

**Se connecter avec :** manager@stockmaster.com / Manager1234

**Menu :** Commandes fournisseurs → **"Nouvelle commande"**

```
Fournisseur           : MobileImport SARL
Entrepôt destination  : ENT-001 Paris-Nord
Date livraison prévue : (dans 7 jours)
Note                  : Commande préventive Galaxy A55

── Ajouter une ligne ──
Produit               : Samsung Galaxy A55
Quantité              : 20
Prix unitaire         : 280
```

> 💡 Le total se calcule automatiquement : 20 × 280 = **5 600 €**

**Cliquer "Créer"** → statut : **BROUILLON**
**Cliquer "Valider"** → statut : **VALIDÉE**
**Cliquer "Envoyer"** → statut : **ENVOYÉE** (le fournisseur a reçu la commande)

✅ Résultat : la commande CF-XXXX apparaît dans la liste.

> 💡 **Que se passe-t-il si la livraison est en retard ?**
> Si la date prévue passe sans réception, la commande apparaît en **rouge ⚠️** dans la liste.
> Sans StockMaster, tu le découvrirais quand un client commande et qu'il n'y a plus rien.

---

## ✅ TÂCHE 4 — Le camion arrive (Bon de réception)
### 👤 Qui : Opérateur (crée) → Manager (valide)
### Pourquoi cette séparation ?
L'opérateur fait le travail physique : il compte les cartons. Le manager vérifie et valide. Si quelqu'un crée de faux bons, le manager le voit avant que le stock soit modifié.

**Se connecter avec :** operator@stockmaster.com / Operator1234

**Menu :** Réceptions → **"Nouveau bon"**

```
Fournisseur       : MobileImport SARL
Entrepôt          : ENT-001 Paris-Nord
Date de réception : (aujourd'hui)
Note              : Lié à commande CF-XXXX

── Ajouter une ligne ──
Produit           : Samsung Galaxy A55
Qté attendue      : 20
Qté reçue         : 18    ← 2 cartons manquants dans le camion !
Qualité OK        : ✅
```

> 💡 **Pourquoi qté attendue ≠ qté reçue ?**
> Le fournisseur avait promis 20 mais 2 cartons ont été endommagés pendant le transport.
> L'application enregistre l'écart. Seule la quantité **reçue** (18) entrera dans le stock.

**Cliquer "Soumettre pour validation"** → statut passe à **EN_ATTENTE_VALIDATION**

L'opérateur a fini son travail. Il ne peut pas valider lui-même.

---

**Se connecter avec :** manager@stockmaster.com / Manager1234

**Menu :** Réceptions → cliquer l'œil 👁 sur le bon → **"Valider"**
apr

---

## ✅ TÂCHE 5 — Un client passe une commande (Bon de sortie)
### 👤 Qui : Opérateur (crée) → Manager (valide)

**Se connecter avec :** operator@stockmaster.com / Operator1234

**Menu :** Sorties → **"Nouveau bon"**

```
Entrepôt          : ENT-001 Paris-Nord
Motif             : LIVRAISON_CLIENT
Nom du client     : Boutique Lomé Tech
Référence client  : BLT-2026-456
Date de sortie    : (aujourd'hui)

── Ajouter une ligne ──
Produit           : Samsung Galaxy A55
Quantité          : 3
```

**Cliquer "Soumettre"** → EN_ATTENTE_VALIDATION

---

**Se connecter avec :** manager@stockmaster.com / Manager1234

**Menu :** Sorties → **"Valider"** sur le bon

> 💡 **Protection automatique :** si tu mets 25 (alors qu'il reste 18), la validation est **bloquée** :
> *"Stock insuffisant : disponible=18, demandé=25"*
> Ça évite les erreurs humaines et les sorties fantômes.

✅ Résultat :
- Stock Samsung Galaxy A55 : **18 → 15 unités**
- Mouvements → SORTIE de 3 unités enregistrée
- Montant du bon : 3 × 449 € = **1 347 €**

---

## ✅ TÂCHE 6 — Rééquilibrer entre entrepôts (Transfert)
### 👤 Qui : Manager (crée et gère)
### Pourquoi ?
Lyon n'a plus de Galaxy A55. Paris en a 15. Au lieu de commander au fournisseur (7 jours de délai), on transfère en interne (2 jours).

**Se connecter avec :** manager@stockmaster.com / Manager1234

**Menu :** Transferts → **"Nouveau transfert"**

```
Source      : ENT-001 Paris-Nord
Destination : ENT-002 Lyon-Sud
Note        : Rééquilibrage Galaxy A55

── Ajouter une ligne ──
Produit     : Samsung Galaxy A55
Quantité    : 5
```

**Créer** → statut : BROUILLON

**Cliquer "Expédier"** → statut : **EXPÉDIÉ**

> 💡 **À ce moment :** Paris perd 5 unités (15 → 10). Ces 5 sont **"en transit"** : ni à Paris ni à Lyon.
> Si le camion tombe en panne, tu sais exactement où sont tes produits.

**Cliquer "Marquer reçu"** → statut : **REÇU**

✅ Résultat dans Stocks :
- Paris : **10** Samsung Galaxy A55
- Lyon : **5** Samsung Galaxy A55

---

## ✅ TÂCHE 7 — Inventaire physique
### 👤 Qui : Manager (planifie et clôture) + Opérateur (compte)
### Pourquoi ?
Le système dit 10 unités à Paris. Ton opérateur en compte physiquement 9. Quelqu'un a fait une erreur de saisie. L'inventaire détecte et corrige l'écart.

**Se connecter avec :** manager@stockmaster.com / Manager1234

**Menu :** Inventaires → **"Planifier un inventaire"**

```
Type              : PARTIEL
Entrepôt          : ENT-001 Paris-Nord
Date planifiée    : (aujourd'hui)
Note              : Vérification mensuel smartphones
```

---

**Se connecter avec :** operator@stockmaster.com / Operator1234

**Menu :** Inventaires → **"Démarrer"** sur l'inventaire créé

Dans le tableau de saisie :
```
Samsung Galaxy A55
Qté système   : 10   (ce que dit l'ordinateur)
Qté comptée   : 9    ← compté physiquement
Écart         : -1   (s'affiche en rouge)
```

---

**Se connecter avec :** manager@stockmaster.com / Manager1234

**Menu :** Inventaires → **"Clôturer"**

✅ Résultat :
- Stock Samsung Galaxy A55 passe de 10 → **9 unités**
- Mouvements → AJUSTEMENT_INVENTAIRE de -1 enregistré automatiquement

> 💡 **Cet ajustement est obligatoire légalement.** Pour la comptabilité et les impôts, chaque écart doit être justifié et tracé.

---

## ✅ TÂCHE 8 — Surveiller les alertes
### 👤 Qui : Tous les rôles (chacun voit les alertes de son périmètre)

**Menu :** Alertes

Ces alertes se déclenchent **automatiquement** sans intervention :

| Alerte | Déclencheur | Qui doit agir |
|--------|------------|---------------|
| 🔴 Stock critique | Quantité < minimum | Manager → passer une commande |
| ⚠️ Commande en retard | Date livraison dépassée | Manager → appeler fournisseur |
| 🟡 Zone saturée | Occupation > 90% | Admin → créer une nouvelle zone |
| 🔵 Stock excédentaire | Quantité > maximum | Manager → arrêter les commandes |

**Cliquer "Traiter"** sur une alerte → tu confirmes l'avoir vue et prise en charge.

---

## ✅ TÂCHE 9 — Vérifier la traçabilité complète
### 👤 Qui : Auditeur (et Admin)

**Se connecter avec :** auditor@stockmaster.com / Auditor1234

> 💡 **L'auditeur ne peut rien modifier.** Il peut seulement consulter. Aucun bouton d'action n'est visible.

**Menu :** Audit

Tu vois chaque action avec :
- Qui l'a faite
- Quand exactement
- L'ancienne valeur et la nouvelle valeur
- L'adresse IP de l'ordinateur utilisé

**Menu :** Rapports → générer un **"Rapport des mouvements"** pour juin 2026

> 💡 Ce rapport sert au comptable, au directeur, et à l'administration fiscale.

---

## 📊 Récapitulatif — ce que tu auras créé

À la fin des 9 tâches, voici l'état du stock Samsung Galaxy A55 à Paris :

| Opération | Qté | Stock après |
|-----------|-----|------------|
| Création produit | — | 0 |
| Bon de réception validé | +18 | 18 |
| Bon de sortie validé | -3 | 15 |
| Transfert vers Lyon | -5 | 10 |
| Ajustement inventaire | -1 | **9** |

> 🎯 **Si tu arrives à 9 unités à Paris et 5 à Lyon, toute la chaîne fonctionne correctement.**

---

## 🐛 Si quelque chose ne fonctionne pas

Note l'anomalie ainsi et envoie-la :

```
PAGE     : /receipts/new
RÔLE     : Opérateur
ACTION   : Clic sur "Soumettre"
PROBLÈME : Rien ne se passe
CONSOLE  : (F12 → Console → copier l'erreur rouge)
```

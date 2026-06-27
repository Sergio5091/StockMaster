# Explication du code des modules Catégories, Produits et Fournisseurs

Ce document explique comment le code de ces modules a été organisé dans ce projet StockMaster, à partir du code actuel du backend Spring Boot et du frontend Vue 3.

---

## 1. Vue d’ensemble

Le projet suit une architecture simple et claire :

- Backend : Spring Boot + JPA/Hibernate + REST API
- Frontend : Vue 3 + TypeScript + Axios
- Structure logique :
  - Domaine (entités)
  - DTOs (objets de transfert)
  - Mapper
  - Repository
  - Service
  - Controller
  - Vue/Service frontend

Les trois modules fonctionnent de manière similaire, avec une séparation nette entre la logique métier et l’interface utilisateur.

---

## 2. Module Catégories

### Objectif
Le module catégories permet de créer, modifier, consulter et désactiver des catégories de produits. Il a aussi été pensé pour supporter une structure hiérarchique avec des sous-catégories.

### Fichiers principaux

- Backend
  - [backend/stockmaster/src/main/java/com/backend/stockmaster/category/domain/Category.java](backend/stockmaster/src/main/java/com/backend/stockmaster/category/domain/Category.java)
  - [backend/stockmaster/src/main/java/com/backend/stockmaster/category/interfaces/CategoryController.java](backend/stockmaster/src/main/java/com/backend/stockmaster/category/interfaces/CategoryController.java)
  - [backend/stockmaster/src/main/java/com/backend/stockmaster/category/application/service/CategoryApplicationService.java](backend/stockmaster/src/main/java/com/backend/stockmaster/category/application/service/CategoryApplicationService.java)
  - [backend/stockmaster/src/main/java/com/backend/stockmaster/category/application/mapper/CategoryMapper.java](backend/stockmaster/src/main/java/com/backend/stockmaster/category/application/mapper/CategoryMapper.java)
  - [backend/stockmaster/src/main/java/com/backend/stockmaster/category/repository/CategoryRepository.java](backend/stockmaster/src/main/java/com/backend/stockmaster/category/repository/CategoryRepository.java)

- Frontend
  - [frontend/src/services/category.service.ts](frontend/src/services/category.service.ts)

### Comment le code a été pensé

1. L’entité Category représente une catégorie avec :
   - id
   - nom
   - description
   - parentId
   - actif
   - dates de création/modification

2. La couche service gère les règles métier :
   - vérification du nom unique
   - validation du parent
   - interdiction d’une catégorie qui se référence elle-même
   - désactivation au lieu d’une suppression physique

3. Le contrôleur expose des endpoints REST pour :
   - lister les catégories
   - récupérer une catégorie par ID
   - récupérer les sous-catégories
   - créer une catégorie
   - modifier une catégorie
   - désactiver une catégorie

### Flux de fonctionnement

- L’utilisateur envoie une requête via l’API.
- Le contrôleur reçoit la demande.
- Le service applique les règles métier.
- Le repository sauvegarde ou récupère les données.
- La réponse est renvoyée au frontend.

### Points importants

- Les catégories ne sont pas supprimées définitivement : elles sont simplement marquées comme inactives.
- Le modèle est prêt pour une logique de hiérarchie.
- Le code est organisé de façon simple et maintenable.

---

## 3. Module Produits

### Objectif
Le module produits permet de gérer les produits du stock : création, modification, recherche, consultation par code-barres, upload d’image et logique de stock minimum/maximum.

### Fichiers principaux

- Backend
  - [backend/stockmaster/src/main/java/com/backend/stockmaster/product/domain/Product.java](backend/stockmaster/src/main/java/com/backend/stockmaster/product/domain/Product.java)
  - [backend/stockmaster/src/main/java/com/backend/stockmaster/product/interfaces/ProductController.java](backend/stockmaster/src/main/java/com/backend/stockmaster/product/interfaces/ProductController.java)
  - [backend/stockmaster/src/main/java/com/backend/stockmaster/product/application/service/ProductService.java](backend/stockmaster/src/main/java/com/backend/stockmaster/product/application/service/ProductService.java)
  - [backend/stockmaster/src/main/java/com/backend/stockmaster/product/application/mapper/ProductMapper.java](backend/stockmaster/src/main/java/com/backend/stockmaster/product/application/mapper/ProductMapper.java)
  - [backend/stockmaster/src/main/java/com/backend/stockmaster/product/repository/ProductRepository.java](backend/stockmaster/src/main/java/com/backend/stockmaster/product/repository/ProductRepository.java)

- Frontend
  - [frontend/src/services/product.service.ts](frontend/src/services/product.service.ts)
  - [frontend/src/views/products/ProductsView.vue](frontend/src/views/products/ProductsView.vue)
  - [frontend/src/views/products/ProductFormModal.vue](frontend/src/views/products/ProductFormModal.vue)

### Comment le code a été pensé

1. L’entité Product contient les informations essentielles d’un produit :
   - référence
   - code-barres
   - nom
   - description
   - catégorie
   - fournisseur principal
   - prix d’achat
   - prix de vente
   - stock minimum et maximum
   - unité de mesure
   - image
   - état actif/inactif

2. La logique métier a été ajoutée dans le service :
   - génération automatique d’une référence unique
   - validation de la catégorie
   - recherche par nom ou référence
   - recherche par code-barres
   - désactivation du produit
   - mise à jour de l’URL de l’image

3. Le contrôleur expose plusieurs endpoints REST :
   - liste paginée des produits
   - détail d’un produit
   - recherche
   - recherche par code-barres
   - création
   - modification
   - désactivation
   - upload d’image

### Ce qui rend ce module fonctionnel

- Les produits sont recherchables facilement grâce à la méthode de recherche.
- La liste est paginée pour éviter la surcharge.
- Les données liées à la catégorie et au fournisseur sont enrichies afin d’afficher des libellés lisibles côté frontend.
- L’upload d’image permet d’associer une image à chaque produit.

### Flux de fonctionnement

- Le frontend collecte les données du formulaire.
- Le service envoie les données à l’API.
- Le backend génère une référence si nécessaire.
- Le produit est sauvegardé dans la base.
- Le frontend affiche la liste mise à jour.



## 4. Module Fournisseurs

### Objectif
Le module fournisseurs permet de gérer les fournisseurs de l’entreprise avec leurs informations principales, leurs coordonnées et leur statut actif/inactif.

### Fichiers principaux

- Backend
  - [backend/stockmaster/src/main/java/com/backend/stockmaster/supplier/domain/Supplier.java](backend/stockmaster/src/main/java/com/backend/stockmaster/supplier/domain/Supplier.java)
  - [backend/stockmaster/src/main/java/com/backend/stockmaster/supplier/interfaces/SupplierController.java](backend/stockmaster/src/main/java/com/backend/stockmaster/supplier/interfaces/SupplierController.java)
  - [backend/stockmaster/src/main/java/com/backend/stockmaster/supplier/application/service/SupplierService.java](backend/stockmaster/src/main/java/com/backend/stockmaster/supplier/application/service/SupplierService.java)
  - [backend/stockmaster/src/main/java/com/backend/stockmaster/supplier/application/mapper/SupplierMapper.java](backend/stockmaster/src/main/java/com/backend/stockmaster/supplier/application/mapper/SupplierMapper.java)
  - [backend/stockmaster/src/main/java/com/backend/stockmaster/supplier/repository/SupplierRepository.java](backend/stockmaster/src/main/java/com/backend/stockmaster/supplier/repository/SupplierRepository.java)

- Frontend
  - [frontend/src/services/supplier.service.ts](frontend/src/services/supplier.service.ts)
  - [frontend/src/views/suppliers/SuppliersView.vue](frontend/src/views/suppliers/SuppliersView.vue)
  - [frontend/src/views/suppliers/SupplierDetailView.vue](frontend/src/views/suppliers/SupplierDetailView.vue)

### Comment le code a été pensé

1. L’entité Supplier contient :
   - code fournisseur
   - nom
   - adresse
   - ville
   - pays
   - téléphone
   - email
   - contact principal
   - délai de livraison
   - statut actif/inactif

2. Le service génère automatiquement un code fournisseur du type SUP-001.

3. Le contrôleur expose les endpoints REST pour :
   - lister les fournisseurs
   - consulter un fournisseur
   - créer un fournisseur
   - modifier un fournisseur
   - désactiver un fournisseur

### Flux de fonctionnement

- Le formulaire frontend envoie les informations du fournisseur.
- Le backend crée l’objet et attribue un code.
- Le fournisseur est enregistré en base.
- Le frontend affiche la liste mise à jour.

---

## 5. Comment les modules sont reliés entre eux

Les modules sont liés par des références simples :

- Un produit appartient à une catégorie.
- Un produit peut avoir un fournisseur principal.

C’est pourquoi le backend enrichit la réponse du produit avec :
- le nom de la catégorie
- le nom du fournisseur

Cela permet d’afficher des informations plus lisibles dans l’interface.

---

## 6. Pourquoi ce code est bien structuré

Le code est organisé de manière professionnelle, car il respecte les principes suivants :

- Séparation des responsabilités
- Utilisation des DTOs pour éviter d’exposer directement les entités
- Utilisation des mappers pour transformer les objets
- Services pour la logique métier
- Repositories pour les accès à la base de données
- Frontend découplé avec services dédiés

---

## 7. Résumé simple

- Catégories : gestion des familles de produits
- Produits : gestion du catalogue et du stock
- Fournisseurs : gestion des partenaires commerciaux

En pratique, le système fonctionne comme suit :

1. Le frontend envoie une donnée.
2. Le backend valide et traite cette donnée.
3. La base de données enregistre l’information.
4. Le frontend affiche le résultat.

---

## 8. Conclusion

Ce code a été conçu pour être clair, extensible et facile à maintenir. Les modules Catégories, Produits et Fournisseurs sont déjà bien organisés et prêts à évoluer avec de nouvelles fonctionnalités comme :

- gestion des stocks en temps réel
- historique des prix
- gestion avancée des fournisseurs
- catégories multi-niveaux plus complètes
- upload d’images plus robuste

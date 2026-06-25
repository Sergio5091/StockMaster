# StockMaster Backend - Complete Endpoint Reference

## Base URL
`http://localhost:8080/api`

## Authentication Endpoints
**Controller**: `AuthController` → `/api/auth`

| Method | Endpoint | Description | Auth Required | Role Required |
|--------|----------|-------------|---|---|
| POST | `/login` | User login with username/password | No | — |
| POST | `/refresh` | Refresh access token | Yes | — |
| POST | `/logout` | Logout user | Yes | — |

**Request/Response Examples**:
```json
POST /api/auth/login
{
  "username": "admin",
  "password": "password123"
}

Response 200:
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "userInfo": {
    "id": 1,
    "username": "admin",
    "email": "admin@stockmaster.com",
    "fullName": "Administrator",
    "role": "ADMINISTRATEUR"
  }
}
```

---

## User Management Endpoints
**Controller**: `UserController` → `/api/users`

| Method | Endpoint | Description | Auth | Role | Pagination |
|--------|----------|-------------|------|------|------------|
| GET | `/` | List all users | ✓ | ADMIN | ✓ |
| GET | `/search` | Search users (filters: role, actif) | ✓ | ADMIN | ✓ |
| GET | `/{id}` | Get user by ID | ✓ | ADMIN | — |
| POST | `/` | Create new user | ✓ | ADMIN | — |
| PUT | `/{id}` | Update user | ✓ | ADMIN | — |
| DELETE | `/{id}` | Delete user | ✓ | ADMIN | — |

**Query Parameters**:
- `page`: 0-based page number (default: 0)
- `size`: Items per page (default: 20)
- `sort`: Sort field,direction (e.g., `username,asc`)
- `role`: Filter by role (ADMINISTRATEUR, GESTIONNAIRE, etc.)
- `actif`: Filter by active status (true/false)

---

## Product Endpoints
**Controller**: `ProductController` → `/api/products`

| Method | Endpoint | Description | Auth | Role | Notes |
|--------|----------|-------------|------|------|-------|
| GET | `/` | List all products | No | — | Filters: categorieId, fournisseurId; Paginated |
| GET | `/{id}` | Get product details | No | — | |
| GET | `/search` | Search products | No | — | Query param: `q` (search term); Paginated |
| GET | `/barcode/{code}` | Find by barcode | No | — | |
| POST | `/` | Create product | ✓ | ADMIN/MANAGER | |
| PUT | `/{id}` | Update product | ✓ | ADMIN/MANAGER | |
| DELETE | `/{id}` | Delete product | ✓ | ADMIN | |
| POST | `/{id}/upload-image` | Upload product image | ✓ | ADMIN/MANAGER | Multipart/form-data |

**Example Create Request**:
```json
POST /api/products
{
  "reference": "PROD-001",
  "codeBarre": "1234567890123",
  "nom": "Laptop Dell XPS 13",
  "description": "High-performance laptop",
  "categorieId": 1,
  "fournisseurPrincipalId": 5,
  "prixAchat": 800.00,
  "prixVente": 1200.00,
  "poidsKg": 1.5,
  "volumeM3": 0.05,
  "unitemesure": "UNIT"
}
```

---

## Category Endpoints
**Controller**: `CategoryController` → `/api/categories`

| Method | Endpoint | Description | Auth | Role |
|--------|----------|-------------|------|------|
| GET | `/` | List categories | No | — |
| GET | `/{id}` | Get category | No | — |
| POST | `/` | Create category | ✓ | ADMIN |
| PUT | `/{id}` | Update category | ✓ | ADMIN |
| DELETE | `/{id}` | Delete category | ✓ | ADMIN |

---

## Supplier Endpoints
**Controller**: `SupplierController` → `/api/suppliers`

| Method | Endpoint | Description | Auth | Role |
|--------|----------|-------------|------|------|
| GET | `/` | List suppliers | No | — |
| GET | `/{id}` | Get supplier details | No | — |
| POST | `/` | Create supplier | ✓ | ADMIN/MANAGER |
| PUT | `/{id}` | Update supplier | ✓ | ADMIN/MANAGER |
| DELETE | `/{id}` | Delete supplier | ✓ | ADMIN |

---

## Warehouse Endpoints
**Controller**: `WarehouseController` → `/api/warehouses`

| Method | Endpoint | Description | Auth | Role |
|--------|----------|-------------|------|------|
| GET | `/` | List warehouses | ✓ | — |
| GET | `/{id}` | Get warehouse details | ✓ | — |
| GET | `/{id}/stats` | Get warehouse statistics | ✓ | — |
| POST | `/` | Create warehouse | ✓ | ADMIN/MANAGER |
| PUT | `/{id}` | Update warehouse | ✓ | ADMIN/MANAGER |
| DELETE | `/{id}` | Delete warehouse | ✓ | ADMIN |

---

## Zone Endpoints
**Controller**: `ZoneController` → `/api/zones`

| Method | Endpoint | Description | Auth | Role |
|--------|----------|-------------|------|------|
| GET | `/` | List zones | ✓ | — |
| GET | `/{id}` | Get zone details | ✓ | — |
| GET | `/{id}/occupancy` | Get zone occupancy info | ✓ | — |
| POST | `/` | Create zone | ✓ | ADMIN/MANAGER |
| PUT | `/{id}` | Update zone | ✓ | ADMIN/MANAGER |
| DELETE | `/{id}` | Delete zone | ✓ | ADMIN |

---

## Location Endpoints
**Controller**: `LocationController` → `/api/locations`

| Method | Endpoint | Description | Auth | Role |
|--------|----------|-------------|------|------|
| GET | `/` | List locations | ✓ | — |
| GET | `/{id}` | Get location details | ✓ | — |
| GET | `/zone/{zoneId}` | Locations in zone | ✓ | — |
| POST | `/` | Create location | ✓ | ADMIN/MANAGER |
| PUT | `/{id}` | Update location | ✓ | ADMIN/MANAGER |
| DELETE | `/{id}` | Delete location | ✓ | ADMIN |

---

## Stock Endpoints
**Controller**: `StockController` → `/api/stocks`

| Method | Endpoint | Description | Auth | Role | Notes |
|--------|----------|-------------|------|------|-------|
| GET | `/` | List all stocks | ✓ | — | Paginated |
| GET | `/{produitId}/{entrepotId}` | Get specific stock | ✓ | — | |
| GET | `/warehouse/{warehouseId}` | All stocks in warehouse | ✓ | — | Returns List |
| GET | `/movements` | Stock movement history | ✓ | — | Deprecated; use /api/stock-movements |
| GET | `/movements/product/{produitId}` | Product movements | ✓ | — | Deprecated; use /api/stock-movements/product/{id} |

**Stock Movement Controller**: `/api/stock-movements`

| Method | Endpoint | Description | Auth | Role | Notes |
|--------|----------|-------------|------|------|-------|
| GET | `/` | List all movements | ✓ | — | Paginated |
| GET | `/{id}` | Get movement details | ✓ | — | |
| GET | `/product/{produitId}` | Movements for product | ✓ | — | Paginated |
| GET | `/warehouse/{entrepotId}` | Movements in warehouse | ✓ | — | Paginated |
| POST | `/` | Record movement | ✓ | MANAGER | Manual movement |

---

## Receipt (Goods Inbound) Endpoints
**Controller**: `GoodsReceiptController` → `/api/receipts`

| Method | Endpoint | Description | Auth | Role | Status Flow |
|--------|----------|-------------|------|------|-------------|
| GET | `/` | List receipts | ✓ | — | — |
| GET | `/{id}` | Get receipt details | ✓ | — | — |
| POST | `/` | Create receipt | ✓ | MANAGER | DRAFT |
| PUT | `/{id}` | Update receipt | ✓ | MANAGER | DRAFT only |
| POST | `/{id}/submit` | Submit receipt | ✓ | MANAGER | DRAFT → SUBMITTED |
| POST | `/{id}/validate` | Validate & add to stock | ✓ | MANAGER | SUBMITTED → VALIDATED |
| POST | `/{id}/cancel` | Cancel receipt | ✓ | MANAGER | Any → CANCELLED |

**Create Receipt Request**:
```json
POST /api/receipts
{
  "numero": "REC-001-2024",
  "fournisseurId": 5,
  "entrepotId": 1,
  "lignes": [
    {
      "produitId": 10,
      "quantite": 50,
      "prixUnitaire": 100.00
    }
  ]
}
```

---

## Issue (Goods Outbound) Endpoints
**Controller**: `GoodsIssueController` → `/api/issues`

| Method | Endpoint | Description | Auth | Role | Status Flow |
|--------|----------|-------------|------|------|-------------|
| GET | `/` | List issues | ✓ | — | — |
| GET | `/{id}` | Get issue details | ✓ | — | — |
| POST | `/` | Create issue | ✓ | MANAGER | DRAFT |
| PUT | `/{id}` | Update issue | ✓ | MANAGER | DRAFT only |
| POST | `/{id}/submit` | Submit issue | ✓ | MANAGER | DRAFT → SUBMITTED |
| POST | `/{id}/validate` | Validate & deduct stock | ✓ | MANAGER | SUBMITTED → VALIDATED |
| POST | `/{id}/cancel` | Cancel issue | ✓ | MANAGER | Any → CANCELLED |

**Create Issue Request**:
```json
POST /api/issues
{
  "motif": "SALE",
  "entrepotId": 1,
  "lignes": [
    {
      "produitId": 10,
      "quantite": 5
    }
  ]
}
```

---

## Transfer Endpoints
**Controller**: `TransferController` → `/api/transfers`

| Method | Endpoint | Description | Auth | Role | Status |
|--------|----------|-------------|------|------|--------|
| GET | `/` | List transfers | ✓ | — | — |
| GET | `/{id}` | Get transfer details | ✓ | — | — |
| POST | `/` | Create transfer | ✓ | MANAGER | DRAFT |
| PUT | `/{id}` | Update transfer | ✓ | MANAGER | DRAFT |
| POST | `/{id}/send` | Send transfer | ✓ | MANAGER | DRAFT → SENT |
| POST | `/{id}/receive` | Receive transfer | ✓ | MANAGER | SENT → RECEIVED |
| POST | `/{id}/cancel` | Cancel transfer | ✓ | MANAGER | Any → CANCELLED |

**Create Transfer Request**:
```json
POST /api/transfers
{
  "entrepotSource": 1,
  "entrepotDestination": 2,
  "lignes": [
    {
      "produitId": 10,
      "quantite": 20
    }
  ]
}
```

---

## Inventory Endpoints
**Controller**: `InventoryController` → `/api/inventories`

| Method | Endpoint | Description | Auth | Role | Status Flow |
|--------|----------|-------------|------|------|-------------|
| GET | `/` | List inventories | ✓ | — | — |
| GET | `/{id}` | Get inventory details | ✓ | — | — |
| POST | `/` | Plan/Create inventory | ✓ | MANAGER | PLANNED |
| POST | `/{id}/start` | Start inventory | ✓ | MANAGER | PLANNED → IN_PROGRESS |
| POST | `/{id}/count` | Record count | ✓ | OPERATOR | Count products |
| POST | `/{id}/validate` | Finalize & adjust stock | ✓ | MANAGER | COMPLETED → VALIDATED |
| POST | `/{id}/cancel` | Cancel inventory | ✓ | MANAGER | Any → CANCELLED |

**Create Inventory Request**:
```json
POST /api/inventories
{
  "numero": "INV-001-2024",
  "type": "FULL",
  "entrepotId": 1,
  "description": "Monthly full inventory"
}
```

**Record Count Request**:
```json
POST /api/inventories/1/count
{
  "produitId": 10,
  "quantiteCounting": 45
}
```

---

## Purchase Order Endpoints
**Controller**: `PurchaseOrderController` → `/api/purchase-orders`

| Method | Endpoint | Description | Auth | Role |
|--------|----------|-------------|------|------|
| GET | `/` | List purchase orders | ✓ | — |
| GET | `/{id}` | Get order details | ✓ | — |
| POST | `/` | Create purchase order | ✓ | MANAGER |
| PUT | `/{id}` | Update order | ✓ | MANAGER |
| POST | `/{id}/submit` | Submit order | ✓ | MANAGER |
| POST | `/{id}/validate` | Validate order | ✓ | MANAGER |
| POST | `/{id}/cancel` | Cancel order | ✓ | MANAGER |

---

## Alert Endpoints
**Controller**: `AlertController` → `/api/alerts`

| Method | Endpoint | Description | Auth | Role |
|--------|----------|-------------|------|------|
| GET | `/` | List alerts | ✓ | — |
| GET | `/{id}` | Get alert details | ✓ | — |
| GET | `/active` | Get active alerts | ✓ | — |
| GET | `/type/{type}` | Alerts by type | ✓ | — |
| POST | `/` | Create alert rule | ✓ | ADMIN |
| PUT | `/{id}` | Update alert rule | ✓ | ADMIN |
| DELETE | `/{id}` | Delete alert rule | ✓ | ADMIN |
| POST | `/{id}/acknowledge` | Acknowledge alert | ✓ | — |

**Alert Types**: LOW_STOCK, HIGH_STOCK, EXPIRY_WARNING, VARIANCE

---

## API Response Format

### Success Response (200)
```json
{
  "id": 1,
  "nom": "Laptop",
  "reference": "PROD-001",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00",
  "createdBy": "admin",
  "updatedBy": "admin"
}
```

### Paginated Response (200)
```json
{
  "content": [
    { "id": 1, "nom": "Item 1" },
    { "id": 2, "nom": "Item 2" }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": { "empty": false, "sorted": true, "unsorted": false }
  },
  "totalElements": 150,
  "totalPages": 8,
  "number": 0,
  "size": 20,
  "numberOfElements": 20,
  "empty": false,
  "first": true,
  "last": false
}
```

### Error Response (4xx/5xx)
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product with id 999 not found",
  "path": "/api/products/999"
}
```

### Created Response (201)
```json
{
  "id": 1,
  "nom": "New Product",
  "reference": "PROD-002",
  "message": "Product created successfully"
}
```

---

## Sorting & Pagination Examples

### Pagination
```
GET /api/products?page=0&size=20
GET /api/products?page=1&size=50
```

### Sorting
```
GET /api/products?sort=nom,asc
GET /api/products?sort=createdAt,desc
GET /api/products?sort=nom,asc&sort=createdAt,desc
```

### Combined
```
GET /api/products?page=0&size=20&sort=nom,asc
GET /api/products?categorieId=1&page=0&size=20&sort=prixVente,desc
```

---

## Common Query Parameters

| Parameter | Type | Default | Example |
|-----------|------|---------|---------|
| `page` | int | 0 | `?page=2` |
| `size` | int | 20 | `?size=50` |
| `sort` | string | id,asc | `?sort=nom,asc` |
| `q` | string | — | `?q=laptop` (search) |
| `id` | long | — | `?id=1` (filter) |
| `actif` | boolean | — | `?actif=true` |
| `role` | string | — | `?role=MANAGER` |

---

## Status Codes Reference

| Code | Meaning | Common Use |
|------|---------|-----------|
| 200 | OK | GET success |
| 201 | Created | POST success |
| 204 | No Content | DELETE success |
| 400 | Bad Request | Invalid input |
| 401 | Unauthorized | Missing token |
| 403 | Forbidden | Insufficient role |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Duplicate unique field |
| 500 | Server Error | Internal error |

---

## Authorization Header

All authenticated endpoints require:
```
Authorization: Bearer <access_token>
```

Example:
```
GET /api/warehouses HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Rate Limiting
Currently not implemented. Future enhancement recommended.

## API Versioning
Currently using single version (v0 implicit). Future: `/api/v1/...`


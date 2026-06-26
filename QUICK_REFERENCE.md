# StockMaster Backend - Quick Reference Guide

## Project Setup
- **Language**: Java 17
- **Framework**: Spring Boot 4.1.0
- **Build**: Maven
- **Database**: MySQL (localhost:3306/stockmaster_db)
- **API Port**: 8080
- **API Docs**: http://localhost:8080/swagger-ui.html

## Authentication
- **Type**: JWT (Bearer Token)
- **Access Token Expiry**: 8 hours
- **Refresh Token Expiry**: 7 days
- **Roles**: ADMINISTRATEUR, GESTIONNAIRE, OPERATEUR, LECTEUR

## All Modules At a Glance

```
✅ 1. User        - Authentication, user management, roles
✅ 2. Warehouse   - Warehouse information and statistics
✅ 3. Zone        - Zone management within warehouses
✅ 4. Product     - Product catalog with barcode support
✅ 5. Category    - Product categorization
✅ 6. Supplier    - Supplier information
✅ 7. Stock       - Stock levels and movement history
✅ 8. Issue       - Goods outbound (sales, adjustments, losses)
✅ 9. Inventory   - Physical inventory counting and validation
✅ 10. Transfer   - Inter-warehouse transfers
✅ 11. Alert      - Stock alerts (low stock, thresholds)
✅ 12. Location   - Physical bin/shelf management
✅ 13. Receipt    - Goods inbound (purchases, returns)
✅ 14. PurchaseOrder - Purchase order management
⚠️  15. Core      - Infrastructure, configs, security
❌ AUDIT         - Missing: Audit trail module
❌ DASHBOARD     - Missing: Business metrics & KPIs
❌ REPORTS       - Missing: Report generation & export
```

## Core Endpoints by Module

### Authentication
```
POST   /api/auth/login
POST   /api/auth/refresh
POST   /api/auth/logout
```

### User Management
```
GET    /api/users
GET    /api/users/search?role=ADMIN&actif=true
POST   /api/users
PUT    /api/users/{id}
DELETE /api/users/{id}
```

### Products & Catalog
```
GET    /api/products
GET    /api/products/search?q=laptop
GET    /api/products/barcode/123456789
GET    /api/categories
GET    /api/suppliers
POST   /api/products (ADMIN/MANAGER)
```

### Stock Operations
```
GET    /api/stocks
GET    /api/stocks/{produitId}/{entrepotId}
GET    /api/stocks/warehouse/{warehouseId}
GET    /api/stock-movements
GET    /api/stock-movements/product/{produitId}
POST   /api/stock-movements
```

### Warehouse & Locations
```
GET    /api/warehouses
GET    /api/zones
GET    /api/locations
POST   /api/warehouses (ADMIN)
```

### Inventory Operations
```
GET    /api/inventories
POST   /api/inventories (Create/Plan)
POST   /api/inventories/{id}/start
POST   /api/inventories/{id}/count (Record count)
POST   /api/inventories/{id}/validate (Finalize)
POST   /api/inventories/{id}/cancel
```

### Goods Inbound
```
GET    /api/receipts
POST   /api/receipts (Create receipt)
POST   /api/receipts/{id}/submit
POST   /api/receipts/{id}/validate (Add to stock)
```

### Goods Outbound
```
GET    /api/issues
POST   /api/issues (Create issue)
POST   /api/issues/{id}/submit
POST   /api/issues/{id}/validate (Deduct from stock)
```

### Transfers & Orders
```
GET    /api/transfers
POST   /api/transfers (Inter-warehouse)

GET    /api/purchase-orders
POST   /api/purchase-orders
```

### Alerts
```
GET    /api/alerts
POST   /api/alerts
```

## Entity Relationships

```
User
├── has Roles
└── creates audit records

Warehouse
├── contains Zones
│   ├── contains Locations
│   └── uses ZoneType (A, B, C, D)
├── has Stock (product inventory)
└── participates in Transfers

Product
├── belongs to Category
├── has Supplier (primary)
├── has Stock (in multiple warehouses)
└── tracked in Inventory

Stock
├── for Product × Warehouse
├── generates StockMovements
└── updated by Receipt/Issue/Inventory

Receipt (Inbound)
├── has ReceiptLines
├── from Supplier
└── updates Stock when validated

Issue (Outbound)
├── has IssueLines
├── has IssueMotif (SALE, ADJUSTMENT, LOSS, RETURN)
└── updates Stock when validated

Inventory
├── has InventoryLines
├── plans Stock count
└── creates Adjustments

Transfer
├── has TransferLines
├── from Warehouse → to Warehouse
└── updates Stock locations

Alert
├── has AlertType
└── triggered by Stock conditions
```

## Common Request Patterns

### Search & Filter
```
GET /api/products?categorieId=1&fournisseurId=2&page=0&size=20&sort=nom,asc
GET /api/users?role=GESTIONNAIRE&actif=true&page=0&size=50
```

### Create Resource
```json
POST /api/products
{
  "reference": "PROD-001",
  "nom": "Laptop",
  "codeBarre": "123456789",
  "categorieId": 1,
  "fournisseurPrincipalId": 2,
  "prixAchat": 500.00,
  "prixVente": 800.00,
  "unitemesure": "UNIT"
}
```

### Update Resource
```json
PUT /api/products/{id}
{
  "nom": "Laptop Pro",
  "prixVente": 850.00
}
```

## Common DTO Classes

```java
// Authorization
LoginRequest { username, password }
AuthResponse { accessToken, refreshToken, userInfo }

// Pagination
Page<T> { content[], totalElements, totalPages, number, size }

// Inventory Count
InventoryCountDTO { produitId, quantiteCounting }

// Stock Movement
StockMovementDTO { 
  id, produitId, entrepotId, 
  mouvement (type), quantite, 
  dateCreation, raison
}

// Receipt/Issue
ReceiptDTO {
  id, numero, status, lignes[], 
  dateCreation, dateValidation, entrepotId
}
```

## Enums

```
Role: ADMINISTRATEUR, GESTIONNAIRE, OPERATEUR, LECTEUR
ZoneType: A, B, C, D
IssueStatus: DRAFT, SUBMITTED, VALIDATED
IssueMotif: SALE, ADJUSTMENT, LOSS, RETURN
ReceiptStatus: DRAFT, SUBMITTED, VALIDATED, COMPLETED
InventoryStatus: PLANNED, IN_PROGRESS, COMPLETED, VALIDATED, CANCELLED
InventoryType: FULL, PARTIAL, CYCLE_COUNT
AlertType: LOW_STOCK, HIGH_STOCK, EXPIRY_WARNING, VARIANCE
MovementType: RECEIPT, ISSUE, TRANSFER, ADJUSTMENT, RETURN
```

## Authentication Header
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Error Codes
```
200 OK              - Success
201 CREATED         - Resource created
204 NO_CONTENT      - Delete success
400 BAD_REQUEST     - Invalid input
401 UNAUTHORIZED    - Missing/invalid token
403 FORBIDDEN       - Insufficient permissions
404 NOT_FOUND       - Resource not found
500 INTERNAL_SERVER_ERROR - Server error
```

## Module File Structure Pattern

Each module follows this structure:
```
module/
├── application/
│   ├── dto/          # *CreateDTO, *UpdateDTO, *DTO, *Mapper
│   └── service/      # *ApplicationService, *Service
├── domain/           # Entity classes, Enums
├── interfaces/       # *Controller classes
└── repository/       # JpaRepository interfaces
```

## Database Notes
- **URL**: jdbc:mysql://localhost:3306/stockmaster_db
- **User**: root
- **Password**: (empty)
- **DDL Mode**: auto-update
- **SQL Formatting**: Enabled for debugging
- **All ID fields**: Auto-increment Long type
- **Audit Fields**: createdAt, updatedAt, createdBy, updatedBy

## Missing Implementations (For Developers)

### Audit Module (⭐ High Priority)
- Track all CRUD operations
- Store old/new values
- Query by entity, user, date range

### Dashboard Module (⭐ High Priority)
- Stock summary metrics
- Movement statistics
- Warehouse utilization
- Alert dashboard

### Reports Module (⭐ High Priority)
- Stock valuation report
- Movement history export
- Inventory variance analysis
- Supplier performance report
- PDF/Excel export capability

## Performance Tips
- All list endpoints use pagination (default 20/page)
- Dashboard should cache data (TTL: 5 minutes)
- Stock queries are frequently accessed - consider caching
- Movement history can use date range filters for optimization
- Use warehouse-scoped queries to reduce data volume

## Security Best Practices
- Always include `Authorization` header with Bearer token
- Tokens expire after 8 hours - use refresh endpoint
- Admin operations require ADMINISTRATEUR role
- Stock operations require GESTIONNAIRE or ADMINISTRATEUR
- Sensitive data (password) never returned in responses

## Useful SQL Queries for Testing
```sql
-- Count products
SELECT COUNT(*) FROM products;

-- Check stock levels
SELECT p.nom, w.nom, s.quantite 
FROM stock s 
JOIN products p ON s.product_id = p.id 
JOIN warehouses w ON s.warehouse_id = w.id;

-- Movement history
SELECT * FROM stock_movements 
ORDER BY date_creation DESC LIMIT 10;

-- Users with roles
SELECT username, role FROM users;
```


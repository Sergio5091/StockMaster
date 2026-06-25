# StockMaster Java Backend - Comprehensive Code Structure Analysis

## Project Overview
- **Framework**: Spring Boot 4.1.0
- **Java Version**: 17
- **Database**: MySQL (stockmaster_db)
- **Authentication**: JWT
- **API Documentation**: OpenAPI/Swagger (`/swagger-ui.html`)
- **Port**: 8080

## Dependencies
```
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-validation
- spring-boot-starter-webmvc
- spring-boot-devtools
- mysql-connector-j
- lombok
- springdoc-openapi (for Swagger)
```

## Configuration (application.properties)
```
Database: jdbc:mysql://localhost:3306/stockmaster_db
Hibernate: auto-update DDL
JPA: Show SQL enabled with formatting
JWT: Secret key, 8h access token, 7d refresh token
Swagger: Enabled at /v3/api-docs and /swagger-ui.html
```

---

# MODULE ARCHITECTURE

## Module Structure Pattern
```
each-module/
├── application/
│   ├── dto/
│   │   ├── *CreateDTO.java
│   │   ├── *UpdateDTO.java
│   │   ├── *DTO.java (Response)
│   │   └── *Mapper.java (Conversion logic)
│   └── service/
│       ├── *ApplicationService.java (Use cases)
│       └── *Service.java (Business logic)
├── domain/
│   └── *.java (Entity, enums)
├── interfaces/
│   └── *Controller.java (REST endpoints)
└── repository/
    └── *Repository.java (JPA interface)
```

---

# DETAILED MODULE ANALYSIS

## 1️⃣ USER MODULE
**Path**: `com.backend.stockmaster.user`
**Status**: ✅ **COMPLETE**

| Component | Details |
|-----------|---------|
| **Entities** | User (extends AuditableEntity), RefreshToken |
| **Controllers** | AuthController, UserController |
| **Services** | UserApplicationService, RefreshTokenService |
| **DTOs** | LoginRequest, RegisterRequest, UserResponse, UserUpdateRequest, PasswordResetRequest, RefreshTokenRequest, AuthResponse |
| **Repositories** | UserRepository, RefreshTokenRepository |
| **Mapper** | UserMapper |

### Endpoints
```
POST   /api/auth/login           - Authenticate user
POST   /api/auth/refresh         - Refresh access token
POST   /api/auth/logout          - Logout user

GET    /api/users                - List users (ADMIN)
GET    /api/users/search         - Search users with filters (ADMIN)
POST   /api/users                - Create user (ADMIN)
PUT    /api/users/{id}           - Update user (ADMIN)
GET    /api/users/{id}           - Get user profile (ADMIN)
DELETE /api/users/{id}           - Delete user (ADMIN)
```

### Key Fields
- username (unique), password, email, fullName, active, role
- Audit fields: createdAt, updatedAt, createdBy, updatedBy

---

## 2️⃣ WAREHOUSE MODULE
**Path**: `com.backend.stockmaster.warehouse`
**Status**: ✅ **COMPLETE**

| Component | Details |
|-----------|---------|
| **Entities** | Warehouse |
| **Controllers** | WarehouseController |
| **Services** | WarehouseApplicationService, WarehouseService |
| **DTOs** | WarehouseDTO, WarehouseCreateDTO, WarehouseUpdateDTO, WarehouseStatsDTO |
| **Repositories** | WarehouseRepository |
| **Mapper** | WarehouseMapper |

### Endpoints
```
GET    /api/warehouses           - List warehouses
GET    /api/warehouses/{id}      - Get warehouse details
GET    /api/warehouses/{id}/stats - Warehouse statistics
POST   /api/warehouses           - Create warehouse (ADMIN/MANAGER)
PUT    /api/warehouses/{id}      - Update warehouse (ADMIN/MANAGER)
DELETE /api/warehouses/{id}      - Delete warehouse (ADMIN)
```

---

## 3️⃣ ZONE MODULE
**Path**: `com.backend.stockmaster.zone`
**Status**: ✅ **COMPLETE**

| Component | Details |
|-----------|---------|
| **Entities** | Zone, ZoneType (enum: A, B, C, D) |
| **Controllers** | ZoneController |
| **Services** | ZoneApplicationService |
| **DTOs** | ZoneDTO, ZoneCreateDTO, ZoneOccupancyDTO |
| **Repositories** | ZoneRepository |
| **Mapper** | ZoneMapper |

### Key Features
- Zone classification (A, B, C, D)
- Occupancy tracking
- Warehouse-specific zones

---

## 4️⃣ PRODUCT MODULE
**Path**: `com.backend.stockmaster.product`
**Status**: ✅ **COMPLETE**

| Component | Details |
|-----------|---------|
| **Entities** | Product, UniteMesure (enum: UNIT, KG, LITER, etc.) |
| **Controllers** | ProductController |
| **Services** | ProductApplicationService, ProductService |
| **DTOs** | ProductDTO, ProductCreateDTO, ProductUpdateDTO |
| **Repositories** | ProductRepository |
| **Mapper** | ProductMapper |

### Endpoints
```
GET    /api/products             - List (with category/supplier filters)
GET    /api/products/{id}        - Get product
GET    /api/products/search      - Search products (by name, reference)
GET    /api/products/barcode/{code} - Find by barcode

POST   /api/products             - Create (ADMIN/MANAGER)
PUT    /api/products/{id}        - Update (ADMIN/MANAGER)
DELETE /api/products/{id}        - Delete (ADMIN)
POST   /api/products/{id}/upload-image - Image upload
```

### Key Fields
- reference (unique), codeBarre (barcode), nom, description
- categorieId, fournisseurPrincipalId
- prixAchat, prixVente, poidsKg, volumeM3
- unitemesure

---

## 5️⃣ CATEGORY MODULE
**Path**: `com.backend.stockmaster.category`
**Status**: ✅ **COMPLETE**

| Component | Details |
|-----------|---------|
| **Entities** | Category |
| **Controllers** | CategoryController, TestController |
| **Services** | CategoryApplicationService |
| **DTOs** | CategoryDTO, CategoryCreateDTO, CategoryUpdateDTO |
| **Repositories** | CategoryRepository |
| **Mapper** | CategoryMapper |

### Features
- Product categorization
- Full CRUD operations

---

## 6️⃣ SUPPLIER MODULE
**Path**: `com.backend.stockmaster.supplier`
**Status**: ✅ **COMPLETE**

| Component | Details |
|-----------|---------|
| **Entities** | Supplier |
| **Controllers** | SupplierController |
| **Services** | SupplierApplicationService, SupplierService |
| **DTOs** | SupplierDTO, SupplierCreateDTO, SupplierUpdateDTO |
| **Repositories** | SupplierRepository |
| **Mapper** | SupplierMapper |

### Features
- Supplier management
- Product sourcing tracking

---

## 7️⃣ STOCK MODULE
**Path**: `com.backend.stockmaster.stock`
**Status**: ✅ **COMPLETE**

| Component | Details |
|-----------|---------|
| **Entities** | Stock, StockMovement, MovementType (enum) |
| **Controllers** | StockController, StockMovementController |
| **Services** | StockApplicationService, StockMovementApplicationService |
| **DTOs** | StockDTO, StockMovementDTO, StockUpdateRequest |
| **Repositories** | StockRepository, StockMovementRepository |
| **Mappers** | StockMapper, StockMovementMapper |

### Endpoints
```
GET    /api/stocks               - List all stocks (paginated)
GET    /api/stocks/{produitId}/{entrepotId} - Get specific stock
GET    /api/stocks/warehouse/{warehouseId}  - Warehouse stocks

GET    /api/stock-movements      - Movement history
GET    /api/stock-movements/product/{produitId} - Product movements
POST   /api/stock-movements      - Record movement
```

### Movement Types
- RECEIPT (Inbound)
- ISSUE (Outbound)
- TRANSFER (Between warehouses)
- ADJUSTMENT (Inventory correction)
- RETURN (From customer)

---

## 8️⃣ ISSUE MODULE (Goods Outbound)
**Path**: `com.backend.stockmaster.issue`
**Status**: ✅ **COMPLETE**

| Component | Details |
|-----------|---------|
| **Entities** | GoodsIssue, GoodsIssueLine, IssueMotif (enum), IssueStatus (enum) |
| **Controllers** | GoodsIssueController |
| **Services** | GoodsIssueService |
| **DTOs** | IssueDTO, IssueCreateDTO, IssueLineDTO, IssueLineCreateDTO |
| **Repositories** | GoodsIssueRepository |

### Endpoints
```
GET    /api/issues               - List issues
GET    /api/issues/{id}          - Get issue details
POST   /api/issues               - Create issue
POST   /api/issues/{id}/submit   - Submit for validation
POST   /api/issues/{id}/validate - Validate & deduct stock
POST   /api/issues/{id}/cancel   - Cancel issue
```

### Status Flow
1. **DRAFT** → 2. **SUBMITTED** → 3. **VALIDATED** (stock reduced)

### Issue Motifs
- SALE (Sale)
- ADJUSTMENT (Correction)
- LOSS (Loss/Damage)
- RETURN (Customer return)

---

## 9️⃣ INVENTORY MODULE
**Path**: `com.backend.stockmaster.inventory`
**Status**: ✅ **COMPLETE**

| Component | Details |
|-----------|---------|
| **Entities** | Inventory, InventoryLine, InventoryStatus (enum), InventoryType (enum) |
| **Controllers** | InventoryController |
| **Services** | InventoryService |
| **DTOs** | InventoryDTO, InventoryCreateDTO, InventoryLineDTO, InventoryCountDTO |
| **Repositories** | InventoryRepository |

### Endpoints
```
GET    /api/inventories          - List inventories
GET    /api/inventories/{id}     - Get inventory
POST   /api/inventories          - Plan/create inventory
POST   /api/inventories/{id}/start - Start & load theoretical quantities
POST   /api/inventories/{id}/count - Record count for product
POST   /api/inventories/{id}/validate - Finalize & adjust stock
POST   /api/inventories/{id}/cancel - Cancel inventory
```

### Workflow
1. **PLANNED** (Created)
2. **IN_PROGRESS** (Started, counting in progress)
3. **COMPLETED** (All items counted)
4. **VALIDATED** (Stock adjusted)
5. **CANCELLED** (Aborted)

---

## 🔟 TRANSFER MODULE
**Path**: `com.backend.stockmaster.transfer`
**Status**: ✅ **COMPLETE**

| Component | Details |
|-----------|---------|
| **Entities** | Transfer, TransferLine, TransferStatus (enum) |
| **Controllers** | TransferController |
| **Services** | TransferService |
| **DTOs** | TransferDTO, TransferCreateDTO, TransferLineDTO, TransferLineCreateDTO |
| **Repositories** | TransferRepository |

### Features
- Inter-warehouse stock transfers
- Line-level tracking
- Status workflow (DRAFT → SENT → RECEIVED)

---

## 1️⃣1️⃣ ALERT MODULE
**Path**: `com.backend.stockmaster.alert`
**Status**: ✅ **COMPLETE**

| Component | Details |
|-----------|---------|
| **Entities** | Alert, AlertType (enum) |
| **Controllers** | AlertController |
| **Services** | AlertService |
| **DTOs** | AlertDTO |
| **Repositories** | AlertRepository |

### Alert Types
- LOW_STOCK (Stock below threshold)
- HIGH_STOCK (Stock above capacity)
- EXPIRY_WARNING (Products near expiry)
- VARIANCE (Inventory variance detected)

---

## 1️⃣2️⃣ LOCATION MODULE
**Path**: `com.backend.stockmaster.location`
**Status**: ✅ **COMPLETE**

| Component | Details |
|-----------|---------|
| **Entities** | Location, LocationStatus (enum) |
| **Controllers** | LocationController |
| **Services** | LocationService |
| **DTOs** | LocationDTO, LocationCreateDTO |
| **Repositories** | LocationRepository |

### Features
- Physical bin/shelf management
- Location status tracking
- Zone-based organization

---

## 1️⃣3️⃣ RECEIPT MODULE (Goods Inbound)
**Path**: `com.backend.stockmaster.receipt`
**Status**: ✅ **COMPLETE**

| Component | Details |
|-----------|---------|
| **Entities** | GoodsReceipt, GoodsReceiptLine, ReceiptStatus (enum) |
| **Controllers** | GoodsReceiptController |
| **Services** | GoodsReceiptService |
| **DTOs** | ReceiptDTO, ReceiptCreateDTO, ReceiptLineDTO, ReceiptLineCreateDTO |
| **Repositories** | GoodsReceiptRepository |

### Endpoints
```
GET    /api/receipts             - List receipts
GET    /api/receipts/{id}        - Get receipt
POST   /api/receipts             - Create receipt
POST   /api/receipts/{id}/submit - Submit receipt
POST   /api/receipts/{id}/validate - Validate & add to stock
```

### Status Flow
- DRAFT → SUBMITTED → VALIDATED → COMPLETED

---

## 1️⃣4️⃣ PURCHASE ORDER MODULE
**Path**: `com.backend.stockmaster.purchaseorder`
**Status**: ✅ **COMPLETE**

| Component | Details |
|-----------|---------|
| **Entities** | PurchaseOrder, PurchaseOrderLine, POStatus (enum) |
| **Controllers** | PurchaseOrderController |
| **Services** | PurchaseOrderService |
| **DTOs** | PODTO, POCreateDTO, POLineDTO, POLineCreateDTO |
| **Repositories** | PurchaseOrderRepository |

### Features
- Purchase order creation & tracking
- Line-level detail management
- Status workflow management

---

## 1️⃣5️⃣ CORE MODULE
**Path**: `com.backend.stockmaster.core`
**Status**: ⚠️ **INFRASTRUCTURE** (Not a business domain)

### Subpackages
```
core/
├── audit/
│   ├── AuditableEntity.java (Base class with audit fields)
│   └── AuditConfig.java
├── config/
│   ├── JwtConfig.java
│   ├── SecurityConfig.java
│   ├── SwaggerConfig.java
│   └── WebConfig.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── BusinessException.java
│   ├── ResourceNotFoundException.java
│   └── UnauthorizedException.java
├── security/
│   ├── JwtAuthenticationFilter.java
│   ├── JwtTokenProvider.java
│   ├── Role.java (enum)
│   └── CustomUserDetailsService.java
├── utils/
│   ├── PaginationUtils.java
│   └── DateUtils.java
└── data/
    └── DataSeeder.java
```

### Roles Defined
- ADMINISTRATEUR (Admin)
- GESTIONNAIRE (Manager)
- OPERATEUR (Operator)
- LECTEUR (Reader)

---

# ❌ MISSING MODULES (7-15 incomplete)

## Missing Module #1: AUDIT LOGS
**Path**: Should be `/audit`
**Status**: ❌ **NOT IMPLEMENTED**

### Current State
- Basic audit fields exist on `AuditableEntity`: createdAt, updatedAt, createdBy, updatedBy
- No audit trail for operations, only creation/modification timestamps

### Todo - Complete Implementation
1. **Create Audit Entity**
   ```java
   AuditLog
   - id, timestamp, userId, entityType, entityId
   - operation (CREATE, UPDATE, DELETE, VIEW)
   - changeDetails (JSON with old/new values)
   - ipAddress, userAgent
   ```

2. **Create Controller**
   ```
   GET /api/audit-logs           - List audit logs
   GET /api/audit-logs/entity/{type}/{id} - Entity history
   GET /api/audit-logs/user/{userId}      - User activities
   ```

3. **Implement AOP interceptor** for automatic audit logging

---

## Missing Module #2: DASHBOARD
**Path**: Should be `/dashboard`
**Status**: ❌ **NOT IMPLEMENTED**

### Required Features
1. **Stock Summary**
   - Total products in catalog
   - Total warehouses
   - Overall stock value

2. **Inventory Metrics**
   - Products with low stock
   - Products with high stock
   - Stock turnover rate
   - Stock aging analysis

3. **Movement Summary**
   - Recent receipts
   - Recent issues
   - Recent transfers
   - Daily movement volume

4. **Warehouse Metrics**
   - Warehouse utilization %
   - Capacity alerts
   - Zone occupancy

5. **Alert Summary**
   - Active alerts count
   - Critical alerts
   - By type breakdown

### Suggested Endpoints
```
GET /api/dashboard/overview         - All metrics
GET /api/dashboard/stock-summary    - Stock summary
GET /api/dashboard/movements        - Movement stats
GET /api/dashboard/warehouse-stats  - Warehouse utilization
GET /api/dashboard/alerts           - Alert summary
GET /api/dashboard/low-stock-products - Products below threshold
```

---

## Missing Module #3: REPORTS
**Path**: Should be `/reports`
**Status**: ❌ **NOT IMPLEMENTED**

### Required Report Types

1. **Stock Valuation Report**
   - Total inventory value
   - By warehouse
   - By category
   - By supplier

2. **Movement Report**
   - Receipts (by date range, supplier)
   - Issues (by date range, motif)
   - Transfers (by source/destination)

3. **Inventory Variance Report**
   - Theoretical vs Actual
   - Variance analysis
   - Adjustments required

4. **Product Performance**
   - Fast movers vs slow movers
   - Dead stock analysis
   - ABC analysis

5. **Supplier Performance**
   - On-time delivery %
   - Quality metrics
   - Cost analysis

### Suggested Endpoints
```
GET /api/reports/stock-valuation?warehouse={id}&date={date}
GET /api/reports/movements?type=RECEIPT&from={date}&to={date}
GET /api/reports/inventory-variance?warehouse={id}
GET /api/reports/product-performance?period=MONTH
GET /api/reports/supplier-performance

GET /api/reports/{reportId}/export/pdf
GET /api/reports/{reportId}/export/excel
```

### Export Formats
- PDF (via iText or Apache POI)
- Excel (.xlsx via Apache POI)
- CSV

---

# SUMMARY TABLE

| # | Module | Entities | Services | Controllers | DTOs | Status |
|----|--------|----------|----------|-------------|------|--------|
| 1 | User | 2 | 2 | 2 | 7 | ✅ Complete |
| 2 | Warehouse | 1 | 2 | 1 | 4 | ✅ Complete |
| 3 | Zone | 2 | 1 | 1 | 3 | ✅ Complete |
| 4 | Product | 2 | 2 | 1 | 3 | ✅ Complete |
| 5 | Category | 1 | 1 | 1 | 3 | ✅ Complete |
| 6 | Supplier | 1 | 2 | 1 | 3 | ✅ Complete |
| 7 | Stock | 2 | 2 | 2 | 3 | ✅ Complete |
| 8 | Issue | 4 | 1 | 1 | 4 | ✅ Complete |
| 9 | Inventory | 4 | 1 | 1 | 4 | ✅ Complete |
| 10 | Transfer | 3 | 1 | 1 | 4 | ✅ Complete |
| 11 | Alert | 2 | 1 | 1 | 1 | ✅ Complete |
| 12 | Location | 2 | 1 | 1 | 2 | ✅ Complete |
| 13 | Receipt | 3 | 1 | 1 | 4 | ✅ Complete |
| 14 | PurchaseOrder | 3 | 1 | 1 | 4 | ✅ Complete |
| 15 | Core | — | — | — | — | ⚠️ Infrastructure |
| — | Audit | ❌ | ❌ | ❌ | ❌ | ❌ Missing |
| — | Dashboard | ❌ | ❌ | ❌ | ❌ | ❌ Missing |
| — | Reports | ❌ | ❌ | ❌ | ❌ | ❌ Missing |

---

# KEY ARCHITECTURAL PATTERNS

## 1. Layered Architecture
```
Controller (REST Interface)
        ↓
Application Service (Use Cases)
        ↓
Service (Business Logic)
        ↓
Repository (Data Access)
        ↓
Entity (Domain Model)
```

## 2. DTO Mapping
Each module has:
- **CreateDTO**: For POST requests
- **UpdateDTO**: For PUT requests
- **DTO/Response**: For GET responses
- **Mapper**: Bidirectional conversion

## 3. Role-Based Access Control
```java
@PreAuthorize("hasRole('ADMINISTRATEUR')")
@PreAuthorize("hasRole('ADMINISTRATEUR') or hasRole('GESTIONNAIRE')")
```

## 4. Pagination & Filtering
All list endpoints use Spring Data `Pageable` interface:
```
GET /api/products?page=0&size=20&sort=nom,asc
```

## 5. Audit Trail
All entities extend `AuditableEntity` with:
- createdAt, updatedAt (timestamps)
- createdBy, updatedBy (username)

---

# RECOMMENDATIONS FOR COMPLETION

## High Priority
1. ✅ **Implement Audit Module** - Critical for compliance
   - Logging: Service methods with AOP
   - Queries: Search audit logs by entity, user, date
   - Retention: Delete old logs (> 1 year)

2. ✅ **Implement Dashboard Module** - Business need
   - Real-time metrics aggregation
   - Cache dashboard data (TTL: 5min)
   - Permissions: Different data for different roles

3. ✅ **Implement Reports Module** - Business intelligence
   - Report templates
   - Export functionality (PDF/Excel)
   - Scheduling: Generate reports on schedule

## Medium Priority
4. Implement **Request Logging Interceptor** for API monitoring
5. Add **Batch Operations** endpoints (bulk import/export)
6. Implement **Notification System** (Email alerts)

## Low Priority
7. API Rate Limiting
8. Document Upload Module
9. Archive/Historical Data Module
10. User Activity Analytics

---

## File Locations
- **Main Code**: `backend/stockmaster/src/main/java/com/backend/stockmaster/`
- **Resources**: `backend/stockmaster/src/main/resources/`
  - `application.properties` - Configuration
  - `init-data.sql` - Initial data
- **Build**: `backend/stockmaster/pom.xml` - Maven configuration
- **Uploads**: `backend/stockmaster/uploads/products/` - Product images

---

## Database Schema Notes
- All entities use `AUTO` increment for IDs
- Foreign keys referenced via ID fields (not relationships to avoid lazy loading issues)
- Timestamp fields: `created_at`, `updated_at` (MySQL TIMESTAMP type)
- String fields for audit: `created_by`, `updated_by`
- All tables lowercase with underscores: `goods_receipts`, `purchase_orders`, etc.


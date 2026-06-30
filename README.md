# RevCart Microservices Architecture

## Overview

This project contains the complete microservices decomposition of the RevCart monolithic e-commerce grocery delivery application into 8 independent microservices plus infrastructure services.

## Architecture

```
┌─────────────────┐
│   API Gateway   │  (Port 8080)
│  (Entry Point)  │
└────────┬────────┘
         │
    ┌────┴────┐
    │ Eureka  │  (Port 8761)
    │ Server  │
    └────┬────┘
         │
    ┌────┴──────────────────────────────────────────────────┐
    │                                                        │
┌───┴───┐  ┌────────┐  ┌──────┐  ┌───────┐  ┌─────────┐  │
│ User  │  │Product │  │ Cart │  │ Order │  │ Payment │  │
│Service│  │Service │  │Service│ │Service│  │ Service │  │
│ 8081  │  │  8082  │  │ 8083 │  │ 8084  │  │  8085   │  │
└───────┘  └────────┘  └──────┘  └───────┘  └─────────┘  │
                                                           │
┌──────────┐  ┌──────────────┐  ┌────────┐               │
│ Delivery │  │Notification  │  │ Coupon │               │
│ Service  │  │   Service    │  │Service │               │
│   8086   │  │     8087     │  │  8088  │               │
└──────────┘  └──────────────┘  └────────┘               │
```

## Services

### Infrastructure Services

#### 1. Eureka Server (Port 8761)
- **Purpose**: Service discovery and registration
- **Location**: `eureka-server/`
- **Access**: http://localhost:8761

#### 2. API Gateway (Port 8080)
- **Purpose**: Single entry point for all client requests
- **Location**: `api-gateway/`
- **Routes**:
  - `/api/auth/**` → User Service
  - `/api/users/**` → User Service
  - `/api/products/**` → Product Service
  - `/api/categories/**` → Product Service
  - `/api/cart/**` → Cart Service
  - `/api/wishlist/**` → Cart Service
  - `/api/orders/**` → Order Service
  - `/api/payments/**` → Payment Service
  - `/api/wallet/**` → Payment Service
  - `/api/delivery/**` → Delivery Service
  - `/api/notifications/**` → Notification Service
  - `/api/coupons/**` → Coupon Service

### Core Microservices

#### 3. User Service (Port 8081)
- **Database**: `revcart_user_db`
- **Entities**: User, Address, UserPreferences, UserStats, Otp
- **Features**:
  - User authentication (JWT)
  - User registration
  - Profile management
  - Address management
  - OTP verification

#### 4. Product Service (Port 8082)
- **Database**: `revcart_product_db`
- **Entities**: Product, Category
- **Features**:
  - Product CRUD operations
  - Category management
  - Product search and filtering
  - Stock management

#### 5. Cart Service (Port 8083)
- **Database**: `revcart_cart_db`
- **Entities**: Cart, CartItem, Wishlist
- **Features**:
  - Shopping cart management
  - Wishlist management
  - Cart-to-order conversion

#### 6. Order Service (Port 8084)
- **Database**: `revcart_order_db`
- **Entities**: Order, OrderItem, OrderStatus
- **Features**:
  - Order creation and management
  - Order status tracking
  - Order history
  - Delivery partner assignment

#### 7. Payment Service (Port 8085)
- **Database**: `revcart_payment_db`
- **Entities**: Payment, Wallet, WalletTransaction
- **Features**:
  - Razorpay integration
  - Payment processing
  - Wallet management
  - Refund processing

#### 8. Delivery Service (Port 8086)
- **Database**: `revcart_delivery_db`
- **Entities**: DeliveryPartner
- **Features**:
  - Delivery partner management
  - Order assignment
  - Delivery tracking
  - Partner performance metrics

#### 9. Notification Service (Port 8087)
- **Database**: `revcart_notification_db`
- **Entities**: Notification
- **Features**:
  - Notification management
  - Real-time updates (WebSocket)
  - Email/SMS notifications

#### 10. Coupon Service (Port 8088)
- **Database**: `revcart_coupon_db`
- **Entities**: Coupon
- **Features**:
  - Coupon management
  - Coupon validation
  - Discount calculation

## Getting Started

### Prerequisites

- Java 17
- Maven 3.8+
- MySQL 8.0+
- AWS S3 credentials (for file uploads)
- Razorpay credentials (for payments)

### Running the Services

1. **Start Eureka Server**
   ```bash
   cd eureka-server
   mvn spring-boot:run
   ```
   Verify at: http://localhost:8761

2. **Start All Microservices** (in separate terminals)
   ```bash
   cd user-service && mvn spring-boot:run
   cd product-service && mvn spring-boot:run
   cd cart-service && mvn spring-boot:run
   cd order-service && mvn spring-boot:run
   cd payment-service && mvn spring-boot:run
   cd delivery-service && mvn spring-boot:run
   cd notification-service && mvn spring-boot:run
   cd coupon-service && mvn spring-boot:run
   ```

3. **Start API Gateway**
   ```bash
   cd api-gateway
   mvn spring-boot:run
   ```

4. **Verify All Services**
   - Check Eureka Dashboard: http://localhost:8761
   - All 8 services should be registered

### Testing

Access all services through the API Gateway at `http://localhost:8080`

**Example: User Registration**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "mobile": "9876543210",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

**Example: User Login**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "mobile": "9876543210",
    "password": "password123"
  }'
```

**Example: Get Products**
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

## Database Configuration

Each service uses its own MySQL database. Update the credentials in each service's `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/<database_name>?createDatabaseIfNotExist=true
    username: root
    password: root
```

## Environment Variables

Set the following environment variables:

```bash
# AWS S3
export AWS_S3_BUCKET_NAME=your-bucket-name
export AWS_REGION=ap-south-1
export AWS_ACCESS_KEY_ID=your-access-key
export AWS_SECRET_ACCESS_KEY=your-secret-key

# Razorpay
export RAZORPAY_KEY_ID=your-razorpay-key
export RAZORPAY_KEY_SECRET=your-razorpay-secret
```

## Project Structure

```
P2/
├── pom.xml                    # Parent POM
├── eureka-server/             # Service Discovery
├── api-gateway/               # API Gateway
├── common-lib/                # Shared utilities
├── user-service/              # User management
├── product-service/           # Product catalog
├── cart-service/              # Shopping cart
├── order-service/             # Order management
├── payment-service/           # Payment processing
├── delivery-service/          # Delivery management
├── notification-service/      # Notifications
└── coupon-service/            # Coupon management
```

## Technology Stack

- **Spring Boot**: 3.3.0
- **Spring Cloud**: 2023.0.0
- **Java**: 17
- **Database**: MySQL 8.0
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Authentication**: JWT (JSON Web Tokens)
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Maven
- **Cloud Storage**: AWS S3
- **Payment Gateway**: Razorpay

## Inter-Service Communication

Services communicate via REST APIs using service discovery:
- Services register with Eureka Server
- API Gateway uses load balancing (lb://) to route requests
- Services can call each other using Eureka service names

## Security

- JWT-based authentication
- Stateless session management
- CORS enabled for cross-origin requests
- Password encryption using BCrypt

## Monitoring

- Eureka Dashboard: http://localhost:8761
- Service health endpoints: `http://localhost:<port>/actuator/health`



## Contributing

This is a project for the Virtusa Java Full Stack Development program.

## License

Proprietary - Revature/Virtusa

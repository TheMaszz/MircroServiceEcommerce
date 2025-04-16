# 🛍️ E-Commerce Microservices

This project is a microservices-based e-commerce application built with modern technologies. It demonstrates a modular architecture where services communicate over REST, and features a seamless Stripe payment integration.

---

## ⚙️ Tech Stack

### 🖥️ Backend
- Java 17 + Spring Boot 3.4.4
- MySQL (Relational Database)
- Feign Client (Inter-service communication)
- Eureka / Consul (Service Discovery)
- Spring Cloud Gateway (API Gateway)
- Stripe SDK (Payment Integration)

### 🌐 Frontend
- Angular 17
- Angular Material / TailwindCSS (UI Design)
- JWT Authentication (Token-based security)

---

## 🧩 Microservices Architecture

- **Gateway Service** – Central entry point for all frontend requests
- **Auth Service** – Handles user authentication and token generation
- **User Service** – Manages user accounts and profiles
- **Email Service** – Manages password recovery via email
- **Product Service** – Manages product catalog, inventory, and availability
- **Cart Service** – Manages cart data and item updates
- **Order Service** – Handles order creation and tracking
- **Payment Service** – Integrates with Stripe for secure payments
- **Service Registry** – Eureka server for service discovery and registration
- **Common Module** – Shared module for DTOs, base configurations, and reusable beans

---

## 💳 Stripe Integration

Stripe is used for processing secure payments. The Payment Service is responsible for:

- Creating Stripe payment sessions
- Redirecting users to Stripe Checkout
- Handling success and cancel URLs
- Verifying webhook events from Stripe

> 🔐 **Security Tip:** Use environment variables to keep your `STRIPE_SECRET_KEY` and other credentials secure. Do not hardcode secrets in the codebase.

---

## 🔄 Workflow

1. Users browse products and add them to their cart
2. On checkout, an order is created via the Order Service
3. The Payment Service creates a Stripe Checkout session
4. User is redirected to Stripe for payment
5. On successful payment, Stripe notifies the system via webhook
6. The Order status is updated, and confirmation is sent to the user

---

## 📦 Prerequisites

- Java 17
- Node.js 20
- Angular 17
- Spring Boot 3.4.4
- Stripe Account
- MySQL
- Docker (for Redis, optional)


# Payment Ledger & Wallet Management System

> A secure, enterprise-grade digital payment platform built using Spring Boot, React, PostgreSQL, Redis, JWT Authentication, and Redisson. The application simulates a real-world fintech system by providing secure wallet operations, transaction processing, immutable ledger management, audit logging, concurrency control, and idempotent payment handling.

---

# Table of Contents

- Project Overview
- Features
- Technology Stack
- Architecture
- Project Workflow
- Backend Modules
- Frontend Modules
- Database Design
- Security
- Concurrency Control
- Redis & Idempotency
- Testing
- Project Structure
- REST APIs
- Future Enhancements
- Learning Outcomes

---

# Project Overview

The Payment Ledger & Wallet Management System is a full-stack enterprise application designed to simulate how modern digital payment platforms securely process financial transactions.

The application allows users to:

- Register and authenticate securely
- Manage digital wallets
- Deposit money
- Withdraw money
- Transfer funds between users
- Track transaction history
- View ledger entries
- Maintain audit logs
- Prevent duplicate payment processing
- Handle concurrent transactions safely

The project follows enterprise software engineering practices including Layered Architecture, RESTful APIs, JWT Authentication, Transaction Management, Redis Integration, Idempotency, and Concurrency Control.

---

# Features

## Authentication & Authorization

- User Registration
- Secure Login
- JWT Authentication
- BCrypt Password Encryption
- Role-Based Access Control (Admin/User)
- Protected REST APIs

---

## User Management

- User Registration
- Profile Management
- Change Password
- User Dashboard
- Role Assignment

---

## Wallet Management

- Automatic Wallet Creation
- Wallet Balance Management
- Wallet Status
   - ACTIVE
   - FROZEN
   - BLOCKED
- Secure Balance Retrieval

---

## Transaction Management

### Deposit

- Deposit Funds
- Wallet Credit
- Transaction Recording
- Ledger Entry Creation
- Audit Logging

### Withdraw

- Wallet Balance Validation
- Insufficient Balance Protection
- Secure Debit Processing
- Transaction Recording
- Ledger Entry Creation
- Audit Logging

### Transfer

- Wallet-to-Wallet Transfer
- Sender & Receiver Validation
- Automatic Rollback
- Ledger Entry Creation
- Audit Logging
- Idempotency Support

---

## Ledger Management

Every successful financial transaction automatically generates immutable ledger entries.

Each ledger entry stores:

- Wallet
- Transaction
- Credit/Debit Entry
- Amount
- Narration
- Timestamp

The ledger provides complete financial traceability.

---

## Audit Logging

The system records every important business event.

Examples include:

- User Registration
- Login
- Deposit
- Withdrawal
- Transfer
- Wallet Activation
- Wallet Freeze
- Duplicate Request Rejection
- Failed Transactions

---

# Technology Stack

## Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Maven
- JWT Authentication
- PostgreSQL
- Redis
- Redisson
- Swagger / OpenAPI

---

## Frontend

- React
- TypeScript
- Vite
- Zustand
- Axios
- CSS

---

## Database

- PostgreSQL

---

## Testing

- JUnit 5
- Mockito

---

## Tools

- IntelliJ IDEA
- VS Code
- Git
- GitHub
- Docker
- Postman
- pgAdmin

---

# Architecture

The project follows a layered architecture.

```text
Client
      │
      ▼
Controller Layer
      │
      ▼
Service Layer
      │
      ▼
Repository Layer
      │
      ▼
PostgreSQL Database
```

Each layer has a single responsibility, making the application scalable, maintainable, and easy to test.

---

# Project Workflow

```text
User
 │
 ▼
Login / Register
 │
 ▼
JWT Authentication
 │
 ▼
Wallet Operations
 │
 ├── Deposit
 ├── Withdraw
 └── Transfer
 │
 ▼
Business Validation
 │
 ▼
@Transactional
 │
 ▼
Pessimistic Lock
 │
 ▼
Wallet Update
 │
 ▼
Transaction Created
 │
 ▼
Ledger Entries Generated
 │
 ▼
Audit Log Created
 │
 ▼
Response Returned
```

---

# Backend Modules

## Authentication Module

- Registration
- Login
- JWT Token Generation
- Password Encryption
- Role Validation

## User Module

- User Profile
- User Management
- Password Updates

## Wallet Module

- Wallet Balance
- Wallet Status
- Wallet Operations

## Transaction Module

- Deposit
- Withdraw
- Transfer
- Transaction History
- Transaction Summary

## Ledger Module

- Credit Entries
- Debit Entries
- Immutable Financial Records

## Audit Module

- Business Activity Tracking
- Security Monitoring
- System Traceability

---

# Frontend Modules

The frontend provides a responsive enterprise dashboard.

Modules include:

- Login
- Registration
- User Dashboard
- Wallet Dashboard
- Deposit
- Withdraw
- Transfer
- Transaction History
- Ledger History
- Reports
- User Management
- Wallet Management
- Profile
- Settings

---

# Database Design

## Tables

- Users
- Roles
- Wallets
- Transactions
- Ledger Entries
- Audit Logs

## Relationships

```text
Role
 │
 ▼
User
 │
 ▼
Wallet
 │
 ▼
Transaction
 │
 ▼
Ledger Entry

Audit Log
```

---

# Security

Implemented using:

- Spring Security
- JWT Authentication
- BCrypt Password Encoder
- Role-Based Authorization
- Protected REST APIs

---

# Transaction Management

Implemented using Spring's `@Transactional` to ensure:

- Atomic Operations
- Automatic Rollback
- Database Consistency
- Reliable Financial Transactions

---

# Concurrency Control

Implemented using:

- JPA Pessimistic Locking (`LockModeType.PESSIMISTIC_WRITE`)
- Redisson Distributed Locking

Benefits:

- Prevents Double Spending
- Prevents Race Conditions
- Ensures Data Consistency
- Safe Concurrent Wallet Updates

---

# Redis & Idempotency

Redis is integrated for:

- High-speed in-memory storage
- Response caching
- Idempotency support
- Distributed locking

Idempotency ensures that duplicate requests with the same Idempotency-Key are processed only once, preventing duplicate payment execution and making the system resilient to retries and network failures.

---

# Exception Handling

Implemented using:

- BusinessExceptions
- GlobalExceptionHandler

Handles:

- Invalid Credentials
- Duplicate Email
- Duplicate Username
- Insufficient Balance
- Wallet Not Found
- Unauthorized Access
- Invalid Transactions

---

# Testing

Implemented:

- Unit Testing
- Integration Testing
- Controller Testing
- Service Testing
- Repository Testing
- Exception Testing

Frameworks:

- JUnit 5
- Mockito

---

# Project Structure

```text
Backend
│
├── config
├── controller
├── dto
├── enums
├── exception
├── model
├── repository
├── security
├── service
└── resources

Frontend
│
├── common
├── components
├── layouts
├── pages
├── routes
├── services
├── store
├── types
└── utils
```

---

# REST APIs

## Authentication

- Register
- Login

## Wallet

- Get Wallet
- Deposit
- Withdraw
- Transfer

## Transactions

- Transaction History
- Transaction Summary

## Ledger

- Ledger History

## Admin

- Dashboard
- User Management
- Wallet Management
- Reports

---

# Enterprise Concepts Implemented

- Layered Architecture
- Repository Pattern
- DTO Pattern
- Dependency Injection
- Spring Security
- JWT Authentication
- BCrypt Password Encoding
- RESTful API Design
- Global Exception Handling
- Business Exception Handling
- Transaction Management
- Pessimistic Locking
- Redis Integration
- Redisson Distributed Locking
- Idempotency
- Audit Logging
- Immutable Ledger Accounting
- Unit Testing
- Integration Testing
- Clean Code Principles

---

# Future Enhancements

- UPI Integration
- Payment Gateway Integration
- QR Code Payments
- Email Notifications
- SMS Alerts
- Scheduled Payments
- Multi-Currency Wallets
- Kafka Event Streaming
- Dockerized Deployment
- Kubernetes Support
- CI/CD Pipeline
- Prometheus & Grafana Monitoring

---

# Learning Outcomes

Through this project, I gained practical experience in:

- Enterprise Backend Development
- Secure REST API Development
- Spring Security & JWT Authentication
- PostgreSQL Database Design
- Redis & Redisson Integration
- Transaction Management
- Concurrency Control
- Idempotent Request Processing
- Financial Ledger Design
- Audit Logging
- Unit & Integration Testing
- Git & GitHub Collaboration
- Enterprise Software Architecture

---


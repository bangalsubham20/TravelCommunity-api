# 🍃 SundaySoul Server — Enterprise Travel API

![Java](https://img.shields.io/badge/Java-21-orange.svg?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-brightgreen.svg?style=for-the-badge&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg?style=for-the-badge&logo=mysql)
![JWT](https://img.shields.io/badge/JWT-Security-black.svg?style=for-the-badge&logo=jsonwebtokens)
![Docker](https://img.shields.io/badge/Docker-Ready-cyan.svg?style=for-the-badge&logo=docker)

> High-performance RESTful API microservice powering the **SundaySoul** travel platform. Built with Spring Boot 3, Java 21, JPA/Hibernate, and MySQL.

---

## ⚡ Highlights & Key Capabilities

- 🔒 **Role-Based Security & JWT**: Statestation authentication with Spring Security and BCrypt password encryption.
- 🏔️ **Expeditions & Content Management**: High-throughput querying for curated expeditions, filtering by difficulty, price, and duration.
- 🌱 **Self-Healing Data Seeder**: Automatic database bootstrapping (`DataSeeder.java`) populating initial trips, promo codes, FAQs, and admin credentials safely on startup.
- 🎟️ **Booking & Discount Engine**: Real-time reservation booking pipelines with promo code validation.
- 📬 **Email Notification Service**: Background mailing service delivering instant booking confirmations to travelers.

---

## 🛠️ Tech Stack

```text
├── Framework    : Spring Boot 3.2.x
├── Language     : Java 21 (Temurin OpenJDK)
├── Database     : MySQL 8.0
├── Security     : Spring Security + JWT (jjwt)
├── Persistence  : Spring Data JPA / Hibernate
└── Build System : Apache Maven
```

---

## 🚀 Getting Started

### 1. Prerequisites
- **Java JDK 21** or **JDK 17**
- **MySQL 8.0+**

### 2. Environment Variables Configuration

Set environment variables in your operating system or deploy platform (never commit real passwords to Git):

```bash
export DB_URL="jdbc:mysql://localhost:3306/sundaysoul?useSSL=false&allowPublicKeyRetrieval=true"
export DB_USERNAME="root"
export DB_PASSWORD="<your_db_password>"
export JWT_SECRET="<your_secure_256bit_jwt_secret_key>"
```

### 3. Launching Backend

```bash
# Clone the repository
git clone https://github.com/bangalsubham20/SundaySoul-Server.git
cd SundaySoul-Server

# Run Spring Boot service via Maven Wrapper
./mvnw spring-boot:run
```
The API server will listen on `http://localhost:8080`.

---

## 📡 Core API Reference

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :---: |
| `GET` | `/api/trips` | Retrieve expeditions catalog with filter params | ❌ |
| `GET` | `/api/trips/{id}` | Get detailed information for a specific trip | ❌ |
| `POST` | `/api/auth/register` | Register a new traveler account | ❌ |
| `POST` | `/api/auth/login` | Authenticate user and issue JWT token | ❌ |
| `POST` | `/api/bookings` | Create a new trip booking | ✅ |
| `GET` | `/api/content/faq` | Retrieve dynamic system FAQs | ❌ |
| `GET` | `/api/offers` | Fetch active promo offers & discount codes | ❌ |

---

## 🐳 Docker Deployment

```bash
# Build Docker image
docker build -t sundaysoul-server .

# Run container with environment variables
docker run -d \
  -p 8080:8080 \
  -e DB_PASSWORD="<your_db_password>" \
  -e JWT_SECRET="<your_secure_jwt_secret>" \
  --name sundaysoul-backend-app \
  sundaysoul-server
```

---

<div align="center">
  <sub>Crafted with passion for seamless exploration by <b>Subham</b> • 🇮🇳 India</sub>
</div>

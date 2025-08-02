# Treso

Treso is a secure REST API for personal expense tracking, built with Java and Spring Boot.

## 🚀 Features

- User registration & JWT authentication
- CRUD operations for expenses
- Pagination support
- PostgreSQL (or MySQL) persistence
- Clean error handling

## 🛠️ Getting Started

### 1. **Clone the Repository**
```bash
git clone https://github.com/YOUR_USERNAME/treso.git
cd treso
```

### 2. Configure Application Properties
- Copy the sample config:
```bash
cp src/main/resources/application-sample.properties src/main/resources/application.properties
```
- Edit `application.properties` and fill in your database details, JWT secret, etc.

> **Never commit your filled `application.properties` to git.**

### 3. Set Up the Database
- Start PostgreSQL (or MySQL).
- Create a database and user.

### 4. Run the Application
- **Using IntelliJ IDEA:**

  Open the project and run the `TresoApplication` main class.

- Or with Maven:
    ```bash
    ./mvnw spring-boot:run
    ```

### 📝 Configuration
- Secrets and passwords are **not committed** to git.
- Use application-sample.properties as a template.

### 🧾 API Endpoints
- `POST /api/auth/register` – Register user
- `POST /api/auth/login` – Login (JWT)
- `GET /api/expenses` – List expenses
- `POST /api/expenses` – Create expense
- `PUT /api/expenses/{id}` – Update expense
- `DELETE /api/expenses/{id}` – Delete expense

> Built with Spring Boot 3, Java 21, and ❤️ by Harshal Patel
---
Let me know if you want to tweak or add anything!  
Ready for your next commit. 🚀
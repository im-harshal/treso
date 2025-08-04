# Treso

Treso is a secure REST API for personal expense tracking, built with Java and Spring Boot.

## 🚀 Features

- User registration & JWT authentication
- CRUD operations for expenses (user-scoped)
- Pagination & sorting support
- PostgreSQL persistence
- Clean validation and error handling
- OpenAPI/Swagger UI for interactive API docs
- Actuator metrics for observability
- **Production-ready Docker support**

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

### 3. Run with Docker (Recommended)
Run with Existing Postgres Container
```bash
docker build -t treso-app .
docker run -p 8080:8080 \
  --network <your_network> \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://<postgres_container_name>:5432/<db> \
  -e SPRING_DATASOURCE_USERNAME=<db_user> \
  -e SPRING_DATASOURCE_PASSWORD=<db_password> \
  treso-app
```
> Replace `<your_network>`, `<postgres_container_name>`, `<db>`, `<db_user>`, `<db_password>` as needed.

### 4. Run Locally (with Maven)
```bash
./mvnw spring-boot:run
```
- By default, connects to PostgreSQL at localhost:5432

### 📝 Configuration
- All secrets/configs should be set via environment variables (see `application-sample.properties` for reference).
- Supports 12-factor app principles for cloud compatibility.

### 🧾 API Endpoints
- `POST /api/auth/register` – Register user
- `POST /api/auth/login` – Login (JWT)
- `GET /api/expenses` – List expenses
- `POST /api/expenses` – Create expense
- `PUT /api/expenses/{id}` – Update expense
- `DELETE /api/expenses/{id}` – Delete expense
- `GET /swagger-ui.html` – Swagger UI
- `GET /actuator/metrics`, `GET /actuator/health`,  `GET /actuator/info`- Metrics

### 🔒 Security
- Do NOT commit secrets or passwords to git.
- Protect `/swagger-ui` and `/actuator` endpoints in production.

Let me know if you want to tweak or add anything! Ready for your next commit. 🚀

> Built with Spring Boot 3, Java 21, Docker and ❤️ by Harshal Patel
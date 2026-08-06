# MiniBilling

Spring Boot REST API с React frontend за фактуриране на газ и електричество.

## Tech Stack

- Java 17, Spring Boot, Spring Security (JWT)
- PostgreSQL 17, JPA/Hibernate
- React, TypeScript, Tailwind CSS

## Изисквания

- Java 17+
- Maven
- PostgreSQL 17
- Node.js 18+

## Настройка

### 1. База данни

Създай база `minibilling` в PostgreSQL.

### 2. Конфигурация

Копирай `application.properties.example` като `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/minibilling
spring.datasource.username=postgres
spring.datasource.password=твоята_парола
spring.jpa.hibernate.ddl-auto=update

app.admin.username=admin
app.admin.password=твоята_парола
app.user.username=user
app.user.password=твоята_парола
```

## Стартиране

### Backend
```bash
.\mvnw spring-boot:run
```

### Frontend
```bash
cd minibilling-frontend
npm install
npm run dev
```

## Импорт на данни

Импортирай CSV файлове чрез `POST /import` в правилна последователност:

1. `prices-N.csv`
2. `users.csv`
3. `readings.csv`

### Формат на CSV файловете

**users.csv**
```
Ime Prezime,referenten_nomer,nomer_na_cenova_lista
```

**readings.csv**
```
referenten_nomer,produkt,data,pokazanie
```

**prices-N.csv**
```
produkt,nachalna_data,kraina_data,cena
```

## API

```
POST /auth/login                          → логин, връща JWT токен
POST /import                              → импорт на CSV файл (ADMIN)
POST /invoices/{reference}?from=&to=      → генерира фактура
GET  /invoices/{reference}?from=&to=      → чете фактура
```

### Статус кодове
- `200 OK` — успех
- `204 No Content` — няма достатъчно отчети
- `400 Bad Request` — невалидни данни
- `401 Unauthorized` — невалиден токен
- `403 Forbidden` — недостатъчни права
- `404 Not Found` — потребителят не съществува

## Unit тестове

```bash
.\mvnw test
```

Тестовете покриват алгоритъма за разпределение на потреблението при смяна на цена.
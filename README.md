# MiniBilling

Spring Boot REST API с React frontend за фактуриране на газ и електричество по CSV файлове.

## Изисквания

- Java 17+
- Maven
- Node.js 18+

## Настройка

### 1. Входни CSV файлове

Създай директория с:

inputdir/

users.csv

readings.csv

prices-1.csv

### 2. Конфигурация

Копирай `application.properties.example` като `application.properties`:

```properties
billing.input.dir=C:/path/to/inputdir/
billing.output.dir=C:/path/to/outputdir/
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
npm start
```

## API
POST /invoices/{reference}?year=2024&month=3  → генерира фактура

GET  /invoices/{reference}?year=2024&month=3  → чете записана фактура

### Статус кодове
- `200 OK` — фактурата като JSON
- `204 No Content` — няма достатъчно отчети
- `404 Not Found` — потребителят не съществува
- `400 Bad Request` — невалиден формат

## Структура на CSV файловете

### users.csv
Ime Prezime,referenten_nomer,nomer_na_cenova_lista

### readings.csv
referenten_nomer,produkt,data,pokazanie

### prices-N.csv
produkt,nachalna_data,kraina_data,cena
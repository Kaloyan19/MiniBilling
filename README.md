# MiniBilling

Spring Boot REST API за фактуриране на газ и електричество по CSV файлове.

## Изисквания

- Java 17+
- Maven

## Настройка

1. Създай входна директория с CSV файлове:
   inputdir/
   users.csv
   readings.csv
   prices-1.csv

2. Копирай `src/main/resources/application.properties.example` като `application.properties` и попълни пътищата:

```properties
billing.input.dir=C:/path/to/inputdir/
```

## Структура на CSV файловете

### users.csv
Ime Prezime Familiq,referenten_nomer,nomer_na_cenova_lista

### readings.csv
referenten_nomer,produkt,data,pokazanie

Продуктът може да е `gas` или `elec`.

### prices-N.csv
produkt,nachalna_data,kraina_data,cena

## Стартиране

```bash
.\mvnw spring-boot:run
```

## API
GET /invoices/{reference}?period=2024-03

- `reference` — абонатен номер на потребителя
- `period` — период във формат `yyyy-MM`

### Отговори
- `200 OK` — фактурата като JSON
- `204 No Content` — няма достатъчно отчети за периода
- `404 Not Found` — потребителят не съществува
- `400 Bad Request` — невалиден формат на период
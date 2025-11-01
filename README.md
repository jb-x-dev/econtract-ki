# eContract KI - Intelligente Vertragsverwaltung

KI-gestütztes Vertragsverwaltungsmodul für die jb-x eBusiness Suite

## 🚀 Features

- ✅ **Vollständige Vertragsverwaltung** - Erstellen, Bearbeiten, Löschen von Verträgen
- ✅ **RESTful API** - Moderne REST API mit Swagger/OpenAPI Dokumentation
- ✅ **Responsive Frontend** - HTML5/JavaScript/CSS3 Benutzeroberfläche
- ✅ **MySQL Datenbank** - Relationale Datenbankstruktur mit Flyway Migration
- ✅ **Apache Solr Integration** - Volltextsuche (vorbereitet)
- ✅ **KI-Integration** - OpenAI API Integration (vorbereitet)
- ✅ **Genehmigungsworkflows** - Mehrstufige Genehmigungsprozesse (vorbereitet)
- ✅ **Audit Trail** - Vollständige Nachvollziehbarkeit aller Aktionen
- ✅ **Multi-Tenancy Ready** - Mandantenfähige Architektur

## 📋 Voraussetzungen

- **Java 17+** (OpenJDK oder Oracle JDK)
- **Maven 3.8+**
- **MySQL 8.0+**
- **Apache Tomcat 10+** (optional, für WAR-Deployment)
- **Apache Solr 9.x** (optional, für Volltextsuche)

## 🛠️ Installation

### 1. Datenbank einrichten

```sql
-- MySQL Datenbank erstellen
CREATE DATABASE econtract_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Benutzer erstellen
CREATE USER 'econtract_user'@'localhost' IDENTIFIED BY 'econtract_pass';
GRANT ALL PRIVILEGES ON econtract_db.* TO 'econtract_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Projekt klonen und bauen

```bash
cd /home/ubuntu/econtract-ki

# Maven Build
mvn clean package

# Das WAR-File wird erstellt unter:
# target/econtract-ki.war
```

### 3. Konfiguration anpassen

Bearbeiten Sie `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/econtract_db
    username: econtract_user
    password: econtract_pass

# Optional: OpenAI API Key
openai:
  api:
    key: sk-your-api-key-here
```

### 4. Anwendung starten

#### Option A: Spring Boot Standalone

```bash
mvn spring-boot:run
```

Die Anwendung läuft dann auf: `http://localhost:8080/econtract`

#### Option B: WAR-Deployment auf Tomcat

```bash
# WAR-File nach Tomcat kopieren
cp target/econtract-ki.war /path/to/tomcat/webapps/

# Tomcat starten
/path/to/tomcat/bin/startup.sh
```

Die Anwendung läuft dann auf: `http://localhost:8080/econtract-ki`

## 📚 API Dokumentation

Nach dem Start der Anwendung ist die API-Dokumentation verfügbar unter:

- **Swagger UI**: `http://localhost:8080/econtract/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/econtract/api-docs`

## 🎯 Verwendung

### Frontend

Öffnen Sie im Browser:

```
http://localhost:8080/econtract/contracts.html
```

### API Beispiele

#### Vertrag erstellen

```bash
curl -X POST http://localhost:8080/econtract/api/v1/contracts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Liefervertrag mit Firma ABC",
    "contractType": "SUPPLIER",
    "partnerName": "ABC GmbH",
    "startDate": "2025-01-01",
    "endDate": "2026-12-31",
    "contractValue": 50000.00,
    "currency": "EUR",
    "department": "Einkauf",
    "ownerUserId": 1,
    "createdBy": 1
  }'
```

#### Alle Verträge abrufen

```bash
curl http://localhost:8080/econtract/api/v1/contracts?page=0&size=20
```

#### Vertrag suchen

```bash
curl http://localhost:8080/econtract/api/v1/contracts/search?q=ABC
```

#### Dashboard-Statistiken

```bash
curl http://localhost:8080/econtract/api/v1/contracts/stats
```

## 🗂️ Projektstruktur

```
econtract-ki/
├── src/main/java/com/jbx/econtract/
│   ├── EContractApplication.java          # Main Application
│   ├── config/                             # Konfigurationen
│   ├── controller/                         # REST Controllers
│   │   └── ContractController.java
│   ├── service/                            # Business Logic
│   │   └── ContractService.java
│   ├── repository/                         # Data Access
│   │   └── ContractRepository.java
│   ├── model/                              # Domain Models
│   │   ├── entity/
│   │   │   └── Contract.java
│   │   └── dto/
│   │       └── ContractDTO.java
│   ├── exception/                          # Exception Handling
│   ├── security/                           # Security
│   └── util/                               # Utilities
├── src/main/resources/
│   ├── application.yml                     # Konfiguration
│   ├── db/migration/                       # Flyway Migrations
│   │   └── V1__initial_schema.sql
│   └── static/                             # Frontend
│       ├── contracts.html
│       ├── css/main.css
│       └── js/contracts.js
├── pom.xml                                 # Maven Dependencies
└── README.md
```

## 🔧 Konfiguration

### Umgebungsvariablen

```bash
# Datenbank
export DB_USERNAME=econtract_user
export DB_PASSWORD=econtract_pass

# OpenAI (optional)
export OPENAI_API_KEY=sk-your-key

# JWT Secret
export JWT_SECRET=your-secret-key

# File Upload
export FILE_UPLOAD_DIR=/var/econtract/uploads

# Solr (optional)
export SOLR_URL=http://localhost:8983/solr
```

## 🧪 Testing

```bash
# Unit Tests ausführen
mvn test

# Integration Tests
mvn verify
```

## 📦 Deployment

### Production Build

```bash
mvn clean package -Pprod
```

### Docker (optional)

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/econtract-ki.war /app/econtract-ki.war
EXPOSE 8080
CMD ["java", "-jar", "/app/econtract-ki.war"]
```

## 🔐 Standard-Login

- **Username**: `admin`
- **Password**: `admin123`

**⚠️ WICHTIG**: Ändern Sie das Passwort nach der ersten Anmeldung!

## 🛡️ Sicherheit

- JWT-basierte Authentifizierung
- Rollenbasierte Zugriffskontrolle (RBAC)
- SQL Injection Prevention durch JPA
- XSS Protection
- CSRF Protection

## 📈 Roadmap

- [ ] Apache Solr Volltextsuche implementieren
- [ ] KI-gestützte Vertragserstellung
- [ ] Genehmigungsworkflow-Engine
- [ ] E-Signatur Integration
- [ ] E-Mail-Benachrichtigungen
- [ ] Dokumenten-Upload und -Verwaltung
- [ ] Fristen-Management mit Erinnerungen
- [ ] Reporting und Analytics Dashboard
- [ ] Multi-Tenancy vollständig implementieren
- [ ] Mobile App (iOS/Android)

## 🤝 Support

Bei Fragen oder Problemen:

- **E-Mail**: support@jb-x.com
- **Website**: https://jb-x.com

## 📄 Lizenz

© 2025 jb-x business solutions GmbH. Alle Rechte vorbehalten.

## 👥 Autoren

- jb-x Development Team

---

**Version**: 1.0.0  
**Datum**: 27. Oktober 2025


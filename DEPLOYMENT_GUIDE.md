# eContract KI v5.0 - Deployment-Anleitung

## 🚀 Schnellstart mit Docker Compose

### Voraussetzungen
- Docker & Docker Compose installiert
- Git installiert
- Eigener Server (Linux empfohlen)
- Domain konfiguriert

### 1. Projekt klonen
```bash
git clone https://github.com/ihre-repo/econtract-ki.git
cd econtract-ki
```

### 2. Konfiguration
Kopieren Sie die `.env.example` Datei zu `.env` und passen Sie die Werte an:
```bash
cp .env.example .env
nano .env
```

**Wichtige Variablen:**
- `MYSQL_ROOT_PASSWORD`
- `MYSQL_DATABASE`
- `MYSQL_USER`
- `MYSQL_PASSWORD`

### 3. Build & Start
```bash
docker-compose up -d --build
```

### 4. Anwendung testen
Öffnen Sie `http://ihre-domain.de` im Browser.

---

## ⚙️ Manuelles Deployment (ohne Docker)

### Voraussetzungen
- Java 17+
- MySQL 8.0+
- Nginx

### 1. Build
```bash
mvn clean package -DskipTests
```

### 2. Datenbank einrichten
- Erstellen Sie eine MySQL-Datenbank und einen Benutzer.
- Passen Sie die `application-production.properties` an.

### 3. Anwendung starten
```bash
java -jar target/econtract-ki.war
```

### 4. Nginx konfigurieren
- Erstellen Sie eine Nginx-Site-Konfiguration (siehe `docker/nginx/conf.d/econtract.conf`).
- Konfigurieren Sie SSL/HTTPS.

---

## 🐧 Systemd-Service

### 1. Service-Datei erstellen
```bash
sudo nano /etc/systemd/system/econtract.service
```

**Inhalt:**
```ini
[Unit]
Description=eContract KI Application
After=network.target

[Service]
User=ubuntu
Group=ubuntu
WorkingDirectory=/home/ubuntu/econtract-ki
ExecStart=/usr/bin/java -jar /home/ubuntu/econtract-ki/target/econtract-ki.war
SuccessExitStatus=143
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

### 2. Service aktivieren
```bash
sudo systemctl daemon-reload
sudo systemctl enable econtract.service
sudo systemctl start econtract.service
sudo systemctl status econtract.service
```

---

## 🔒 Nginx mit SSL (Let's Encrypt)

### 1. Certbot installieren
```bash
sudo apt update
sudo apt install certbot python3-certbot-nginx
```

### 2. Zertifikat erstellen
```bash
sudo certbot --nginx -d ihre-domain.de
```

### 3. Nginx-Konfiguration
Siehe `docker/nginx/conf.d/econtract.conf` für ein Beispiel.

---

## 💾 Backup & Restore

### Backup
```bash
docker-compose exec mysql mysqldump -u root -p"${MYSQL_ROOT_PASSWORD}" --all-databases > backup.sql
```

### Restore
```bash
docker-compose exec -T mysql mysql -u root -p"${MYSQL_ROOT_PASSWORD}" < backup.sql
```

---

## 📞 Support

Bei Fragen oder Problemen:
- **Dokumentation:** Siehe `*.md` Dateien im Archiv
- **GitHub:** Erstellen Sie ein Issue

---

**© 2025 jb-x business solutions GmbH | eContract KI v5.0**


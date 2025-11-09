# Flyway Repair - Schritt-für-Schritt Anleitung

## 🎯 Ziel

Die fehlgeschlagene V16 Migration aus der Flyway History löschen, damit sie beim nächsten Deployment erneut ausgeführt werden kann.

---

## ⏱️ Geschätzte Zeit: 5-10 Minuten

---

## 📋 Voraussetzungen

- Zugriff auf Render Dashboard
- PostgreSQL Client (z.B. DBeaver, pgAdmin, oder psql)

---

# Option 1: Via Render Web Console (Einfachste Methode)

## Schritt 1: Render Dashboard öffnen

1. Gehen Sie zu: https://dashboard.render.com/
2. Loggen Sie sich ein
3. Klicken Sie auf Ihre **Database** (nicht den Service!)

**Screenshot-Hinweis:** Sie sollten eine Liste Ihrer Services sehen. Wählen Sie die **PostgreSQL Database** aus (z.B. "econtract-db" oder ähnlich).

---

## Schritt 2: Shell öffnen

1. In der Database-Ansicht, klicken Sie auf **"Shell"** (oben rechts)
2. Es öffnet sich eine Terminal-Konsole

**Screenshot-Hinweis:** Die Shell sieht aus wie ein schwarzes Terminal-Fenster im Browser.

---

## Schritt 3: SQL-Befehl ausführen

Kopieren Sie folgenden Befehl und fügen Sie ihn in die Shell ein:

```sql
DELETE FROM flyway_schema_history 
WHERE version = '16' AND success = false;
```

Drücken Sie **Enter**.

**Erwartete Ausgabe:**
```
DELETE 1
```

Das bedeutet: 1 Zeile wurde gelöscht (die fehlgeschlagene V16 Migration).

---

## Schritt 4: Prüfen

Führen Sie folgenden Befehl aus, um zu prüfen, ob V16 gelöscht wurde:

```sql
SELECT version, description, success, installed_on 
FROM flyway_schema_history 
WHERE version >= '14'
ORDER BY installed_rank;
```

**Erwartete Ausgabe:**
```
 version |        description         | success |     installed_on
---------+----------------------------+---------+---------------------
 14      | contract upload workflow   | t       | 2025-11-08 ...
 15      | convert contract status    | t       | 2025-11-09 ...
 (V16 sollte NICHT in der Liste sein!)
```

---

## Schritt 5: Service neu starten

1. Gehen Sie zurück zum **Service** (econtract-ki)
2. Klicken Sie auf **"Manual Deploy"** → **"Deploy latest commit"**
3. Warten Sie ~10-15 Minuten

**Was passiert:**
- Render baut v1.0.16 neu
- Flyway führt V16 aus (jetzt erfolgreich!)
- Flyway führt V19 aus (erstellt alle fehlenden Tabellen)
- Application startet! 🎉

---

# Option 2: Via DBeaver (Grafische Oberfläche)

## Schritt 1: DBeaver installieren

Falls noch nicht installiert:
- Download: https://dbeaver.io/download/
- Installieren Sie die Community Edition (kostenlos)

---

## Schritt 2: Connection String aus Render kopieren

1. Render Dashboard → **Database** → **"Connect"**
2. Wählen Sie **"External Connection"**
3. Kopieren Sie die **"Connection String"**

**Format:**
```
postgresql://user:password@host:port/database
```

**Beispiel:**
```
postgresql://econtract_user:abc123xyz@dpg-xyz.oregon-postgres.render.com:5432/econtract_db
```

---

## Schritt 3: Verbindung in DBeaver erstellen

1. DBeaver öffnen
2. **"Database"** → **"New Database Connection"**
3. Wählen Sie **"PostgreSQL"**
4. Klicken Sie **"Next"**

**Connection Settings:**
- **Host:** `dpg-xyz.oregon-postgres.render.com` (aus Connection String)
- **Port:** `5432` (aus Connection String)
- **Database:** `econtract_db` (aus Connection String)
- **Username:** `econtract_user` (aus Connection String)
- **Password:** `abc123xyz` (aus Connection String)

5. Klicken Sie **"Test Connection"**
6. Sollte "Connected" anzeigen ✅
7. Klicken Sie **"Finish"**

---

## Schritt 4: SQL ausführen

1. Rechtsklick auf die Verbindung → **"SQL Editor"** → **"New SQL Script"**
2. Kopieren Sie folgenden SQL-Code:

```sql
-- Fehlgeschlagene V16 Migration löschen
DELETE FROM flyway_schema_history 
WHERE version = '16' AND success = false;

-- Prüfen ob erfolgreich gelöscht
SELECT version, description, success, installed_on 
FROM flyway_schema_history 
WHERE version >= '14'
ORDER BY installed_rank;
```

3. Markieren Sie die erste Zeile (DELETE)
4. Klicken Sie auf **"Execute SQL Statement"** (oder Strg+Enter)
5. **Erwartete Ausgabe:** "1 row affected"

6. Markieren Sie die zweite Query (SELECT)
7. Klicken Sie auf **"Execute SQL Statement"**
8. **Erwartete Ausgabe:** Tabelle ohne V16

---

## Schritt 5: Service neu starten

Siehe Option 1, Schritt 5.

---

# Option 3: Via psql (Command Line)

## Schritt 1: psql installieren

**macOS:**
```bash
brew install postgresql
```

**Windows:**
- Download: https://www.postgresql.org/download/windows/
- Installieren Sie nur "Command Line Tools"

**Linux:**
```bash
sudo apt-get install postgresql-client
```

---

## Schritt 2: Connection String aus Render kopieren

Siehe Option 2, Schritt 2.

---

## Schritt 3: Mit psql verbinden

```bash
psql "postgresql://user:password@host:port/database"
```

**Beispiel:**
```bash
psql "postgresql://econtract_user:abc123xyz@dpg-xyz.oregon-postgres.render.com:5432/econtract_db"
```

**Erwartete Ausgabe:**
```
SSL connection (protocol: TLSv1.3, cipher: TLS_AES_256_GCM_SHA384, bits: 256, compression: off)
Type "help" for help.

econtract_db=>
```

---

## Schritt 4: SQL ausführen

```sql
DELETE FROM flyway_schema_history 
WHERE version = '16' AND success = false;
```

**Erwartete Ausgabe:**
```
DELETE 1
```

Prüfen:
```sql
SELECT version, description, success, installed_on 
FROM flyway_schema_history 
WHERE version >= '14'
ORDER BY installed_rank;
```

**Erwartete Ausgabe:**
```
 version |        description         | success |     installed_on
---------+----------------------------+---------+---------------------
 14      | contract upload workflow   | t       | 2025-11-08 ...
 15      | convert contract status    | t       | 2025-11-09 ...
```

Beenden:
```sql
\q
```

---

## Schritt 5: Service neu starten

Siehe Option 1, Schritt 5.

---

# ✅ Erfolgskriterien

Nach dem Neustart sollten Sie in den Render Logs sehen:

```
✅ Flyway Community Edition 10.4.1 by Redgate
✅ Successfully validated 16 migrations
✅ Current version of schema "public": 15
✅ Migrating schema "public" to version "16 - performance indexes"
✅ Successfully applied 1 migration to schema "public", now at version v16
✅ Migrating schema "public" to version "19 - comprehensive repair all tables"
✅ Successfully applied 1 migration to schema "public", now at version v19
✅ V19: Comprehensive Repair completed successfully!
✅ Migrated X partners from contracts
✅ Started EContractApplication in XX seconds
✅ Your service is live 🎉
```

---

# ❌ Troubleshooting

## Problem: "Connection refused"

**Ursache:** Render Database ist nicht von außen erreichbar.

**Lösung:** 
1. Render Dashboard → Database → Settings
2. Prüfen Sie "External Connections" ist aktiviert
3. Kopieren Sie die korrekte External Connection String

---

## Problem: "Authentication failed"

**Ursache:** Falsches Passwort oder Username.

**Lösung:**
1. Kopieren Sie die Connection String erneut aus Render
2. Achten Sie auf Sonderzeichen im Passwort (müssen ggf. escaped werden)

---

## Problem: "Table flyway_schema_history does not exist"

**Ursache:** Flyway wurde noch nie ausgeführt.

**Lösung:**
- Das ist unwahrscheinlich, da die App bereits deployed wurde
- Prüfen Sie, ob Sie mit der richtigen Datenbank verbunden sind

---

## Problem: "DELETE 0" (keine Zeile gelöscht)

**Ursache:** V16 ist bereits gelöscht oder war erfolgreich.

**Lösung:**
1. Führen Sie den SELECT-Befehl aus, um zu prüfen
2. Wenn V16 nicht in der Liste ist: Gut! Starten Sie den Service neu
3. Wenn V16 mit `success = t` in der Liste ist: Auch gut! Starten Sie den Service neu

---

# 📞 Support

Falls Probleme auftreten:
1. Senden Sie mir die Ausgabe der SQL-Befehle
2. Senden Sie mir die Render Logs nach dem Neustart
3. Ich helfe Ihnen weiter!

---

# 🎉 Nach erfolgreichem Repair

**Die Application sollte jetzt:**
- ✅ Erfolgreich starten
- ✅ Alle Tabellen haben (contract_uploads, revenue_items, partners)
- ✅ Alle Indexes erstellt
- ✅ Partner-Daten migriert
- ✅ Login funktioniert
- ✅ Dashboard lädt

**Testen Sie:**
1. https://econtract-ki.onrender.com/econtract/login.html
2. Login: `admin` / `admin123`
3. Dashboard sollte laden ohne Fehler

---

**Viel Erfolg!** 🚀

# Flyway Repair Guide - eContract KI

**Datum:** 09.11.2025  
**Problem:** V16 Migration fehlgeschlagen, V17 kann nicht ausgeführt werden  
**Lösung:** Flyway Repair durchführen

---

## Problem-Beschreibung

**Was ist passiert?**

1. V16 Migration versuchte, Indexes auf `partners` Tabelle zu erstellen
2. `partners` Tabelle existierte noch nicht (wird erst in V17 erstellt)
3. V16 schlug fehl mit: `ERROR: relation "partners" does not exist`
4. Flyway markierte V16 als "fehlgeschlagen"
5. V17 wird nicht ausgeführt (weil V16 fehlgeschlagen ist)

**Aktueller Zustand:**
- ✅ V1-V15: Erfolgreich angewendet
- ❌ V16: Fehlgeschlagen
- ⏳ V17: Nicht ausgeführt (blockiert durch V16)

**Ziel:**
- ✅ V16 reparieren (fehlgeschlagenen Eintrag löschen)
- ✅ V16 erneut ausführen (mit Fix)
- ✅ V17 ausführen (partners Tabelle erstellen)

---

## Lösung 1: Flyway Repair via Render Dashboard (Empfohlen)

### Schritt 1: Render Dashboard öffnen

1. Gehen Sie zu: https://dashboard.render.com/
2. Melden Sie sich an
3. Wählen Sie Service: **econtract-ki**

---

### Schritt 2: Shell öffnen

1. Klicken Sie auf **"Shell"** in der linken Navigation
2. Oder: Service → **"Connect"** → **"Shell"**

**Alternative:** Wenn "Shell" nicht verfügbar ist:
- Render Free Tier hat möglicherweise keinen Shell-Zugriff
- Verwenden Sie dann **Lösung 2** (SQL Repair)

---

### Schritt 3: Flyway Status prüfen

**Im Shell, führen Sie aus:**

```bash
cd /opt/render/project/src
./mvnw flyway:info
```

**Erwartete Ausgabe:**
```
+------------+---------+---------------------+------+---------------------+---------+
| Category   | Version | Description         | Type | Installed On        | State   |
+------------+---------+---------------------+------+---------------------+---------+
| Versioned  | 1       | initial schema      | SQL  | 2025-11-08 12:00:00 | Success |
| ...        | ...     | ...                 | ...  | ...                 | ...     |
| Versioned  | 15      | convert contract... | SQL  | 2025-11-09 04:00:00 | Success |
| Versioned  | 16      | performance indexes | SQL  | 2025-11-09 05:00:00 | Failed  | ← PROBLEM!
| Versioned  | 17      | add partners table  | SQL  |                     | Pending |
+------------+---------+---------------------+------+---------------------+---------+
```

**Wichtig:** Notieren Sie, dass V16 "Failed" ist!

---

### Schritt 4: Flyway Repair durchführen

**Im Shell, führen Sie aus:**

```bash
./mvnw flyway:repair
```

**Was macht `flyway:repair`?**
1. Entfernt fehlgeschlagene Migrationen aus `flyway_schema_history`
2. Korrigiert Checksummen
3. Bereitet Flyway für erneute Migration vor

**Erwartete Ausgabe:**
```
[INFO] Successfully repaired schema history table "public"."flyway_schema_history"
[INFO] Repair of failed migration in Schema History table "public"."flyway_schema_history" successful
[INFO] Removed failed migration: 16
```

---

### Schritt 5: Flyway Migrate durchführen

**Im Shell, führen Sie aus:**

```bash
./mvnw flyway:migrate
```

**Was passiert jetzt?**
1. Flyway führt V16 erneut aus (mit Fix - ohne partners Indexes)
2. Flyway führt V17 aus (erstellt partners Tabelle)
3. Alle Migrationen sollten erfolgreich sein

**Erwartete Ausgabe:**
```
[INFO] Migrating schema "public" to version "16 - performance indexes"
[INFO] Successfully applied 1 migration to schema "public", now at version v16
[INFO] Migrating schema "public" to version "17 - add partners table"
[INFO] Successfully applied 1 migration to schema "public", now at version v17
```

---

### Schritt 6: Flyway Status erneut prüfen

**Im Shell, führen Sie aus:**

```bash
./mvnw flyway:info
```

**Erwartete Ausgabe:**
```
+------------+---------+---------------------+------+---------------------+---------+
| Category   | Version | Description         | Type | Installed On        | State   |
+------------+---------+---------------------+------+---------------------+---------+
| ...        | ...     | ...                 | ...  | ...                 | ...     |
| Versioned  | 16      | performance indexes | SQL  | 2025-11-09 06:00:00 | Success | ← FIXED!
| Versioned  | 17      | add partners table  | SQL  | 2025-11-09 06:00:01 | Success | ← NEW!
+------------+---------+---------------------+------+---------------------+---------+
```

**Alle Migrationen sollten jetzt "Success" sein!** ✅

---

### Schritt 7: Service neu starten

**Wichtig:** Render muss den Service neu starten, damit die Änderungen wirksam werden.

**Option A: Automatisch (empfohlen)**
- Render startet automatisch neu nach Shell-Aktivität

**Option B: Manuell**
1. Render Dashboard → Service → **"Manual Deploy"**
2. Wählen Sie Branch: **master**
3. Klicken Sie **"Deploy"**

---

### Schritt 8: Verifizierung

**Nach dem Neustart, prüfen Sie:**

1. **Application Logs:**
   ```
   Started EContractApplication in X seconds
   Current version of schema "public": 17
   ```

2. **Partner-Daten abfragen:**
   - Siehe Abschnitt "Partner-Daten prüfen" unten

---

## Lösung 2: SQL Repair (Falls Shell nicht verfügbar)

### Schritt 1: Datenbank-Verbindung herstellen

1. Render Dashboard → **Database** (nicht Service!)
2. Klicken Sie auf Ihre PostgreSQL Datenbank
3. Kopieren Sie die **External Connection String**

**Format:**
```
postgresql://user:password@host:port/database
```

---

### Schritt 2: PostgreSQL Client verwenden

**Option A: psql (Command Line)**

```bash
psql "postgresql://user:password@host:port/database"
```

**Option B: DBeaver / pgAdmin (GUI)**
1. Öffnen Sie DBeaver oder pgAdmin
2. Erstellen Sie neue Verbindung
3. Geben Sie Host, Port, User, Password, Database ein
4. Testen Sie die Verbindung

---

### Schritt 3: Flyway Schema History prüfen

```sql
SELECT installed_rank, version, description, type, script, checksum, 
       installed_by, installed_on, execution_time, success
FROM flyway_schema_history
WHERE version IN ('16', '17')
ORDER BY installed_rank DESC;
```

**Erwartete Ausgabe:**
```
 installed_rank | version | description         | success 
----------------+---------+---------------------+---------
 16             | 16      | performance indexes | f       ← FALSE = Failed!
```

---

### Schritt 4: Fehlgeschlagene Migration löschen

```sql
-- Vorsicht: Nur fehlgeschlagene Migrationen löschen!
DELETE FROM flyway_schema_history
WHERE version = '16' AND success = false;
```

**Bestätigung:**
```
DELETE 1
```

---

### Schritt 5: Service neu starten

**Render Dashboard:**
1. Service → **"Manual Deploy"**
2. Branch: **master**
3. **"Deploy"**

**Was passiert:**
- Render startet Service neu
- Flyway erkennt, dass V16 fehlt
- Flyway führt V16 erneut aus (mit Fix)
- Flyway führt V17 aus
- Alle Migrationen erfolgreich ✅

---

### Schritt 6: Verifizierung

**Nach Deployment, prüfen Sie Logs:**

```
Successfully applied 1 migration to schema "public", now at version v16
Successfully applied 1 migration to schema "public", now at version v17
Started EContractApplication in X seconds
```

---

## Partner-Daten prüfen

### Via SQL (Datenbank-Client)

```sql
-- Anzahl Partner
SELECT COUNT(*) FROM partners;

-- Erste 5 Partner
SELECT id, name, partner_type, email, city, is_active
FROM partners
ORDER BY id
LIMIT 5;

-- Partner nach Typ
SELECT partner_type, COUNT(*) as count
FROM partners
GROUP BY partner_type
ORDER BY count DESC;
```

---

### Via Application Logs

**Nach Deployment, suchen Sie in den Logs nach:**

```
INSERT INTO partners (...)
```

**Anzahl eingefügter Partner:**
```
INSERT 0 X  ← X = Anzahl Partner
```

---

## Troubleshooting

### Problem: Shell nicht verfügbar

**Lösung:** Verwenden Sie **Lösung 2** (SQL Repair)

---

### Problem: `flyway:repair` schlägt fehl

**Fehler:**
```
[ERROR] Unable to repair schema history table
```

**Lösung:**
1. Prüfen Sie Datenbank-Verbindung
2. Prüfen Sie, ob `flyway_schema_history` Tabelle existiert
3. Verwenden Sie **Lösung 2** (SQL Repair)

---

### Problem: V16 schlägt erneut fehl

**Fehler:**
```
ERROR: relation "partners" does not exist
```

**Ursache:** Der Fix wurde noch nicht deployed!

**Lösung:**
1. Prüfen Sie, ob Commit `38c37ed` deployed ist
2. Warten Sie auf Deployment
3. Führen Sie Flyway Repair erneut durch

---

### Problem: V17 schlägt fehl

**Fehler:**
```
ERROR: relation "partners" already exists
```

**Ursache:** V17 wurde bereits teilweise ausgeführt

**Lösung:**
```sql
-- Prüfen, ob partners Tabelle existiert
SELECT table_name 
FROM information_schema.tables 
WHERE table_name = 'partners';

-- Falls ja: V17 aus History löschen
DELETE FROM flyway_schema_history
WHERE version = '17';

-- Service neu starten
```

---

### Problem: Keine Partner in Tabelle

**Ursache:** Sample Contracts haben keine `partner_name`

**Lösung:**
```sql
-- Prüfen, ob Contracts partner_name haben
SELECT COUNT(*) FROM contracts WHERE partner_name IS NOT NULL;

-- Falls 0: Manuell Partner hinzufügen
INSERT INTO partners (name, partner_type, is_active, created_by)
VALUES 
    ('Test Partner GmbH', 'CUSTOMER', true, 1),
    ('Supplier AG', 'SUPPLIER', true, 1),
    ('Service Provider Ltd', 'SERVICE_PROVIDER', true, 1);
```

---

## Zusammenfassung

**Schritte:**
1. ✅ Flyway Repair durchführen (Shell oder SQL)
2. ✅ Service neu starten
3. ✅ V16 & V17 werden automatisch angewendet
4. ✅ Partner-Daten prüfen

**Erwartetes Ergebnis:**
- ✅ V16: Success (ohne partners Indexes)
- ✅ V17: Success (partners Tabelle erstellt)
- ✅ Partners Tabelle mit Daten gefüllt
- ✅ Application läuft stabil

---

## Kontakt

**Bei Problemen:**
- GitHub Issues: https://github.com/jb-x-dev/econtract-ki/issues
- Email: admin@jb-x.com

---

**Erstellt am:** 09.11.2025, 02:00 Uhr  
**Autor:** Manus AI Agent  
**Version:** 1.0.13-SNAPSHOT

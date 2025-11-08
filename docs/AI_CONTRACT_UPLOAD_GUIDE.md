# AI Contract Upload - Benutzerhandbuch

## Übersicht

Das **AI Contract Upload Feature** ermöglicht es Ihnen, Verträge hochzuladen und automatisch durch künstliche Intelligenz analysieren zu lassen. Die KI extrahiert wichtige Vertragsdaten, die Sie dann überprüfen und bestätigen können. Anschließend wird der Vertrag automatisch angelegt und Rechnungszeitpunkte werden generiert.

## Funktionen

### 1. Automatische Vertragsanalyse
- **Unterstützte Formate:** PDF, Word (.docx), Text (.txt)
- **KI-Modell:** OpenAI GPT-4.1-mini
- **Extrahierte Daten:**
  - Vertragsnummer
  - Titel und Vertragstyp
  - Vertragspartner
  - Start- und Enddatum
  - Vertragswert und Währung
  - Kündigungsfrist
  - Abrechnungszyklus und -betrag
  - Zahlungsbedingungen

### 2. Automatische Rechnungsgenerierung
- Basierend auf Abrechnungszyklus (monatlich, quartalsweise, jährlich)
- Automatische Berechnung von Rechnungsterminen
- Status: "SCHEDULED" für zukünftige Rechnungen

### 3. Umsatzdaten-Import
- Import von CSV oder Excel-Dateien
- Automatische Zuordnung zu Rechnungen
- Unterstützte Umsatztypen: Recurring, One-Time, Usage-Based

## Schritt-für-Schritt Anleitung

### Schritt 1: Vertrag hochladen

1. Navigieren Sie zu **Upload** in der Navigation
2. Ziehen Sie eine Vertragsdatei in die Upload-Zone ODER
3. Klicken Sie auf "Datei auswählen" und wählen Sie eine Datei aus
4. Klicken Sie auf "Hochladen & Analysieren"

**Hinweis:** Die Analyse kann 10-30 Sekunden dauern, abhängig von der Dateigröße.

### Schritt 2: Extrahierte Daten überprüfen

Nach erfolgreicher Analyse werden die extrahierten Daten angezeigt:

- **Pflichtfelder:**
  - Titel
  - Vertragstyp
  - Vertragspartner
  
- **Optionale Felder:**
  - Vertragsnummer (wird automatisch generiert, falls leer)
  - Start- und Enddatum
  - Vertragswert
  - Kündigungsfrist
  - Abrechnungsinformationen

**Wichtig:** Überprüfen Sie alle Felder sorgfältig und korrigieren Sie bei Bedarf!

### Schritt 3: Abrechnungszyklus festlegen

Wenn der Vertrag wiederkehrende Zahlungen hat:

1. Wählen Sie den **Abrechnungszyklus**:
   - Monatlich
   - Quartalsweise
   - Jährlich
   - Einmalig

2. Geben Sie den **Abrechnungsbetrag** ein

3. Wählen Sie das **Datum der ersten Rechnung**

**Beispiel:**
- Abrechnungszyklus: Monatlich
- Abrechnungsbetrag: 10.000 EUR
- Erste Rechnung: 15.01.2025
- Vertragsende: 31.12.2026

→ Das System generiert automatisch 24 Rechnungen (Januar 2025 - Dezember 2026)

### Schritt 4: Vertrag anlegen

1. Klicken Sie auf "Vertrag anlegen & Rechnungen generieren"
2. Das System:
   - Legt den Vertrag an
   - Generiert alle Rechnungen basierend auf dem Abrechnungszyklus
   - Setzt den Status auf "SCHEDULED"
3. Sie werden zur Vertragsübersicht weitergeleitet

## Umsatzdaten importieren

### CSV-Format

Erstellen Sie eine CSV-Datei mit folgenden Spalten:

```csv
date,description,amount,type,notes
2025-01-15,Monatliche Gebühr,10000.00,RECURRING,Januar 2025
2025-01-20,Setup-Fee,5000.00,ONE_TIME,Einmalige Einrichtung
2025-02-15,Monatliche Gebühr,10000.00,RECURRING,Februar 2025
```

**Spalten:**
- `date`: Datum im Format YYYY-MM-DD
- `description`: Beschreibung der Umsatzposition
- `amount`: Betrag (Dezimalpunkt verwenden)
- `type`: RECURRING, ONE_TIME, oder USAGE_BASED
- `notes`: Optionale Notizen

### Excel-Format

Erstellen Sie eine Excel-Datei (.xlsx) mit denselben Spalten wie oben.

### Import durchführen

1. Öffnen Sie den Vertrag in der Vertragsübersicht
2. Klicken Sie auf "Umsatzdaten importieren"
3. Wählen Sie Ihre CSV oder Excel-Datei
4. Das System:
   - Importiert alle Umsatzpositionen
   - Ordnet sie automatisch den passenden Rechnungen zu (basierend auf Datum)
   - Zeigt eine Zusammenfassung

## API-Endpunkte

Für Entwickler und Integrationen:

### Upload Contract
```http
POST /api/v1/contracts/upload
Content-Type: multipart/form-data

file: [Binary File]
userId: 1
```

### Get Upload Status
```http
GET /api/v1/contracts/upload/{uploadId}/status

Response:
{
  "uploadId": 123,
  "status": "EXTRACTED",
  "extractedData": { ... }
}
```

### Confirm & Create Contract
```http
POST /api/v1/contracts/upload/{uploadId}/confirm
Content-Type: application/json

{
  "contractNumber": "VTR-2025-001",
  "title": "Dienstleistungsvertrag",
  ...
}
```

### Import Revenue Data
```http
POST /api/v1/contracts/{contractId}/revenue/import
Content-Type: multipart/form-data

file: [CSV or Excel File]
userId: 1
```

### Get Revenue Summary
```http
GET /api/v1/contracts/{contractId}/revenue/summary

Response:
{
  "totalRevenue": 120000.00,
  "recurringRevenue": 100000.00,
  "oneTimeRevenue": 20000.00,
  "itemCount": 24,
  "unassignedCount": 0
}
```

## Häufige Fragen (FAQ)

### Welche Dateiformate werden unterstützt?
- PDF (.pdf)
- Microsoft Word (.docx)
- Text (.txt)

### Wie genau ist die KI-Extraktion?
Die KI erreicht eine Genauigkeit von ca. 85-95%, abhängig von der Qualität und Struktur des Vertrags. Überprüfen Sie immer alle extrahierten Daten!

### Was passiert, wenn die KI Daten nicht erkennt?
Fehlende Felder bleiben leer und können manuell ausgefüllt werden.

### Kann ich die Rechnungen nachträglich ändern?
Ja, Sie können die generierten Rechnungen in der Rechnungsübersicht bearbeiten.

### Werden die hochgeladenen Dateien gespeichert?
Ja, die Originaldateien werden im System gespeichert und können jederzeit heruntergeladen werden.

### Kann ich mehrere Verträge gleichzeitig hochladen?
Aktuell nicht, aber Sie können mehrere Uploads nacheinander durchführen.

## Tipps für beste Ergebnisse

1. **Verwenden Sie klare, strukturierte Verträge**
   - Überschriften und Absätze helfen der KI
   - Vermeiden Sie handschriftliche Notizen

2. **Überprüfen Sie Datumsangaben**
   - Die KI erkennt verschiedene Datumsformate
   - Kontrollieren Sie Start- und Enddatum

3. **Geben Sie Abrechnungsinformationen an**
   - Auch wenn die KI sie nicht findet
   - Ermöglicht automatische Rechnungsgenerierung

4. **Importieren Sie Umsatzdaten regelmäßig**
   - Halten Sie Ihre Finanzdaten aktuell
   - Nutzen Sie CSV-Templates für konsistente Daten

## Support

Bei Fragen oder Problemen:
- **Email:** support@econtract-ki.com
- **Dokumentation:** https://docs.econtract-ki.com
- **GitHub Issues:** https://github.com/jb-x-dev/econtract-ki/issues

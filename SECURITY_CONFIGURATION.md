# eContract KI - Security Configuration Guide

**Datum:** 09.11.2025  
**Version:** 1.0.12-SNAPSHOT

---

## Overview

Diese Anleitung beschreibt, wie Sie die eContract KI Anwendung **privat** und **sicher** halten können.

---

## Implemented Security Measures

### 1. robots.txt ✅

**Location:** `/src/main/resources/static/robots.txt`

**Purpose:** Verhindert, dass Suchmaschinen die Anwendung crawlen und indexieren.

**Coverage:**
- Google, Bing, Yahoo, DuckDuckGo
- Baidu, Yandex
- AI Crawlers (GPTBot, Claude, CCBot)

**Test:**
```bash
curl https://econtract-ki.onrender.com/robots.txt
```

**Expected:**
```
User-agent: *
Disallow: /
```

---

### 2. Security Headers ✅

**Location:** `/src/main/java/com/jbx/econtract/config/SecurityHeadersFilter.java`

**Headers:**

| Header | Value | Purpose |
|--------|-------|---------|
| `X-Robots-Tag` | `noindex, nofollow, noarchive, nosnippet` | Verhindert Indexierung |
| `X-XSS-Protection` | `1; mode=block` | XSS Schutz |
| `X-Content-Type-Options` | `nosniff` | MIME-Type Sniffing verhindern |
| `X-Frame-Options` | `DENY` | Clickjacking verhindern |
| `Content-Security-Policy` | Restrictive | XSS & Injection Schutz |
| `Referrer-Policy` | `no-referrer` | Keine Referrer-Informationen |
| `Permissions-Policy` | Restrictive | Geolocation, Camera, Mic blockiert |

**Test:**
```bash
curl -I https://econtract-ki.onrender.com/econtract/login.html | grep -E "X-Robots|X-XSS|X-Frame"
```

---

### 3. Spring Security Authentication ✅

**Location:** `/src/main/java/com/jbx/econtract/config/SecurityConfig.java`

**Protection:**
- ✅ Alle Seiten außer `/login.html` erfordern Authentication
- ✅ Alle APIs (`/api/v1/**`) erfordern Authentication
- ✅ Session-basierte Authentication
- ✅ BCrypt Password Hashing

**Public Endpoints:**
- `/login.html` - Login-Seite
- `/health` - Health Check (für Render)
- `/api/public/**` - Öffentliche APIs
- `/css/**`, `/js/**`, `/images/**` - Static Resources

---

### 4. IP Whitelisting (Optional) ⏳

**Location:** `/src/main/java/com/jbx/econtract/config/IPWhitelistFilter.java`

**Status:** Implementiert, aber **standardmäßig deaktiviert**

**Aktivierung:**

**application.yml:**
```yaml
app:
  security:
    ip-whitelist:
      enabled: true
      allowed-ips: 1.2.3.4,5.6.7.8
```

**Oder via Environment Variable:**
```bash
export APP_SECURITY_IP_WHITELIST_ENABLED=true
export APP_SECURITY_IP_WHITELIST_ALLOWED_IPS=1.2.3.4,5.6.7.8
```

**Ihre IP herausfinden:**
```bash
curl https://api.ipify.org
```

**Note:** Health Check Endpoint (`/health`) ist **immer** zugänglich (für Render Monitoring).

---

## Recommended Security Configuration

### Level 1: Basic (Current) ✅

**Implemented:**
- ✅ robots.txt
- ✅ Security Headers
- ✅ Spring Security Authentication
- ✅ BCrypt Password Hashing

**Protection:**
- ✅ Verhindert Crawling durch Suchmaschinen
- ✅ Schützt vor XSS, Clickjacking
- ✅ Erfordert Login für alle Seiten

**Suitable for:** Development, Testing

---

### Level 2: Enhanced (Recommended)

**Additional Measures:**
- ✅ IP Whitelisting aktivieren
- ✅ Starkes Admin-Passwort (min. 16 Zeichen)
- ✅ HTTPS erzwingen (Render macht das automatisch)

**Configuration:**

1. **IP Whitelisting aktivieren:**
   ```yaml
   app:
     security:
       ip-whitelist:
         enabled: true
         allowed-ips: YOUR_IP_HERE
   ```

2. **Admin-Passwort ändern:**
   ```sql
   UPDATE users 
   SET password = '$2b$12$NEW_BCRYPT_HASH_HERE' 
   WHERE username = 'admin';
   ```

**Suitable for:** Production (Single User)

---

### Level 3: Maximum (Enterprise)

**Additional Measures:**
- ✅ Two-Factor Authentication (2FA)
- ✅ Rate Limiting (Brute-Force Schutz)
- ✅ Audit Logging
- ✅ Intrusion Detection
- ✅ VPN-Only Access

**Implementation:** Requires additional development

**Suitable for:** Enterprise, High-Security Environments

---

## How to Enable IP Whitelisting

### Step 1: Find Your IP Address

**Option A: Web Browser**
- Gehen Sie zu: https://www.whatismyip.com/
- Notieren Sie Ihre IP-Adresse

**Option B: Command Line**
```bash
curl https://api.ipify.org
```

**Example Output:**
```
203.0.113.42
```

---

### Step 2: Configure Render Environment Variables

**Render Dashboard:**
1. Gehe zu https://dashboard.render.com/
2. Wähle Service "econtract-ki"
3. Klicke auf "Environment"
4. Füge hinzu:
   - **Key:** `APP_SECURITY_IP_WHITELIST_ENABLED`
   - **Value:** `true`
5. Füge hinzu:
   - **Key:** `APP_SECURITY_IP_WHITELIST_ALLOWED_IPS`
   - **Value:** `203.0.113.42` (Ihre IP)
6. Klicke "Save Changes"
7. Service wird automatisch neu deployed

---

### Step 3: Test IP Whitelisting

**From Your IP (should work):**
```bash
curl https://econtract-ki.onrender.com/econtract/login.html
```

**Expected:** Login-Seite HTML

**From Different IP (should fail):**
- Öffnen Sie die URL in einem VPN oder anderem Netzwerk
- **Expected:** "Access Denied: Your IP address is not authorized..."

---

### Step 4: Add Multiple IPs (Optional)

**Example:**
```
APP_SECURITY_IP_WHITELIST_ALLOWED_IPS=203.0.113.42,198.51.100.10,192.0.2.5
```

**Use Cases:**
- Ihr Home-Netzwerk
- Ihr Office-Netzwerk
- Ihr Mobilfunknetz

**Note:** Dynamische IPs (z.B. Mobilfunk) ändern sich häufig!

---

## Security Best Practices

### 1. Strong Passwords ✅

**Current Admin Password:** `admin123` ❌ **WEAK!**

**Recommendation:**
- Mindestens 16 Zeichen
- Groß-/Kleinbuchstaben, Zahlen, Sonderzeichen
- Keine Wörter aus dem Wörterbuch

**Generate Strong Password:**
```bash
openssl rand -base64 24
```

**Update Password:**
```sql
-- Generate BCrypt hash first (use Java or online tool)
UPDATE users 
SET password = '$2b$12$YOUR_NEW_HASH_HERE' 
WHERE username = 'admin';
```

---

### 2. Regular Security Updates ✅

**Check for Updates:**
- Spring Boot
- Dependencies (pom.xml)
- PostgreSQL
- Render Platform

**Frequency:** Monthly

---

### 3. Audit Logging

**Recommendation:** Log all login attempts

**Implementation:**
```java
@Component
public class LoginAuditListener implements ApplicationListener<AuthenticationSuccessEvent> {
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        String ip = ((WebAuthenticationDetails) event.getAuthentication().getDetails()).getRemoteAddress();
        log.info("Successful login: user={}, ip={}", username, ip);
    }
}
```

---

### 4. Backup Strategy

**Recommendation:**
- Daily database backups
- Store backups encrypted
- Test restore procedure

**Render PostgreSQL:**
- Automatic backups (Paid plans)
- Manual backup via `pg_dump`

---

### 5. Monitoring

**Recommendation:**
- Monitor failed login attempts
- Alert on suspicious activity
- Track API usage

**Tools:**
- Render Metrics
- Spring Boot Actuator
- External monitoring (UptimeRobot)

---

## Common Security Questions

### Q: Kann Google meine Anwendung finden?

**A:** Nein, durch `robots.txt` und `X-Robots-Tag` Header wird Crawling verhindert.

**Note:** Bereits indexierte Seiten bleiben sichtbar! Verwenden Sie Google Search Console um Removal zu beantragen.

---

### Q: Kann jemand ohne Passwort auf die Anwendung zugreifen?

**A:** Nein, alle Seiten außer Login erfordern Authentication.

**Exception:** Health Check (`/health`) ist öffentlich für Render Monitoring.

---

### Q: Ist IP Whitelisting sicher genug?

**A:** Ja, für Single-User Anwendungen.

**Limitations:**
- Dynamische IPs ändern sich
- VPN kann umgangen werden
- Nicht geeignet für Multi-User

**Recommendation:** Kombinieren Sie IP Whitelisting mit starkem Passwort.

---

### Q: Was passiert, wenn ich meine IP ändere?

**A:** Sie können nicht mehr auf die Anwendung zugreifen.

**Solution:**
1. Render Dashboard öffnen (von anderem Gerät)
2. Neue IP zu `ALLOWED_IPS` hinzufügen
3. Service neu starten

**Prevention:** Fügen Sie mehrere IPs hinzu (Home, Office, Mobile).

---

### Q: Kann ich die Anwendung komplett offline nehmen?

**A:** Ja, mehrere Optionen:

**Option 1: Render Service pausieren**
- Dashboard → Service → "Suspend Service"
- Kostenlos, kann jederzeit reaktiviert werden

**Option 2: IP Whitelisting auf leere Liste**
- `ALLOWED_IPS=` (leer)
- Niemand kann zugreifen (außer Health Check)

**Option 3: Service löschen**
- Permanent, Daten gehen verloren

---

## Security Checklist

### Before Going Live

- [ ] **robots.txt** deployed
- [ ] **Security Headers** aktiviert
- [ ] **Admin-Passwort** geändert (stark!)
- [ ] **IP Whitelisting** konfiguriert (optional)
- [ ] **HTTPS** erzwungen (Render macht das automatisch)
- [ ] **Backup-Strategie** definiert
- [ ] **Monitoring** eingerichtet

### Monthly Maintenance

- [ ] **Dependencies** updaten
- [ ] **Security Patches** einspielen
- [ ] **Logs** prüfen auf verdächtige Aktivitäten
- [ ] **Backups** testen
- [ ] **IP Whitelist** aktualisieren (falls nötig)

---

## Troubleshooting

### Problem: "Access Denied" nach IP Whitelisting

**Solution:**
1. Prüfen Sie Ihre aktuelle IP: `curl https://api.ipify.org`
2. Vergleichen Sie mit `ALLOWED_IPS` in Render
3. Fügen Sie neue IP hinzu
4. Warten Sie auf Deployment (~5 Min)

---

### Problem: robots.txt wird nicht angezeigt

**Solution:**
1. Prüfen Sie: https://econtract-ki.onrender.com/robots.txt
2. Falls 404: Datei fehlt im Deployment
3. Prüfen Sie `src/main/resources/static/robots.txt`
4. Neu deployen

---

### Problem: Security Headers fehlen

**Solution:**
1. Prüfen Sie: `curl -I https://econtract-ki.onrender.com/econtract/login.html`
2. Falls Header fehlen: SecurityHeadersFilter nicht aktiv
3. Prüfen Sie `@Component` Annotation
4. Neu deployen

---

## Contact & Support

**Bei Fragen zur Sicherheit:**
- GitHub Issues: https://github.com/jb-x-dev/econtract-ki/issues
- Email: admin@jb-x.com

**Security Vulnerabilities:**
- **NICHT** öffentlich melden!
- Email direkt an: security@jb-x.com

---

**Erstellt am:** 09.11.2025  
**Autor:** Manus AI Agent  
**Version:** 1.0.12-SNAPSHOT

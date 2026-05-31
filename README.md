# Webec Survey App

Eine vollständige Web-Anwendung zum Erstellen, Durchführen und Auswerten von Umfragen mit rollenbasierter Sicherheit.

## 🎯 Projekt-Übersicht

Die Survey App ermöglicht es:
- **Koordinatoren** (Administratoren): Umfragen zu erstellen, zu öffnen/schließen und Ergebnisse anzuschauen
- **Respondenten** (normale Benutzer): Sich zu registrieren/anmelden, offene Umfragen auszufüllen und Ergebnisse zu sehen

Das Projekt demonstriert **Authentifizierung und Autorisierung mit rollenbasierter Sicherheit** als Zusatzthema.

## 🛠 Technologie-Stack

### Backend
- **Java 17** + **Spring Boot 3.1.6**
- **Spring Data JPA** für Datenbankoperationen
- **Thymeleaf** als Template-Engine
- **H2 Database** (embedded, persistiert in `./data/surveydb.mv.db`)

### Frontend
- **HTML5** (semantisches Markup)
- **CSS3** (handgeschriebenes CSS, keine Frameworks)
- **JavaScript** (minimales, selbstgeschriebenes JS für UI-Interaktionen)

### Build & Test
- **Maven** als Build-Tool
- **JUnit 5** für Unit-Tests
- **Mockito** für Test-Mocking
- **Playwright** für E2E-Tests (für E2E-Tests muss der Browser mit localhost:8081 erreichbar sein)
- **Spring Boot Test** für Integrationstests

### Deployment
- **JVM (Embedded Tomcat)** - läuft standardmäßig auf Port 8081 (siehe `src/main/resources/application.properties`)

Hinweis: Beim ersten Start legt die Anwendung eine H2-Datei im Ordner `./data/` an. Es existiert außerdem eine kleine Convenience-Seeding-Logik, die beim Start einen Benutzer `coordinator` mit Passwort `password` anlegt (nur Demo/Zwecke).

---

## 📦 Installation & Setup

### Voraussetzungen
- Java 17+
- Maven 3.6+

### Schritt 1: Projekt klonen/öffnen
Wechsle in das Projektverzeichnis. Beispiel (Windows PowerShell):

```powershell
# Ersetze <path-to-project-root> durch den Pfad zu deinem Projekt-Ordner
Set-Location '<path-to-project-root>'
```

### Schritt 2: Dependencies herunterladen & Projekt bauen
Mit dem Maven Wrapper (falls vorhanden) oder lokalem Maven:

```powershell
# Mit Maven Wrapper
.\mvnw.cmd clean install -DskipTests
# Oder mit lokalem Maven
mvn clean install -DskipTests
```

### Schritt 3: Anwendung starten

```powershell
# Mit Maven Wrapper
.\mvnw.cmd -DskipTests spring-boot:run
# Oder mit lokalem Maven
mvn -DskipTests spring-boot:run
```

### Schritt 4: Browser öffnen

```
http://localhost:8081
```

---

## 🔐 Authentifizierung und Autorisierung (Zusatzthema)

### Architektur

Das Projekt implementiert **rollenbasierte Sicherheit (RBAC)** mit zwei Rollen:

#### 1. **Benutzer (User Entity)**
```java
@Entity
@Table(name = "app_user")
public class User {
    private Long id;
    private String username;
    private String password;
    private UserRole role;  // COORDINATOR oder RESPONDENT
}
```

#### 2. **Rollen (UserRole Enum)**
```java
public enum UserRole {
    COORDINATOR,   // Admin: Umfragen erstellen, öffnen/schließen
    RESPONDENT     // Normal-User: Umfragen ausfüllen
}
```

### Authentifizierung (Login/Register)

**Session-basierte Authentifizierung:**

```java
@PostMapping("/login")
public String doLogin(@RequestParam String username, @RequestParam String password, 
                       HttpSession session, Model model) {
    var opt = userRepository.findByUsername(username);
    if (opt.isPresent() && opt.get().getPassword().equals(password)) {
        session.setAttribute("userId", opt.get().getId());
        return "redirect:/";
    }
    model.addAttribute("error", "Ungültiger Benutzername oder Passwort");
    return "login";
}
```

- **Registrierung:** Neue Benutzer wählen bei der Registrierung, ob sie **Coordinator** oder **Respondent** sein möchten
- **Login:** Benutzer authentifizieren sich mit Benutzername + Passwort
- **Session:** Nach erfolgreichem Login wird die `userId` in der HTTP-Session gespeichert

### Autorisierung (Rollenbasierte Zugriffskontrolle)

**GlobalControllerAdvice - Benutzer in alle Templates laden:**
```java
@ControllerAdvice
public class GlobalControllerAdvice {
    @ModelAttribute("currentUser")
    public User currentUser(HttpSession session) {
        Object id = session.getAttribute("userId");
        if (id instanceof Long) {
            return userRepository.findById((Long) id).orElse(null);
        }
        return null;
    }
}
```

**Thymeleaf Conditional Rendering - nur für Coordinators:**
```html
<span th:if="${currentUser != null and currentUser.role.name() == 'COORDINATOR'}">
    <a class="btn wide" href="/coordinator/create">Umfrage erstellen</a>
</span>
```

**Controller-Level Authorization - nur eingeloggte Respondents können Umfragen beantworten:**
```java
@PostMapping("/survey/{id}/submit")
@Transactional
public String submit(@PathVariable Long id, HttpServletRequest req, 
                     HttpSession session, Model model) {
    Object uid = session.getAttribute("userId");
    if (!(uid instanceof Long)) {
        model.addAttribute("error", "Sie müssen angemeldet sein, um die Umfrage auszufüllen.");
        model.addAttribute("survey", s);
        return "take_survey";
    }
    // ... Umfrage beantworten
}
```

### Sicherheits-Features

1. **Duplikat-Check für Responses:**
   - Ein eingeloggter Respondent kann eine Umfrage nur **einmal** ausfüllen
   ```java
   if (responseRepository.findBySurveyAndRespondent(s, user).isPresent()) {
       model.addAttribute("error", "Sie haben diese Umfrage bereits ausgefüllt.");
   }
   ```

2. **Session-Management:**
   - Sessions werden automatisch verwaltet
   - Logout invalidiert die Session
   ```java
   @GetMapping("/logout")
   public String logout(HttpSession session) {
       session.invalidate();
       return "redirect:/";
   }
   ```

3. **Rollen-Specific Features:**
   - **Coordinators:** Nur sie können Umfragen erstellen, öffnen und schließen
   - **Respondents:** Nur sie können Umfragen ausfüllen (und nur wenn sie eingeloggt sind)

---

## 📋 Funktionen

### 🔑 Authentifizierung & Nutzerverwaltung
- Registrierung mit Rollenwahl (Coordinator / Respondent)
- Login mit Benutzername + Passwort
- Session-basierte Authentifizierung
- Logout

### 📝 Umfrage-Verwaltung (Coordinator)
- Umfrage mit bis zu 10 Fragen erstellen
- Pro Frage 1-5 Antwortmöglichkeiten
- Fragen und Optionen dynamisch hinzufügen/löschen
- Umfrage öffnen (für Respondents verfügbar machen)
- Umfrage schließen (keine neuen Antworten möglich)

### 📍 Umfrage-Durchführung (Respondent)
- Offene Umfragen ansehen
- Umfrage ausfüllen (Radio-Buttons für jede Frage)
- Anti-Duplikat: Einmal pro Umfrage pro User
- Validierung: Alle Fragen müssen beantwortet werden
- Submit-Bestätigung mit Redirect zu Ergebnissen

### 📊 Ergebnisanzeige (Coordinator & Respondent)
- Tabellarische Anzeige aller Ergebnisse
- Farbcodierung:
  - **Hellgrün:** Meistgewählte Option(en)
  - **Rot → Gelb → Grün:** Gradient zwischen wenigsten und meisten
  - **Farb-Opazität:** Die Hintergründe werden als dezente RGBA-Farben ausgegeben (leicht durchsichtig, alpha ≈ 0.22), damit die Hervorhebungen nicht zu markant sind
- Konsistente Tabellenbreiten (70% / 30%)
- Gesamtzahl der Antworten angezeigt

---

## 🗄 Datenbank-Schema

Die H2 In-Memory DB wird automatisch erstellt mit folgenden Tabellen:

```
app_user           → Benutzer mit Rolle (COORDINATOR/RESPONDENT)
survey             → Umfragen mit Titel und Status (open/closed)
question           → Fragen pro Umfrage
option             → Antwortmöglichkeiten pro Frage
response           → Benutzer-Antwort auf eine Umfrage
answer             → Einzelne Antwort pro Frage
```

**Datenbank-Browser (H2 Console):**
- URL: http://localhost:8081/h2-console
- JDBC URL: `jdbc:h2:./data/surveydb`
- User: `SA`
- Passwort: (leer)

DB-Reset / Daten löschen
Wenn du alle persistierten Daten (Users, Surveys, Responses etc.) löschen möchtest, kannst du die H2-Dateien im `data/`-Ordner entfernen oder die Tabellen über die H2-Konsole leeren. Beispiel (PowerShell):

```powershell
# Beispiel: relative Pfade aus dem Projektroot
Remove-Item ".\data\surveydb.mv.db" -Force -ErrorAction SilentlyContinue
Remove-Item ".\data\surveydb.trace.db" -Force -ErrorAction SilentlyContinue
```

Die Anwendung erzeugt beim nächsten Start automatisch eine frische DB-Datei.


---

## 🎨 Frontend-Struktur

### HTML Templates (Thymeleaf)
- `index.html` - Startseite mit Umfrage-Listen
- `login.html` - Login-Formular
- `register.html` - Registrierungs-Formular (mit Coordinator-Haken)
- `create_survey.html` - Umfrage-Erstellung (dynamische Fragen/Optionen)
- `take_survey.html` - Umfrage-Durchführung
- `results.html` - Tabellarische Ergebnisse mit Farbcodierung

### CSS
- `styles.css` - Handgeschriebenes CSS (keine Libraries)
  - Flexbox-Layouts
  - Responsive Tables mit `colgroup`
  - RGBA-Farbcodierung für Ergebnisse
  - Hover-States und Transitions

### JavaScript
- `create_survey.html` - Dynamische Frage/Option-Verwaltung (add/remove)
- keine externe Libs

---

## 🚀 Deployment

### JAR-Build
```bash
mvn clean package -DskipTests
java -jar target/survey-app-0.0.1-SNAPSHOT.jar
```

Hinweis: Es ist eine `.gitignore` im Projektstamm vorhanden, die `target/` und die H2-DB-Dateien (`data/surveydb.*`) ausschließt. Falls diese Artefakte bereits ins Git geratet sind, entferne sie mit `git rm --cached <path>` aus dem Index, ohne die lokalen Dateien zu löschen.

---

## 📂 Baumstruktur

```
Webec-Survey-App/
├── src/
│   ├── main/
│   │   ├── java/surveyapp/
│   │   │   ├── model/          (User, Survey, Question, Option, Response, Answer)
│   │   │   ├── repository/     (Repository-Interfaces)
│   │   │   ├── controller/     (Auth, Home, Coordinator, Respondent, Results)
│   │   │   └── SurveyAppApplication.java
│   │   ├── resources/
│   │   │   ├── application.properties
│   │   │   ├── static/
│   │   │   │   └── styles.css
│   │   │   └── templates/
│   │   │       ├── index.html
│   │   │       ├── login.html
│   │   │       ├── register.html
│   │   │       ├── create_survey.html
│   │   │       ├── take_survey.html
│   │   │       └── results.html
│   └── test/
│       └── java/surveyapp/
│           ├── repository/    (Unit-Tests)
│           ├── controller/    (Unit-Tests)
│           ├── integration/   (Integration-Tests)
│           ├── e2e/          (E2E-Tests + Page Objects)
├── pom.xml
├── data/                      (H2 Datenbankdatei, nach erste Start)
│   └── surveydb.mv.db
└── README.md
```

---



# Uputstvo za Konfiguraciju Produkcionog Okruženja

Ovaj dokument opisuje kako podesiti aplikaciju za produkciono okruženje koristeći Spring Profile i Docker Compose. Pristup osigurava da osetljivi podaci (kao što su lozinke za bazu) ostanu bezbedni i izvan koda.

### Korak 1: Kreiranje `application-prod.properties`

U direktorijumu `src/main/resources/` kreirajte novi fajl pod nazivom `application-prod.properties`. Ovaj fajl će sadržati konfiguraciju specifičnu za produkciju.

**Sadržaj za `src/main/resources/application-prod.properties`:**

```properties
# URL produkcione baze podataka se učitava iz environment varijable
spring.datasource.url=${DB_URL}

# Korisničko ime i lozinka se takođe učitavaju iz environment varijabli
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}

# U produkciji, Hibernate treba samo da validira šemu baze, ne da je menja.
spring.jpa.hibernate.ddl-auto=validate
```

### Korak 2: Ažuriranje `docker-compose.yml`

Potrebno je definisati podrazumevani profil i pripremiti `environment` sekciju za prihvatanje produkcionih vrednosti.

**Izmenite `app` servis u `docker-compose.yml`:**

```yaml
services:
  app:
    build: .
    ports:
      - "11056:11056"
    environment:
      # Postavljamo 'dev' kao podrazumevani profil.
      # Ova vrednost će biti pregažena na produkciji.
      - SPRING_PROFILES_ACTIVE=dev 
      
      # Placeholderi za produkcione vrednosti.
      # Ostaće prazni pri lokalnom pokretanju.
      - DB_URL=
      - DB_USER=
      - DB_PASSWORD=
    depends_on:
      db:
        condition: service_healthy
    networks:
      - bank-network
    volumes:
      - ./logs:/app/logs
```

### Korak 3: Kreiranje `.env` fajla (Samo na Produkcionom Serveru)

Ovo je najvažniji korak. Na mašini gde ćete pokretati produkcionu verziju aplikacije, kreirajte fajl pod nazivom `.env` u istom direktorijumu gde je i `docker-compose.yml`.

**Sadržaj za `.env` fajl (primer):**

```bash
# Aktiviramo produkcioni profil
SPRING_PROFILES_ACTIVE=prod

# Definišemo stvarne vrednosti za konekciju na produkcionu bazu
DB_URL=jdbc:sqlserver://hostname-vase-prod-baze:1433;databaseName=ime_prod_baze;...
DB_USER=korisnik_prod_baze
DB_PASSWORD=jaka-i-tajna-lozinka-123!
```

**VAŽNO:** Ovaj fajl **nikada** ne sme biti dodat u Git repozitorijum.

### Korak 4: Ažuriranje `.gitignore` fajla

Da biste osigurali da `.env` fajl nikada ne završi u Gitu, dodajte ga u `.gitignore`.

**Dodajte sledeći red u `.gitignore`:**

```
.env
```

### Kako Funkcioniše

*   **Lokalno:** Pokrenete `docker-compose up`. Docker ne nalazi `.env` fajl, `SPRING_PROFILES_ACTIVE` ostaje `dev`, i aplikacija koristi `application-dev.properties` za spajanje na lokalnu bazu.
*   **Na Produkciji:** Pre pokretanja, kreirate `.env` fajl. Kada pokrenete `docker-compose up`, Docker učitava vrednosti iz `.env` fajla, `SPRING_PROFILES_ACTIVE` postaje `prod`, i aplikacija koristi `application-prod.properties`, popunjavajući podatke za bazu iz environment varijabli.

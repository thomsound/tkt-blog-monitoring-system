# The Key Technology Coding Assessment

# Blog-Post Monitoring System

## Run

> Alle Befehle werden aus dem Verzeichnis `tkt-blog-monitoring-system` heraus ausgeführt.

### Lokal:
Voraussetzung ist ein lokaler Kafka-Broker, erreichbar unter `localhost:9092`.

Starten der einzelnen Services:

```./mvnw spring-boot:run -pl blog-fetcher```

```./mvnw spring-boot:run -pl blog-post-processor```

```./mvnw spring-boot:run -pl web-socket-server```

### Docker:

`docker compose up`

## ... and visit

### Client

Das (sehr basale) Frontend ist dann unter [localhost:8080/client.html](localhost:8080/client.html) zu erreichen.

## Tests

```./mvnw test```



## Aufgabe:

### Erstelle ein Full Stack System mit folgenden Anforderungen:

Das Backend:
- ruft zyklisch (alle paar Sekunden) die Blogbeiträge von der Seite thekey.academy ab (über die Wordpress API - https://developer.wordpress.org/rest-api/reference/posts/)
- verarbeitet die Blogbeiträge zu einer einfachen Word Count Map ({“und”: 5, “der”: 3, ...})
- die Map per WebSocket an das Frontend

Das Frontend:
- zeigt die Word Count Map der neuen Beiträge an und aktualisiert sich selbstständig neu bei neuen Daten

### Bonuspunkte:
- Eventgetriebene Verarbeitung
- Aktualisierung im Frontend nur bei tatsächlich neuen Blogbeiträgen - nicht immer komplett neu
- Microservice-Architektur

### Programmiersprachen:
- Backend in einer gängigen, modernen Programmiersprache (z.B. Scala, Java, C#)- Frameworks dürfen gerne genutzt werden
- Frontend kann extrem basic sein, es dient nur dazu die Kommunikation mit dem Backend abzubilden.
- Datenspeicherung gerne in-memory

### Was wollen wir sehen:
- hohe Codequalität
- Testabdeckung
- Production-ready code - so wie du auch eine Aufgabe hier in der Firma lösen würdest
- Abgabe bitte als github mit Anweisungen wie wir es testen können innerhalb von 1 Woche oder bis zum Wunschtermin.

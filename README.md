(This repository was made public at the request of The Key Technology)

# The Key Technology Coding Assessment

# Blog-Post Monitoring System

Meine Lösung besteht aus drei Services, die über Kafka miteinander Kommunizieren.

### blog-fetcher
Der blog-fetcher-Service sendet regelmäßige Poll-Requests an die Wordpress API und soll was auf dem Blog passiert in einen Eventstream überführen.
Über das Feld `modified_gmt` werden `Updates` und `Creates` getrackt und über `UpdateMessages` kommuniziert.
Zusätzlich wird regelmäßig überprüft, ob noch alle bekannten Posts vorhanden sind, um ggfs. eine `DeleteMessage` abzusetzen.
Auf diese Weise werden keine redundanten Events in das System gegeben.

Der Einfachheit halber beinhalten die `UpdateMessage`s bereits den Inhalt der Posts. In einem größeren System würden sie lediglich alle Kafka-Consumer über ein Update informieren, so dass diese dann jeweils den Teil der Daten laden, den sie für ihre Aufgabe benötigen.

### blog-post-processor
Dieser Service verarbeitet den Textinhalt der Posts zu einer Word-Count-Map. Dabei werden immer nur die Unterschiede neu berechnet, so dass der Total-Count (die Wortanzahl über alle Posts) nicht immer komplett neu berechnet werden muss.
Das Ergebnis wird dann wieder über Kafka an den `web-socket-server` kommuniziert.

### web-socket-server
Dieser Frontendserver ist von außen erreichbar. Er stellt einen simplen Browser-Client über Http zur Verfügung und verwaltet die Websocket-Verbindungen zu allen Clients, die darüber aufgebaut werden.

Hier wird der letzte State lediglich in einer Variable im Speicher gehalten, um jedem Client, der sich neu verbindet, die aktuellen Daten liefern zu können. Im Ernstfall könnte dieser Service sich die Daten natürlich erneut zusenden lassen um z.B. auch nach einem Neustart weiterhin konsistente Daten zu liefen.

## Run ...

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

Das (sehr basale) Frontend ist dann unter <b>[localhost:8080/client.html](localhost:8080/client.html)</b> zu erreichen.

## Tests

Tests existieren beispielhaft für den `blog-fetcher`-Service.

```./mvnw test```

## End-to-end Tests

Dieser Test startet das komplette System und einen Mockserver, der die third-party API simuliert, in Docker um dann via Selenium zu prüfen ob das gewünschte Ergebnis angezeigt wird.

```
# Alle module packen (falls noch nicht geschehen)
./mvnw package -DskipTests

# End-to-end Test starten
./mvnw test -P e2e
```

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

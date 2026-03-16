# BUSINESS RULES

## Overview

Questo documento definisce le Business Rules delle classi presenti nel package **domain**.

Tutte le regole vanno gestite a livello Domain o Application. Il livello Infrastructure NON deve contenere NESSUNA BUSINESS LOGIC.

---

## Package beach

### Classe Beach.java

Definisce un'istanza di una spiaggia. Nel DDD, viene considerata una **Aggregate Root**, poiché gestisce l'intero ciclo di vita di una spiaggia e le sue entità/value object associati.

Una spiaggia, appena creata, deve avere:

> - `id` (uguale a zero se deve essere salvato nel DB, poi messo uguale all'ID assegnato dal DB),
> - `ownerId` (obbligatorio, può essere modificato),
> - `addressId` (obbligatorio, non modificabile direttamente),
> - `beachGeneral` (obbligatorio, modificabile tramite i suoi metodi),
> - `beachInventory` (opzionale alla creazione, modificabile tramite i suoi metodi),
> - `beachServices` (opzionale alla creazione, modificabile tramite i suoi metodi),
> - `parking` (opzionale alla creazione, modificabile tramite i suoi metodi),
> - `seasons` (lista di stagioni; non possono essere eliminate),
> - `zones` (lista di zone; possono essere eliminate solo se non sono in uso in nessuna stagione),
> - `extraInfo` (informazioni testuali aggiuntive),
> - `active` (stato di attivazione della spiaggia).

BUSINESS LOGIC:

> - Una spiaggia può essere attivata (`active = true`) solo se sono stati definiti `beachInventory`, `beachServices`, `parking` e se esistono almeno una stagione (`Season`) e una zona (`Zone`).
> - Se una di queste condizioni viene a mancare dopo l'attivazione, la spiaggia viene disattivata automaticamente.

### Classe BeachGeneral.java

Contiene gli attributi generali di una spiaggia. Nel DDD, è considerata un **Value Object**.

Ogni oggetto creato contiene:

> - `name` (nome della spiaggia; non può essere vuoto o superare i 100 caratteri),
> - `description` (breve descrizione dello stabilimento),
> - `phoneNumber` (numero di telefono, con validazione del formato).

### Classe BeachInventory.java

Contiene l'inventario degli oggetti disponibili in una spiaggia. Nel DDD, è considerata un **Value Object**.

Ogni oggetto creato contiene:

> - `countOmbrelloni` (numero di ombrelloni; >= 0),
> - `countTende` (numero di tende; >= 0),
> - `countExtraSdraio` (numero di sdraio extra*; >= 0),
> - `countExtraLettini` (numero di lettini extra*; >= 0),
> - `countExtraSedie` (numero di sedie extra*; >= 0),
> - `countCamerini` (numero di camerini; >= 0).

\* Con "extra" si intendono gli oggetti non inclusi nel noleggio di ombrelloni/tende.

### Classe BeachServices.java

Definisce i servizi offerti da una spiaggia. Nel DDD, è considerata un **Value Object**.

Ogni oggetto creato contiene attributi booleani che indicano la disponibilità di:

> - `bathrooms` (WC),
> - `showers` (docce),
> - `pool` (piscina),
> - `bar` (bar),
> - `restaurant` (ristorante),
> - `wifi` (Wi-Fi),
> - `volleyballField` (campo da beach volley).

### Classe Parking.java

Descrive la struttura del parcheggio di una spiaggia. Nel DDD, è considerata un **Value Object**.

Ogni oggetto creato contiene:

> - `nAutoPark` (numero di posti auto; >= 0),
> - `nMotoPark` (numero di posti moto; >= 0),
> - `nBikePark` (numero di posti bici; >= 0),
> - `nElectricPark` (numero di posti per auto elettriche; >= 0),
> - `CCTV` (presenza di videosorveglianza).

### Classe Season.java

Definisce una stagione specifica per una spiaggia. Nel DDD, è considerata una **Entity**.

Ogni oggetto creato contiene:

> - `name` (nome della stagione),
> - `startDate` (data di inizio),
> - `endDate` (data di fine),
> - `pricing` (riferimento all'oggetto `Pricing` con le tariffe della stagione),
> - `zoneTariffs` (lista di `ZoneTariff` che definiscono i prezzi per ogni zona in quella stagione).

BUSINESS LOGIC:

> - `startDate` deve essere precedente a `endDate`.
> - Le stagioni non possono sovrapporsi temporalmente.
> - Ogni stagione deve avere almeno una `ZoneTariff` definita.

### Classe Pricing.java

Definisce il listino prezzi per gli extra di una stagione. Nel DDD, è considerata un **Value Object**.

Ogni oggetto creato contiene:

> - `id` (identificativo del listino),
> - `priceLettino` (prezzo di un lettino extra; >= 0),
> - `priceSdraio` (prezzo di una sdraio extra; >= 0),
> - `priceSedia` (prezzo di una sedia extra; >= 0),
> - `priceParking` (prezzo di un posto parcheggio, unico per tutti i tipi di veicolo; >= 0),
> - `priceCamerino` (prezzo di un camerino; >= 0).

### Classe ZoneTariff.java

Definisce le tariffe per gli spot (ombrelloni/tende) in una specifica zona per una stagione. Nel DDD, è considerata un **Value Object**.

Ogni oggetto creato contiene:

> - `zoneName` (riferimento al nome della `Zone`),
> - `priceOmbrellone` (prezzo di un ombrellone in quella zona; >= 0),
> - `priceTenda` (prezzo di una tenda in quella zona; >= 0).

---

## Package Booking

### Classe Booking.java

Definisce una prenotazione effettuata da un cliente per una specifica spiaggia e data. Nel DDD, viene considerata una **Aggregate Root**, poiché gestisce l'intero ciclo di vita di una prenotazione e le sue entità/value object associati.

Una prenotazione, appena creata, deve avere:

> - `id` (uguale a zero se deve essere salvato nel DB, poi messo uguale all'ID assegnato dal DB),
> - `beachId` (ID della spiaggia a cui si riferisce la prenotazione),
> - `customerId` (ID del cliente che ha effettuato la prenotazione),
> - `date` (data della prenotazione),
> - `spotIds` (lista degli ID degli spot prenotati; non può essere vuota),
> - `extraSdraio` (numero di sdraio extra prenotate; >= 0),
> - `extraLettini` (numero di lettini extra prenotati; >= 0),
> - `extraSedie` (numero di sedie extra prenotate; >= 0),
> - `camerini` (numero di camerini prenotati; >= 0),
> - `parking` (oggetto `Parking` che definisce i posti auto prenotati per tipo; non può essere null),
> - `status` (stato iniziale della prenotazione, di default `PENDING`).

BUSINESS LOGIC:

> - `beachId` e `customerId` devono essere validi (maggiori di 0).
> - `date` non può essere null.
> - `spotIds` non può essere null o vuota, e ogni `spotId` deve essere valido (maggiore di 0).
> - Tutte le quantità extra e i posti auto devono essere >= 0.
> - Lo stato di una prenotazione può essere modificato solo se rispetta determinate condizioni:
>   - `confirmBooking()`: solo se lo stato è `PENDING`.
>   - `rejectBooking()`: solo se lo stato è `PENDING`.
>   - `cancelBooking()`: solo se lo stato è `PENDING` o `CONFIRMED`.
> - L'aggiunta di quantità extra o posti auto è consentita solo se lo stato è `PENDING` o `CONFIRMED`. Se lo stato era `CONFIRMED`, torna a `PENDING` dopo l'aggiunta.
> - La quantità aggiunta deve essere maggiore di 0 e non superare la disponibilità.

### Enum BookingStatus.java

Definisce i possibili stati di una prenotazione. Nel DDD, è considerata un **Value Object**.

Gli stati possibili sono:

> - `PENDING` (in attesa di conferma),
> - `CONFIRMED` (confermata),
> - `REJECTED` (rifiutata),
> - `CANCELLED` (annullata).

### Classe BookingParking.java

Definisce i posti parcheggio prenotati. Nel DDD, è considerata un **Value Object** e rappresenta i dettagli del parcheggio all'interno di una prenotazione.

Ogni oggetto creato contiene:

> - `bookingId` (ID della prenotazione a cui si riferisce il parcheggio),
> - `parkingType` (tipo di parcheggio prenotato: Auto, Moto, Bici, Elettrica),
> - `numberOfSpots` (numero di posti auto prenotati per quel tipo; >= 0).

BUSINESS LOGIC:

> - `bookingId` deve essere valido (maggiore di 0).
> - `numberOfSpots` deve essere >= 0.

### Classe PriceCalculator.java

È un **Domain Service** che calcola il prezzo totale di una prenotazione. Essendo un servizio, non ha stato e contiene solo logica.

Il metodo `calculateTotal` richiede:

> - `booking` (l'oggetto `Booking` per cui calcolare il prezzo).
> - `beach` (l'oggetto `Beach` completo, che contiene le informazioni su stagioni, zone e tariffe).

BUSINESS LOGIC:

> 1. **Trova la stagione attiva**: identifica la stagione corretta in base alla data della prenotazione. Se non esiste una stagione attiva per quella data, lancia un'eccezione.
> 2. **Calcola il costo degli extra**: somma i costi di sdraio, lettini, sedie e camerini extra, usando il listino prezzi (`Pricing`) della stagione attiva.
> 3. **Calcola il costo del parcheggio**: somma il numero totale di veicoli prenotati e li moltiplica per il prezzo fisso del parcheggio (`priceParking`) definito nel listino.
> 4. **Calcola il costo degli spot**:
>    - Per ogni `spotId` nella prenotazione, identifica la `Zone` e lo `SpotType` corrispondenti.
>    - Se uno `spotId` non esiste, lancia un'eccezione.
>    - Trova la tariffa di zona (`ZoneTariff`) per quella `Zone` nella stagione attiva.
>    - Aggiunge al totale il prezzo corretto in base al tipo di spot.
> 5. **Restituisce il totale**: ritorna il prezzo finale calcolato.

---

## Package common

### Classe Address.java

Definisce un indirizzo geografico. Nel DDD, è considerata un **Value Object**.

Ogni oggetto creato contiene:

> - `id` (identificativo dell'indirizzo; 0 se non ancora salvato nel DB),
> - `street` (nome della via),
> - `streetNumber` (numero civico),
> - `city` (città),
> - `zipCode` (codice postale),
> - `country` (nazione).

BUSINESS LOGIC:

> - Tutti i campi stringa (`street`, `streetNumber`, `city`, `zipCode`, `country`) non possono essere nulli o vuoti.
> - Le lunghezze massime per i campi sono:
>   - `street`: 255 caratteri
>   - `streetNumber`: 10 caratteri
>   - `city`: 100 caratteri
>   - `zipCode`: 20 caratteri
>   - `country`: 100 caratteri

### Enum ObjectStatus.java

Definisce i possibili stati di un oggetto generico (es. parcheggi, spot). Nel DDD, è considerata un **Value Object**.

Gli stati possibili sono:

> - `PENDING` (in attesa),
> - `OCCUPIED` (occupato),
> - `FREE` (libero).

### Interfaccia TransactionContext.java

È un'interfaccia "marker" o "token" utilizzata per rappresentare il contesto di una transazione. Nell’Hexagonal Architecture, facilita l'isolamento del dominio dall'infrastruttura, permettendo ai servizi di dominio di richiedere un contesto transazionale senza dipendere direttamente da implementazioni specifiche (es. JDBC `Connection`).

BUSINESS LOGIC:

> - Non contiene metodi o attributi. La sua presenza come parametro indica che l'operazione deve essere eseguita all'interno di una transazione.

---

## Package layout

### Classe Spot.java

Definisce un singolo spot (ombrellone o tenda) all'interno di una zona della spiaggia. Nel DDD, è considerata una **Entity**.

Ogni oggetto creato contiene:

> - `id` (identificativo dello spot; `null` se nuovo e non ancora salvato nel DB),
> - `type` (tipo di spot: `UMBRELLA` o `TENT`),
> - `row` (riga in cui si trova lo spot; >= 0),
> - `column` (colonna in cui si trova lo spot; >= 0).

BUSINESS LOGIC:

> - `type` non può essere `null`.
> - `row` e `column` non possono essere valori negativi.

### Enum SpotType.java

Definisce i possibili tipi di spot disponibili in una spiaggia. Nel DDD, è considerata un **Value Object**.

I tipi possibili sono:

> - `UMBRELLA` (ombrellone),
> - `TENT` (tenda).

### Classe Zone.java

Definisce una zona specifica all'interno della spiaggia, che raggruppa un insieme di spot. Nel DDD, è considerata una **Entity**.

Ogni oggetto creato contiene:

> - `name` (nome della zona),
> - `spots` (lista degli `Spot` che appartengono a questa zona).

BUSINESS LOGIC:

> - `name` non può essere nullo o vuoto e non può superare i 50 caratteri.
> - La lista `spots` viene copiata per garantire immutabilità esterna.

---

## Package moderation

### Classe Ban.java

Rappresenta un'azione di ban emessa da un amministratore. Nel DDD, è considerata una **Entity**.

Ogni oggetto creato contiene:

> - `id` (identificativo del ban),
> - `bannedId` (ID dell'utente o della spiaggia bannata),
> - `banType` (tipo di ban: `BEACH` o `APPLICATION`),
> - `bannedFromBeachId` (ID della spiaggia da cui l'utente è stato bannato, se `banType` è `BEACH`),
> - `adminId` (ID dell'amministratore che ha emesso il ban),
> - `reason` (motivazione del ban),
> - `createdAt` (data e ora di creazione del ban).

BUSINESS LOGIC:

> - `bannedId` e `adminId` devono essere validi.
> - `reason` non può essere vuota.
> - `createdAt` non può essere `null`.
> - Se `banType` è `BEACH`, `bannedFromBeachId` deve essere specificato.
> - Se `banType` è `APPLICATION`, `bannedFromBeachId` deve essere `null`.

### Enum BanType.java

Definisce il tipo di ban. Nel DDD, è considerata un **Value Object**.

I tipi possibili sono:

> - `BEACH` (ban da una specifica spiaggia),
> - `APPLICATION` (ban dall'intera applicazione).

### Classe Report.java

Rappresenta una segnalazione (report) inviata da un utente. Nel DDD, è considerata una **Aggregate Root**.

Ogni oggetto creato contiene:

> - `id` (identificativo del report),
> - `reporterId` (ID dell'utente che ha inviato la segnalazione),
> - `reportedId` (ID dell'utente o della spiaggia segnalata),
> - `reportedType` (tipo di entità segnalata: `USER` o `BEACH`),
> - `description` (descrizione della segnalazione),
> - `createdAt` (data e ora di creazione),
> - `status` (stato del report, di default `PENDING`),
> - `bookingId` (ID della prenotazione a cui si riferisce la segnalazione, se applicabile).

BUSINESS LOGIC:

> - `reporterId` e `reportedId` non possono essere uguali.
> - `description` non può essere vuota e ha una lunghezza massima di 1024 caratteri.
> - `createdAt` non può essere una data futura.
> - Lo stato di un report può essere modificato (`approve()` o `reject()`) solo se è `PENDING`.

### Enum ReportStatus.java

Definisce i possibili stati di un report. Nel DDD, è considerata un **Value Object**.

Gli stati possibili sono:

> - `PENDING` (in attesa di revisione),
> - `APPROVED` (approvato),
> - `REJECTED` (rifiutato).

### Enum ReportTargetType.java

Definisce il tipo di entità che può essere segnalata. Nel DDD, è considerata un **Value Object**.

I tipi possibili sono:

> - `USER` (un utente),
> - `BEACH` (una spiaggia).

---

## Package review

### Classe Review.java

Rappresenta una recensione lasciata da un cliente per una specifica spiaggia. Nel DDD, è considerata una **Entity**.

Ogni oggetto creato contiene:

> - `id` (identificativo della recensione),
> - `beachId` (ID della spiaggia recensita),
> - `customerId` (ID del cliente che ha lasciato la recensione),
> - `rating` (punteggio da 1 a 5),
> - `comment` (commento testuale),
> - `createdAt` (data e ora di creazione).

BUSINESS LOGIC:

> - `beachId` e `customerId` devono essere validi.
> - `rating` deve essere un valore compreso tra 1 e 5.
> - `comment` non può essere vuoto e ha una lunghezza massima di 1024 caratteri.
> - `createdAt` non può essere `null`.

---

## Package user

### Classe User.java

È una classe astratta che rappresenta un utente generico del sistema. Nel DDD, è considerata una **Entity**.

Ogni utente contiene:

> - `id` (identificativo dell'utente),
> - `email` (indirizzo email dell'utente),
> - `username` (nome utente),
> - `name` (nome anagrafico),
> - `surname` (cognome anagrafico).

BUSINESS LOGIC:

> - `email` non può essere nulla, vuota, deve avere una lunghezza tra 6 e 80 caratteri e contenere il carattere '@'.
> - `username` non può essere nullo, vuoto e non può superare i 50 caratteri.
> - `name` non può essere nullo, vuoto e non può superare i 100 caratteri.
> - `surname` non può essere nullo, vuoto e non può superare i 50 caratteri.
> - Contiene un metodo astratto `getRole()` che restituisce il ruolo specifico dell'utente.
> - Contiene un metodo astratto `isOTP()` che indica se l'utente deve cambiare la password al prossimo login.

### Classe Admin.java

Rappresenta un utente con privilegi di amministratore. Estende `User`. Nel DDD, è considerata una **Entity**.

Ogni amministratore contiene, oltre agli attributi di `User`:

> - `OTP` (boolean che indica se l'amministratore deve cambiare la password al prossimo login).

BUSINESS LOGIC:

> - Il ruolo restituito da `getRole()` è `ADMIN`.
> - `isOTP()` restituisce il valore dell'attributo `OTP`.

### Classe Customer.java

Rappresenta un cliente del sistema. Estende `User`. Nel DDD, è considerata una **Entity**.

Ogni cliente contiene, oltre agli attributi di `User`:

> - `phoneNumber` (numero di telefono del cliente),
> - `addressId` (ID dell'indirizzo associato al cliente),
> - `active` (boolean che indica se l'account del cliente è attivo).

BUSINESS LOGIC:

> - Il ruolo restituito da `getRole()` è `CUSTOMER`.
> - `isOTP()` restituisce sempre `false` per un cliente.
> - `phoneNumber` non può essere nullo, vuoto, non può superare i 50 caratteri e deve seguire un formato specifico (es. `+<numeri>`).
> - `addressId` deve essere valido (maggiore di 0).
> - Un cliente può chiudere il proprio account impostando `active` a `false`.

### Classe Owner.java

Rappresenta il proprietario di una spiaggia. Estende `User`. Nel DDD, è considerata una **Entity**.

Ogni proprietario contiene, oltre agli attributi di `User`:

> - `active` (boolean che indica se l'account del proprietario è attivo),
> - `OTP` (boolean che indica se il proprietario deve cambiare la password al prossimo login).

BUSINESS LOGIC:

> - Il ruolo restituito da `getRole()` è `OWNER`.
> - `isOTP()` restituisce il valore dell'attributo `OTP`.
> - Un proprietario può chiudere il proprio account impostando `active` a `false`.

### Enum Role.java

Definisce i possibili ruoli che un utente può avere nel sistema. Nel DDD, è considerata un **Value Object**.

I ruoli possibili sono:

> - `CUSTOMER` (cliente),
> - `OWNER` (proprietario di spiaggia),
> - `ADMIN` (amministratore del sistema).

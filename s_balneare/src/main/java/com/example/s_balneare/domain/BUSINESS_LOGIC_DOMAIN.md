# BUSINESS RULES

## Overview

Questo documento definisce le Business Rules delle classi presenti nel package **domain**

Tutte le regole vanno gestite a livello Domain o Application. Il livello Infrastructure NON deve contenere NESSUNA BUSINESS LOGIC.

## Package beach

### Classe Beach.java

Definisce un'istanza di una spiaggia. Nel DDD, viene considerata una Aggregate Root visto che contiene tutti i dettagli che definisce una spiaggia.

Una spiaggia, appena creata, deve avere:

> - id (uguale a zero, se deve essere salvato nel DB, poi messo uguale all'ID assegnato dal DB),
> - ownerId (obbligatorio averlo a creazione, può essere modificato),
> - addressId (obbligatorio avere PRIMA un oggetto Address prima di creare Beach, non modificabile se non tramite metodi
>   messi a disposizione da Address),
> - beachGeneral (obbligatorio averlo, creare prima un oggetto BeachGeneral prima di creare Beach, modificabile tramite
>   metodi BeachGeneral),
> - beachInventory (non obbligatorio alla creazione, aggiunta tramite metodo updateInventory(), modificabile solo
>   tramite metodi di BeachInventory),
> - beachServices (non obbligatorio alla creazione, aggiunta tramite metodo updateServices(), modificabile solo
>   tramite metodi di BeachServices),
> - parking (non obbligatorio alla creazione, aggiunta tramite metodo updateParking(), modificabile solo tramite metodi
>   di Parking),
> - seasons (se inserito null, viene sostituita da una lista vuota; le Season possono essere aggiunte e modificate, ma
>   NON ELIMINATE),
> - zones (se inserito null, viene sostituita da una lista vuota; le zones possono essere aggiunte, modificate ed
>   eliminate SOLO SE NON APPARTENGONO A NESSUNA SEASON),
> - extraInfo (informazioni aggiuntive che non possono essere espresse tramite gli attributi presenti),
> - active (inizialmente disattivata: una spiaggia può essere attivata SE E SOLO SE ha beachInventory, beachServices e
>   parking implementate e abbia almeno una stagione e una zona; se negli update successivi viene eliminato qualcosa, la
>   spiaggia viene automaticamente disattivata).

### Classe BeachGeneral.java

Contiene attributi generali di una determinata spiaggia. Nel DDD, viene considerata una Value Object.

Ogni oggetto creato contiene:

> - name (nome della spiaggia),
> - description (breve descrizione sullo stabilimento),
> - phoneNumber (numero di telefono dello stabilimento: con controllo base su esistenza prefisso).

### Classe BeachInventory.java

Contiene l'inventario di una spiaggia. Nel DDD, viene considerata una Value Object.

Ogni oggetto creato contiene:

> - countOmbrelloni (numero di ombrelloni posseduti dalla spiaggia di riferimento),
> - countTende (numero di tende possedute dalla spiaggia di riferimento),
> - countExtraSdraio (numero di sdraio extra* possedute dalla spiaggia di riferimento),
> - countExtraLettini (numero di lettini extra* posseduti dalla spiaggia di riferimento),
> - countExtraSedie (numero di sedie extra* possedute dalla spiaggia di riferimento),
> - countCamerini (numero di camerini posseduti dalla spiaggia di riferimento).

\* con extra, mi riferisco ad oggetti che non sono inclusi negli ombrelloni/tende.

TUTTI I VALORI DEVONO ESSERE >= 0.

### Classe BeachServices.java

Definisce i servizi offerti dalla spiaggia. Nel DDD, viene considerata una Value Object.

Ogni oggetto creato contiene:

> - bathrooms (disponibilità WC),
> - showers (disponibilità docce con acqua calda/fredda, anche a pagamento),
> - pool (disponibilità piscina),
> - bar (presenza bar),
> - restaurant (presenza ristorante),
> - wifi (disponibilità Wi-Fi),
> - volleyballField (disponibilità campo/i da Beach Volley).

### Classe Parking.java

Mostra la struttura di un parcheggio privato messo a disposizione da una spiaggia. Se essa non esiste, allora tutti i valori degli attributi sono impostati a zero. Nel DDD, viene considerata una Value Object.

Ogni oggetto creato contiene:

> - nAutoPark (numero di posti auto; >= 0),
> - nMotoPark (numero di posti moto; >= 0),
> - nBikePark (numero di posti per le bici; >= 0),
> - nElectricPark (numero di posti riservati alle auto elettriche; >= 0),
> - CCTV (presenza di videosorveglianza o meno).

### Classe Season.java

Definisce una determinata stagione di una spiaggia. Nel DDD, viene considerata una Entity.

Ogni oggetto creato contiene:

> - name (nome della stagione),
> - startDate (data di inizio della stagione),
> - endDate (data di fine stagione),
> - pricing (riferimento ad un oggetto Pricing, che definisce i prezzi per la stagione) [vedi **Pricing.java**],
> - zoneTariffs (lista di prezzi per ciascuna zona di spiaggia messa a disposizione in quella stagione) [vedi **ZoneTariff.java**].

BUSINESS LOGIC:

> - startDate < endDate,
> - Non posso avere 2 stagioni che si sovrappongono di periodo,
> - Ogni stagione ha almeno una zona, quindi zoneTariff NON PUÒ essere una lista vuota.

### Classe Pricing.java

Definisce i prezzi di una determinata stagione. Nel DDD, viene considerata una Value Object.

Ogni oggetto creato contiene:

> - id (identificativo listino prezzi: null se oggetto nuovo e non salvato nel DB),
> - priceLettino (prezzo di un lettino extra*),
> - priceSdraio (prezzo di una sdraio extra*),
> - priceSedia (prezzo di una sedia extra*),
> - priceParking (prezzo di un posto di parcheggio: indipendentemente dal tipo di parcheggio, esso ha un prezzo univoco),
> - priceCamerino (prezzo di un camerino).

\* vedi **BeachInventory.java** per una definizione sugli extra.

TUTTI I PREZZI SONO OBBLIGATORI DA METTERE E TUTTI DEVONO AVERE UN VALORE >= 0.

### Classe ZoneTariff.java

Definisce i prezzi per i vari Spot di una determinata zona per una determinata stagione. Nel DDD, viene considerata una Value Object.

Ogni zona quindi ha una ZoneTariff per ogni stagione che ne fa parte.

Ogni oggetto creato contiene:

> - zoneName (riferimento a Zone.java) [vedi **layout/Zone.java**],
> - priceOmbrellone (prezzo di un ombrellone),
> - priceTenda (prezzo di una tenda).

TUTTI I PREZZI SONO OBBLIGATORI DA METTERE E TUTTI DEVONO AVERE UN VALORE >=0.

---

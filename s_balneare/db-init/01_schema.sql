
-- TBD: gestione delete e cascade methods



DROP DATABASE IF EXISTS stabilimenti;
CREATE DATABASE stabilimenti;
USE stabilimenti;

-- INDIRIZZI UTENTI E SPIAGGIA

CREATE TABLE addresses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    street VARCHAR(255) NOT NULL,
    streetNumber VARCHAR(20) NOT NULL,
    city VARCHAR(100) NOT NULL,
    zipCode VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL
);

-- SPIAGGIA E SERVIZI

CREATE TABLE beaches (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    telephoneNumber varchar(50) UNIQUE NOT NULL,
    addressId INT UNIQUE NOT NULL,
    extraInfo VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (addressId) REFERENCES addresses(id)
);

CREATE TABLE parkings (
    beachId INT PRIMARY KEY,
    nAutoPark INT NOT NULL,
    nMotoPark INT NOT NULL,
    nBikePark INT NOT NULL,
    nElectricPark INT NOT NULL,
    CCTV BOOLEAN NOT NULL,
    FOREIGN KEY (beachId) REFERENCES beaches(id)
);

CREATE TABLE beach_services (
    beachId INT PRIMARY KEY,
    bathrooms BOOLEAN NOT NULL,
    showers BOOLEAN NOT NULL,
    pool BOOLEAN NOT NULL,
    bar BOOLEAN NOT NULL,
    restaurant BOOLEAN NOT NULL,
    wifi BOOLEAN NOT NULL,
    volleyballField BOOLEAN NOT NULL,
    FOREIGN KEY (beachId) REFERENCES beaches(id)
);

CREATE TABLE beach_inventories (
    beachId INT PRIMARY KEY,
    countOmbrelloni INT NOT NULL,
    countTende INT NOT NULL,
    countExtraSdraio INT NOT NULL,
    countExtraLettini INT NOT NULL,
    countExtraSedie INT NOT NULL,
    countCamerini INT NOT NULL,
    FOREIGN KEY (beachId) REFERENCES beaches(id)
);

-- PREZZI, STAGIONI E ZONE

CREATE TABLE pricings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    priceLettino INT NOT NULL,
    priceSdraio INT NOT NULL,
    priceSedia INT NOT NULL,
    priceParking INT NOT NULL,
    priceCamerino INT NOT NULL
);

CREATE TABLE seasons (
    id INT PRIMARY KEY AUTO_INCREMENT,
    startDate DATE NOT NULL,
    endDate DATE NOT NULL,
    name VARCHAR(50) NOT NULL,
    beachId  INT NOT NULL,
    pricingsId INT UNIQUE NOT NULL,
    PRIMARY KEY (beachId,id),
    FOREIGN KEY (beachId) REFERENCES beaches(id),
    FOREIGN KEY (pricingsId) REFERENCES pricings(id)
);

CREATE TABLE zones (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name varchar(50) NOT NULL,
    beachId INT NOT NULL,
    PRIMARY KEY (id, beachId),
    FOREIGN KEY (beachId) REFERENCES beaches(id)
);

CREATE TABLE zone_tariffs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    seasonId INT NOT NULL,
    zoneId INT NOT NULL,
    priceOmbrellone INT NOT NULL,
    priceTenda INT NOT NULL,
    FOREIGN KEY (seasonId) REFERENCES seasons(id),
    FOREIGN KEY (zoneId) REFERENCES zones(id)
);

CREATE TABLE spots (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type ENUM('UMBRELLA', 'TENT') NOT NULL,
    `row` INT NOT NULL,
    `column` INT NOT NULL,
    zoneId INT NOT NULL,
    FOREIGN KEY (zoneId) REFERENCES zones(id)
);

-- UTENTI

CREATE TABLE app_users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name varchar(50) NOT NULL,
    surname varchar(50) NOT NULL,
    username varchar(50) UNIQUE NOT NULL,
    email varchar(50) UNIQUE NOT NULL,
    hashPassword varchar(255) NOT NULL,
    active BOOLEAN NOT NULL
);

CREATE TABLE customers (
    id INT PRIMARY KEY,
    telephoneNumber varchar(50) UNIQUE NOT NULL,
    addressId INT NOT NULL,
    FOREIGN KEY (addressId) REFERENCES addresses(id),
    FOREIGN KEY (id) REFERENCES app_users(id)
);

CREATE TABLE owners (
    id INT PRIMARY KEY,
    beachId INT UNIQUE NOT NULL,
    FOREIGN KEY (beachId) REFERENCES beaches(id),
    FOREIGN KEY (id) REFERENCES app_users(id)
);

CREATE TABLE admins (
   id INT PRIMARY KEY AUTO_INCREMENT,
   FOREIGN KEY (id) REFERENCES app_users(id)
);

-- GESTIONE APP

CREATE TABLE bookings (
    beachId INT NOT NULL,
    customerId INT NOT NULL,
    date DATE NOT NULL,
    extraSdraio INT NOT NULL,
    extraLettini INT NOT NULL,
    extraSedie INT NOT NULL,
    camerini INT NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'REJECTED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    FOREIGN KEY (beachId) REFERENCES beaches(id),
    FOREIGN KEY (customerId) REFERENCES customers(id),
    PRIMARY KEY (beachId,customerId,date)
);

CREATE TABLE booking_spots (
    beachId INT NOT NULL,
    customerId INT NOT NULL,
    date DATE NOT NULL,
    spot INT NOT NULL,
    PRIMARY KEY (beachId,customerId,date,spot),
    FOREIGN KEY (beachId,customerId,date) REFERENCES bookings(beachId,customerId,date),
    FOREIGN KEY (spot) REFERENCES spots(id)
);

CREATE TABLE bans (
    id INT PRIMARY KEY AUTO_INCREMENT,
    bannedId INT NOT NULL,
    banType ENUM('BEACH', 'APPLICATION'),
    bannedFromBeachId INT,
    adminId INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    time DATETIME NOT NULL,
    FOREIGN KEY (bannedId) REFERENCES customers(id),
    FOREIGN KEY (bannedFromBeachId) REFERENCES beaches(id),
    FOREIGN KEY (adminId) REFERENCES admins(id)
);

CREATE TABLE reviews (
    id INT PRIMARY KEY AUTO_INCREMENT,
    beachId INT NOT NULL,
    customerId INT NOT NULL,
    rating TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment VARCHAR(500) NOT NULL,
    createdAt DATETIME NOT NULL,
    FOREIGN KEY (beachId) REFERENCES beaches(id),
    FOREIGN KEY (customerId) REFERENCES customers(id)
);

CREATE TABLE reports (
    id INT NOT NULL PRIMARY KEY,
    reporterId INT NOT NULL,
    reportedId INT NOT NULL,
    reportedType ENUM('USER', 'BEACH'),
    description VARCHAR(500) NOT NULL,
    createdAt DATETIME NOT NULL,
    status ENUM('PENDING', 'RESOLVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    FOREIGN KEY (reportedId) REFERENCES app_users(id),
    FOREIGN KEY (reporterId) REFERENCES app_users(id)
);

-- Controlli

-- Ti aggiungo un controllo necessario per la tabella bans, poi decidi tu se tenerla o modificarla
ALTER TABLE bans
ADD CONSTRAINT chk_bans_beach_id_matches_type CHECK (
    (banType = 'BEACH' AND bannedFromBeachId IS NOT NULL)
        OR
    (banType = 'APPLICATION' AND bannedFromBeachId IS NULL)
);

ALTER TABLE bans -- Impedisce inserimento di ban identici
    ADD CONSTRAINT uq_ban_single_active UNIQUE (bannedId, bannedFromBeachId, banType);

ALTER TABLE zones -- Per evitare ad una spiaggia di creare zoneId uguali
    ADD CONSTRAINT uq_zone_name_per_beach UNIQUE (beachId, name);

ALTER TABLE zone_tariffs -- Per evitare la creazione di stesse zoneId nelle stesse stagioni con prezzi diversi
    ADD CONSTRAINT uq_pricing_per_season_zone UNIQUE (seasonId, zoneId);

ALTER TABLE reviews -- Per evitare che un utente possa lasciare più review ad una spiaggia (come su Google)
    ADD CONSTRAINT uq_review_per_customer UNIQUE (beachId, customerId);

ALTER TABLE spots -- Per permettere l'unicità dei posti nella zona
    ADD CONSTRAINT uq_spot_position UNIQUE (zoneId, `row`, `column`);

ALTER TABLE seasons -- Per impedire la creazione di stagioni duplicate
    ADD CONSTRAINT uq_season_dates_beach UNIQUE (beachId, startDate, endDate);

ALTER TABLE reports -- Uguale a reviews
    ADD CONSTRAINT uq_report_unique UNIQUE (reporterId, reportedId);

ALTER TABLE addresses -- Riduce dimensione tabella, per utenti diversi con stesso indirizzo (es. più utenti del solito palazzo)
    ADD CONSTRAINT uq_full_address UNIQUE (street, streetNumber, city, zipCode, country);

-- CHECK >= 0
ALTER TABLE parkings
    ADD CONSTRAINT chk_nAutoPark_nonneg CHECK (nAutoPark >= 0),
    ADD CONSTRAINT chk_nMotoPark_nonneg CHECK (nMotoPark >= 0),
    ADD CONSTRAINT chk_nBikePark_nonneg CHECK (nBikePark >= 0),
    ADD CONSTRAINT chk_nElectricPark_nonneg CHECK (nElectricPark >= 0);

ALTER TABLE beach_inventories
    ADD CONSTRAINT chk_ombrelloni_nonneg CHECK (countOmbrelloni >= 0),
    ADD CONSTRAINT chk_tende_nonneg CHECK (countTende >= 0),
    ADD CONSTRAINT chk_extra_sdraio_nonneg CHECK (countExtraSdraio >= 0),
    ADD CONSTRAINT chk_extra_lettini_nonneg CHECK (countExtraLettini >= 0),
    ADD CONSTRAINT chk_extra_sedie_nonneg CHECK (countExtraSedie >= 0),
    ADD CONSTRAINT chk_camerini_nonneg CHECK (countCamerini >= 0);

ALTER TABLE pricings
    ADD CONSTRAINT chk_price_lettino_nonneg CHECK (priceLettino >= 0),
    ADD CONSTRAINT chk_price_sdraio_nonneg CHECK (priceSdraio >= 0),
    ADD CONSTRAINT chk_price_sedia_nonneg CHECK (priceSedia >= 0),
    ADD CONSTRAINT chk_price_parking_nonneg CHECK (priceParking >= 0),
    ADD CONSTRAINT chk_price_camerino_nonneg CHECK (priceCamerino >= 0);

ALTER TABLE zone_tariffs
    ADD CONSTRAINT chk_price_ombrellone_nonneg CHECK (priceOmbrellone >= 0),
    ADD CONSTRAINT chk_price_tenda_nonneg CHECK (priceTenda >= 0);

ALTER TABLE bookings
    ADD CONSTRAINT chk_extra_sdraio_booking_nonneg CHECK (extraSdraio >= 0),
    ADD CONSTRAINT chk_extra_lettini_booking_nonneg CHECK (extraLettini >= 0),
    ADD CONSTRAINT chk_extra_sedie_booking_nonneg CHECK (extraSedie >= 0),
    ADD CONSTRAINT chk_camerini_booking_nonneg CHECK (camerini >= 0);

ALTER TABLE spots
    ADD CONSTRAINT chk_spot_row_col_positive CHECK (`row` >= 0 AND `column` >= 0);

ALTER TABLE seasons -- Integrità date stagioni
    ADD CONSTRAINT chk_season_dates_valid CHECK (startDate < endDate);

ALTER TABLE reports -- Impedisce di fare report su se stessi
    ADD CONSTRAINT chk_reporter_not_reported CHECK (reporterId <> reportedId);
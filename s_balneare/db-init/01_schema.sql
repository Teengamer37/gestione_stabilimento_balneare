DROP DATABASE IF EXISTS stabilimenti;
CREATE DATABASE stabilimenti;
USE stabilimenti;

-- INDIRIZZI UTENTI E SPIAGGIA
CREATE TABLE addresses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    street VARCHAR(255) NOT NULL,
    streetNumber VARCHAR(10) NOT NULL,
    city VARCHAR(100) NOT NULL,
    zipCode VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL
);

-- UTENTI
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(80) UNIQUE NOT NULL,
    hashPassword VARCHAR(255) NOT NULL
);

CREATE TABLE customers (
    id INT PRIMARY KEY,
    phoneNumber VARCHAR(50) UNIQUE NOT NULL,
    addressId INT NOT NULL,
    active BOOLEAN NOT NULL,
    FOREIGN KEY (addressId) REFERENCES addresses(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE owners (
    id INT PRIMARY KEY,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    OTP BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE admins (
    id INT PRIMARY KEY AUTO_INCREMENT,
    OTP BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- SPIAGGIA E SERVIZI
CREATE TABLE beaches (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(512) NOT NULL,
    phoneNumber varchar(50) UNIQUE NOT NULL,
    addressId INT UNIQUE NOT NULL,
    extraInfo VARCHAR(512),
    active BOOLEAN NOT NULL DEFAULT FALSE,
    closed BOOLEAN NOT NULL DEFAULT FALSE,
    ownerId INT UNIQUE NOT NULL,
    FOREIGN KEY (ownerId) REFERENCES owners(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (addressId) REFERENCES addresses(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE parkings (
    beachId INT PRIMARY KEY,
    nAutoPark INT NOT NULL,
    nMotoPark INT NOT NULL,
    nElectricPark INT NOT NULL,
    CCTV BOOLEAN NOT NULL,
    FOREIGN KEY (beachId) REFERENCES beaches(id) ON DELETE CASCADE ON UPDATE CASCADE
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
    FOREIGN KEY (beachId) REFERENCES beaches(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE beach_inventories (
    beachId INT PRIMARY KEY,
    countExtraSdraio INT NOT NULL,
    countExtraLettini INT NOT NULL,
    countExtraSedie INT NOT NULL,
    countCamerini INT NOT NULL,
    FOREIGN KEY (beachId) REFERENCES beaches(id) ON DELETE CASCADE ON UPDATE CASCADE
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
    startDate DATE NOT NULL,
    endDate DATE NOT NULL,
    name VARCHAR(50) NOT NULL,
    beachId  INT NOT NULL,
    pricingsId INT UNIQUE NOT NULL,
    PRIMARY KEY (beachId, name),
    FOREIGN KEY (beachId) REFERENCES beaches(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (pricingsId) REFERENCES pricings(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE zones (
    name VARCHAR(50) NOT NULL,
    beachId INT NOT NULL,
    PRIMARY KEY (name, beachId),
    FOREIGN KEY (beachId) REFERENCES beaches(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE zone_tariffs (
    seasonName VARCHAR(50) NOT NULL,
    beachId INT NOT NULL,
    zoneName VARCHAR(50) NOT NULL,
    priceOmbrellone INT NOT NULL,
    priceTenda INT NOT NULL,
    PRIMARY KEY (seasonName, beachId, zoneName),
    FOREIGN KEY (beachId, seasonName) REFERENCES seasons(beachId, name) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (zoneName, beachId) REFERENCES zones(name, beachId) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE spots (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type ENUM('UMBRELLA', 'TENT') NOT NULL,
    `row` INT NOT NULL,
    `column` INT NOT NULL,
    zoneName VARCHAR(50) NOT NULL,
    beachId INT NOT NULL,
    FOREIGN KEY (zoneName, beachId) REFERENCES zones(name, beachId) ON DELETE CASCADE ON UPDATE CASCADE
);

-- GESTIONE APP
CREATE TABLE bookings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    beachId INT NOT NULL,
    customerId INT,
    callerName VARCHAR(100),
    callerPhone VARCHAR(50),
    date DATE NOT NULL,
    extraSdraio INT NOT NULL DEFAULT 0,
    extraLettini INT NOT NULL DEFAULT 0,
    extraSedie INT NOT NULL DEFAULT 0,
    camerini INT NOT NULL DEFAULT 0,
    autoPark INT NOT NULL DEFAULT 0,
    motoPark INT NOT NULL DEFAULT 0,
    electricPark INT NOT NULL DEFAULT 0,
    totalPrice DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'REJECTED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    FOREIGN KEY (beachId) REFERENCES beaches(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (customerId) REFERENCES customers(id) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE booking_spots (
    bookingId INT NOT NULL,
    date DATE NOT NULL,
    spotId INT NOT NULL,
    PRIMARY KEY (bookingId, spotId),
    FOREIGN KEY (bookingId) REFERENCES bookings(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (spotId) REFERENCES spots(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE bans (
    id INT PRIMARY KEY AUTO_INCREMENT,
    bannedId INT NOT NULL,
    banType ENUM('BEACH', 'APPLICATION'),
    bannedFromBeachId INT,
    adminId INT NOT NULL,
    reason VARCHAR(512) NOT NULL,
    createdAt DATETIME NOT NULL,
    FOREIGN KEY (bannedId) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (bannedFromBeachId) REFERENCES beaches(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (adminId) REFERENCES admins(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE reviews (
    id INT PRIMARY KEY AUTO_INCREMENT,
    beachId INT NOT NULL,
    customerId INT NOT NULL,
    rating TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment VARCHAR(1024) NOT NULL,
    createdAt DATETIME NOT NULL,
    FOREIGN KEY (beachId) REFERENCES beaches(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (customerId) REFERENCES customers(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE reports (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    reporterId INT NOT NULL,
    reportedId INT NOT NULL,
    reportedType ENUM('USER', 'BEACH'),
    description VARCHAR(512) NOT NULL,
    createdAt DATETIME NOT NULL,
    status ENUM('PENDING', 'RESOLVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    bookingId INT NOT NULL,
    FOREIGN KEY (reportedId) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (reporterId) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (bookingId) REFERENCES bookings(id) ON DELETE CASCADE ON UPDATE CASCADE
);


-- VINCOLI AGGIUNTIVI (CONTROLLI)
ALTER TABLE spots
    -- Spot deve essere unico nella zona
    ADD CONSTRAINT uq_spots UNIQUE (`row`, `column`, zoneName, beachId);

ALTER TABLE bookings
    -- Un utente può effettuare una sola prenotazione per una spiaggia in quella data
    ADD CONSTRAINT uq_booking_beach_customer_date UNIQUE (beachId, customerId, date),
    -- Crea vincolo tra la prenotazione e la data, utile per bookings_spots
    ADD CONSTRAINT UNIQUE uq_bookingsSpot (id, date);

ALTER TABLE booking_spots
    -- Spot può avere una sola prenotazione nella solita data
    ADD CONSTRAINT uq_bookingSpot_date UNIQUE (spotId, date);

ALTER TABLE bans
    -- Un utente può ricevere una sola volta un ban permanente da una spiaggia o dall'applicazione
    ADD CONSTRAINT uq_ban_single_active UNIQUE (bannedId,bannedFromBeachId,banType);

ALTER TABLE reviews
    -- Un utente può lasciare una sola recensione per la spiaggia
    ADD CONSTRAINT uq_review_per_customer UNIQUE (beachId,customerId);

ALTER TABLE seasons
    -- La spiaggia può avere una sola stagione che inizia o finisce in quelle specifiche date
    ADD CONSTRAINT uq_season_dates_beach UNIQUE (beachId,startDate,endDate);

ALTER TABLE reports
    -- Per evitare spam di reports da parte di un utente
    ADD CONSTRAINT uq_report_unique UNIQUE (reporterId,reportedId,createdAt),
    -- Un utente può reportare la prenotazione una sola volta soltanto
    ADD CONSTRAINT uq_reporter_booking UNIQUE (reporterId, bookingId);


-- CHECK >= 0
ALTER TABLE parkings
    ADD CONSTRAINT chk_nAutoPark_nonneg CHECK (nAutoPark >= 0),
    ADD CONSTRAINT chk_nMotoPark_nonneg CHECK (nMotoPark >= 0),
    ADD CONSTRAINT chk_nElectricPark_nonneg CHECK (nElectricPark >= 0);

ALTER TABLE beach_inventories
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
    ADD CONSTRAINT chk_camerini_booking_nonneg CHECK (camerini >= 0),
    ADD CONSTRAINT chk_autoPark_booking_nonneg CHECK (autoPark >= 0),
    ADD CONSTRAINT chk_motoPark_booking_nonneg CHECK (motoPark >= 0),
    ADD CONSTRAINT chk_electricPark_booking_nonneg CHECK (electricPark >= 0);

ALTER TABLE spots
    ADD CONSTRAINT chk_spot_row_col_positive CHECK (`row` >= 0 AND `column` >= 0);

ALTER TABLE seasons
    ADD CONSTRAINT chk_season_dates_valid CHECK (startDate < endDate);
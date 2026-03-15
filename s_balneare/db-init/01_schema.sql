
-- TODO: gestione delete e cascade methods



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
    FOREIGN KEY (addressId) REFERENCES addresses(id),
    FOREIGN KEY (id) REFERENCES users(id)
);

CREATE TABLE owners (
    id INT PRIMARY KEY,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    OTP BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id) REFERENCES users(id)
);

CREATE TABLE admins (
    id INT PRIMARY KEY AUTO_INCREMENT,
    OTP BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id) REFERENCES users(id)
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
    FOREIGN KEY (ownerId) REFERENCES owners(id),
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
    startDate DATE NOT NULL,
    endDate DATE NOT NULL,
    name VARCHAR(50) NOT NULL,
    beachId  INT NOT NULL,
    pricingsId INT UNIQUE NOT NULL,
    PRIMARY KEY (beachId,name),
    FOREIGN KEY (beachId) REFERENCES beaches(id),
    FOREIGN KEY (pricingsId) REFERENCES pricings(id)
);

CREATE TABLE zones (
    name VARCHAR(50) NOT NULL,
    beachId INT NOT NULL,
    PRIMARY KEY (name, beachId),
    FOREIGN KEY (beachId) REFERENCES beaches(id)
);

CREATE TABLE zone_tariffs (
    seasonName VARCHAR(50) NOT NULL,
    beachId INT NOT NULL,
    zoneName VARCHAR(50) NOT NULL,
    priceOmbrellone INT NOT NULL,
    priceTenda INT NOT NULL,
    PRIMARY KEY (seasonName,beachId,zoneName),
    FOREIGN KEY (beachId,seasonName) REFERENCES seasons(beachId,name),
    FOREIGN KEY (zoneName,beachId) REFERENCES zones(name,beachId) ON UPDATE CASCADE
);

CREATE TABLE spots (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type ENUM('UMBRELLA', 'TENT') NOT NULL,
    `row` INT NOT NULL,
    `column` INT NOT NULL,
    zoneName VARCHAR(50) NOT NULL,
    beachId INT NOT NULL,
    UNIQUE (`row`,`column`,zoneName, beachId),
    FOREIGN KEY (zoneName,beachId) REFERENCES zones(name,beachId) ON UPDATE CASCADE
);

-- GESTIONE APP

CREATE TABLE bookings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    beachId INT NOT NULL,
    customerId INT,
    callerName VARCHAR(100),
    callerPhone VARCHAR(50),
    date DATE NOT NULL,
    extraSdraio INT NOT NULL,
    extraLettini INT NOT NULL,
    extraSedie INT NOT NULL,
    camerini INT NOT NULL,
    autoPark INT NOT NULL DEFAULT 0,
    motoPark INT NOT NULL DEFAULT 0,
    bikePark INT NOT NULL DEFAULT 0,
    electricPark INT NOT NULL DEFAULT 0,
    totalPrice DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'REJECTED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    FOREIGN KEY (beachId) REFERENCES beaches(id),
    FOREIGN KEY (customerId) REFERENCES customers(id),
    UNIQUE (beachId,customerId,date),
    UNIQUE (id,date)
);

CREATE TABLE booking_spots (
    bookingId INT NOT NULL,
    date DATE NOT NULL,
    spotId INT NOT NULL,
    PRIMARY KEY (bookingId,spotId),
    FOREIGN KEY (bookingId,date) REFERENCES bookings(id,date),
    FOREIGN KEY (spotId) REFERENCES spots(id),
    UNIQUE (spotId,date)
);

CREATE TABLE bans (
    id INT PRIMARY KEY AUTO_INCREMENT,
    bannedId INT NOT NULL,
    banType ENUM('BEACH', 'APPLICATION'),
    bannedFromBeachId INT,
    adminId INT NOT NULL,
    reason VARCHAR(512) NOT NULL,
    createdAt DATETIME NOT NULL,
    FOREIGN KEY (bannedId) REFERENCES customers(id),
    FOREIGN KEY (bannedFromBeachId) REFERENCES beaches(id),
    FOREIGN KEY (adminId) REFERENCES admins(id)
);

CREATE TABLE reviews (
    id INT PRIMARY KEY AUTO_INCREMENT,
    beachId INT NOT NULL,
    customerId INT NOT NULL,
    rating TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment VARCHAR(1024) NOT NULL,
    createdAt DATETIME NOT NULL,
    FOREIGN KEY (beachId) REFERENCES beaches(id),
    FOREIGN KEY (customerId) REFERENCES customers(id)
);

CREATE TABLE reports (
    id INT NOT NULL PRIMARY KEY,
    reporterId INT NOT NULL,
    reportedId INT NOT NULL,
    reportedType ENUM('USER', 'BEACH'),
    description VARCHAR(512) NOT NULL,
    createdAt DATETIME NOT NULL,
    status ENUM('PENDING', 'RESOLVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    bookingId INT NOT NULL,
    FOREIGN KEY (reportedId) REFERENCES users(id),
    FOREIGN KEY (reporterId) REFERENCES users(id),
    FOREIGN KEY (bookingId) REFERENCES bookings(id)
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
    ADD CONSTRAINT uq_ban_single_active UNIQUE (bannedId,bannedFromBeachId,banType);

ALTER TABLE reviews -- Per evitare che un utente possa lasciare più review ad una spiaggia (come su Google)
    ADD CONSTRAINT uq_review_per_customer UNIQUE (beachId,customerId);

ALTER TABLE seasons -- Per impedire la creazione di stagioni duplicate
    ADD CONSTRAINT uq_season_dates_beach UNIQUE (beachId,startDate,endDate);

ALTER TABLE reports -- Minimizza report giornalieri, impedisce inserimento più report per lo stesso booking
    ADD CONSTRAINT uq_report_unique UNIQUE (reporterId,reportedId,createdAt),
    ADD CONSTRAINT uq_reporter_booking UNIQUE (reporterId, bookingId);

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
    ADD CONSTRAINT chk_customer_param_nonneg CHECK (customerId IS NOT NULL OR (callerName IS NOT NULL AND callerPhone IS NOT NULL)),
    ADD CONSTRAINT chk_extra_sdraio_booking_nonneg CHECK (extraSdraio >= 0),
    ADD CONSTRAINT chk_extra_lettini_booking_nonneg CHECK (extraLettini >= 0),
    ADD CONSTRAINT chk_extra_sedie_booking_nonneg CHECK (extraSedie >= 0),
    ADD CONSTRAINT chk_camerini_booking_nonneg CHECK (camerini >= 0),
    ADD CONSTRAINT chk_autoPark_booking_nonneg CHECK (autoPark >= 0),
    ADD CONSTRAINT chk_motoPark_booking_nonneg CHECK (motoPark >= 0),
    ADD CONSTRAINT chk_bikePark_booking_nonneg CHECK (bikePark >= 0),
    ADD CONSTRAINT chk_electricPark_booking_nonneg CHECK (electricPark >= 0);

ALTER TABLE spots
    ADD CONSTRAINT chk_spot_row_col_positive CHECK (`row` >= 0 AND `column` >= 0);

ALTER TABLE seasons -- Integrità date stagioni
    ADD CONSTRAINT chk_season_dates_valid CHECK (startDate < endDate);

ALTER TABLE reports -- Impedisce di fare report su se stessi, e di fare più report su un booking
    ADD CONSTRAINT chk_reporter_not_reported CHECK (reporterId <> reportedId);
DROP DATABASE IF EXISTS stabilimenti;
CREATE DATABASE stabilimenti;
USE stabilimenti;

-- INDIRIZZI UTENTI E SPIAGGIA

CREATE TABLE addresses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    street VARCHAR(255) NOT NULL,
    streetNumber VARCHAR(20) NOT NULL,
    city VARCHAR(100) NOT NULL,
    zipCode INT NOT NULL,
    country VARCHAR(100) NOT NULL
);

-- SPIAGGIA E SERVIZI

CREATE TABLE parkings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nAutoPark INT NOT NULL,
    nMotoPark INT NOT NULL,
    nBikePark INT NOT NULL,
    nElectricPark INT NOT NULL,
    CCTV BOOLEAN NOT NULL
);

CREATE TABLE beach_services (
    id INT PRIMARY KEY AUTO_INCREMENT,
    bathrooms BOOLEAN NOT NULL,
    showers BOOLEAN NOT NULL,
    pool BOOLEAN NOT NULL,
    bar BOOLEAN NOT NULL,
    restaurant BOOLEAN NOT NULL,
    wifi BOOLEAN NOT NULL,
    volleyballField BOOLEAN NOT NULL
);

CREATE TABLE beach_inventories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    countOmbrelloni INT NOT NULL,
    countTende INT NOT NULL,
    countExtraSdraio INT NOT NULL,
    countExtraLettini INT NOT NULL,
    countExtraSedie INT NOT NULL,
    countCamerini INT NOT NULL
);
CREATE TABLE beaches (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    telephoneNumber varchar(50) NOT NULL,
    beachInventory INT NOT NULL,
    beachServices INT NOT NULL,
    address INT NOT NULL,
    parkingSpace INT NOT NULL,
    extraInfo VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (beachInventory) REFERENCES beach_inventories(id),
    FOREIGN KEY (beachServices) REFERENCES beach_services(id),
    FOREIGN KEY (address) REFERENCES addresses(id),
    FOREIGN KEY (parkingSpace) REFERENCES parkings(id)
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
    beachId  INT NOT NULL,
    pricingsId INT NOT NULL,
    FOREIGN KEY (beachId) REFERENCES beaches(id),
    FOREIGN KEY (pricingsId) REFERENCES pricings(id)
);

CREATE TABLE zones (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name varchar(50) NOT NULL,
    beachId INT NOT NULL,
    FOREIGN KEY (beachId) REFERENCES beaches(id)
);

CREATE TABLE zone_pricings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    seasonId INT NOT NULL,
    zone INT NOT NULL,
    priceOmbrellone INT NOT NULL,
    priceTenda INT NOT NULL,
    FOREIGN KEY (seasonId) REFERENCES seasons(id),
    FOREIGN KEY (zone) REFERENCES zones(id)
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
    username varchar(50) NOT NULL,
    email varchar(50) NOT NULL,
    active BOOLEAN NOT NULL
);

CREATE TABLE customers (
    id INT PRIMARY KEY,
    phone_number varchar(50) NOT NULL,
    address INT NOT NULL,
    FOREIGN KEY (address) REFERENCES addresses(id),
    FOREIGN KEY (id) REFERENCES app_users(id)
);

CREATE TABLE owners (
    id INT PRIMARY KEY,
    beach INT NOT NULL,
    FOREIGN KEY (beach) REFERENCES beaches(id),
    FOREIGN KEY (id) REFERENCES app_users(id)
);

CREATE TABLE admins (
   id INT PRIMARY KEY AUTO_INCREMENT,
   FOREIGN KEY (id) REFERENCES app_users(id)
);

-- GESTIONE APP

CREATE TABLE bookings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    beach INT NOT NULL,
    customer INT NOT NULL,
    date DATE NOT NULL,
    extraSdraio INT NOT NULL,
    extraLettini INT NOT NULL,
    extraSedie INT NOT NULL,
    camerini INT NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'REJECTED', 'CANCELLED') NOT NULL,
    FOREIGN KEY (beach) REFERENCES beaches(id),
    FOREIGN KEY (customer) REFERENCES customers(id)
);

CREATE TABLE booking_spots (
    booking INT NOT NULL,
    spot INT NOT NULL,
    PRIMARY KEY (booking, spot),
    FOREIGN KEY (booking) REFERENCES bookings(id),
    FOREIGN KEY (spot) REFERENCES spots(id)
);

CREATE TABLE bans (
    id INT PRIMARY KEY AUTO_INCREMENT,
    banned INT NOT NULL,
    banType ENUM('BEACH', 'APPLICATION'),
    bannedFromBeach INT,
    admin INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    time DATETIME NOT NULL,
    FOREIGN KEY (banned) REFERENCES customers(id),
    FOREIGN KEY (bannedFromBeach) REFERENCES beaches(id),
    FOREIGN KEY (admin) REFERENCES admins(id)
);

CREATE TABLE reviews (
    id INT PRIMARY KEY AUTO_INCREMENT,
    beach INT NOT NULL,
    customer INT NOT NULL,
    rating TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment VARCHAR(500) NOT NULL,
    createdAt DATETIME NOT NULL,
    FOREIGN KEY (beach) REFERENCES beaches(id),
    FOREIGN KEY (customer) REFERENCES customers(id)
);

CREATE TABLE reports (
    id INT NOT NULL PRIMARY KEY,
    reporter INT NOT NULL,
    reported INT NOT NULL,
    reportedType ENUM('USER', 'BEACH'),
    description VARCHAR(500) NOT NULL,
    createdAt DATETIME NOT NULL,
    status ENUM('PENDING', 'RESOLVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    FOREIGN KEY (reported) REFERENCES app_users(id),
    FOREIGN KEY (reporter) REFERENCES app_users(id)
);


-- Ti aggiungo un controllo necessario per la tabella bans, poi decidi tu se tenerla o modificarla
ALTER TABLE bans
ADD CONSTRAINT chk_bans_beach_id_matches_type CHECK (
    (banType = 'BEACH' AND bannedFromBeach IS NOT NULL)
        OR
    (banType = 'APPLICATION' AND bannedFromBeach IS NULL)
);
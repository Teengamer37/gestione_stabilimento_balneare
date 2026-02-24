DROP DATABASE IF EXISTS stabilimenti;
CREATE DATABASE stabilimenti;
USE stabilimenti;

-- INDIRIZZI UTENTI E SPIAGGIA

CREATE TABLE address (
    id INT PRIMARY KEY AUTO_INCREMENT,
    street VARCHAR(255) NOT NULL,
    streetNumber VARCHAR(20) NOT NULL,
    city VARCHAR(100) NOT NULL,
    zipCode INT NOT NULL,
    country VARCHAR(100) NOT NULL
);

-- SPIAGGIA E SERVIZI

CREATE TABLE parking(
    id INT PRIMARY KEY AUTO_INCREMENT,
    nAutoPark INT NOT NULL,
    nMotoPark INT NOT NULL,
    nBikePark INT NOT NULL,
    nElectricPark INT NOT NULL,
    CCTV BOOLEAN NOT NULL
);

CREATE TABLE beach_services(
    id INT PRIMARY KEY AUTO_INCREMENT,
    bathrooms BOOLEAN NOT NULL,
    showers BOOLEAN NOT NULL,
    pool BOOLEAN NOT NULL,
    bar BOOLEAN NOT NULL,
    restaurant BOOLEAN NOT NULL,
    wifi BOOLEAN NOT NULL,
    volleyballField BOOLEAN NOT NULL
);

CREATE TABLE beach_inventory(
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
    nome VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    phone_number varchar(50) NOT NULL,
    inventory_id INT NOT NULL,
    service_id INT NOT NULL,
    address INT NOT NULL,
    parking INT NOT NULL,
    FOREIGN KEY (inventory_id) REFERENCES beach_inventory (id),
    FOREIGN KEY (service_id) REFERENCES beach_services (id),
    FOREIGN KEY (address) REFERENCES address (id),
    FOREIGN KEY (parking) REFERENCES parking (id)
);

-- PREZZI, STAGIONI E ZONE

CREATE TABLE pricing(
    id INT PRIMARY KEY AUTO_INCREMENT,
    priceLettino INT NOT NULL,
    priceSdraio INT NOT NULL,
    priceSedia INT NOT NULL,
    priceParking INT NOT NULL,
    priceChangingRoom INT NOT NULL
);

CREATE TABLE season (
    id INT PRIMARY KEY AUTO_INCREMENT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    beach_id  INT NOT NULL,
    pricing_id INT NOT NULL,
    FOREIGN KEY (beach_id) REFERENCES beaches (id),
    FOREIGN KEY (pricing_id) REFERENCES pricing (id)
);

CREATE TABLE zone(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name varchar(50) NOT NULL,
    beach_id INT NOT NULL,
    FOREIGN KEY (beach_id) REFERENCES beaches(id)
);
CREATE TABLE zone_pricing (
    id INT PRIMARY KEY AUTO_INCREMENT,
    season_id INT NOT NULL,
    zone_id INT NOT NULL,
    price_o INT NOT NULL,
    price_t INT NOT NULL,
    FOREIGN KEY (season_id) REFERENCES season(id),
    FOREIGN KEY (zone_id) REFERENCES zone(id)
);

CREATE TABLE spot (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type ENUM('UMBRELLA', 'TENT') NOT NULL,
    r INT NOT NULL,
    c INT NOT NULL,
    zone_id INT NOT NULL,
    FOREIGN KEY (zone_id) REFERENCES zone(id)
);

-- UTENTI
CREATE TABLE app_user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome varchar(50) NOT NULL,
    cognome varchar(50) NOT NULL,
    username varchar(50) NOT NULL,
    email varchar(50) NOT NULL,
    active BOOLEAN NOT NULL
);

CREATE TABLE customer (
    id INT PRIMARY KEY,
    phone_number varchar(50) NOT NULL,
    address INT NOT NULL,
    FOREIGN KEY (address) REFERENCES address (id),
    FOREIGN KEY (id) REFERENCES app_user(id)
);

CREATE TABLE owner
(
    id INT PRIMARY KEY,
    beach_id INT NOT NULL,
    FOREIGN KEY (beach_id) REFERENCES beaches (id),
    FOREIGN KEY (id) REFERENCES app_user(id)
);

CREATE TABLE admin (
   id INT PRIMARY KEY AUTO_INCREMENT,
   nome varchar(50) NOT NULL,
   cognome varchar(50) NOT NULL,
   username varchar(50) NOT NULL,
   email varchar(50) NOT NULL,
   active BOOLEAN NOT NULL
);

-- GESTIONE APP

CREATE TABLE booking (
    id INT PRIMARY KEY AUTO_INCREMENT,
    beach_id INT NOT NULL,
    customer_id INT NOT NULL,
    date DATE NOT NULL,
    extraSdraio INT NOT NULL,
    extraLettini INT NOT NULL,
    extraSedie INT NOT NULL,
    changingRooms INT NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'REJECTED', 'CANCELLED') NOT NULL,
    FOREIGN KEY (beach_id) REFERENCES beaches(id),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE booking_spots(
    booking_id INT NOT NULL,
    spot_id INT NOT NULL,
    PRIMARY KEY (booking_id, spot_id),
    FOREIGN KEY (booking_id) REFERENCES booking (id),
    FOREIGN KEY (spot_id) REFERENCES spot(id)
);

CREATE TABLE ban (
    id INT PRIMARY KEY AUTO_INCREMENT,
    banned_user_id INT NOT NULL,
    ban_type ENUM('BEACH', 'APPLICATION'),
    banned_from_beach_id INT,
    admin_id INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    time DATETIME NOT NULL,
    FOREIGN KEY (banned_user_id) REFERENCES customer(id),
    FOREIGN KEY (banned_from_beach_id) REFERENCES beaches(id),
    FOREIGN KEY (admin_id) REFERENCES admin(id)
);

CREATE TABLE review(
    id INT PRIMARY KEY AUTO_INCREMENT,
    beach_id INT NOT NULL,
    customer_id INT NOT NULL,
    rating TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment VARCHAR(500) NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (beach_id) REFERENCES beaches(id),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);
CREATE TABLE report(
    id INT NOT NULL PRIMARY KEY,
    reporter_id INT NOT NULL,
    reported_id INT NOT NULL,
    reported_type ENUM('USER', 'BEACH'),
    description VARCHAR(500) NOT NULL,
    time DATETIME NOT NULL,
    status ENUM('PENDING', 'RESOLVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    FOREIGN KEY (reported_id) REFERENCES app_user(id),
    FOREIGN KEY (reporter_id) REFERENCES app_user(id)
)



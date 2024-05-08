-- Suppression de la table de jonction "person_allergy" si elle existe
DROP TABLE IF EXISTS person_allergy;

-- Suppression de la table de jonction "person_medication" si elle existe
DROP TABLE IF EXISTS person_medication;

-- Suppression de la table "person" si elle existe
DROP TABLE IF EXISTS person;

-- Suppression de la table "allergy" si elle existe
DROP TABLE IF EXISTS allergy;

-- Suppression de la table "medication" si elle existe
DROP TABLE IF EXISTS medication;

-- Suppression de la table "fire_station" si elle existe
DROP TABLE IF EXISTS fire_station;

-- Suppression de la table "address" si elle existe
DROP TABLE IF EXISTS address;


-- Création de la table "address"
CREATE TABLE address (
    id INT AUTO_INCREMENT PRIMARY KEY,
    label VARCHAR(255) NOT NULL,
    zip VARCHAR(10) NOT NULL,
    city VARCHAR(100) NOT NULL
);

-- Création de la table "person"
CREATE TABLE person (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    birthday DATE,
    address_id INT,
    FOREIGN KEY (address_id) REFERENCES address(id)
);

-- Création de la table "fire_station"
CREATE TABLE fire_station (
    id INT AUTO_INCREMENT PRIMARY KEY,
    station INT NOT NULL,
    address_id INT NOT NULL,
    FOREIGN KEY (address_id) REFERENCES address(id)
);

-- Création de la table "allergy"
CREATE TABLE allergy (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Création de la table "medication"
CREATE TABLE medication (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dosage INT NOT NULL
);

-- Création de la table de jonction "person_medication"
CREATE TABLE person_medication (
    person_id INT NOT NULL,
    medication_id INT NOT NULL,
    PRIMARY KEY (person_id, medication_id),
    FOREIGN KEY (person_id) REFERENCES person(id),
    FOREIGN KEY (medication_id) REFERENCES medication(id)
);

-- Création de la table de jonction "person_allergy"
CREATE TABLE person_allergy (
    person_id INT NOT NULL,
    allergy_id INT NOT NULL,
    PRIMARY KEY (person_id, allergy_id),
    FOREIGN KEY (person_id) REFERENCES person(id),
    FOREIGN KEY (allergy_id) REFERENCES allergy(id)
);
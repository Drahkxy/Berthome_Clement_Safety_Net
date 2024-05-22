DROP TABLE IF EXISTS medical_record_medication;
DROP TABLE IF EXISTS medical_record_allergy;
DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS allergy;
DROP TABLE IF EXISTS medication;
DROP TABLE IF EXISTS fire_station;
DROP TABLE IF EXISTS address;
DROP TABLE IF EXISTS medical_record;

CREATE TABLE address (
    id INT AUTO_INCREMENT PRIMARY KEY,
    label VARCHAR(255) NOT NULL,
    zip VARCHAR(10) NOT NULL,
    city VARCHAR(100) NOT NULL
);

CREATE TABLE person (
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    phone VARCHAR(255),
    email VARCHAR(255),
    address_id INT,
    PRIMARY KEY (first_name, last_name),
    FOREIGN KEY (address_id) REFERENCES address(id)
);

CREATE TABLE fire_station (
    id INT AUTO_INCREMENT PRIMARY KEY,
    station INT NOT NULL,
    address_id INT NOT NULL,
    FOREIGN KEY (address_id) REFERENCES address(id)
);

CREATE TABLE allergy (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE medication (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dosage INT NOT NULL
);

CREATE TABLE medical_record (
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    birthday DATE,
    PRIMARY KEY (first_name, last_name)
);

CREATE TABLE medical_record_medication (
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    medication_id INT NOT NULL,
    PRIMARY KEY (first_name, last_name, medication_id),
    FOREIGN KEY (first_name, last_name) REFERENCES medical_record(first_name, last_name),
    FOREIGN KEY (medication_id) REFERENCES medication(id)
);

CREATE TABLE medical_record_allergy (
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    allergy_id INT NOT NULL,
    PRIMARY KEY (first_name, last_name, allergy_id),
    FOREIGN KEY (first_name, last_name) REFERENCES medical_record(first_name, last_name),
    FOREIGN KEY (allergy_id) REFERENCES allergy(id)
);

-- SQL commands to create the database schema
-- no need to run it while using Hibernate with ddl-auto=update

-- create database + switch to it
CREATE DATABASE IF NOT EXISTS semestral;
USE semestral;

-- table for countries
CREATE TABLE IF NOT EXISTS countries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    iso_code VARCHAR(3),
    population BIGINT,
    continent VARCHAR(255)
);

-- table for cities
CREATE TABLE IF NOT EXISTS cities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    country_id BIGINT NOT NULL,
    population BIGINT,
    latitude DOUBLE,
    longitude DOUBLE,
    FOREIGN KEY (country_id) REFERENCES countries(id)
);

-- table for weather records
CREATE TABLE IF NOT EXISTS records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    min_temperature DOUBLE NOT NULL,
    max_temperature DOUBLE NOT NULL,
    pressure INT NOT NULL,
    humidity INT NOT NULL,
    wind_speed DOUBLE NOT NULL,
    wind_deg INT NOT NULL,
    timestamp DATETIME NOT NULL,
    city_id BIGINT NOT NULL,
    FOREIGN KEY (city_id) REFERENCES cities(id)
);

-- indexes on primary keys
CREATE INDEX IF NOT EXISTS idx_city_country ON cities(country_id);
CREATE INDEX IF NOT EXISTS idx_record_city ON records(city_id);
CREATE INDEX IF NOT EXISTS idx_record_timestamp ON records(timestamp);

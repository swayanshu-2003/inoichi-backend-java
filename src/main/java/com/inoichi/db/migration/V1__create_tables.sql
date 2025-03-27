-- Ensure the schema exists
CREATE SCHEMA IF NOT EXISTS app;

-- Create Users Table
CREATE TABLE IF NOT EXISTS app.users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    name VARCHAR(255),
    age INT,
    gender VARCHAR(10),
    address TEXT
);

-- Create Patient Details Table
CREATE TABLE IF NOT EXISTS app.patient_details (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    name VARCHAR(255),
    age INT,
    gender VARCHAR(10),
    weight DOUBLE PRECISION,
    height DOUBLE PRECISION,
    bmi DOUBLE PRECISION,
    preferred_language VARCHAR(100),
    photo_id VARCHAR(255),
    chronic_diseases TEXT,
    address TEXT,
    blood_group VARCHAR(10),
    emergency_contact_details TEXT,
    CONSTRAINT fk_patient_user FOREIGN KEY (user_id) REFERENCES app.users (id) ON DELETE CASCADE
);

-- Create Doctor Details Table
CREATE TABLE IF NOT EXISTS app.doctor_details (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    specialization VARCHAR(255),
    years_of_experience INT,
    organization VARCHAR(255),
    about TEXT,
    CONSTRAINT fk_doctor_user FOREIGN KEY (user_id) REFERENCES app.users (id) ON DELETE CASCADE
);

-- Create Consultations Table
CREATE TABLE IF NOT EXISTS app.consultations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    doctor_id UUID NOT NULL,
    patient_id UUID NOT NULL,
    booking_date TIMESTAMP NOT NULL,
    appointment_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_consultation_doctor FOREIGN KEY (doctor_id) REFERENCES app.doctor_details (id) ON DELETE CASCADE,
    CONSTRAINT fk_consultation_patient FOREIGN KEY (patient_id) REFERENCES app.patient_details (id) ON DELETE CASCADE
);

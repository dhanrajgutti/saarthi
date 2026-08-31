CREATE DATABASE IF NOT EXISTS vehical_auth_db;
USE vehical_auth_db;

DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    role ENUM('POLICE', 'PUC_CENTER', 'INSURANCE_AGENCY') NOT NULL,
    department VARCHAR(100),
    center_name VARCHAR(100),
    agency_name VARCHAR(100),
    license_number VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_role (role),
    INDEX idx_username (username)
);

INSERT INTO users (username, password, full_name, email, phone, role, department) VALUES
('police_admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Rajesh Kumar', 'rajesh.kumar@police.gov.in', '+91-98765-43210', 'POLICE', 'Traffic Police Department - Mumbai'),
('police_inspector', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Amit Sharma', 'amit.sharma@police.gov.in', '+91-98765-43211', 'POLICE', 'RTO Enforcement Wing - Delhi');

INSERT INTO users (username, password, full_name, email, phone, role, center_name, license_number) VALUES
('puc_mumbai_01', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Suresh Patel', 'suresh@mumbaipuc.com', '+91-98765-43220', 'PUC_CENTER', 'Mumbai PUC Testing Center - Andheri', 'PUC-MH-001245'),
('puc_delhi_01', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Vikram Singh', 'vikram@delhipuc.in', '+91-98765-43221', 'PUC_CENTER', 'Delhi Emission Testing Lab', 'PUC-DL-005678');

INSERT INTO users (username, password, full_name, email, phone, role, agency_name, license_number) VALUES
('insurance_lic_01', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Priya Nair', 'priya.nair@licindia.com', '+91-98765-43230', 'INSURANCE_AGENCY', 'Life Insurance Corporation of India', 'IRDA-LIC-1001'),
('insurance_hdfc_01', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Karan Mehta', 'karan.mehta@hdfcergo.com', '+91-98765-43231', 'INSURANCE_AGENCY', 'HDFC ERGO General Insurance', 'IRDA-HDFC-2056');

-- =============== E-CHALLAN MODULE TABLES ===============

DROP TABLE IF EXISTS evidence;
DROP TABLE IF EXISTS challan;
DROP TABLE IF EXISTS vehicle;
DROP TABLE IF EXISTS owner;
DROP TABLE IF EXISTS police_officer;
DROP TABLE IF EXISTS violation_master;

-- 1. VIOLATION MASTER (reference table)
CREATE TABLE violation_master (
    violation_id INT AUTO_INCREMENT PRIMARY KEY,
    violation_name VARCHAR(150) NOT NULL UNIQUE,
    mv_act_section VARCHAR(20) NOT NULL,
    fine_amount INT NOT NULL DEFAULT 500,
    description VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_vm_active (is_active)
);

INSERT INTO violation_master (violation_name, mv_act_section, fine_amount, description) VALUES
('Not Wearing Helmet',          '194D',    500, 'Rider / pillion not wearing ISI standard helmet'),
('Not Wearing Seatbelt',        '177',     500, 'Driver / passenger not wearing seat belt in car'),
('Triple Seat / Overloading',   '182B',   1000, 'Extra pillion riders on two-wheeler or vehicle overload'),
('Signal Jump / Red Light',     '177',    1000, 'Violation of traffic signal / red light jumping'),
('Wrong Parking',               '177',     500, 'Vehicle parked in no-parking zone / obstruction'),
('Mobile Phone While Driving',  '177',    1000, 'Using hand-held mobile phone while driving'),
('Over Speeding',               '183(1)', 1000, 'Exceeding prescribed speed limit for vehicle class'),
('Dangerous / Rash Driving',    '184',    1000, 'Driving at a speed or manner dangerous to public'),
('No Valid PUC Certificate',    '175',    1000, 'Vehicle without valid Pollution Under Control cert'),
('No Valid Insurance',          '196',    2000, 'Vehicle without valid third-party insurance'),
('Driving Without RC / Fitness','192B',   2000, 'Vehicle without valid Registration Certificate'),
('Driving Without Licence',     '192A',   5000, 'Driving without a valid driving licence'),
('Wrong Side Driving',          '119',     500, 'Driving on the wrong side of the road'),
('Drink and Drive / Alcohol',   '185',   10000, 'Driving under influence of alcohol or drugs'),
('Others / Miscellaneous',      '177',     500, 'Any other violation not listed above');

-- 2. OWNER
CREATE TABLE owner (
    owner_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_name VARCHAR(150) NOT NULL,
    mobile VARCHAR(10),
    email VARCHAR(150),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_owner_mobile (mobile),
    INDEX idx_owner_name (owner_name)
);

INSERT INTO owner (owner_name, mobile, email, address) VALUES
('Amit Sharma',        '9820011234', 'amit.sharma@email.com',   '12, MG Road, Andheri West, Mumbai - 400058'),
('Priya Verma',        '9833312345', 'priya.verma@email.com',   '45, Nehru Nagar, Bandra East, Mumbai - 400051'),
('Rajesh Patel',       '9004454321', 'raj.patel@email.com',     '7, LBS Marg, Ghatkopar, Mumbai - 400086'),
('Sneha Iyer',         '9810087654', 'sneha.iyer@email.com',   'Flat 302, Green Park, Delhi - 110016'),
('Vikram Singh',       '9999012345', 'vikram.singh@email.com',  'House 14, Sector 15, Gurgaon - 122001'),
('Anita Desai',        '9821145678', 'anita.desai@email.com',  '8, Residency Road, Bengaluru - 560025'),
('Karan Mehta',        '9766677777', 'karan.mehta@email.com',  'Plot 22, Koregaon Park, Pune - 411001'),
('Sunita Rao',         '9444488222', 'sunita.rao@email.com',   '15, TTK Road, Alwarpet, Chennai - 600018'),
('Deepak Joshi',       '9898976765', 'deepak.joshi@email.com', '20, C.G. Road, Navrangpura, Ahmedabad - 380009'),
('Rohit Yadav',        '9911123222', 'rohit.yadav@email.com',  '5, Hazratganj, Lucknow - 226001'),
('Meera Nair',         '9847012321', 'meera.nair@email.com',   '2, Kowdiar Gardens, Trivandrum - 695003'),
('Nikhil Jain',        '9300055555', 'nikhil.jain@email.com',  'Shop 18, MI Road, Jaipur - 302001'),
('Aditya Das',         '9088011222', 'aditya.das@email.com',   '6, Ballygunge Place, Kolkata - 700019'),
('Sonal Gupta',        '9988833444', 'sonal.gupta@email.com',  'Sector 21D, Chandigarh - 160021'),
('Amrita Sen',         '7002200111', 'amrita.sen@email.com',   '4, Garcha Road, Bhubaneswar - 751001');

-- 3. VEHICLE
CREATE TABLE vehicle (
    vehicle_no VARCHAR(20) PRIMARY KEY COMMENT 'Format: MH-04 AB 1234',
    owner_id BIGINT NOT NULL,
    vehicle_type VARCHAR(40) NOT NULL,
    make VARCHAR(100),
    model VARCHAR(100),
    fuel_type VARCHAR(20),
    registration_date DATE,
    registration_state VARCHAR(50),
    insurance_status VARCHAR(20) DEFAULT 'Valid' COMMENT 'Valid / Expired',
    puc_status VARCHAR(20)       DEFAULT 'Valid' COMMENT 'Valid / Expired',
    rc_status VARCHAR(20)        DEFAULT 'Active' COMMENT 'Active / Expired / Suspended',
    engine_number VARCHAR(50),
    chassis_number VARCHAR(50),
    blacklisted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicle_owner FOREIGN KEY (owner_id) REFERENCES owner(owner_id),
    INDEX idx_vehicle_type (vehicle_type),
    INDEX idx_vehicle_owner (owner_id),
    INDEX idx_vehicle_black (blacklisted)
);

INSERT INTO vehicle (vehicle_no, owner_id, vehicle_type, make, model, fuel_type, registration_date, registration_state, insurance_status, puc_status, rc_status, engine_number, chassis_number, blacklisted) VALUES
('MH-04 AB 1234', 1,  'Car / Jeep',        'Maruti Suzuki',   'Alto 800 LXi',     'Petrol',  '2020-05-15', 'Maharashtra',  'Valid',   'Expired', 'Active',  'EN37FE881234', 'CH12345678MSA1', FALSE),
('MH-12 CD 5678', 2,  'Two Wheeler',       'Honda',           'Activa 6G',        'Petrol',  '2021-09-22', 'Maharashtra',  'Valid',   'Valid',   'Active',  'ENHW21K78901', 'CH78901234HW12', FALSE),
('MH-03 ZA 1120', 3,  'HMV — Truck / Bus', 'Tata Motors',     'LPT 1109 CRX',     'Diesel',  '2019-03-05', 'Maharashtra',  'Valid',   'Valid',   'Active',  'ENTM19D11220', 'CH11220033TM01', FALSE),
('DL-01 BC 4455', 4,  'Two Wheeler',       'Bajaj Auto',      'Pulsar 150',       'Petrol',  '2018-07-11', 'Delhi',        'Valid',   'Expired', 'Active',  'ENBA18P45000', 'CH45000011BA01', TRUE),
('DL-10 XY 8832', 5,  'Car / Jeep',        'Hyundai',         'Creta 1.6 SX',     'Diesel',  '2022-01-28', 'Delhi',        'Valid',   'Valid',   'Active',  'ENHM22D99800', 'CH99800765HM02', FALSE),
('KA-05 DE 7788', 6,  'Car / Jeep',        'Tata Motors',     'Nexon EV',         'Electric','2023-02-14', 'Karnataka',    'Valid',   'Valid',   'Active',  'EN-TM23E0012', 'CHTM001234EV03', FALSE),
('GJ-06 HI 2211', 7,  'Two Wheeler',       'TVS Motor',       'Jupiter 125',      'Petrol',  '2021-11-03', 'Gujarat',      'Expired', 'Valid',   'Active',  'ENTV21P88901', 'CH88901045TV04', FALSE),
('TN-09 FG 9900', 8,  'HMV — Truck / Bus', 'Ashok Leyland',   'Ecomet 1214',      'Diesel',  '2017-08-30', 'Tamil Nadu',   'Valid',   'Valid',   'Active',  'ENAL17D55330', 'CH55330009AL05', FALSE),
('UP-32 JK 3344', 10, 'HMV — Truck / Bus', 'Volvo Trucks',    'FH 500',           'Diesel',  '2020-10-08', 'Uttar Pradesh','Valid',   'Expired', 'Active',  'ENVT20D00451', 'CH00451908VT06', FALSE),
('RJ-14 LM 8899', 12, 'Car / Jeep',        'Mahindra',        'Bolero B6',        'Diesel',  '2019-06-20', 'Rajasthan',    'Valid',   'Valid',   'Active',  'ENMM19D22341', 'CH22341677MM07', FALSE),
('MH-05 FG 9900', 3,  'Three Wheeler',     'Bajaj Auto',      'RE Compact 4S',    'CNG',     '2022-03-17', 'Maharashtra',  'Valid',   'Valid',   'Active',  'ENBA22G77612', 'CH77612305BA08', FALSE),
('PB-02 MN 1234', 14, 'Two Wheeler',       'Hero MotoCorp',   'Splendor Plus',    'Petrol',  '2019-12-04', 'Punjab',       'Valid',   'Expired', 'Suspended','ENHM19P22890', 'CH22890011HM09', FALSE);

-- 4. POLICE OFFICER
CREATE TABLE police_officer (
    officer_id VARCHAR(20) PRIMARY KEY,
    officer_name VARCHAR(150) NOT NULL,
    badge_no VARCHAR(20) NOT NULL UNIQUE,
    police_station VARCHAR(200) NOT NULL,
    role ENUM('POLICE','RTO_ENFORCEMENT','INSPECTOR','SUPERINTENDENT') DEFAULT 'POLICE',
    user_id BIGINT UNIQUE COMMENT 'FK to users.id',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_officer_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_officer_station (police_station),
    INDEX idx_officer_user (user_id)
);

INSERT INTO police_officer (officer_id, officer_name, badge_no, police_station, role, user_id) VALUES
('PO43210', 'Rajesh Kumar', 'BADGE-43210', 'Mumbai Central Traffic Police Station',       'POLICE',            1),
('PO11223', 'Amit Sharma',  'BADGE-11223', 'Delhi RTO Enforcement Wing - Shahdara',        'RTO_ENFORCEMENT',   2),
('PO55440', 'Priya Nair',   'BADGE-55440', 'Andheri Traffic Division - Mumbai',            'INSPECTOR',         NULL),
('PO88001', 'Karan Mehta',  'BADGE-88001', 'Thane Superintendent Traffic Office',          'SUPERINTENDENT',    NULL),
('PO77123', 'Vikram Singh', 'BADGE-77123', 'Worli Traffic Outpost - Mumbai',                'POLICE',            NULL),
('PO66987', 'Sneha Iyer',   'BADGE-66987', 'Pune Swargate Traffic Police Station',         'INSPECTOR',         NULL);

-- 5. CHALLAN
CREATE TABLE challan (
    challan_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    challan_number VARCHAR(20) NOT NULL UNIQUE,
    vehicle_no VARCHAR(20) NOT NULL,
    officer_id VARCHAR(20) NOT NULL,
    violation_ids VARCHAR(200) NOT NULL COMMENT 'Comma separated violation_id list',
    total_fine_amount INT NOT NULL DEFAULT 0,
    challan_date DATE NOT NULL,
    challan_time TIME NOT NULL,
    location VARCHAR(300) NOT NULL,
    gps_coords VARCHAR(60),
    status ENUM('PENDING_PAYMENT','PAID','CANCELLED','DISPUTED','EXPIRED') DEFAULT 'PENDING_PAYMENT',
    remarks VARCHAR(500),
    evidence_vehicle_photo TEXT COMMENT 'Base64 / signed URL',
    evidence_plate_photo   TEXT COMMENT 'Base64 / signed URL',
    evidence_driver_photo  TEXT COMMENT 'Base64 / signed URL',
    evidence_video         TEXT COMMENT 'Base64 / signed URL',
    payment_status VARCHAR(20) DEFAULT 'Unpaid',
    payment_date DATE,
    payment_reference VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_challan_vehicle FOREIGN KEY (vehicle_no) REFERENCES vehicle(vehicle_no),
    CONSTRAINT fk_challan_officer FOREIGN KEY (officer_id) REFERENCES police_officer(officer_id),
    INDEX idx_challan_number (challan_number),
    INDEX idx_challan_vehicle (vehicle_no),
    INDEX idx_challan_officer (officer_id),
    INDEX idx_challan_status (status),
    INDEX idx_challan_date (challan_date)
);

INSERT INTO challan (challan_number, vehicle_no, officer_id, violation_ids, total_fine_amount, challan_date, challan_time, location, gps_coords, status, remarks, evidence_vehicle_photo, evidence_plate_photo, payment_status) VALUES
('CH20260000123', 'MH-04 AB 1234', 'PO43210', '9',          1000, '2026-07-15', '09:14:00', 'Andheri Flyover, Western Express Hwy', '19.1197° N, 72.8469° E', 'PENDING_PAYMENT', 'PUC expired 2 months, notice sent.', NULL, NULL, 'Unpaid'),
('CH20260000124', 'DL-01 BC 4455', 'PO11223', '1,7',       1500, '2026-07-18', '11:42:00', 'Shahdara Red Light Junction',         '28.6747° N, 77.3005° E', 'PENDING_PAYMENT', 'No helmet + overspeeding through signal.', NULL, NULL, 'Unpaid'),
('CH20260000122', 'GJ-06 HI 2211', 'PO77123', '10',         2000, '2026-07-09', '13:05:00', 'SG Highway Crossroads, Ahmedabad',     '23.0394° N, 72.5112° E', 'PENDING_PAYMENT', 'Insurance policy not provided on demand.', NULL, NULL, 'Unpaid'),
('CH20260000121', 'UP-32 JK 3344', 'PO77123', '9,8',        2000, '2026-07-02', '08:22:00', 'Agra-Ghaziabad Highway Plaza',         '28.6685° N, 77.4520° E', 'PAID',            'Both PUC expired & dangerous driving. Paid via Paytm.', NULL, NULL, 'Paid'),
('CH20260000120', 'MH-12 CD 5678', 'PO55440', '5',          500,  '2026-06-28', '17:55:00', 'Bandra Link Road No-Parking Zone',     '19.0596° N, 72.8295° E', 'PAID',            'Wrong parking cleared after fine.', NULL, NULL, 'Paid');

-- 6. EVIDENCE
CREATE TABLE evidence (
    evidence_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    challan_id BIGINT NOT NULL,
    vehicle_photo TEXT,
    plate_photo TEXT,
    driver_photo TEXT,
    video TEXT,
    gps_location VARCHAR(60),
    captured_timestamp DATETIME NOT NULL,
    officer_id VARCHAR(20) NOT NULL,
    police_station VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_evidence_challan FOREIGN KEY (challan_id) REFERENCES challan(challan_id) ON DELETE CASCADE,
    INDEX idx_evidence_challan (challan_id)
);

INSERT INTO evidence (challan_id, gps_location, captured_timestamp, officer_id, police_station) VALUES
(1, '19.1197° N, 72.8469° E', '2026-07-15 09:14:00', 'PO43210', 'Mumbai Central Traffic Police Station'),
(2, '28.6747° N, 77.3005° E', '2026-07-18 11:42:00', 'PO11223', 'Delhi RTO Enforcement Wing - Shahdara'),
(3, '23.0394° N, 72.5112° E', '2026-07-09 13:05:00', 'PO77123', 'Worli Traffic Outpost - Mumbai'),
(4, '28.6685° N, 77.4520° E', '2026-07-02 08:22:00', 'PO77123', 'Worli Traffic Outpost - Mumbai'),
(5, '19.0596° N, 72.8295° E', '2026-06-28 17:55:00', 'PO55440', 'Andheri Traffic Division - Mumbai');

-- =============== PUC CERTIFICATE MODULE TABLES ===============

DROP TABLE IF EXISTS puc_certificate;

CREATE TABLE puc_certificate (
    puc_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    certificate_number VARCHAR(30) NOT NULL UNIQUE COMMENT 'Format: MH13010822P1002125',
    vehicle_no VARCHAR(20) NOT NULL COMMENT 'Vehicle registration number',
    owner_name VARCHAR(150),
    vehicle_type VARCHAR(60),
    fuel_type VARCHAR(30),
    registration_date DATE,
    engine_number VARCHAR(50),
    chassis_number VARCHAR(50),
    center_id VARCHAR(60) COMMENT 'PUC Center / Machine ID',
    center_name VARCHAR(150),
    test_date_time DATETIME NOT NULL,
    emission_norms VARCHAR(30) DEFAULT 'BHARAT STAGE VI',
    odometer_km DECIMAL(10,1),
    co_percent DECIMAL(5,2) COMMENT 'Carbon Monoxide %',
    hc_ppm DECIMAL(6,2) COMMENT 'Hydrocarbons PPM',
    co2_percent DECIMAL(5,2) COMMENT 'Carbon Dioxide %',
    lambda_value DECIMAL(4,2) COMMENT 'Lambda (Air-Fuel ratio)',
    pollution_under_control VARCHAR(5) NOT NULL DEFAULT 'Yes' COMMENT 'Yes / No',
    valid_from DATE NOT NULL,
    valid_upto DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'Valid' COMMENT 'Valid / Expired / Fail / Revoked',
    remarks TEXT COMMENT 'Optional remarks',
    issued_by_user_id BIGINT COMMENT 'FK to users.id',
    issued_by_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_puc_cert_no (certificate_number),
    INDEX idx_puc_vehicle (vehicle_no),
    INDEX idx_puc_center (center_id),
    INDEX idx_puc_status (status),
    INDEX idx_puc_valid_upto (valid_upto)
);

INSERT INTO puc_certificate (certificate_number, vehicle_no, owner_name, vehicle_type, fuel_type, registration_date, engine_number, chassis_number, center_id, center_name, test_date_time, emission_norms, odometer_km, co_percent, hc_ppm, co2_percent, lambda_value, pollution_under_control, valid_from, valid_upto, status, remarks, issued_by_user_id, issued_by_name) VALUES
('MH13150725P1002001', 'MH-04 AB 1234', 'Amit Sharma',        'Car / Jeep',                 'Petrol',  '2020-05-15', 'EN37FE881234', 'CH12345678MSA1', 'PUC10024', 'Mumbai PUC Testing Center - Andheri', '2025-07-15 10:30:00', 'BHARAT STAGE VI', 42350.0, 0.22, 38, 12.4, 0.99, 'Yes', '2025-07-15', '2026-07-14', 'Expired', 'All emission parameters within limits.', 3, 'Suresh Patel'),
('MH13020526P1002002', 'MH-12 CD 5678', 'Priya Verma',        'Two Wheeler',                'Petrol',  '2021-09-22', 'ENHW21K78901', 'CH78901234HW12', 'PUC10024', 'Mumbai PUC Testing Center - Andheri', '2026-05-02 14:12:00', 'BHARAT STAGE VI', 18920.0, 0.18, 42, 11.8, 0.97, 'Yes', '2026-05-02', '2027-05-01', 'Valid',   'Excellent condition, well maintained.', 3, 'Suresh Patel'),
('MH13140726P1002003', 'MH-03 ZA 1120', 'Rajesh Patel',       'HMV — Truck / Bus',          'Diesel',  '2019-03-05', 'ENTM19D11220', 'CH11220033TM01', 'PUC10024', 'Mumbai PUC Testing Center - Andheri', '2026-07-14 09:45:00', 'BHARAT STAGE VI', 128400.0, 0.38, 210, 13.1, 1.02, 'Yes', '2026-07-14', '2027-07-13', 'Valid',   'Smoke opacity within permissible limits.', 3, 'Suresh Patel'),
('DL10010826P5678001', 'DL-10 XY 8832', 'Vikram Singh',       'Car / Jeep',                 'Diesel',  '2022-01-28', 'ENHM22D99800', 'CH99800765HM02', 'PUC10023', 'Delhi Emission Testing Lab',            '2026-08-01 11:20:00', 'BHARAT STAGE VI', 35670.0, 0.25, 78, 12.8, 1.00, 'Yes', '2026-08-01', '2027-07-31', 'Valid',   'All readings within BS VI limits.', 4, 'Vikram Singh'),
('KA05010626P1002001', 'KA-05 DE 7788', 'Anita Desai',        'Car / Jeep',                 'Electric','2023-02-14', 'EN-TM23E0012', 'CHTM001234EV03', 'PUC10025', 'Solapur Center',                       '2026-06-01 16:00:00', 'BHARAT STAGE VI', 9840.0,  NULL, NULL, NULL, NULL, 'Yes', '2026-06-01', '2028-05-31', 'Valid',   'Electric Vehicle - Emission exempt, validity 2 years.', 3, 'Suresh Patel');


# Create TSI LOS Service
create database _tsi_los_service;
CREATE USER demo WITH PASSWORD 'demo@123';
GRANT CONNECT ON DATABASE _tsi_los_service TO demo;
GRANT ALL PRIVILEGES ON DATABASE _tsi_los_service TO demo;
# Create TSI Backend Service
create database _tsi_backend_service;
CREATE USER demo WITH PASSWORD 'demo@123';
GRANT CONNECT ON DATABASE _tsi_backend_service TO demo;
GRANT ALL PRIVILEGES ON DATABASE _tsi_backend_service TO demo;
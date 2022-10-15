# Create Tenant
create database _tsi_accelerator;
CREATE USER demo WITH PASSWORD 'demo@123';
GRANT CONNECT ON DATABASE _tsi_accelerator TO demo;
GRANT ALL PRIVILEGES ON DATABASE _tsi_accelerator TO demo;
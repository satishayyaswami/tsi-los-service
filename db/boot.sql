# Create TSI Admin
create database _tsi_admin;
CREATE USER demoadmin WITH PASSWORD 'admin@123';
GRANT CONNECT ON DATABASE _tsi_admin TO demoadmin;
GRANT ALL PRIVILEGES ON DATABASE _tsi_admin TO demoadmin;

# Create Tenant
create database _tsi_accelerator;
CREATE USER demo WITH PASSWORD 'demo@123';
GRANT CONNECT ON DATABASE _tsi_accelerator TO demo;
GRANT ALL PRIVILEGES ON DATABASE _tsi_accelerator TO demo;

# Create TSI Admin
create database _tsi_admin;
CREATE USER demoadmin WITH PASSWORD 'admin@123';
GRANT CONNECT ON DATABASE _tsi_admin TO demoadmin;
GRANT ALL PRIVILEGES ON DATABASE _tsi_admin TO demoadmin;
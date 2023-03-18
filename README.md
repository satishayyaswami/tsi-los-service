## TSI Backend Service

TSI Backend Service, a part of TSI Digital Accelerator, exposes core backend services of a business as REST APIs.

TSI Backend Service is currently under very early stage of development. We invite volunteer software engineers & aspiring developers to join and help us build this project.  

### Getting Started

#### Init setup

- Install <a href="https://www.postgresql.org/download/">PostgreSQL</a> database
- Clone TSI Backend Service into your Projects folder
- Create the _tsi_backend_service database and the demo user by following the instructions in db/setup.sql. Make sure that the same database credential is configured in web/WEB-INF/_config.tsi 
- Install <a href="https://openjdk.org/projects/jdk/17/">Open JDK</a> and <a href="https://ant.apache.org/bindownload.cgi">Apache Ant</a>
- Edit the tsi-backend-service.bat and change the installation directories as appropriate
- Start the TSI Backend Service by running the tsi-backend-service.bat in a cmd window


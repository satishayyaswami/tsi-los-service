## TSI LOS Service

TSI LOS Service, a part of TSI Digital Accelerator, exposes core backend services of a Loan Origination System as REST APIs.

### Getting Started

#### Init setup

- Install <a href="https://www.postgresql.org/download/">PostgreSQL</a> database
- Clone TSI LOS Service into your Projects folder
- Create the _tsi_los_service database and the demo user by following the instructions in db/setup.sql. Make sure that the same database credential is configured in web/WEB-INF/_config.tsi 
- Install <a href="https://openjdk.org/projects/jdk/17/">Open JDK</a> and <a href="https://ant.apache.org/bindownload.cgi">Apache Ant</a>
- Edit the tsi-los-service.bat and change the installation directories as appropriate
- Start the TSI LOS Service by running the tsi-los-service.bat in a cmd window


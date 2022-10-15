## TSI Accelerator

TSI Accelerator is a service wrapper written in Java. While there are several low code frameworks emerging to accelerate digital adoption across organizations, we notice that many application development teams are still reinventing the wheel when it comes to integration with the third party services & implementing standard business processes.

TSI Accelerator which comes as REST supported API server aims to wrap third party APIs (Indian context for now) out of the box. In addition, it aims to come with reference implementations for various commonly used business processes. The developers can feel free to either run TSI Accelerator as separate server or copy design / code and integrate as part of their tech stack. 

### Getting Started

#### Init setup

- Install PostgreSQL database
- Clone TSI Accelerator into your Projects folder
- Create the _tsi_admin database and the demoadmin user by following the instructions in db/admin-setup.sql. Make sure that the same database credential is configured in web/WEB-INF/_admin.tsi 
- Install <a href="https://openjdk.org/projects/jdk/17/">Open JDK</a> and <a href="https://ant.apache.org/bindownload.cgi">Apache Ant</a>
- Edit the tsi.bat and change the installation directories as appropriate
- Start the TSI Accelerator by running the tsi.bat in a cmd window

#### Setup demo tenant

- Set up your business account database. Follow the instructions provided in db/tenant-setup.sql
- Create the demo account. Review \tests\demo-account.json file. Ensure that the db-config credentials matches with the tenant db configuration. Review the Provider API configuration.

```
curl 
-X POST 
-H "Content-Type: application/json"
-d @<<full_path>>/tests/demo-account.json
http://localhost:8080/tsi/system/setup/account

```

- Create the demo API user

### Running Demo Usecases

...
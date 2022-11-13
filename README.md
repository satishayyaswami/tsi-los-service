## TSI Accelerator

TSI Accelerator is an Opensource Enterprise Information Management System (EIM) for Small & Medium Organisations in India. The primary purpose is to enable organisations across diverse sectors such as finance, health, agriculture, manufacturing, skill development and commerce go digital quickly and capture new value from technologies such as mobile, cloud, data engineering, AI/ML etc

TSI Accelerator is currently under very early stage of development. We invite volunteer software engineers & aspiring developers to join and help us build this project.  

### Getting Started

#### Init setup

- Install <a href="https://www.postgresql.org/download/">PostgreSQL</a> database
- Clone TSI Accelerator into your Projects folder
- Create the _tsi_admin database and the demoadmin user by following the instructions in db/admin-setup.sql. Make sure that the same database credential is configured in web/WEB-INF/_admin.tsi 
- Install <a href="https://openjdk.org/projects/jdk/17/">Open JDK</a> and <a href="https://ant.apache.org/bindownload.cgi">Apache Ant</a>
- Edit the tsi.bat and change the installation directories as appropriate
- Start the TSI Accelerator by running the tsi.bat in a cmd window

#### Setup demo tenant

- Set up your business account database. Follow the instructions provided in db/tenant-setup.sql
- Create the demo account. Review /tests/demo-account.json file. Ensure that the db-config credentials matches with the tenant db configuration. Review the Provider API configuration.
```
curl -X POST 
     -H "Content-Type: application/json"
     -d @<<full_path>>/tests/setup/demo-account.json
     http://localhost:8080/tsi/system/setup/account

```
- Create the demo API user. Review /tests/demo-api-user.json file. The account-code should match the account-code used in the demo-account.json.
```
curl -X POST 
     -H "Content-Type: application/json"
     -d @<<full_path>>/tests/setup/demo-api-user.json
     http://localhost:8080/tsi/system/setup/apiuser
```
  The API response will look something like below
```
{"secret":"55c0d6cad6f000b960f4bb324649d6e5","user":"demo@tsiconsulting.in"}
```
  Convert the credential {user}:{secret} into base64 encoded format. The encoded string will look like below
```
ZGVtb0B0c2ljb25zdWx0aW5nLmluOjU1YzBkNmNhZDZmMDAwYjk2MGY0YmIzMjQ2NDlkNmU1
```
### Running Demo Usecases

- <a href="https://github.com/tsiconsulting/tsi-accelerator/tree/main/src/in/tsiconsulting/accelerator/solutions/buildingblocks/kyc">Running KYC Validation Use Case</a>
- <a href="https://github.com/tsiconsulting/tsi-accelerator/tree/main/src/in/tsiconsulting/accelerator/solutions/finance/loan/los">Proof-of-concept Loan Origination System</a>

To know more, join the <a href="https://discord.gg/86HT2VhVzS">discord community</a>
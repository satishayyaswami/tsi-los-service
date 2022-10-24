## TSI Accelerator

TSI Accelerator is a backend service wrapper written in Java. While there are several low code frameworks emerging to accelerate digital adoption across organizations on the frontend, we notice that many application development teams are still reinventing the wheel when it comes to integration with the third party services & implementing standard business processes.

TSI Accelerator aims to wrap several third party APIs out-of-the-box (Indian context for now). In addition, it aims to come with reference implementations for various commonly used business processes across industries. Organizations can include it as part of their tech stack and benefit from faster development & lower engineering costs. Most importantly it lets them deploy their internal technical teams on the core business. Besides the end user organisations, we believe that the software product & services companies will also benefit by including it as part of their solutions.  

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
## Loan Origination System

### Proto

Proto system lets you define LOS workflow using a JSON file with <a href="https://en.wikipedia.org/wiki/Finite-state_machine">states & transitions</a>. The frontend system should trigger the workflow using post-loan-application API which returns the loan-app-id. The frontend system should subsequently call the post-los-activity with the appropriate transitions. The Proto system takes care of the workflow management and supports queries such as getLoanApplications(), getLoanActivities(loan-app-id) etc out-of-the-box.  

#### Define LOS Workflow
Check out tests/proto-get-los-workflow.json. Review the state - transition workflow definitions. Submit the workflow to the LOS system.
```
curl -X POST 
     -H "Content-Type: application/json"
     -H "Authorization:Basic <<base64encodedstring>>" 
     -d @..tests/los/proto-post-los-workflow.json
     http://localhost:8080/tsi/solutions/finance/loan/los/proto
```
#### Create Loan Application
The frontend system should call this API at the time of loan application creation. Checkout proto-post-loan-application.json for how to pass the loan application data to the Proto system.
```
curl -X POST 
     -H "Content-Type: application/json"
     -H "Authorization:Basic <<base64encodedstring>>"
     -d @../tests/los/proto-post-loan-application.json
     http://localhost:8080/tsi/solutions/finance/loan/los/proto
```
#### Get Data Fields
Get Data Fields API helps you get the data fields that needs to be passed for a given transition. Checkout tests/proto-get-data-fields.json for an example on how to call this service.
```
curl -X GET 
     -H "Content-Type: application/json" 
     -H "Authorization:Basic <<base64encodedstring>>" 
     -d @../tests/los/proto-get-data-fields.json
     http://localhost:8080/tsi/solutions/finance/loan/los/proto
```
#### Post LOS Activity
The frontend system should call this API upon every transition on the UI. Checkout tests/proto-post-los-activity.json for an example payload.
```
curl -X POST
     -H "Content-Type: application/json"
     -H "Authorization:Basic <<base64encodedstring>>"
     -d @../tests/los/proto-post-los-activity.json
      http://localhost:8080/tsi/solutions/finance/loan/los/proto
```
#### Get LOS Workflow
The API for retrieving the workflow JSON below
```
curl -X GET 
     -H "Content-Type: application/json"
     -H "Authorization:Basic <<base64encodedstring>>" 
     -d @../tests/los/proto-get-los-workflow.json 
     http://localhost:8080/tsi/solutions/finance/loan/los/proto
```
####  Get LOS Applications
The API for retrieving the loan applications
```
curl -X GET 
     -H "Content-Type: application/json"
     -H "Authorization:Basic <<base64encodedstring>>"
     -d @..tests/los/proto-get-los-applications.json 
     http://localhost:8080/tsi/solutions/finance/loan/los/proto
```
#### Get LOS Activities
The API for retrieving the activity history of a loan application
```
curl -X GET 
     -H "Content-Type: application/json"
     -H "Authorization:Basic <<base64encodedstring>>"
     -d @..tests/los/proto-get-los-activities.json 
     http://localhost:8080/tsi/solutions/finance/loan/los/proto
```




TSI Accelerator - v0.1
--------------------------------------------------------------------------------------------

# Create Account
curl -X POST -H "Content-Type: application/json" -d @C:\work\tsi-accelerator\tests\new-account.json http://localhost:8080/tsi/system/setup/account

# Create New API User
curl -X POST -H "Content-Type: application/json" -d @C:\work\tsi-accelerator\tests\new-api-user.json http://localhost:8080/tsi/system/setup/apiuser

{"api-secret":"72a6ed47e992669ceb79d7c22e826e26","api-key":"6198023d7872b2f8b15e3e8158b73d71"}
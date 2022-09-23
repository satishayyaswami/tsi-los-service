TSI Accelerator - v0.1
--------------------------------------------------------------------------------------------

# Create Account
curl -X POST -H "Content-Type: application/json" -d @C:\work\tsi-accelerator\tests\new-account.json http://localhost:8080/tsi/system/setup/account

# Create New API User
curl -X POST -H "Content-Type: application/json" -d @C:\work\tsi-accelerator\tests\new-api-user.json http://localhost:8080/tsi/system/setup/apiuser

{"api-secret":"967c39ca98c93738eda8edc9db60714c","api-key":"37c818f4a0c3a47d23e7473ebfeaa5dc"}
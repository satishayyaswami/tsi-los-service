## Building Blocks

### Digitap

#### PAN Basic Validation

- Checkout tests/digitap-post-pan-basic-validation.json. Enter the PAN & Name and use the  API below with base64encoded credential for the user.
```
curl -X POST 
     -H "Content-Type: application/json" 
     -H "Authorization:Basic <<base64encodedstring>>"
     -d @..\tests\digitap-post-pan-basic-validation.json
      http://localhost:8080/tsi/solutions/bb/kyc/digitap

```
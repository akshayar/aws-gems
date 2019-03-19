#AWS SAML Integration
* Create an LDAP Account. Use JumpCloud. 
* Add AWS as an application. 
* Generate SSL Certificate and Private Keys

```
openssl genrsa -out private.pem 2048
 openssl req -new -x509 -sha256 -key private.pem -out cert.pem -days 365

```
* Add Private key and certificate to application configuration. 
* Export application metadata. 
* Login to AWS as root or admin user. 
     * Create a SAML Identity Provider with application metadata exported above.
     * Create a SAML role referring to IdP created above.
     * Give the role required permission.
     * Note the ARN of IdP and role created above. 
* Login to LDAP. Modify applicaiton configuration created above. 
     * Add IdP and Role arn. 
* Create users and groups. Give group permission on the application. 
* Login to JumpCloud as user. Click on AWS application configured above. 
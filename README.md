# Keycloak extension to add an expiration date to a user role mapping

This `keycloak` extension adds an expiration date to the existing 
user role mapping. If the expiration date is reached, the mapping
will be automatically removed from the database.

# API

###### [GET] /user-role-expirations/api/users/{userId}/user-role-expirations
Return a list of all client for the realm and its client roles. Additionally
each role has `active` flag and an optional `expirationDate`.

###### [POST] /user-role-expirations/api/users/{userId}/user-role-expirations
Creates or updates the given `UserRoleExpirationDto` for the user.

# Contributing
Take a look into the contribution guidelines.

# License
This code is under the Apache License, Version 2.0, January 2004.
# DigiBrain Server

This is a server created for DigiBrain mobile app implemented using .NET Core 5.

## secrets.json

In order to run this project, a `secrets.json` file has to be created before.

The next `key`:`value` pairs have to be defined there:

  * `DatabaseHost` - the host of the PostgreSQL database.
  * `DatabaseName` - the name of the database created on the server defined at the previous key.
  * `DatabasePassword` - the password of the `postgres` user defined in the PostreSQL server.
  * `JWTSecret` - the JWT secret. It can be anything, but it must have at least 16 characters.

The form of `secrets.json` file is:

```
{
  "DatabaseHost": "HOST",
  "DatabaseName": "NAME",
  "DatabasePassword": "PASSWORD",
  "JWTSecret": "SECRET"
}
```

## firebase.json

In order to push notifications to mobile users, the project is using Firebase SCM and it needs a file with a private key.

This file is generated from Firebase Console and it is renamed to `firebase.json`.
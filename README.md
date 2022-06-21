# JSON validation service

### A REST-service for validating JSON documents against JSON Schemas

### API Specification:

The primary interface of application is REST (JSON over HTTP).

#### Endpoints
```
POST    /schema/SCHEMAID        - Upload a JSON Schema with unique `SCHEMAID`
GET     /schema/SCHEMAID        - Download a JSON Schema with unique `SCHEMAID`

POST    /validate/SCHEMAID      - Validate a JSON document against the JSON Schema identified by `SCHEMAID`
```

#### Responses

Valid JSON Schema Upload
```
{
"action": "uploadSchema",
"id": "config-schema",
"status": "success"
}
```
Invalid JSON Schema Upload
```
{
"action": "uploadSchema",
"id": "config-schema",
"status": "error",
"message": "Invalid JSON"
}
```

### DB:

Since we need to have a db running to store all the jsonSchemas, use the following to create a mongo db container on docker:

```
docker run -d
-p 27017:27017
--name testdb
-v jschemas:/data/db
mongo:latest
```

### Server:

To start the server you just need to run the following command on the root of the project:

```
$ sbt run
```
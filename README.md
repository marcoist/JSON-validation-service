Since we need to have a db running to store all the jsonSchemas, use the following to create a mongo db container on docker:

```
docker run -d
-p 27017:27017
--name testdb
-v jschemas:/data/db
mongo:latest
```
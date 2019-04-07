#### Run Via Compose
```$xslt
docker pull abhinavrungta/mirror-coding-challenge
docker-compose up
```

#### Run Locally via SBT
```$xslt
./sbt dist
./sbt run
```

#### Docker Build and Push
Run via Docker
```$xslt
./sbt run
docker build -t abhinavrungta/mirror-coding-challenge .
docker push abhinavrungta/mirror-coding-challenge
docker run -it -p 9000:9000 abhinavrungta/mirror-coding-challenge
```

### Endpoints

#### Login User
```$xslt
curl -X POST \
  http://localhost:9000/v1/login \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "user1",
    "password": "pwd"
}'
```
Response
```$xslt
{
    "access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJNaXJyb3IiLCJpZCI6InVzZXIxIiwiZXhwIjoxNTU0NjY5MTEzfQ.dJq12SqoHhYvlV__7f8xJEsLYAf-my6deWbWz3moFI4",
    "publishkey": "pub-c-a9d8d19a-bc6b-4b23-b225-5b4df4c34b8d",
    "subscribekey": "sub-c-7f222f76-594c-11e9-be9b-e284dc0944ea",
    "uuid": "user1",
    "channel": "user:user1",
    "nextstep": "in the client, subscribe to channel user:uuid\n Listen for join events to subscribe new channels or listen for incoming direct messages and display it accordingly based on room id."
}
```
- Client should subscribe to channel (user:user1) after login and register a listener.
- All subsequent requests to backend endpoint must be authenticated with given token. 

#### List Users
```
curl -X GET \
  http://localhost:9000/v1/listUsers \
  -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJNaXJyb3IiLCJpZCI6ImFiaGkiLCJleHAiOjE1NTQ2NTkwODV9.5AJzocx2U4loMXZ3sOIoSUeI8YQYysq8VtbLTbP6Zw4'
```

#### List Groups
```
curl -X GET \
  http://localhost:9000/v1/user1/listGroups \
  -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJNaXJyb3IiLCJpZCI6ImFiaGkiLCJleHAiOjE1NTQ2NTkwODV9.5AJzocx2U4loMXZ3sOIoSUeI8YQYysq8VtbLTbP6Zw4'
```

#### Create Group
```
curl -X POST \
  http://localhost:9000/v1/user1/createGroup \
  -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJNaXJyb3IiLCJpZCI6ImFiaGkiLCJleHAiOjE1NTQ2NjAxMTl9.TKHQkiO8aoIHKLy2Z8VqfTFxtX4xGAGa666YXAGCmw8' \
  -H 'Content-Type: application/json' \
  -d '{
    "groupName": "grpName",
    "users": [
        "user1",
        "user2",
        "user3"
    ]
}'
```

When executed backend sends a special event to the inbound channel (user:uuid) of users in the group
```$xslt
{  
   "eventType":"join_group",
   "channel":"grp:grpName"
}
```
Client subscribes to channel `grp:grpName` on receiving the event.

#### Sending Message to a user1 from user2
channel `user:user1`
```$xslt
{
    message_id: 10001,
    chat_id: "user1:user2", // lexographical ordering.
    author: "user2",
    content: "What are you doing this weekend?",
    timestamp: 1425244035
}
```

#### Sending Message to a grp1 from user2
channel `grp:grp1`
```$xslt
{
    message_id: 10001,
    chat_id: "grp1", // lexographical ordering.
    author: "user2",
    content: "What are you doing this weekend?",
    timestamp: 1425244035
}
```
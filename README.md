# Corporate messenger REST API (Spring Boot based) 

Supported options:

- traffic encryption (https)

- creating/editing users
 
- users' passwords hashing (BCrypt used)

- JSON Web Token authorization (session&refresh tokens pair)

- creating new chats, editing chats' membership

- saving all ever sent messages into the server database (PostgreSQL was used)

- getting messages by request, supported timestamp relation (before/after)


## Examples

### Create user
 
#### Request

Type: `POST`

Endpoint: `/api/v1/auth/sign-up`

Header: `Content-Type: application/json`

```json
{
    "username":"someUser",
    "password":"somePassword"
}
```

#### Response
```json
{
    "sessionToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzb21lVXNlcjEiLCJyZWZyZXNoIjpmYWxzZSwiaWF0IjoxNTk0MDA5NDQzLCJleHAiOjE1OTQzMDk0NDN9.8TcVDIIKHdpmqTWIQ7MmSToQtH7sKCa_BM9vVpqKAnk",
    "sessionExpiresOn": 1594309443000,
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzb21lVXNlcjEiLCJyZWZyZXNoIjp0cnVlLCJpYXQiOjE1OTQwMDk0NDMsImV4cCI6MTU5NDAxMTI0M30.D4xVTpqfwCrhMROOpqMx8RJgAsl_-7nGAmMgy3s-0L0",
    "refreshExpiresOn": 1594011243000,
    "username": "someUser"
}
```

### Get chat messages
  
#### Request

Type: `PUT` (using PUT here)

Endpoint: `/api/v1/messenger/messages`

Header (shorten): `Authorization: Bearer eyJhbGciOiJIUzI1.....wH3cXBHhWAWwe-xxkPdutwaHqo`

Header: `Content-Type: application/json`

```json
{
    "action":"GET_MESSAGES_BEFORE_TIMESTAMP",
    "chatName":"someChat",
    "timestamp":1694007343000
}
```

#### Response
```json
{
    "chatName": "someChat",
    "messages": [
        {
            "author": "someUser",
            "content": "how are you?",
            "timestamp": 1594007079000
        },
        {
            "author": "someOtherUser",
            "content": "how are you ? (someOtherUser)",
            "timestamp": 1594007636000
        }
    ]
}
```

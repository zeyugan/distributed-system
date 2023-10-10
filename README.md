# distributed-system

In this project, you are asked to design and implement a system for remote file
access based on client-server architecture. The files are stored on the local disk of
the server. The server program implements a set of services for clients to remotely
access the files. Meanwhile, the client program provides an interface for users to
invoke these services. On receiving a request input from the user, the client sends
the request to the server. The server performs the requested service and returns the
result to the client. The client then presents the result on the console to the user. The
client-server communication is carried out using UDP.

# requirements

## client

- implement an in-memory cache for read service, and do not call server if the exact parameter has been called before.
- specify cache TTL on start
- implement calls to the 3 services
- implement at-least once and at-most once semantics

## server

- Service 1: read a file by the following parameters: file name, offset, and number of bytes to read.
- Service 2: insert byte(s) into a file by the following parameters: file name, offset, and byte stream to insert.
- Service 3: allow client to regsiter for update for a file by passing in the filename and duration of subscription.
- Service 4: generate uuid for client to use for at-least-once and at-most-once semantics

## report

- design request and response format
-

# milestones

**DEADLINE October 23, 2023 (Monday)**

## stage 1: MVP 01/10/2023

- [x] Client to Server local connectivity (JW)
- [x] Implement Service 1 with external library (ZY)
- [x] Implement Service 2 with external library (ZY)
- [x] Implement Service 3 with external library (JW)
- [x] Implement client call to service 1 (ZH)
- [x] Implement client call to service 2 (ZH)
- [x] Implement client call to service 3 (ZH)
- [x] Implement client side cache (ZH)

## stage 2: Implementation 08/10/2023

- [x] Implement Service 1 without external library
- [x] Implement Service 2 without external library
- [ ] Implement Service 3 without external library
- [ ] Implement UUID generation
- [ ] Write service check UUID
- [x] Write service check cache
- [ ] Finalize request and response format
- [ ] Implement at-least-once semantics
- [ ] Implement at-most-once semantics
- [ ] Client to Server remote connectivity

## stage 3: Final Product 15/10/2023


# design

## request format

| Operation    | UUID        | Offset    | Length    | Content       |
|--------------|-----------|-----------|-----------|---------------|
| (1 byte)     | (8 bytes) | (4 bytes) | (4 bytes) | Variable Size |
| R/W/S/I Char | String    | Integer   | Integer   | String        |

- for read operation, content is only the filepath
- for write operation, content is the <filepath|data> string separated by `|`
- e.g. write "W 00000000 \<offset> 0000 /server/test.txt|hello world"
- e.g. subscrption "S 0000000 0000 \<duration millisecond> \<filepath>"
- e.g. read "R \<snowflake UUID> \<offset> \<length> /server/test.txt"
- e.g. request UUID "I 00000000 0000 0000 /server/test.txt"
- e.g. request file modified time "T 00000000 0000 0000 /server/test.txt"

client local byte order -> network byte order htonl
network byte order -> system local byte order ntohl


## response format
| Code     | Message       |
|----------|---------------|
| (4 byte) | Variable size |
| Integer  | String        |

- e.g. response "0 hello world"
- e.g. response "0 xxxx-xxxx-xxxx-xxxx"


# Usage
## Client
### Quick Start
Client has been complied to executables in folder `cmd`, you can simply run it:
```shell
./cmd/client_macos
```

Client provide multiple executable for different platforms:
| Platform    | Executable     |
|-------------|----------------|
| Linux x64   | client_linux64 |
| Linux x32   | client_linux32 |
| Mac OS      | client_macos   |
| Windows x64 | client_win64   |
| Windows x32 | client_win32   |


### Optional Command Line Arguments
You can initalize the client with command line arguments below:

|Parameter | Description                 | Default Value |
|----------|-----------------------------|---------------|
| `debug`  | Whether show debug message  | `true`
| `ip`     | IP address of server        | `10.0.0.2`
| `p`      | Port of server              | `12345`
| `type`   | Request type                | `idempotence`
| `t`      | Freshness interval of cache | `30`(second)


Here is a example:
```shell
# This command allows you to customize the server info
./cmd/client_macos -ip 192.168.1.1 -p 6666
```

Note: if you set `type` to any other value expect `idempotence`, the request will not be idempotent. For example:
```shell
# Those command are idempotent
./cmd/client_macos -type idempotence
./cmd/client_macos

# Those command are not idempotent
./cmd/client_macos -type non-idempotence
./cmd/client_macos -type doge
./cmd/client_macos -type I-love-distrubuted-system
```

### Debug
Note: debug mode is ON by default, you can turn it off manually by setting the debug parameter to `false`

When debug mode is on, debug message will be shown on terminal while using, providing debug information speacially the `raw request bytes` and `raw respond bytes`.

A generic message is liked:
```shell
### debug msg
### funtion: main.register
### request bytes: [83 0 0 0 0 88 27 0 0 100 111 103 101]
```

The 2nd column shows the `funtion name` of the request caller.\
The 3rd column shows the `raw request bytes`, each byte is shown as the `ASCII decimal format`

Here is the comparison between the example and its raw content before marshalling:

```shell
### request bytes: [83 0 0 0 0 88 27 0 0 100 111 103 101]

### raw content before marshalling
option: 'S'
UUID:   ""
offset: 0
length: 7
content: doge
```

Through debug mode, we can check if the bytes after marshalling meet our expection.
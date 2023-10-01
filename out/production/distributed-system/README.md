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

## report
- design request and response format
- 

# milestones
**DEADLINE October 23, 2023 (Monday)**

## stage 1: MVP 01/10/2023
- [ ] Client to Server local connectivity (JW)
- [ ] Implement Service 1 with external library (ZY)
- [ ] Implement Service 2 with external library (ZY)
- [ ] Implement Service 3 with external library (JW)
- [ ] Implement client call to service 1 (ZH)
- [ ] Implement client call to service 2 (ZH)
- [ ] Implement client call to service 3 (ZH)
- [ ] Implement client side cache (ZH)

## stage 2: Implementation 08/10/2023
- [ ] Implement Service 1 without external library
- [ ] Implement Service 2 without external library
- [ ] Implement Service 3 without external library
- [ ] Finalize request and response format
- [ ] Implement at-least-once semantics
- [ ] Implement at-most-once semantics
- [ ] Client to Server remote connectivity

## stage 3: Final Product 15/10/2023
- [ ] Oracle
- [ ] MySQL
- [ ] Microsoft SQL Server



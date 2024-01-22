# MTCG

This repository is for the 3rd semester project "MonsterTradingCardGame" for software-engineering 1.

## Setup

To run the project, modify the connection string in "connection_string.txt" to allow access to the database. The database structure can be recreated with the commands found in setup.sql

## Structure

The project consists of two main parts, the HTTP-server, and the actual application. Additionally, there are the database helper functions

### HTTP Server

The Server manages routing and resolving of requests, and sends them to the specified services.

### DB

The db part is an attempt to simplify database access, but due to some more complex calls this hasn't worked out completely. It still makes the code more readable, and abstracts SQL statements to enums.

### MTCG

The last part, the application itself, mostly consists of the Service classes, implementing the Service interface, and the corresponding main classes, which for the most part just interface with the database functions. There are two relevant exceptions: The battle class (and helpers), and the session handler:

Sessions are one of the few things, that isn't stored in the database, but in memory. Everytime a user makes a request that has to be authenticated, the session handler looks it up in its map, and compares the requirements (admin, username).

The battle handler is responsible for matching players, and creating a new battle. As multiple users might try to start a battle simultaneously, it needs to be thread-safe, and run the actual battles asynchronously. It implements a queue system, and creates a separate battle instance per two players. Since every new request also creates a new thread, the battle handler only needs to be thread-safe, and only one player runs the actual battle for it to be multithreading. Both players then read from their shared battle class once the battle is complete to retrieve the log.

## Potential improvements

- Currently, the exact REST path is discarded after the router resolves it, as multiple functions share the same service class. This could be alleviated by either creating a separate service per route, which however would result in about 20 services, or otherwise parsing the route, which may defeat the point of the service interface, depending on implementation. This inefficiency also results in another issue, the service implementations have quite cluttered switches.

- As previously mentioned, the db functions are not very complete, and might not improve db access, as some hacks and manually adapting the query is required in quite a few instances. For statements which are fully supported though, it simplifies db calls quite a bit. Depending on IDE the whole system might be obsolete though.

## Unit tests

There are two parts of the project which are tested through unit tests: the router and the fight logic. The router is relevant because it allows most other code to rely on different forms of testing, as the router is guaranteed to be working, the fight logic is relevant to test as other testing methods are not precise enough to map an error to a specific part of the code. For other methods integration tests are easier to integrate, and server the same purpose, assuming the database and router are working correctly (hence the unit tests for the router).

## Lessons learned

- Starting with the HTTP server made it quite easy testing the application with integration tests (manual using postman or automated using the provided curl script).
- A separate db helper class would probably make sense for larger, more complex projects, but was overkill here.

## Unique feature

My unique feature is the ability to crit: Whenever two monsters fight (i.e. no spells involved), they can critically strike for an increase in damage of 50%. Each card has a crit modifier, default to 0.

## Further notes

The curl script was modified to automatically store the authentication tokens in variables.

Time tracking was added directly to the commits for a total of about 60 hours of work; commits without any time are less than 5 minutes.

https://github.com/Nanogamer7/swen-mtcg/
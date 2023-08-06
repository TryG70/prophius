# PROPHIUS ASSESSMENT

### Introduction

Task: Build a Social Media API.

---

### Task description

Requirements

● Use Spring Boot to create the API.
● Design entities for "User," "Post," and "Comment" with appropriate
relationships (e.g., one-to-many, many-to-many).
● Implement CRUD operations for users, posts, and comments.
● Each user should have the following properties:
username, email, profile picture, and a list of followers/following relationships.
● Each post should have the following properties:
content, creation date, likes count, and the user who created it.
● Users should be able to
follow/unfollow other users (1),
like and comment on posts (2).
(1) Should be implemented as the base version and
(2) should be in a newer version and we should be able to undo the feature
(3) if needed. Use proper github structure (e.g. tags, branches)

● Write unit tests to ensure the correctness of your API endpoints.
● Implement user authentication and authorization using Spring Security and
JWT tokens.
● Use an appropriate database of your choice (e.g., H2, MySQL, PostgreSQL)
to store the data.

Bonus (optional):

● Add pagination and sorting for listing posts and comments.
● Implement notifications for post likes and comments.
● Include search and filter capabilities for posts and users.
● Dockerize your application for easy deployment.


### Technologies

- Java
- Maven
- PostgreSQL
- Spring Security
- Spring Boot
- Spring Data JPA

### Requirements

You need the following to build and run the application:

- [JDK 17](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [Maven 3.8.6](https://maven.apache.org) (This is the build tool used.)


## How to run Application(s)
### step 1 - clone project with Terminal from [here](https://github.com/TryG70/ClusteredData-Warehouse)

```
git clone git@github.com:TryG70/ClusteredData-Warehouse.git
```

### step 2 - move into the project directory
```
cd prophius-assessment/
```

### step 3 - Open the clusteredData-wareHouse Folder in an IDE, As a maven Project.

```
mvn spring-boot:run
```


### step 6 - Generate the .jar files with Terminal

```
mvn clean install 
```
OR
```
./mvnw clean install
```


## PostMan Collection for Integration Tests.
- trygod-prophius [here](https://api.postman.com/collections/22955162-bdbe8c82-763f-4082-95c4-ff92f0319d8b?access_key=PMAT-01H75Q70C629YHESBXE478VTYR)


## Running The Tests with Maven

To run the tests with maven, you would need to run the maven command for testing, after the code has been compiled.
```
mvn <option> test
```

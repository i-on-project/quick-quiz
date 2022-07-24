# Quick-Quiz

## What is it?

Quick-Quiz is an aplication where an Organizer can setup a quiz session and share quizzes, and where participants can answer anonymously.

This project consists in a system which is comprised of a web application and a HTTP API.

## Requirements

> ### Requirements to build the system

- SDK Java 11 or above
- Gradle 5.0 or above

> ### Requirements to execute the system

- [Local instalation of MongoDB](https://www.mongodb.com/try/download/community) or [MongoDB Atlas account](https://www.mongodb.com/)

- [API Key for SendGrid](https://app.sendgrid.com/) (Optional)

> ### Environment Variables

- **MONGODB** Example: mongodb://localhost:27017

- **SENDGRID_API_KEY** Api key generated in sendgrid service (Optional)

- **QQ_HOST**  used for authenticatoin/registration link generation and websocket AllowedOriginPatterns (defaults to *<http://localhost:8080>* when not set)

> #### How to

On the command line run the following comands:

1. *gradlew build* or *gradlew build -x test* (if you want to skip the tests)

2. *gradlew bootRun* to run the application

3. If default the defaultconfiguration is ketp, navigate to *<http://localhost:8080>*

## Notes

For details about the HTTP API you can consult the OpenApi documentation in Docs.

To access a working example of the application on-line please go to <https://i-on-quickquiz.herokuapp.com/>.

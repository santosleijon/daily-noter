# daily-noter

**daily-noter** is a web application for writing daily notes as fast and easy as possible.

**Disclaimer:** This application is built as a technical exercise and is not meant for production use.

## Roadmap / future features:
- [x] Pagination
- [ ] Calendar date picker
- [ ] Search function
- [ ] OCR image recognization: Upload a photo of a hand written note
- [ ] AI summaries of notes

## Technology stack

**Backend:**
* [PostgreSQL](https://www.postgresql.org/) - The world's most advanced open source relational database.
* [Java 21](https://dev.java/) - The latest LTS release of Java at the time of writing.
* [Javalin](https://javalin.io/) - Lightweight web framework built on top of Jetty.
* [Guice](https://github.com/google/guice) - Lightweight dependency injection framework.
* [Jackson](https://github.com/FasterXML/jackson) - JSON parser for Java.
* [Password4j](https://password4j.com/) -  Java fluent cryptographic library used for password hashing with the argon2 algorithm.
* [JUnit Jupiter](https://junit.org/junit5/) - Testing framework for unit tests and integration tests (part of JUnit 5).
* [Mockito](https://site.mockito.org/) - Framework for writing mocks in automated tests, mainly mocks of database integration classes in this project.
* [AssertJ](https://github.com/assertj/assertj) - Provides a rich set of test assertion methods that read like natural language.

**Frontend:**
* [TypeScript](https://www.typescriptlang.org/) - Strongly typed programming language built on JavaScript.
* [React](https://react.dev/) - JavaScript library for building web apps using reusable components.
* [Vite](https://vite.dev/) - A fast build tool for development servers and production build.
* [Tailwind](https://tailwindcss.com/) - A CSS framework that allows styling of HTML elements using predefined classes. 

**Deployment:**
* [Hetzner](https://www.hetzner.com/cloud/) - European cloud service provider with affordable virtual private server (VPS).
* [NGINX](https://nginx.org/) - Used as a reverse proxy to route DNS names to different services exposed on different ports locally.
* [Docker/OCI containers with Podman](https://podman.io/) - Used to build and run this application as containerized services.

![Production deployment](https://github.com/santosleijon/daily-noter/blob/main/docs/deployment.png?raw=true)

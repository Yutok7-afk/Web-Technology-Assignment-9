# Scalable Real-Time Cricket Score Management System

A Spring Boot + MySQL web application for creating matches, managing teams and players, recording ball-by-ball events, calculating batting/bowling statistics, and displaying a responsive live dashboard.

## Technology
- Java 26
- Spring Boot 4.1.0
- Spring Web
- Spring Data JPA / Hibernate
- MySQL
- HTML, CSS and JavaScript
- Maven

## Run
1. Create the database using `database.sql` or let the JDBC URL create it automatically.
2. Edit `src/main/resources/application.properties` and replace `YOUR_MYSQL_PASSWORD` with the MySQL password.
3. Open a terminal in the project directory.
4. Run `mvn spring-boot:run`.
5. Open `http://localhost:8080/`.

The first run inserts demo teams, players and two live matches automatically when the teams table is empty.

## REST endpoints
GET  /api/health
GET  /api/teams
POST /api/teams
GET  /api/players
GET  /api/players?teamId=1
POST /api/players
GET  /api/matches
GET  /api/matches/{id}
POST /api/matches
PUT  /api/matches/{id}/status
POST /api/matches/{id}/events
GET  /api/matches/{id}/dashboard

## Sample event JSON
{
  "batsmanId": 1,
  "bowlerId": 6,
  "type": "FOUR",
  "runs": 0,
  "note": "Cover drive"
}

Supported event types: DOT, RUN, FOUR, SIX, WICKET, WIDE, NO_BALL.

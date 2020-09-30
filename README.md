# Project page with advices
Project of web page where u can:
- ask and answer questions
- rate answers 
- get points for good answers and popular questions
- read random facts
- get back a forgotten password with help of mail service
- register new users with avatars
- see ranking of top 100 users
- see newest and most popular five questions
- create your own categories
- as an admin delete users, categories or other creations of normall users
# How to run it
U must set up application.properties (path: src/main/resources) for your datasource(if u use postgres u only need to change spring.datasource.url, spring.datasource.username, spring.datasource.password).

Change ajax's url to http://localhost:8080/random-fact if u want random facts to work while not on heroku.

Now u can start application but without setting up in application.properties spring.mail.username and spring.mail.password mail service won't work which will make sending mails with links refreshing forgotten passwords impossible.
# Admin login and password
login: adminCodeA$32z@aaQ6
password: h$a5sZs3^^
# Used technologies
- Java 11
- SpringBoot
- Thymeleaf
- HTML
- CSS
- JavaScript
- jQuery
- PostgreSQL
- H2

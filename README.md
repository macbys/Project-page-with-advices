# Project page with advices
Project of web page where u can ask and answer questions.
# Possible future improvements
- consuming some rest api 
- make profile pages for other users
- make admin which could delete other users and their creations
# How to run it
U must set up application.properties (path: src/main/resources) for your datasource(if u use postgres u only need to change spring.datasource.url, spring.datasource.username, spring.datasource.password).

Now u can start application but without setting up in application.properties spring.mail.username and spring.mail.password mail service won't work which will make sending mails with link refreshing forgotten passwords impossible.

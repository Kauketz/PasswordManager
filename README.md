# About

I am a student in Cyber Security, and this is my basic password manager project.
Technologies used: Java (openjdk 23.0.1 2024-10-15), JavaFX, PostgreSQL 17

### WARNING: You should NOT use this as a real password manager. There are much better and more secure options out there, such as [BitWarden](https://github.com/bitwarden)

# The purpose of this project

There are 4 reasons for why I wanted to make this project:

1. Improve upon an earlier project.
   I had actually written code for a password manager a year ago. But that one was really poor and I wanted to improve the project quality and my coding abilities.
2. Learn Java
   I haven't used a lot of Java since I learned it for the first time a year ago. I wanted to brush up my skills, as well as improve and learn new things about the language.
3. Database and SQL
   I wanted to learn how I could connect my code to a PostgreSQL database and use SQL in Java to pass queries to insert as well as retrieve data.
4. Implement basic cryptographic methods
   The project uses cryptographic theory to hash and encrypt password. The first version of my password manager used a basic caesar cipher to encrypt not only the master user, but also the service logins.
   This implementation is a vast improvement to that.

# How it works

Before using the password manager itself, you have to set up the database. Using pgAdmin from having installed Postgres, you can create a database and the neccessary tables. I will get back to this later.
When launching you can log in, register, exit the application or be redirected to the github page. As a new user you must register, which creates a new master user in the "master" table of the database.
After having logged in you are confronted with the main page, which gives you three main options:

- Add new login
- Open generator
- Open vault
  The generator only generates a password based on user preferance.
  The vault opens a pop up with the list of usernames and passwords.

There is one more button here, and that is to close the master account. It deletes the master user and all its logins.

# My experiences with this project

This project was very fun to make, especially as it slowly gained more and more functionality. One of the more frustrating aspects of the development process was using SceneBuilder and creating the .fxml files.
I had problems with the program not working, and images now loading properly.

For the coding part I had trouble with connecting to the database. But once this worked, I felt like the hardest part was over.
I felt like I learned a lot, not only Java but also about developing an app. You need to properly plan ahead so that you don't need to erease a lot of code later on when you decide to change something (for example when I decided to change everything about a stage and had to rewrite a lot of code).

# Areas of improvement

As of now, the user has to create the database themselves manually with its tables. The creation of the tables I believe can be automated in Java, but I am not so sure about the creation of the entire database itself. I may need to do some more research about this to see if it is possible.

I only realized this when I was close to finishing the project, but it would make more sence to have a button for the generator in the "new log in" page, instead of on the main page. This is an example of poor planning that I was talking about. Note to self: make sure you have a complete vision of the application and user interface and NOT simply freestyle it as you are developing.

Security aspects. The way the code works is very odd when it comes to security. It takes in the master password when logging in, and keeps passing it along to different classes where it may be needed to encrypt/decrypt. While this works, it is very awkward. There is definetly some potential for improvement here. Also the master password isn't encrypted, only hashed. I could also encrypt it for added security.

# Images used:

- [Lock](https://www.flaticon.com/free-icon/password-manager_2471610?term=password+manager&page=1&position=45&origin=tag&related_id=2471610)
- [App icon](https://www.flaticon.com/free-icon/key_10385952?term=password+manager&page=1&position=41&origin=tag&related_id=10385952)
- [Sync](https://www.flaticon.com/free-icon/sync_3031712?term=refresh&page=1&position=16&origin=search&related_id=3031712)

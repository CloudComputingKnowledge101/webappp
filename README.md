# webappp  

# Prequisites to build this webapp  

install java version 17  

maven version 4.0.0  

This application is built on Spring tool Suite-4  

Database used is MySQL  

Version control carried out by Git  

Test tool by Postman  


## Build Instructions  

Clone this repository in your local repository and check your java version  

Use maven to build the project using "$ mvn clean install"  

This server will run at http://localhost:8080/, test can be done using Postman.  

MySQL port is default 3306.  

Server: server side as RESTful architectural style.  

As a default, it is listening at http://localhost:8080/  


## following RESTful APIs are implemented:  

GET   
PUT  
POST  
DELETE  

## Creating AMI IDs:   
Packer is utilized to build Amazon Machine Images (AMIs) tailored specifically for the Spring Boot application.  
By defining a Packer template, the application environment—including necessary dependencies, configurations, and optimizations—can be packaged into an AMI.  
This ensures that each deployment is consistent, reducing configuration drift and enhancing reliability.  

## Automating Deployment:  
GitHub Actions are set up to automatically trigger deployment processes whenever changes are pushed to the repository.  
This includes building the Spring Boot application, running tests, and deploying the application to AWS using the custom AMIs created by Packer.  




https://spring.io/guides/tutorials/rest/

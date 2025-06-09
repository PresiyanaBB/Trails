# Welcome to the backend part of Trails 👋

## Description

Trails is a personal project that showcases various locations across Bulgaria where artists have created public artworks, such as graffiti and mosaics. The project was developed to encourage artists to contribute to making their cities more vibrant and enjoyable places to live.

People feel better when they see beautiful artwork on building walls, rather than the typical, monotonous facades reminiscent of the communist era. This is precisely our mission — to inspire positive emotions and attract visitors to experience the beauty of our amazing country.

This is the backend of Trails. Here, you can explore the code behind all the magic.

# Get started

## Backend - Trails

1. create in /src/main/resources/secret.properties
    - add the following according to you:
    ```
    spring.datasource.url=
    spring.datasource.username=
    spring.datasource.password=
    ```
2. Must have sdk 23.02
3. Run TrailApplication - it runs on http://localhost:8080

## Frontend - Trails-UI

1. run `npm install`
2. run `npm run dev`

###### The frontend must run on http://localhost:5500 , if you use another port in the backend (Trails) in trails/clients/CorsConfig you must set your port

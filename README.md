# API El Nacional
API RESTful para la obtención de resultados de búsqueda de la página web de [El Nacional](https://elnacional.com.py).
Esta web fue seleccionada por cumplir con las características requeridas para esta API, aunque ocasionalmente presenta tiempos de respuesta elevados.

## Tabla de Contenidos
1. [Datos Generales](#datos-generales)
2. [Inicialización](#inicialización)
3. [Servicios](#servicios)
4. [Ejemplos de Búsquedas](#ejemplos-de-búsquedas)

## Datos Generales
- **Spring Boot:** 3.4.1
- **Java:** 21
- **Model version:** 4.0.0
- **Versión de Wildfly:** 35.0.0.Final
- **Tipo de Artefacto:** War
- **Componentes Necesarios:** Maven
- **Base de datos:** MySQL
- **Puerto donde se debe exponer la app:** Localmente en el puerto 8090

## Inicialización

Para inicializar la aplicación localmente, primero debes crear un esquema llamado "elnacional_api_bd", en caso de utilizar el JDNI para acceder al datasource de la base de datos, el nombre del JDNI es "elNacionalApiDS"
Para generar el .war, modificar la main Application para que esté con el perfil "PROD", si se ejecuta en desarrollo el perfil debe estar en "DEV" (Comentar la no utilizada), las lineas se ven de la siguiente forma:
springApplication.setAdditionalProfiles("PROD");
springApplication.setAdditionalProfiles("DEV");

A continuación, necesitas ejecutar el siguiente script para crear las tabla requerida:

DROP TABLE IF EXISTS `parametros`;

CREATE TABLE `parametros` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) DEFAULT NULL,
  `valor` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

Luego ingresar los siguientes datos a la tabla:
Como nombre API_KEY y como valor por ejemplo e7x9k2p4m5n8q3r1
Como nombre SECRET_KEY y como valor vT#mK9$nP4@jL6*wQ8
Como nombre URL_WEB y como valor https://elnacional.com.py/

La tabla se vería de esta forma:

![image](https://github.com/user-attachments/assets/e88bc140-8d2b-4552-b67c-e9f09e745c0f)

## Servicios

- GET /consulta/generar-firma para obtener la firma HmacSHA256
- GET /consulta/buscar-noticias para buscar los resultados de la búsqueda de noticias
  1- Parametros
    a) El parámetro "q" para ingresar los valores de busqueda
    b) El parametro "f" para ingresar el valor true o false para determinar si se van a obtener los valores de contenidoFoto en base64 y contentTypeFoto
  2- Headers
    a) Ingresar la key "firma" con el valor obtenido con el servicio de obtener la firma, por ejemplo x36mvR/6Oim7R5yZ5Y4lBGvJYBd9H7JZqEExIIgjiyI=
    b) Ingresar la key "apiKey" con el valor de la api key agregada en la tabla, por ejemplo e7x9k2p4m5n8q3r1
    c) Ingresar la key "Accept" para ingresar el tipo de resultado, por ejemplo application/json, application/xml, text/plain, text/html

Un ejemplo de como se deberia ver la request es:

![image](https://github.com/user-attachments/assets/68cc8521-3da3-41ce-8ad0-6348f3f62430)

Este sería un resultado de la request de generación de firma:
![image](https://github.com/user-attachments/assets/e9d3729a-7858-404d-9217-e6fc4ce36af0)

Para acceder a los servicios desde swagger:
- /swagger-ui/index.html
Ambos servicios se encuentran de esta forma:
![image](https://github.com/user-attachments/assets/2eda4957-9877-4846-a170-41ca1d025f8a)

## Ejemplos de Búsquedas

https://github.com/user-attachments/assets/ae6ca601-c064-41ec-acb3-a3e3579244bd

https://github.com/user-attachments/assets/8de0f2d0-5ccf-4f1e-9344-f3bda37d6e8c

Petición con imágenes en base64:
https://github.com/user-attachments/assets/1c456dc4-feaa-4265-97a2-8fc4ae252aae

Decodificación de base64:
https://github.com/user-attachments/assets/98ae841c-18f9-4b13-b7c2-8511124a2ccc









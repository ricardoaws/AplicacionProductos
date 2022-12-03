#Utilizamos la imagen de openjdk8 para ejecutar el microservicio
FROM adoptopenjdk/openjdk8
MAINTAINER David Gomez <dgm83@uoc.es>
#Establecemos la carpeta app como la carpeta raiz donde se construirá el microservicio dentro de la imagen del contenedor
WORKDIR /app

#Copiamos el contenido de la carpeta .mvn dentro de la carpeta app/.mvn de la imagen del contenedor
COPY .mvn/ .mvn
#Copiamos los ficheros mvnw y pom.xml dentro de la carpeta app de la imagen del contenedor
COPY mvnw pom.xml ./
#Lanzamos el comando dependency:go-offline en la imagen del contenedor para asegurar que tiene todas las dependencias
#instaladas
RUN ./mvnw dependency:go-offline
#Copiamos el codigo del microservicio myidp dentro de la carpeta app dentro de la imagen del contenedor
COPY src ./src
#Ejecutamos mvnw spring-boot:run para compilar la aplicación (microservicio)
CMD ["./mvnw", "spring-boot:run"]

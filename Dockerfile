FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/rumahsewa141.jar /rumahsewa141/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/rumahsewa141/app.jar"]

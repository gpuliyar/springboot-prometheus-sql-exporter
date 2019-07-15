# prometheus-sql-exporter

> Important Note: First create a folder `data` inside the prometheus folder. Give `777` permission to the `data` folder before you start the below set of commands

## How to run?
```
docker-compose up --build -d
```

## Check the port as random ports assigned to the Container
The docker-compose exposes ephemeral host port to avoid network conflicts. To find the host port of the container, use the below command
```
docker ps
```

## How to update?
> Let's say that you did some fix to the exporter functionality and you would like to test it by deploying the exporter service again. You don't want to disturb other services. In that case:
```
docker-compose up --build -d --no-deps exporter
```

## Cleanup
### How to stop all the services defined in the docker-compose.yml file
> The below command will stop the containers but the containers are still in your host.
```
docker-compose stop
```

### How to remove all the services defined in the docker-compose.yml file
> The eblow command will remove the containers from your host. Do not worry, it will not remove any other containers that are not part of the docker-compose.yml file
```
yes | docker-compose rm
```

### If you want to remove a specific service that is part of your docker-compose.yml file
> It will remove only that service which you mentioned as the input parameter
```
yes | docker compose rm <service-name>
```

### More cleanup that you can do. DO NOT ATTEMPT the below commands if you do not understand the purpose and intent of it.
> The below command will remove all dangling volumes in your host. Important Note: The below command will even remove the volumes that are not part of your service list. `BE CAREFUL`
```
yes | docker volume prune
```

### More cleanup that you can do. DO NOT ATTEMPT the below commands if you do not understand the purpose and intent of it.
> The below command will remove all dangling networks in your host. Important Note: The below command will even remove the networks that are not part of your service list. `BE CAREFUL`
```
yes | docker network prune
```

### More cleanup that you can do. DO NOT ATTEMPT the below commands if you do not understand the purpose and intent of it.
> The below command will remove all dangling images in your host. Important Note: The below command will even remove the images that are not part of your service list. `BE CAREFUL`
```
yes | docker image prune
```

### More cleanup that you can do. DO NOT ATTEMPT the below commands if you do not understand the purpose and intent of it.
> The below command will remove all dangling components - network, volume, image, volume in your host. Important Note: The below command will even remove all the dangling components that are not part of your service list. `BE CAREFUL`
```
yes | docker system prune
```

## Commands to individually start the containers - If you are familiar with Docker then use it or else it's not worth the hassle
### To start the postgresql
```
docker run --rm -e POSTGRES_PASSWORD=welcome -p 5432:5432 -d gpuliyar/postgres
```

### To start the exporter
> Ensure to file the right values in the `env.file`
```
docker run --rm --env-file=env.file -p 8080:8080 -d gpuliyar/prometheus-sql-exporter
```

### To start the python script
```
docker run --rm -e 'database.host=<host info here>' -d gpuliyar/pyscript
```

## What if I want to run and debug the application using my IDE (IntelliJ)
> In that case, you need to setup the following environment variables in your configuration and then choose to either run the exporter in the normal or debug mode. Do ensure that the database is up and running.
```
database.url=<database jdbc url>
database.username=<database username>
database.password=<database password>
database.driver.class=<database driver class name>
interval.gauge=15000
interval.counter=15000
interval.histogram=15000
interval.summary=15000
```

### Sample file will look like:
```
database.url=jdbc:postgresql://postgres-service:5432/postgres
database.username=postgres
database.password=welcome
database.driver.class=org.postgresql.Driver
interval.gauge=15000
interval.counter=15000
interval.histogram=15000
interval.summary=15000
```
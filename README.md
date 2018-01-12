GitHub https://github.com/stehrn/docker

#### Run locally within docker
Build the image, give it tag name 'hello'
```
docker build -t hello .
```
Run image in (-d) detached mode, (-p) publish port. -rm automatically removes container when it exits.
```
docker run -d --rm -p 8080:8080 --name hello_vertex hello:latest 
```
View container process
```
docker ps
```
Other useful commands
```
docker container logs hello_vertex
```
View web page
```
curl http://localhost:8080
```
Note you will see an error: "Failed to get count: Connection refused: localhost/127.0.0.1:6379" unless \
a separate Redis container has been started. To do so run:
```
docker run -d --rm --publish 6379 --volume data:/Users/nikstehr/IdeaProjects/docker/data --name hello_redis redis:latest
```
Connect to redis container and run commands:
```
docker exec -it hello_redis redis-cli
KEYS *
GET counter
INCR counter
SET counter 1001
```
see [Redis commands](https://redis.io/commands/)

Below we'll see how to compose a service that does this for us.

#####Starting up separate Redis container 

#### Run from docker cloud
Get an account from https://cloud.docker.com/ and log in
```
docker login
```

##### Tag the image
```
docker tag hello stehrn/get-started:latest
docker images
```
(where stehrn is username, hello is image name, and get-started is repository name)

##### Publish image
```
docker push stehrn/get-started:latest
```
You should see updated image in docker cloud

##### Run container sourcing published image
```
docker run -d -p 8080:8080 stehrn/get-started:latest
```

##### Run service
This will spoin up 5 replicas in one service sourced from published image
```
docker swarm init
docker stack deploy -c docker-compose.yml get-started
docker service ls
docker service ps get-started_web
```

Cleanup:
```
docker stack rm get-started
docker swarm leave --force
```
 
#### Run from command line outside of docker
java -jar target/docker-1.0-SNAPSHOT-fat.jar -conf src/main/resources/application-conf.json
curl http://localhost:8080


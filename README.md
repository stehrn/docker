GitHub https://github.com/stehrn/docker

#### Run locally within docker
Build the image, give it tag name 'hello'
```
docker build -t hello .
```
Run image in (-d) detached mode, (-p) publish port. -rm automatically removes container when it exits.
```
docker run -d --rm -p 8080:8080 --name hello_vertx hello:latest
```
View container process
```
docker ps
```
Other useful commands
```
docker container logs hello_vertx
```
View web page
```
curl http://localhost:8080
```
Note you will see an error: "Failed to get count: Connection refused: localhost/127.0.0.1:6379" unless \
a separate Redis container has been started. To do so run:
```
docker run -d --rm -p 6379:6379 --name hello_redis --hostname 127.0.0.1 redis:alpine
```
('-p 6379:6379' maps the host port 63379 to same container port)

A good (ctl)[https://www.ctl.io/developers/blog/post/docker-networking-rules/] blog on network mapping.

OK, so you still will see an error because the vertx container has redis IP address of localhost (127.0.0.1), not the container port redis running in. \
To see IP of running redis container:
```
docker inspect --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' hello_redis
```
docker compose, explained further below, is best solution to inter-container communication. For now, to get this working \
edit application-conf.json redis.host, rebuild image and restart container. You should for the 1s time see counter:
```
Hello, hostname: 423fcb29871a, count: 1
```
TODO: config should be passed in at container runtime, not image build time.

Now that its up, you could also connect to redis container and run commands:
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
docker run -d -p 8080:8080 --name hello_vertx stehrn/get-started:latest
```

##### Run service
docker-compose.yml has 2 services: web and redis.

This will spin up 5 replicas in one service sourced from published image
```
docker swarm init
docker stack deploy -c docker-compose.yml get-started
docker service ls
docker service ps get-started_web
```
View: http://0.0.0.0:8080

Cleanup:
```
docker stack rm get-started
docker swarm leave --force
```
 
#### Run from command line outside of docker
java -jar target/docker-1.0-SNAPSHOT-fat.jar -conf src/main/resources/application-conf.json
curl http://localhost:8080


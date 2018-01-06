https://github.com/stehrn/docker

#### Run locally within docker
Build the image, give it tag name 'hello'
```
docker build -t hello .
```
Run image in detached mode, expose/publish port
```
docker run -d -p 8080:8080 hello
```
View container proess
```
docker ps
```
Other useful commands (container ID is shown in ps command)
```
docker container logs ab4538097493
```

curl http://localhost:8080

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
java -jar target/docker-1.0-SNAPSHOT-fat.jar
curl http://localhost:8080
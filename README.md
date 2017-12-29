
#### Run locally within docker
Build the image, give it tag name 'hello'
```
docker build -t hello .
```
Run image in detached mode, expose/publish port
```
docker run -d -p 4000:80 hello
```
View container proess
```
docker ps
```
Other useful commands (container ID is shown in ps command)
```
docker container logs ab4538097493
```

curl http://localhost:4000

#### Run from docker cloud
Get an account from [https://cloud.docker.com/](Docker cloud) and log in
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
docker run -d -p 4000:80 stehrn/get-started:latest
```

#### Run from command line outside of docker
java -jar target/docker-1.0-SNAPSHOT-fat.jar
curl http://localhost:8080
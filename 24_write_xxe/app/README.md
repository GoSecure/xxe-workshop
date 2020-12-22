
gradle buildDocker
docker rm books1
docker run --name books1 -p 8001:8001 books

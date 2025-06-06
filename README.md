# Tunescout
A Spring Boot Music Recommendation Application integrating Spring AI with Ollama.

## Notes
The app is a simple api written in Spring Boot with 4 api calls:
```
1. POST /recommendations/artists?query={artist name}
2. POST /recommendations/artists/async?query={artist name} 
3. GET /recommendations/artists/status/{requestId}
4. GET /recommendations/artists/{requestId}
```

The goal was to experiment with integrating Ollama and SpringBoot.

It currently also spans a postgres database that I 
do not make use of yet and caches the results for 
one hour in redis, so the same query will return the
same results for one hour.

Because of the nature of Ollama, it is not appropriate to use in in a synchronous manner, but I still 
provided one sample synchronous call for comparison.

Call #1 makes a call to Ollama api and delivers the results
right away as a json payload. For example the following query:
```
curl "http://localhost:8080/recommendations/artists?query=Zorglub"
```
returns (after a few seconds):
```
{
  "content": [
    {
      "name": "Arthur Dent",
      "description": "Arthur dent is similar to Zorglub because they both make Galaxy inspired music.",
      "genre": "Space Rock"
    },
    ...
  ]
}
```

Where as call #2, spawns a request to Ollama asynchronously and makes it available when 
its status is COMPLETED.

For example, for the same band query as above:

```
curl -X POST "http://localhost:8080/recommendations/artists/async?query=Zorglub"
```
The response could be:
```
{
  "content": {
    "requestId": "b3ba0aab-ab5d-41d7-97ad-2e6f7e7b0537",
    "status": "PROCESSING"
  }
}
```

The same query will return the same request identifier 
with the current status of the request, or you can 
check the status with the following query:
```
curl "http://localhost:8080/recommendations/artists/status/b3ba0aab-ab5d-41d7-97ad-2e6f7e7b0537"
```

And when the status is COMPLETE, the results 
can be fetched with the following call:
```
curl "http://localhost:8080/recommendations/artists/b3ba0aab-ab5d-41d7-97ad-2e6f7e7b0537"
```

Returning the same payload as the synchronous call.


## Run the app
The easiest way to get started is to clone the repo and 
start the app with docker-compose, however before you do 
that you will need to have ollama installed locally on 
your machine with the model llama3.2 installed.

This is because I have not been able to make ollama 
work well in a docker container for now. 
My current attempts we unsuccessful, due to the
size and slowness while running in a container.
It also takes quite some time to pull the model that
the app is using, so it is not really efficient at the moment.
I will leave this integration for a future iteration.

In the meantime, go to the [Ollama download page](https://ollama.com/download), install it for your system and issue the command:
```
ollama pull llama3.2
```
The model is about 2.6 Gb so it will take some time to pull.

Once the model is installed, you can just invoke:
```
docker-compose up
```
from the tunescout root directory, 
which should start the necessary containers.

If you wish to try and fix my initial attemps at running ollama in a container, I provided a starter script with the **_docker-compose-ollama.yml_** file. 


## Future Works
- Integrate Swagger-UI, see: https://medium.com/@ruwanpradeep9911/implementing-swagger-with-spring-boot-a-step-by-step-guide-4b121e607bd1
- Make Ollama run in a docker container
- add rate limits and prevent too many requests
- Persist past queries in postgres and serve the results if ollama is too busy


# Run scripts

## Map generation
```
./gradlew generateMap --args="<file>"
```


## Game

### Running locally
#### Server
```
./gradlew run --args="-mode server -port 5555 -map <file>"
```
#### Client
```
./gradlew run --args="-mode client -port 5555 -map <file> -host localhost"
```


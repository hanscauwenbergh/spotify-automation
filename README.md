# Spotify automation

## How to run

```
gradle clean build
java -jar build/libs/spotify-automation-1.0-SNAPSHOT.jar RefreshRecentSelection --client-id=<client-id> --client-secret=<client-secret> -- redirect-uri=<redirect-uri> --access-token=<access-token> --refresh-token=<refresh-token>
```
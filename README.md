# Spotify automation

## Setup

1. Create a [Spotify application](https://developer.spotify.com/dashboard/applications), choosing a random redirect URI (e.g. `http://localhost:8080/redirect`).
2. Obtain your client ID and secret.
3. Run `gradle clean build`.
4. Run `java -jar build/libs/spotify-automation-1.0-SNAPSHOT.jar GetTokens --client-id=<client-id> --client-secret=<client-secret> --redirect-uri=<redirect-uri>`.
5. Click the generated link in the console and complete the authentication flow.
6. Obtain the code query parameter value after the redirect and input it into the console.
7. Obtain the generated access and refresh token from the console.
8. Run `java -Xmx512m -jar build/libs/spotify-automation-1.0-SNAPSHOT.jar RefreshRecentSelection --client-id=<client-id> --client-secret=<client-secret> --redirect-uri=<redirect-uri> --access-token=<access-token> --refresh-token=<refresh-token>`.
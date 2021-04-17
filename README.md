# Spotify automation

## Setup

1. Create a [Spotify application](https://developer.spotify.com/dashboard/applications), choosing a random redirect URI (e.g. `http://localhost:8080/redirect`).
1. Obtain your client ID and secret.
1. Run `gradle clean build`.
1. Run `java -jar build/libs/spotify-automation-1.0-SNAPSHOT.jar GetTokensCommand --client-id=<client-id> --client-secret=<client-secret> -- redirect-uri=<redirect-uri>`.
1. Click the generated link in the console and complete the authentication flow.
1. Obtain the code query parameter value after the redirect and input it into the console.
1. Obtain the generated access and refresh token from the console.
1. Run `java -jar build/libs/spotify-automation-1.0-SNAPSHOT.jar RefreshRecentSelection --client-id=<client-id> --client-secret=<client-secret> -- redirect-uri=<redirect-uri> --access-token=<access-token> --refresh-token=<refresh-token>`.

# Spotify Stats App
#### This is an Android application that connects to a user's Spotify account and displays basic listening statistics<br>such as top tracks, top artists, and recent listening activity.
This project is currently only in the prototype stage.

### Important Note (Prototype Limitations)
#### This app uses the Spotify Web API, which has strict development restrictions. Because of this, <br>the current setup process is more complex than what a finished app would require.

In the expected final version, users would simply:<br>
Download app -> Connect Spotify -> View Stats

However, due to Spotify's development mode limitations, additional setup is required for this prototype.

### Running the App
#### The easiest way to run the app is to use our team's shared Spotify Developer application.
**Steps:**
1. Ask a team member for:
   * The **Client ID**
   * Confirmation that your Spotify account has been added under <br>**Users and Access** (we will need an email linked to your Spotify account)
2. Open the file named: `local.properties`<br>
3. Add to `local.properties`:<br>
   ```propteries
   SPOTIFY_CLIENT_ID=given_client_id_here
   ```
4. Sync and run the project in Android Studio
5. Log in using your Spotify account that was granted access (the email you provided us)

### Security Notes
- The **Client ID** is stored locally using `local.properties`and is **NOT** committed to GitHub
- The **Client Secret ID** is never used in this app for security reasons
- Each developer and user (for now) must configure their own environment for use

### Technical Overview
- Language: Kotlin
- Platform: Android
- Architecture: MVVM (Model View Viewmodel)
- API: Spotify Web API
- Networking: Retrofit
<br><br>
#### The app retrieves:
- User profile
- Top tracks
- Top artists
- Recently played tracks
- **More to come**

### Future Improvements
#### This prototype is designed to evolve into a more complete app. Planned improvements include:
- Simplified authentication flow for end users
- Backend integration for secure token management
- Local database for caching stats
- Additional stats and analytics
- Improved UI

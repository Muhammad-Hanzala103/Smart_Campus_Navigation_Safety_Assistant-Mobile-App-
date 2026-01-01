# CNSMS Android App - Demo Script

**Goal**: Demonstrate the complete end-to-end functionality of the mobile client and its integration with the backend.

### 1. Registration & Login

*   **Start**: Open the app. The Splash Screen shows and then navigates to the Login screen.
*   **Action**: Tap on "Don't have an account? Register".
*   **Demonstrate**: Fill out the Name, Email, and Password fields. Show validation by trying to submit with a short password.
*   **Action**: Correctly fill out the form and tap "Register".
*   **Narrate**: "The app sends the user details to the `/api/auth/register` endpoint. On success, the server saves the new user, and the app shows a success message before returning to the login screen."
*   **Action**: Enter the newly created user's credentials and tap "Login".
*   **Narrate**: "The app now calls the `/api/auth/login` endpoint. The server validates the credentials and returns a JWT token. The app securely stores this token and navigates to the main screen."

### 2. Main Screen & Incident Reporting

*   **Demonstrate**: Show the main screen with the Bottom Navigation bar (Map, Report, Incidents, Profile).
*   **Action**: Navigate to the "Report" tab.
*   **Narrate**: "This is the core feature for reporting a new incident."
*   **Action**:
    1.  Tap "Select Image" and pick an image from the gallery.
    2.  Show the image preview.
    3.  Tap on the mini-map to select a location.
    4.  Fill in the Category and Description.
    5.  Tap "Submit Incident".
*   **Narrate**: "When submitting, the app first compresses the image to ensure it's under 1MB. It then packages the image and all the form data into a single `multipart/form-data` request and sends it to the `/api/incidents` endpoint. The loading indicator shows this process."
*   **Demonstrate**: Show the success toast message with the new Incident ID.

### 3. Viewing Incidents & AI Analysis

*   **Action**: Navigate to the "Incidents" tab.
*   **Demonstrate**: Show the list of incidents, including the one just created.
*   **Narrate**: "This screen calls the `/api/incidents` endpoint to get a list of all reports. It displays key information and a severity badge if AI analysis has been performed."
*   **Action**: Tap on the new incident to open its detail view (or a bottom sheet).
*   **Action**: In the detail view, tap the "Re-analyze" button.
*   **Narrate**: "The app is now calling the `/api/incidents/analyze` endpoint with the incident ID. The backend runs its AI model and returns the results."
*   **Demonstrate**: Show the AI analysis results: the color-coded severity badge, the list of detected labels with confidence scores, and the plain-text recommendation.

### 4. Profile & Settings

*   **Action**: Navigate to the "Profile" tab.
*   **Demonstrate**: Show the user's information and the "Logout" and "Settings" buttons.
*   **Action**: Tap "Settings".
*   **Narrate**: "For easy debugging, the settings screen allows changing the server's Base URL at runtime without needing to recompile the app."
*   **Action**: Go back to the Profile screen and tap "Logout". Confirm the dialog.
*   **Demonstrate**: Show that the app returns to the Login screen and the user is logged out.
*   **Narrate**: "Logging out clears the secure token, so the user must authenticate again to access the app."

This completes the demonstration of the CNSMS mobile client.

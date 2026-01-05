# Smart Campus Navigation Safety Assistant (CNSMS-Android)

## Introduction

The Smart Campus Navigation Safety Assistant is an Android application designed to enhance the safety and navigation experience for students and staff on a university campus. The app provides features for user authentication, incident reporting, and accessing campus services like shuttle information.

## Features

*   **User Authentication:** Secure login, registration, and password recovery for personalized access.
*   **Incident Reporting:** Allows users to report incidents on campus, contributing to a safer environment.
*   **QR Code Integration:** Utilizes QR code scanning for various campus-related interactions.
*   **Shuttle Information:** Provides details about campus shuttle services.
*   **Navigation:** Helps users navigate the campus.

## Tech Stack

*   **Language:** Java
*   **UI:** Android XML with Material Design Components
*   **Networking:**
    *   Retrofit: For making HTTP requests to the backend API.
    *   Gson: for serializing and deserializing JSON data.
*   **Image Loading:**
    *   Glide: For efficient loading and caching of images.
*   **QR Code Scanning:**
    *   ZXing (Zebra Crossing): Integrated via `journeyapps/zxing-android-embedded`.

## Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

*   Android Studio
*   An Android device or emulator

### Installation

1.  Clone the repo
    ```sh
    git clone https://example.com/your_project_repo.git
    ```
2.  Open the project in Android Studio.
3.  Build the project. Gradle will automatically download the necessary dependencies.
4.  Run the application on an Android device or emulator.

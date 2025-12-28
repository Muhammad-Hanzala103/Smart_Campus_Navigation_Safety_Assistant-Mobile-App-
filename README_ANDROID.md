# CNSMS Android Client - README

This document provides complete instructions for setting up, running, debugging, and testing the CNSMS Android client.

## 1. Project Setup

1.  **Import Project**: Open Android Studio and select `File > New > Import Project...`, then choose the project's root folder.
2.  **Sync Gradle**: Let Android Studio download all the required libraries.

## 2. Running with a Backend Server

This app requires a running Flask backend. The app must know the server's IP address and port.

### Method A: Using the Android Emulator (Easiest)

This is the recommended method for development.

1.  **Run your Flask Server**: Start your Python server on your computer. It will likely run on `http://127.0.0.1:5000/`.
2.  **Run the App**: Run the Android app in the Android Emulator. The default Base URL is already set to `http://10.0.2.2:5000/`, which is the special IP address the emulator uses to connect to your computer.

### Method B: Using a Physical Android Device (over Wi-Fi)

This requires your phone and computer to be on the **same Wi-Fi network**.

1.  **Find your Computer's Local IP Address**:
    *   On Windows, open Command Prompt (`cmd`) and type `ipconfig`. Find the "IPv4 Address" under your Wi-Fi adapter (e.g., `192.168.1.100`).
    *   On macOS, open Terminal and type `ifconfig | grep "inet "`. Find the `inet` address that is not `127.0.0.1`.
2.  **Set the Base URL in the App**:
    *   Run the app on your phone.
    *   Navigate to **Settings** from the main screen's menu.
    *   Change the Base URL to your computer's IP address (e.g., `http://192.168.1.100:5000/`). **Don't forget the trailing slash `/`!**
    *   Save and restart the app.
3.  **Check Your Firewall**: If the app still can't connect, you must create an **inbound firewall rule** on your computer to allow connections on port **5000**.

## 3. Debugging the App

### USB Debugging

1.  Enable Developer Options and USB Debugging on your phone.
2.  Connect your phone to your computer via USB.
3.  Select your device in Android Studio and click Run.
4.  View logs in the **Logcat** window.

### Wi-Fi Debugging (ADB over Wi-Fi)

This is useful for debugging without a USB cable.

1.  Connect your phone via USB first.
2.  Open a terminal or command prompt and run:
    ```sh
    adb tcpip 5555
    ```
3.  Disconnect the USB cable.
4.  Find your phone's IP address (in Wi-Fi settings).
5.  Connect to your phone over the network:
    ```sh
    adb connect YOUR_PHONE_IP:5555
    ```
6.  You can now run and debug the app from Android Studio wirelessly.

## 4. Sample `curl` Commands for Backend Testing

Use these to test your server independently.

**Register a User**
```bash
curl -X POST -H "Content-Type: application/json" -d '{"name":"Test User","email":"test@example.com","password":"password123"}' http://127.0.0.1:5000/api/register
```

**Login**
```bash
curl -X POST -H "Content-Type: application/json" -d '{"email":"test@example.com","password":"password123"}' http://127.0.0.1:5000/api/login
```

**Create Incident**
```bash
curl -X POST http://127.0.0.1:5000/api/incidents -H "Authorization: Bearer YOUR_JWT_TOKEN" -F "image=@/path/to/your/image.jpg" -F "description=Test incident" -F "category=Vandalism" -F "x=100" -F "y=200"
```

## 5. Acceptance Test Checklist

- [ ] **Registration**: Can a new user be created successfully?
- [ ] **Login/Logout**: Can a user log in, receive a token, and log out?
- [ ] **Forgot/Reset Password**: Does the password reset flow work as expected?
- [ ] **Incident Upload**: Can you pick an image, mark a location on the map, and successfully upload an incident?
- [ ] **AI Analysis**: Is the analysis result screen shown after upload, with correct severity and labels?
- [ ] **Settings**: Can you change the Base URL in settings?
- [ ] **Error Handling**: Are clear error messages shown for invalid inputs, wrong passwords, or network failures?

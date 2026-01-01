# CNSMS-Android - Project README

This document provides a complete guide to setting up, running, debugging, and demonstrating the CNSMS Android client.

## 1. Project Setup

1.  **Import**: Open Android Studio, select `File > New > Import Project...`, and choose the project's root folder.
2.  **Sync Gradle**: Allow Android Studio to download all the necessary libraries.
3.  **Set Backend URL**: Open `app/src/main/java/com/example/cnsmsclient/ui/SettingsActivity.java` or use the in-app Settings screen to change the `BASE_URL` to your server's address.
    *   **For Emulator**: Use `http://10.0.2.2:5000/`
    *   **For Physical Device (Wi-Fi)**: Use your computer's local IP (e.g., `http://192.168.1.100:5000/`)

## 2. Running and Debugging

### Via USB

1.  Enable Developer Options and USB Debugging on your device.
2.  Connect the device to your computer. Select it in Android Studio and click Run.
3.  Use the `Logcat` window to view logs.

### Via Wi-Fi

1.  Connect via USB first.
2.  Run `adb tcpip 5555` in the Android Studio Terminal.
3.  Disconnect USB.
4.  Run `adb connect YOUR_PHONE_IP:5555`.
5.  You can now deploy and debug wirelessly.

## 3. `curl` Commands for Backend Testing

```bash
# Register
curl -X POST -H "Content-Type: application/json" -d '{"name":"Test","email":"test@test.com","password":"password"}' http://127.0.0.1:5000/api/auth/register

# Login
curl -X POST -H "Content-Type: application/json" -d '{"email":"test@test.com","password":"password"}' http://127.0.0.1:5000/api/auth/login

# Create Incident (replace with a valid token)
curl -X POST http://127.0.0.1:5000/api/incidents -H "Authorization: Bearer <TOKEN>" -F "image=@/path/to/image.jpg" -F "description=Test" -F "category=Test" -F "x=100" -F "y=100"
```

## 4. Acceptance Checklist

- [ ] App compiles and runs.
- [ ] User can register and then log in successfully.
- [ ] Token is saved, and `Authorization` header is present in subsequent requests (check Logcat).
- [ ] User can select an image and upload an incident report.
- [ ] Incident list updates with the new report.
- [ ] Map screen loads and displays map image.
- [ ] User can log out.

---

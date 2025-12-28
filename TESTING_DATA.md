# Smart Campus App - Testing Data & Setup

## Folder Structure
```
app/src/main/java/com/example/smartcampusnavigationsafetyassistant/
├── activity/          (Optional package separation, currently all in root for simplicity as per request)
├── adapter/
│   └── EventsAdapter.java
├── model/
│   ├── AttendanceRequest.java
│   ├── Event.java
│   ├── Incident.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── MapNode.java
│   ├── Shuttle.java
│   └── User.java
├── network/
│   ├── ApiClient.java
│   ├── ApiService.java
│   ├── AuthInterceptor.java
│   └── Repository.java
├── utils/
│   ├── Constants.java
│   ├── MapOverlayView.java
│   ├── PathFinder.java
│   └── SessionManager.java
├── EventsActivity.java
├── HomeActivity.java
├── LoginActivity.java
├── MapActivity.java
├── ProfileActivity.java
├── QRScannerActivity.java
├── SafetyActivity.java
├── ShuttleActivity.java
└── SplashActivity.java
```

## Backend API - Example JSON Responses

### POST /login
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR...",
  "user": {
    "id": "u123",
    "name": "John Doe",
    "email": "john@university.edu",
    "studentId": "2023001"
  }
}
```

### GET /map
```json
[
  {
    "id": "n1",
    "name": "Main Gate",
    "description": "Main entrance of the campus",
    "x": 100,
    "y": 900,
    "neighbors": ["n2"]
  },
  {
    "id": "n2",
    "name": "Library",
    "description": "Central Library",
    "x": 300,
    "y": 500,
    "neighbors": ["n1", "n3"]
  },
  {
    "id": "n3",
    "name": "Cafeteria",
    "description": "Student Canteen",
    "x": 600,
    "y": 400,
    "neighbors": ["n2"]
  }
]
```

### GET /shuttle
```json
{
  "id": "s1",
  "name": "Campus Shuttle A",
  "currentX": 300,
  "currentY": 500,
  "nextStop": "Cafeteria",
  "eta": "2 mins"
}
```

### GET /events
```json
[
  {
    "id": "e1",
    "title": "Science Fair",
    "description": "Annual science fair at the auditorium.",
    "date": "2023-10-25"
  },
  {
    "id": "e2",
    "title": "Exam Schedule Released",
    "description": "Check the portal for final exam dates.",
    "date": "2023-11-01"
  }
]
```

### GET /profile
```json
{
  "id": "u123",
  "name": "John Doe",
  "email": "john@university.edu",
  "studentId": "2023001"
}
```

## Backend Integration
1. **Flask Server**: Ensure your Flask server is running and accessible.
2. **Base URL**: Update `Constants.java` (or `ApiClient.java`) with your computer's IP address if testing on a physical device (e.g., `http://192.168.1.5:5000/api/`). `localhost` will not work on a real Android device (use `10.0.2.2` for emulator).
3. **Images**: Place a `map.png` in `app/src/main/res/drawable/` to replace the placeholder if you have the real map.

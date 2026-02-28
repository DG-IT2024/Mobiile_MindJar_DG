# MindJar
MindJar is a mobile application designed to help users reflect on their emotions and thoughts. 
The app allows users to log in, select how they are feeling, and write down their thoughts in a simple and organized way. 
It focuses on promoting self-awareness through a clean interface and easy navigation. 

## Features
- Login screen with email and password validation
- Real-time input validation with helper text and toast messages
- Emotion selection on the dashboard
- Text input for writing thoughts and feelings
- Fragment-based navigation
- Bottom navigation bar for switching screens
- Back stack handling for smooth navigation flow
- Simple and user-friendly UI design

## Tech Stack
- Platform: Android
- Language: Java
- UI Design: XML (ConstraintLayout)
- IDE: Android Studio
- Design Components: Material Design Components

## Setup & Run
1. Follow these steps to run the project locally:
2. Clone or download the project repository
3. Open Android Studio
4. Click Open and select the MindJar project folder
5. Allow Gradle to sync and finish building
6. Connect an Android device or start an emulator
7. Click Run ▶️ to launch the app

Requirements:
- Android Studio installed
- Android SDK configured
- Emulator or physical Android device

```bash
MindJar/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example
│   │   │   │   ├── myapplication/
│   │   │   │   │   ├── LoginActivity.java
│   │   │   │   │   ├── SignUpActivity.java
│   │   │   │   │   ├──  DashboardActivity.java
│   │   │   │   │   ├── HomeFragment.java
│   │   │   │   │   ├──  RealizationFragment.java
│   │   │   │   │   ├── HopeFragment.java
│   │   │   │   │   ├──  VideosFragment.java
│   │   │   │   │   ├── HotlineFragment.java
│   │   │   │   │   ├── MyJourneyFragment.java
│   │   │   │   │   └── EntryDetails.java
│   │   │   │   
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_sign_up.xml
│   │   │   │   │   ├── activity_dashboard.xml
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── fragment_home.xml
│   │   │   │   │   ├── fragment_hope.xml
│   │   │   │   │   ├── fragment_hotline.xml
│   │   │   │   │   ├── fragment_my_journey.xml
│   │   │   │   │   ├── fragment_realization.xml
│   │   │   │   │   ├── fragment_videos.xml
│   │   │   │   │   └──fragment_entry_details.xml
│   │   │   │   │
│   │   │   │   ├── layout-land/
│   │   │   │   │   ├── activity_sign_up.xml
│   │   │   │   │   ├── activity_dashboard.xml
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── fragment_home.xml
│   │   │   │   │   ├── fragment_hope.xml
│   │   │   │   │   ├── fragment_hotline.xml
│   │   │   │   │   ├── fragment_my_journey.xml
│   │   │   │   │   ├── fragment_realization.xml
│   │   │   │   │   ├── fragment_videos.xml
│   │   │   │   │   └──fragment_entry_details.xml
│   │   │   │   │
│   │   │   │   │   
│   │   │   │   ├── drawable/
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── themes.xml
│   │   │   │
│   │   │   └── AndroidManifest.xml
│   │   │
│   │   └── test/
│   │
│   └── build.gradle
│
├── gradle/
├── build.gradle
├── settings.gradle
└── README.md
```





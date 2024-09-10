
# TrashApp


TrashApp is a Kotlin-based Android application designed to help users classify types of waste through image processing using machine learning technology. This application is equipped with a TensorFlow Lite model that has been trained to detect various types of waste. In addition to the classification feature, TrashApp also provides features for storing waste detection history and the latest news about waste management taken from the News API.



## Features

- Waste Classification:
    - Real-time detection of waste types using the device camera.
    - Accurate and easy-to-understand detection results.
    - Supports various types of waste (adjust to the types of waste that have been trained on the model).
- Detection History:
    - Stores waste detection results in a list that can be accessed at any time.
    - CRUD (Create, Read, Update, Delete) features for managing historical data.
- Latest News:
    - Presents the latest articles about waste management from various news sources.
    - Gets news through integration with the News API.





## Tech Stack

- Programming Language: Kotlin
- Framework: Android SDK
- Library: TensorFlow Lite, Retrofit, Room, Hilt, News API
- Tools: Android Studio



## Screenshots

<img src="https://github.com/user-attachments/assets/ace888ac-519b-4637-b6e1-2239443a80b5" width="200">
<img src="https://github.com/user-attachments/assets/a929fb2f-09f8-41a7-a2ad-5308f9907d20" width="200">
<img src="https://github.com/user-attachments/assets/6360e58f-7846-4d34-8bf4-d57724423da8" width="200">
<img src="https://github.com/user-attachments/assets/801f9262-d7a2-471a-a10e-8765a118e77b" width="200">
<img src="https://github.com/user-attachments/assets/dea8077f-aa53-4630-8b51-2a86a7537f0f" width="200">

## Installation

1. Clone the repository

```bash
  git clone https://github.com/wildanfatah22/TrashApp.git
```
2. Open the project in Android Studio: Open the project you just cloned in Android Studio.
3. Sync Gradle: Wait for Android Studio to finish syncing Gradle.
4. Configure News API:
    - Create an account on the News API (https://newsapi.org/) and get an API key.
    - Replace the API key in the build.gradle file (app module) with your API key.


## How to Get a News API Key
1. Visit the News API website (https://newsapi.org/).
2. Sign up or log in to your account.
3. Create a new project.
4. You will be given a unique API key. Save it carefully.



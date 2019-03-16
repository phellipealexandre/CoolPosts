# Cool Posts Repository

## Intro
This repository consists of an Android project that fetches posts and comments from [Jsonplaceholder](https://jsonplaceholder.typicode.com) and shows them to the user in two screens. It uses the MVVM pattern with many architecture components. The codebase is entirely written in Kotlin and uses Dagger2 as dependency injection framework.

This project considers orientation changes on the device. By using ViewModel and LiveData the lifecycle of the activities are better managed.

To manage the data the user sees, the concept of [single source of truth](https://developer.android.com/jetpack/docs/guide#persisting_data) is used, where the main source of data is the Room database.

## Screenshot
![](images/AppScreenshot1.png)
![](images/AppScreenshot2.png)

## Most important technologies and libraries
* Kotlin
* Androidx
* RxJava2
* Dagger2
* Room
* LiveData
* ViewModel
* Retrofit
* Mockito-kotlin
* Glide
* JUnit
* Espresso
* MockWebServer and RESTMock

## Big picture view of architecture

![](images/CoolPostsBigPicture.png)

## Testing strategy

![](images/GeneralTestingStrategy.png)

## UI test configuration

To configure the UI tests, this project uses the capabilities of Dagger to change the database and remote service in runtime. Although it requires an additional configuration step, it brings some advantages such as: 
* Nice level of integration in Activity test since it uses real ViewModel and Repository implementations 
* Once configured, the developer does not need an additional build flavor to run or other configuration in production code, you can execute all tests anytime with a device attached.

![](images/UITestingConfiguration.png)

## Recommendations to run
This project has a Makefile with many shortcuts to help the developer's life. For instance to clear the app data you can execute the following command:
```
make clear-app-data
```

You can also disable the current device animations before the UI tests by executing:
```
make disable-animations
```

Other thing you can do is run all tests (Instrumented and non-instrumented) with a single command:
```
make run-all-tests
```

Ps: If you want to run via command line, be sure you have just one emulator/device running, adb installed in your terminal and Java properly configured :)
Ps2: If you want to run via Android Studio, be sure you have the last stable version (3.3.2)
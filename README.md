# Cool Posts

## Intro
This repository consists of an Android project that fetches posts and comments from [JSONPlaceholder](https://jsonplaceholder.typicode.com) and shows them to the user in two screens. It uses the MVVM pattern with many architecture components. The codebase is entirely written in Kotlin and uses Dagger 2 for dependency injection.

This project considers orientation changes on the device. By using ViewModel and LiveData the lifecycle of the activities are better managed.

To manage the data the user sees, the concept of [single source of truth](https://developer.android.com/jetpack/docs/guide#persisting_data) is used, the single source is the Room database.

## Screenshot
![](images/AppScreenshot1.png)&nbsp;&nbsp;![](images/AppScreenshot2.png)

## Most important technologies and libraries
* [Kotlin](https://kotlinlang.org/)
* [AndroidX](https://developer.android.com/jetpack/androidx)
* [RxJava2](https://github.com/ReactiveX/RxJava)
* [Dagger 2](https://google.github.io/dagger/)
* [Room](https://developer.android.com/topic/libraries/architecture/room)
* [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
* [Retrofit](https://square.github.io/retrofit/)
* [Mockito-Kotlin](https://github.com/nhaarman/mockito-kotlin)
* [Glide](https://github.com/bumptech/glide)
* [JUnit4](https://junit.org/junit4/)
* [Espresso](https://developer.android.com/training/testing/espresso)
* [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver)
* [RESTMock](https://github.com/andrzejchm/RESTMock)

## Big picture view of architecture

![](images/CoolPostsBigPicture.png)

## Testing strategy

![](images/GeneralTestingStrategy.png)

## UI test configuration

To configure the UI tests, this project uses the capabilities of Dagger to change the database and remote service at runtime. Although it requires an additional Dagger configuration step in AndroidTest package, it brings some advantages such as: 
* Nice level of integration in Activity test since it uses real ViewModel and Repository implementations 
* Once configured, the developer does not need an additional build flavor or other configuration in production code, you can execute all tests anytime with a device attached.

![](images/UITestingConfiguration.png)

## Recommendations to run the project
This project has a Makefile with many shortcuts to help the developer's life. For instance, to clear the app data you can execute the following command:
```
make clear-app-data
```

You can also disable the current device animations before running the UI tests by executing this command:
```
make disable-animations
```

Another thing you can do is run all tests (Instrumented and non-instrumented) with a single command:
```
make run-all-tests
```

Ps: If you would like to run via command line, be sure to have just one emulator/device running, adb installed in your terminal and Java properly configured :)

Ps2: If you would like to run via Android Studio, be sure to have the last stable version (3.3.2)
run-unit-tests:
	./gradlew test

run-instrumented-tests:
	./gradlew connectedAndroidTest

run-all-tests:
	./gradlew test connectedAndroidTest

clear-app-data:
	adb shell pm clear com.phellipesilva.coolposts
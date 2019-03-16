run-unit-tests:
	./gradlew test

run-instrumented-tests: clear-app-data disable-animations
	./gradlew connectedAndroidTest

run-all-tests: run-unit-tests run-instrumented-tests

clear-app-data:
	adb shell pm clear com.phellipesilva.coolposts

disable-animations:
	adb shell settings put global window_animation_scale 0
	adb shell settings put global transition_animation_scale 0
	adb shell settings put global animator_duration_scale 0

enable-animations:
	adb shell settings put global window_animation_scale 1
	adb shell settings put global transition_animation_scale 1
	adb shell settings put global animator_duration_scale 1
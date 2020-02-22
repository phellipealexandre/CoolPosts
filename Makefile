run-all-tests: run-unit-tests build-install-app run-instrumented-tests

run-unit-tests:
	./gradlew test

run-instrumented-tests: clear-app-data disable-animations
	./gradlew connectedAndroidTest

clean-build:
	./gradlew clean cleanBuildCache build

build-install-app:
	./gradlew installDebug

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
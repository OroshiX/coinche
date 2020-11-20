[![build status](https://github.com/OroshiX/coinche/workflows/Test,%20Build%20and%20Release%20apk/badge.svg)](https://github.com/OroshiX/coinche/actions)

# FlutterCoinche

A flutter client for coinche

## Compilation

```shell script
dart tool/env.dart
flutter clean # (except the first time)
flutter pub get
flutter pub run build_runner build --delete-conflicting-outputs --enable-experiment=non-nullable
# for android
flutter build apk --split-per-abi
# ios (only on mac)
flutter build ios
```

Keystore information stored in a file named `key.properties` located under `android/` folder, containing these lines

```
keyAlias=myalias
storeFile=/path/to/key.jks
```

In order to override the build number and name for Android, add `--build-name` and `--build-number` parameters to the build command.

[https://flutter.dev/docs/deployment/android#reference-the-keystore-from-the-app](https://flutter.dev/docs/deployment/android#reference-the-keystore-from-the-app)



# Web build
## Disable CORS for development
We need to disable CORS-POLICY in order to make calls to the API (outside of localhost). In order to do that, we must set an environment variable called `CHROME_EXECUTABLE` to the right location, but with the right parameters too. So the steps are:
### Windows
* Create a script `chrome-unsafe.bat`
* Write this inside the file:
```
"C:\Program Files (x86)\Google\Chrome\Application\chrome.exe" --disable-web-security --user-data-dir="<some-tmp-folder>" %*
```
* Setup the environment variable ```CHROME_EXECUTABLE``` to point to that file (`chrome-unsafe.bat`)
### Linux
* Create a script `chrome-unsafe.sh`
* Write inside the file:
```#!/bin/sh
/usr/bin/google-chrome-stable --disable-web-security --user-data-dir="A-TEMP-LOCATION" $*
```
* Make the script executable with `chmod a+x chrome-unsafe.sh`
* Setup the environment variable in `~/.bash_aliases` with this line:
```
export CHROME_EXECUTABLE=/path/to/chrome-unsafe.sh
```
### MacOS
* Create a script `chrome-unsafe.sh` in the **same directory as the chrome executable**, which means in this location:
```
/Applications/Google Chrome.app/Contents/MacOS/
```
* Write this line inside:
```shell script
/Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome --disable-web-security --user-data-dir="tmp-dir" $*
```
* Add execute permission to the script
```shell script
chmod +x /Applications/Google Chrome.app/Contents/MacOS/chrome-unsafe.sh
```
* Add this to `.bash_profile`:
```shell script
export CHROME_EXECUTABLE="/Applications/Google Chrome.app/Contents/MacOS/chrome-unsafe.sh"
```
* If you have issues with a `Permission denied errno = 13`, run `flutter clean -v` or if it is not resolved, manually delete `.dart_tool` and `build` directories.
## Run
```shell script
flutter run web --dart-define=FLUTTER_WEB_USE_SKIA=true
```
The environment variable `CHROME_EXECUTABLE` is automatically consulted by the flutter tool, so the option to "disable web security" is automatically applied (locally).

## Build
```shell script
flutter build web --dart-define=FLUTTER_WEB_USE_SKIA=true
```

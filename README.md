# rehagoal-smartwatch

--------------------

rehagoal-smartwatch is the Android Wear smartwatch component for the rehagoal-webapp.
It provides an interface to execute workflows and receive notifications.


## Getting Started

To get you started you can simply clone the repository and install the dependencies:

### Prerequisites

You need git to clone the repository. You can get git from

[https://git-scm.com/][git].

### Clone rehagoal-smartwatch

Clone the rehagoal-smartwatch repository using [git][git]:

```

# Over ssh with key-based authentication
git clone git@github.com:RehaGoal/rehagoal-smartwatch.git

# Over HTTPS
git clone https://github.com/RehaGoal/rehagoal-smartwatch.git

```

### Dependencies
This projects uses the following packages:
- `com.google.android.gms:play-services-wearable`: Google Play services
- `com.google.code.gson:gson`: Google JSON wrapper 
- `com.google.android.support:wearable`: Support library for Android Wear
- `com.google.android.wearable:wearable`: Android Wear 

## Build
The DATA API requires that both components (`rehagoal-webapp` & `rehagoal-smartwatch`) are both signed with the same release key.

The `app/build.gradle` file contains the section `signingConfigs`, which requires:
- that a file name `rehagoal_keystore.jks` exists within the `/app` folder
- `storePassword` can pull a system env called `ANDROID_KEYSTORE_PASSWORD` containing the password for the keystore file
- `keyAlias` can pull a system env called `ANDROID_KEY_ALIAS` which reflects the alias for the key
- `keyPassword` can pull a system env called `ANDROID_KEY_PASSWORD` which contains the password for the alias key

### Debug version
To build a debug version of the application, you can run gradle:

```
./gradlew assembleDebug
```

### Release version
To build a release version of the application, you can run gradle:

```
./gradlew assembleRelease
```

[git]: https://git-scm.com/
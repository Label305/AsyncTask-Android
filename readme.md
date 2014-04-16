# Stan for Android [![Build Status](https://travis-ci.org/Label305/Stan-for-Android.svg?branch=master)](https://travis-ci.org/Label305/Stan-for-Android)

Android library with various extensions to android views and various helpers, used at Label305 for app development

## Gradle

Add the following lines to the settings.gradle when using Stan as submodule
```
include ':stan'

project (':stan').projectDir = new File(settingsDir, '/$PATH_TO_SUBMODULE/stan/app')
```

Then in app/build.gradle add the following line:
```
dependencies {

    ...

    compile project(':stan')

    ...
}
```

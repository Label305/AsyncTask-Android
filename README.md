# Stan for Android [![Build Status](https://travis-ci.org/Label305/Stan-for-Android.svg?branch=master)](https://travis-ci.org/Label305/Stan-for-Android)

Android library with various extensions to android views and various helpers, used at Label305 for app development

## Features

* all kinds of views which support custom fonts
   * Fonts need to be placed in assets
   * A string needs to point to the location of the asset
   * This string is used in the xml to define the font used for the custom font view
* a safe AsyncTask
  * Which has callbacks for exceptions, but does throw RTE
  * Also an ExponentialBackoffAsyncTask is included, which retries failed Tasks several times (can be defined)
* ~~Http functions~~ moved to [Kama-for-Android](https://github.com/Label305/Kama-for-Android)
  * ~~Getter/Poster/Deleter/Putter functions supported~~
* extensions on the Imageview
  * SimpleNetworkImageView uses Volley to load an image from url
  * SvgImageView is an Imageview which supports svg images
* an AbstractExpandableTitleView
  * animated
* Memory management
  * BitmapCache
  * SvgCache
* Several utils
  * ~~Analytics~~ moved to [Jeff-for-Android](https://github.com/Label305/Jeff-for-Android)
  * Logger
  * ArrayAdapter
  * PixelUtils
  * StringUtils

## Usage - Gradle

Add the following lines to the app/build.gradle when using Stan
```
dependencies {

    ...

    compile 'com.label305.stan:library:0.1.1'

    ...
}
```


## Libraries used
 * [NineOldAndroids](http://nineoldandroids.com/)
 * [OrmLite](http://ormlite.com/)
 * [Crashlytics](http://www.crashlytics.com)
 * [androidsvg](https://code.google.com/p/androidsvg/)
 * [volley](https://android.googlesource.com/platform/frameworks/volley/)
 * [Mockito](https://code.google.com/p/mockito/)
 * [Hamcrest](https://code.google.com/p/hamcrest/)

## License
Copyright 2014 Label305 B.V.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

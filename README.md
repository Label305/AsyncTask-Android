# Stan for Android [![Build Status](https://travis-ci.org/Label305/Stan-for-Android.svg?branch=master)](https://travis-ci.org/Label305/Stan-for-Android)

Stan is a toolkit library for Android which consists of the following modules:

  * `lib-analytics`
  * `lib-async`
  * `lib-core`
  * `lib-geofencing`
  * `lib-svg`
  * `lib-widget`

## Usage

Add the following to your dependencies:

    compile 'com.label305.stan:lib-analytics:x.x.x'
    compile 'com.label305.stan:lib-async:x.x.x'
    compile 'com.label305.stan:lib-geofencing:x.x.x'
    compile 'com.label305.stan:lib-svg:x.x.x'
    compile 'com.label305.stan:lib-utils:x.x.x'
    compile 'com.label305.stan:lib-widget:x.x.x'
    
Replace `x.x.x` with the latest version name. See [the releases page][1].
    
## Documentation

The javadocs can be found [here][2]

## Modules

### Analytics

The `lib-analytics` module provides a bridge between you and Google Analytics with various quick access functions.
To use this class, initialize it on startup using `Analytics.init(Context, boolean)`, and add the following to your `strings.xml`, replacing the values:

	<string name="key_analytics">MY_ANALYTICS_KEY</string>
	<string name="key_analytics_debug">MY_DEBUG_ANALYTICS_KEY</string> <!-- optional -->
	
The string `key_analytics_debug` is optional, and provides a way to send analytics events to a separate profile.

### Async

The `lib-async` module provides an `AsyncTask` which provides proper exception handling, and easy event handling.
It is loosely based on RoboGuice's `AsyncTask`.

The `ExponentialBackoffAsyncTask` provides a way to keep retrying the requests when an `Exception` occurs, up to a maximum number of times. When subclassing this class, override `shouldRetry(Exception, int)` to determine whether to retry the request. By default, this class only retries if an `IOException` is thrown, upto a maximum of 3 times.

### Geofencing

The `lib-geofencing` module provides easy geofencing utilities.

### SVG

The `lib-svg` module contains an `SvgImageView`, which can show SVG images. SVG images should be stored in the `raw` folder.

### Utils

The `lib-utils` module provides a couple of utility classes. The `Logger` class provides logging, but only if the application is in a debug state. To initialize this class, call `Logger.setIsDebug(boolean)`.

### Widget

The `lib-widget` module contains several extensions to Android's `View` classes to use custom fonts, and more.
To use one of the `CustomFont` view classes, place the fonts in the `assets` directory, and add the following to your layout xml:

    <com.label305.stan.widget.CustomFontTextView
        ...
        xmlns:stan="http://schemas.android.com/apk/res-auto"
        stan:font="myfont.ttf" />

## License
	Copyright 2014 Label305 B.V.
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

[1]: https://github.com/Label305/Stan-for-Android/releases
[2]: http://label305.github.io/Stan-for-Android/javadoc/

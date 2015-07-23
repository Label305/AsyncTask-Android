# AsyncTask-Android

AsyncTask made simpler.

## Set up

Add the following to the dependencies section of your `build.gradle`:

```groovy
compile 'com.label305:asynctask:x.x.x.'
```

## SimpleAsyncTask

Use a `SimpleAsyncTask` when doing work that will not throw a checked `Exception`:

```java
public class MyAsyncTask extends SimpleAsyncTask<String> {

  @Override
  protected String doInBackgroundSimple() {
    return "Test";
  }

  @Override
  protected void onSuccess(final String result) {
    // Do something with the result
  }
}
```

## AsyncTask

Use an `AsyncTask` when doing work that may throw a checked `Exception`:

```java
public class MyAsyncTask extends AsyncTask<String, IOException> {

  @Override
  protected String doInBackground() throws IOException {
    return myWebservice.getSomeString();
  }

  @Override
  protected void onSuccess(final String result) {
    // Do something with the result
  }

  @Override
  protected void onException(final IOException e) {
    // Handle IOException
  }

}
```

## License
  Copyright 2015 Label305 B.V.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
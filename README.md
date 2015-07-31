# AsyncTask-Android [ ![Download](https://api.bintray.com/packages/label305/Label305/AsyncTask-Android/images/download.svg) ](https://bintray.com/label305/Label305/AsyncTask-Android/_latestVersion)

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
  protected String doInBackground() {
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

## Executing

Like Android's `AsyncTask`, you can call `execute()` on the `AsyncTask` instance to start it:

```java
new MyAsyncTask().execute();
```

Alternatively, you can use an [`AsyncTaskExecutor`](https://github.com/Label305/AsyncTask-Android/blob/master/asynctask/src/main/java/com/label305/asynctask/AsyncTaskExecutor.java)
to delegate the execution. A default implementation is available as `AsyncTaskExecutor.DEFAULT_EXECUTOR`, which is used by `AsyncTask.execute()`.

```java
AsyncTaskExecutor.DEFAULT_EXECUTOR.execute(new MyAsyncTask());
```

## Testing

The `AsyncTaskExecutor` can come in handy while testing. When practicing dependency injection, you can inject a different implementation that executes the
`AsyncTask` synchronously. [TestAsyncTaskExecutor](https://github.com/Label305/AsyncTask-Android/blob/master/asynctask-test/src/main/java/com/label305/asynctask/TestAsyncTaskExecutor.java)
can be used for such tests. To access this class, add the following to your relevant `build.gradle` section:

```groovy
testCompile 'com.label305:asynctask-test:x.x.x' // For unit tests
androidTestCompile 'com.label305:asynctask-test:x.x.x' // For instrumentation tests
```

For example, suppose we have the following class for asynchronously retrieving a `User` from some web service:

```java
public class UserRetriever {

  private final WebService mWebService;

  private final AsyncTaskExecutor mExecutor;

  public UserRetriever(final WebService webService, final AsyncTaskExecutor executor) {
    mWebService = webService;
    mExecutor = executor;
  }

  /**
   * Retrieves the User asynchronously, and notifies given callback when the User is retrieved.
   */
  public void retrieveUser(final UserCallback callback) {
    mExecutor.execute(new RetrieveUserTask(callback));
  }

  public interface UserCallback {

    void onUserRetrieved(User user);
  }

  private class RetrieveUserTask extends SimpleAsyncTask<User> {

    private final UserCallback mCallback;

    private RetrieveUserTask(final UserCallback callback) {
      mCallback = callback;
    }

    @Override
    public User doInBackground() {
      return mWebService.retrieveUser();
    }

    @Override
    public void onSuccess(final User user) {
      mCallback.onUserRetrieved(user);
    }
  }
}
```

We can test this class using mock objects, in this case Mockito:

```java
public class UserRetrieverTest {

  @Test
  public void retrieveUser_retrievesUser_andNotifiesCallback() {
    /* Given */
    User user = new User();

    WebService webService = mock(WebService.class);
    when(webService.retrieveUser()).thenReturn(user);

    UserCallback callback = mock(UserCallback.class);

    UserRetriever userRetriever = new UserRetriever(webService, TestAsyncTaskExecutor.instance());

    /* When */
    userRetriever.retrieveUser(callback);

    /* Then */
    verify(callback).onUserRetrieved(user);
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
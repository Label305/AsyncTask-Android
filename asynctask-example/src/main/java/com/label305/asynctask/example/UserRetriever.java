/*
 * Copyright 2015 Label305
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.label305.asynctask.example;

import android.support.annotation.NonNull;
import com.label305.asynctask.AsyncTask;
import com.label305.asynctask.AsyncTaskExecutor;
import java.io.IOException;

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

    void onException(IOException e);
  }

  private class RetrieveUserTask extends AsyncTask<User, IOException> {

    private final UserCallback mCallback;

    private RetrieveUserTask(final UserCallback callback) {
      mCallback = callback;
    }

    @Override
    public User doInBackground() throws IOException {
      return mWebService.retrieveUser();
    }

    @Override
    public void onSuccess(final User user) {
      mCallback.onUserRetrieved(user);
    }

    @Override
    protected void onException(@NonNull final IOException e) {
      mCallback.onException(e);
    }
  }
}
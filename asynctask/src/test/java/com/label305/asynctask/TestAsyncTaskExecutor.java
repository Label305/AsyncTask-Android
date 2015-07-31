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

package com.label305.asynctask;

import android.os.Handler;
import android.support.annotation.NonNull;
import java.util.concurrent.Executor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Niek Haarman <niek@label305.com>
 */
public class TestAsyncTaskExecutor implements AsyncTaskExecutor {

  private static final TestAsyncTaskExecutor INSTANCE = new TestAsyncTaskExecutor();

  private final Executor mExecutor = new TestExecutor();

  private final Handler mHandler;

  private TestAsyncTaskExecutor() {
    mHandler = Mockito.mock(Handler.class);
    when(mHandler.post(any(Runnable.class)))
        .then(new TestAnswer());
  }

  @Override
  public <T, E extends Exception, A extends AsyncTask<T, E>> A execute(@NonNull final A task) {
    return task.execute(mExecutor, mHandler);
  }

  public static AsyncTaskExecutor instance() {
    return INSTANCE;
  }

  private static class TestExecutor implements Executor {

    @Override
    public void execute(@NonNull final Runnable command) {
      command.run();
    }
  }

  private static class TestAnswer implements Answer<Void> {

    @Override
    public Void answer(final InvocationOnMock invocation) throws Throwable {
      Runnable runnable = (Runnable) invocation.getArguments()[0];
      runnable.run();
      return null;
    }
  }
}

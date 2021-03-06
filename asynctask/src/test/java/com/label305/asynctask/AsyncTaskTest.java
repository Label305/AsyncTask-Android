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

import android.support.annotation.NonNull;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Matchers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@SuppressWarnings({ "rawtypes", "HardCodedStringLiteral", "ProhibitedExceptionThrown", "NewExceptionWithoutArguments" })
public class AsyncTaskTest {

  @Test
  public void emptyExecution() throws Exception {
    /* Given */
    TestAsyncTask task = spy(new TestAsyncTask());

    /* When */
    execute(task);

    /* Then */
    InOrder inOrder = inOrder(task);

    inOrder.verify(task).onPreExecute();
    inOrder.verify(task).doInBackground();
    inOrder.verify(task).onSuccess(Matchers.any(Void.class));
    inOrder.verify(task).onFinally();

    verify(task, never()).onCancelled();
    verify(task, never()).onException(any(Exception.class));
    verify(task, never()).onInterrupted(Matchers.any(InterruptedException.class));
    verify(task, never()).onRuntimeException(Matchers.any(RuntimeException.class));
  }

  @Test
  public void resultExecution() throws Exception {
    /* Given */
    final String result = "result";
    TestAsyncTask task = spy(new TestAsyncTask() {
      @Override
      protected Object doInBackground() {
        return result;
      }
    });

    /* When */
    execute(task);

    /* Then */
    InOrder inOrder = inOrder(task);

    inOrder.verify(task).onPreExecute();
    inOrder.verify(task).doInBackground();
    inOrder.verify(task).onSuccess(result);
    inOrder.verify(task).onFinally();

    verify(task, never()).onCancelled();
    verify(task, never()).onInterrupted(any(InterruptedException.class));
    verify(task, never()).onException(any(Exception.class));
    verify(task, never()).onRuntimeException(any(RuntimeException.class));
  }

  @Test
  public void cancelledExecution() throws Exception {
    /* Given */
    TestAsyncTask task = spy(new TestAsyncTask() {
      @Override
      protected void onPreExecute() {
        cancel();
      }
    });

    /* When */
    execute(task);

    /* Then */
    InOrder inOrder = inOrder(task);

    inOrder.verify(task).onPreExecute();
    inOrder.verify(task).onCancelled();
    inOrder.verify(task).onFinally();

    verify(task, never()).onSuccess(any());
    verify(task, never()).doInBackground();
    verify(task, never()).onInterrupted(any(InterruptedException.class));
    verify(task, never()).onException(any(Exception.class));
    verify(task, never()).onRuntimeException(any(RuntimeException.class));
  }

  @Test
  public void interruptCancelledExecution() throws Exception {
    /* Given */
    TestAsyncTask task = spy(new TestAsyncTask() {
      @Override
      protected void onPreExecute() {
        cancelInterrupt();
      }

      @Override
      protected void onInterrupted(@NonNull final InterruptedException e) {
      }
    });

    /* When */
    execute(task);

    /* Then */
    InOrder inOrder = inOrder(task);

    inOrder.verify(task).onPreExecute();
    inOrder.verify(task).onInterrupted(any(InterruptedException.class));
    inOrder.verify(task).onCancelled();
    inOrder.verify(task).onFinally();

    verify(task, never()).doInBackground();
    verify(task, never()).onSuccess(any());
    verify(task, never()).onException(any(Exception.class));
    verify(task, never()).onRuntimeException(any(RuntimeException.class));
  }

  @Test
  public void doInBackgroundExceptionThrown() throws Exception {
    /* Given */
    final Exception ex = new Exception();
    TestAsyncTask task = spy(new TestAsyncTask() {
      @Override
      protected Object doInBackground() throws Exception {
        throw ex;
      }
    });

    /* When */
    execute(task);

    /* Then */
    InOrder inOrder = inOrder(task);

    inOrder.verify(task).onPreExecute();
    inOrder.verify(task).doInBackground();
    inOrder.verify(task).onException(ex);
    inOrder.verify(task).onFinally();

    verify(task, never()).onRuntimeException(any(RuntimeException.class));
    verify(task, never()).onCancelled();
    verify(task, never()).onSuccess(any());
    verify(task, never()).onInterrupted(any(InterruptedException.class));
  }

  @Test
  public void preExecuteRuntimeExceptionThrown() throws Exception {
    /* Given */
    final RuntimeException rte = new RuntimeException();
    TestAsyncTask task = spy(new TestAsyncTask() {
      @Override
      protected void onPreExecute() {
        throw rte;
      }
    });

    /* When */
    try {
      execute(task);
      assertThat("Execute didn't throw", false, is(true));
    } catch (RuntimeException e) {
      assertThat(e, is(rte));
    }

    /* Then */
    InOrder inOrder = inOrder(task);

    inOrder.verify(task).onPreExecute();
    inOrder.verify(task).onRuntimeException(rte);
    inOrder.verify(task).onFinally();

    verify(task, never()).doInBackground();
    verify(task, never()).onSuccess(any());
    verify(task, never()).onCancelled();
    verify(task, never()).onInterrupted(any(InterruptedException.class));
    verify(task, never()).onException(any(Exception.class));
  }

  @Test
  public void successRuntimeExceptionThrown() throws Exception {
    /* Given */
    final RuntimeException rte = new RuntimeException();
    TestAsyncTask task = spy(new TestAsyncTask() {
      @Override
      protected Object doInBackground() throws Exception {
        throw rte;
      }
    });

    /* When */
    try {
      execute(task);
      assertThat("Execute didn't throw", false, is(true));
    } catch (RuntimeException e) {
      assertThat(e, is(rte));
    }

    /* Then */
    InOrder inOrder = inOrder(task);

    inOrder.verify(task).onPreExecute();
    inOrder.verify(task).doInBackground();
    inOrder.verify(task).onRuntimeException(rte);
    inOrder.verify(task).onFinally();

    verify(task, never()).onSuccess(any());
    verify(task, never()).onCancelled();
    verify(task, never()).onException(any(Exception.class));
  }

  @Test
  public void exceptionRuntimeExceptionThrown() throws Exception {
    /* Given */
    final RuntimeException rte = new RuntimeException();
    TestAsyncTask task = spy(new TestAsyncTask() {
      @Override
      protected Object doInBackground() throws Exception {
        throw new Exception();
      }

      @Override
      protected void onException(@NonNull final Exception e) {
        throw rte;
      }
    });

    /* When */
    try {
      execute(task);
      assertThat("Execute didn't throw", false, is(true));
    } catch (RuntimeException e) {
      assertThat(e, is(rte));
    }

    /* Then */
    InOrder inOrder = inOrder(task);

    inOrder.verify(task).onPreExecute();
    inOrder.verify(task).doInBackground();
    inOrder.verify(task).onException(any(Exception.class));
    inOrder.verify(task).onRuntimeException(rte);
    inOrder.verify(task).onFinally();

    verify(task, never()).onSuccess(any(Void.class));
    verify(task, never()).onCancelled();
    verify(task, never()).onInterrupted(any(InterruptedException.class));
  }

  @Test
  public void finallyRuntimeExceptionThrown() throws Exception {
    /* Given */
    final RuntimeException rte = new RuntimeException();
    TestAsyncTask task = spy(new TestAsyncTask() {
      @Override
      protected void onFinally() {
        throw rte;
      }
    });

    /* When */
    try {
      execute(task);
      assertThat("Execute didn't throw", false, is(true));
    } catch (RuntimeException e) {
      assertThat(e, is(rte));
    }

    /* Then */
    InOrder inOrder = inOrder(task);

    inOrder.verify(task).onPreExecute();
    inOrder.verify(task).doInBackground();
    inOrder.verify(task).onSuccess(any());
    inOrder.verify(task).onFinally();
    inOrder.verify(task).onRuntimeException(rte);

    verify(task, never()).onException(any(Exception.class));
    verify(task, never()).onCancelled();
    verify(task, never()).onInterrupted(any(InterruptedException.class));
  }

  @Test
  public void doInBackgroundRuntimeExceptionThrown() throws Exception {
    /* Given */
    final RuntimeException rte = new RuntimeException();
    TestAsyncTask task = spy(new TestAsyncTask() {
      @Override
      protected Object doInBackground() throws Exception {
        throw rte;
      }
    });

    /* When */
    try {
      execute(task);
      assertThat("Execute didn't throw", false, is(true));
    } catch (RuntimeException e) {
      assertThat(e, is(rte));
    }

    /* Then */
    InOrder inOrder = inOrder(task);

    inOrder.verify(task).onPreExecute();
    inOrder.verify(task).doInBackground();
    inOrder.verify(task).onRuntimeException(rte);
    inOrder.verify(task).onFinally();

    verify(task, never()).onException(any(Exception.class));
    verify(task, never()).onCancelled();
    verify(task, never()).onSuccess(any());
    verify(task, never()).onInterrupted(any(InterruptedException.class));
  }

  @Test
  public void onRuntimeException_RuntimeExceptionThrown() throws Exception {
    /* Given */
    final RuntimeException rte = new RuntimeException();
    TestAsyncTask task = spy(new TestAsyncTask() {

      @Override
      protected void onPreExecute() {
        throw rte;
      }

      @Override
      protected void onRuntimeException(final RuntimeException e) {
        throw e;
      }
    });

    /* When */
    try {
      execute(task);
      assertThat("Execute didn't throw", false, is(true));
    } catch (RuntimeException e) {
      assertThat(e, is(rte));
    }

    /* Then */
    InOrder inOrder = inOrder(task);

    inOrder.verify(task).onPreExecute();
    inOrder.verify(task).onRuntimeException(rte);
    inOrder.verify(task).onFinally();

    verify(task, never()).onException(any(Exception.class));
    verify(task, never()).onCancelled();
    verify(task, never()).onSuccess(any());
    verify(task, never()).onInterrupted(any(InterruptedException.class));
  }

  private static void execute(final TestAsyncTask task) {
    TestAsyncTaskExecutor.instance().execute(task);
  }

  private static class TestAsyncTask<T, E extends Exception> extends AsyncTask<T, E> {

    @Override
    protected T doInBackground() throws E {
      return null;
    }

    @Override
    protected void onException(@NonNull final E e) {
    }
  }
}
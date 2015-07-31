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
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

/**
 * @author Niek Haarman <niek@label305.com>
 */
class Task<T, E extends Exception> implements Runnable {

  @NonNull
  private final AsyncTask<T, E> mParent;

  @NonNull
  private final Handler mHandler;

  Task(@NonNull final AsyncTask<T, E> parent, @NonNull final Handler handler) {
    mParent = parent;
    mHandler = handler;
  }

  @SuppressWarnings("NestedTryStatement")
  @Override
  public void run() {
    try {
      doPreExecute();

      if (mParent.isCancelled()) {
        doCancel();
        return;
      }

      boolean success = false;
      RuntimeException runtimeException = null;
      T result = null;

      //noinspection OverlyBroadCatchBlock
      try {
        result = doDoInBackground();
        success = true;
      } catch (RuntimeException e) {
        runtimeException = e;
      } catch (Exception e) {
        doException((E) e);
      }

      if (runtimeException != null) {
        //noinspection ProhibitedExceptionThrown
        throw runtimeException;
      }

      if (success) {
        if (mParent.isCancelled()) {
          doCancel();
        } else {
          doSuccess(result);
        }
      }
    } catch (RuntimeException e) {
      doRuntimeException(e);
    } finally {
      try {
        doFinally();
      } catch (RuntimeException e) {
        doRuntimeException(e);
      }
    }
  }

  private void doPreExecute() {
    postToUiThreadAndWait(
        new Runnable() {
          @Override
          public void run() {
            mParent.onPreExecute();
          }
        }
    );
  }

  @Nullable
  private T doDoInBackground() throws E { // hihi doodoo
    return mParent.doInBackground();
  }

  private void doSuccess(@Nullable final T r) {
    postToUiThreadAndWait(
        new Runnable() {
          @Override
          public void run() {
            mParent.onSuccess(r);
          }
        }
    );
  }

  private void doCancel() {
    postToUiThreadAndWait(
        new Runnable() {
          @Override
          public void run() {
            mParent.onCancelled();
          }
        }
    );
  }

  private void doRuntimeException(@NonNull final RuntimeException e) {
    fixStackTrace(e);
    mHandler.post(
        new Runnable() {
          @Override
          public void run() {
            mParent.onRuntimeException(e);
          }
        }
    );
  }

  private void doException(@NonNull final E e) {
    fixStackTrace(e);
    mHandler.post(new Runnable() {
      @Override
      public void run() {
        mParent.onException(e);
      }
    });
  }

  private void fixStackTrace(@NonNull final Exception e) {
    StackTraceElement[] launchLocation = mParent.getLaunchLocation();
    if (launchLocation != null) {
      final ArrayList<StackTraceElement> stack = new ArrayList<>(Arrays.asList(e.getStackTrace()));
      stack.addAll(Arrays.asList(launchLocation));
      e.setStackTrace(stack.toArray(new StackTraceElement[stack.size()]));
    }
  }

  private void doFinally() {
    postToUiThreadAndWait(
        new Runnable() {
          @Override
          public void run() {
            mParent.onFinally();
          }
        }
    );
  }

  /**
   * Posts the specified runnable to the UI thread using a handler, and
   * waits for operation to finish.
   *
   * @param runnable the runnable to post
   */
  private void postToUiThreadAndWait(@NonNull final Runnable runnable) {
    final RuntimeException[] exceptions = new RuntimeException[1];
    final CountDownLatch latch = new CountDownLatch(1);

    // Execute the runnable in the UI thread, but wait for it to complete.
    mHandler.post(
        new Runnable() {
          @Override
          public void run() {
            try {
              runnable.run();
            } catch (RuntimeException e) {
              exceptions[0] = e;
            } finally {
              latch.countDown();
            }
          }
        }
    );

    // Wait for onSuccess to finish
    try {
      latch.await();
    } catch (@NonNull final InterruptedException e) {
      mHandler.post(
          new Runnable() {
            @Override
            public void run() {
              mParent.onInterrupted(e);
            }
          }
      );
    }

    if (exceptions[0] != null) {
      //noinspection ProhibitedExceptionThrown
      throw exceptions[0];
    }
  }
}
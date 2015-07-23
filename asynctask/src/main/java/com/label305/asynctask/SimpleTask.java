package com.label305.asynctask;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

class SimpleTask<T> implements Runnable {

  @NonNull
  private final SimpleAsyncTask<T> mParent;

  @NonNull
  private final Handler mHandler;

  SimpleTask(@NonNull final SimpleAsyncTask<T> parent) {
    mParent = parent;
    mHandler = parent.getHandler() != null ? parent.getHandler() : new Handler(Looper.getMainLooper());
  }

  @SuppressWarnings("NestedTryStatement")
  @Override
  public void run() {
    try {
      doPreExecute();

      boolean success = false;
      T result = null;
      //noinspection OverlyBroadCatchBlock
      try {
        result = doDoInBackgroundSimple();
        success = true;
      } catch (RuntimeException e) {
        doRuntimeException(e);
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

  protected final void doPreExecute() {
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
  private T doDoInBackgroundSimple() { // hihi doodoo
    return mParent.doInBackgroundSimple();
  }

  protected final void doSuccess(@Nullable final T r) {
    postToUiThreadAndWait(
        new Runnable() {
          @Override
          public void run() {
            mParent.onSuccess(r);
          }
        }
    );
  }

  protected final void doCancel() {
    postToUiThreadAndWait(
        new Runnable() {
          @Override
          public void run() {
            mParent.onCancelled();
          }
        }
    );
  }

  protected final void doRuntimeException(@NonNull final RuntimeException e) {
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

  void fixStackTrace(@NonNull final Exception e) {
    StackTraceElement[] launchLocation = mParent.getLaunchLocation();
    if (launchLocation != null) {
      final ArrayList<StackTraceElement> stack = new ArrayList<>(Arrays.asList(e.getStackTrace()));
      stack.addAll(Arrays.asList(launchLocation));
      e.setStackTrace(stack.toArray(new StackTraceElement[stack.size()]));
    }
  }

  protected final void doFinally() {
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
  protected final void postToUiThreadAndWait(@NonNull final Runnable runnable) {
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
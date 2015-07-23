package com.label305.asynctask;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

class OldTask<ResultT, E extends Exception> implements Runnable {

  @NonNull
  private final OldAsyncTask<ResultT, E> mParent;

  @NonNull
  private final Handler mHandler;

  OldTask(@NonNull final OldAsyncTask<ResultT, E> parent) {
    mParent = parent;
    mHandler = parent.getHandler() != null ? parent.getHandler() : new Handler(Looper.getMainLooper());
  }

  @SuppressWarnings("NestedTryStatement")
  @Override
  public void run() {
    try {
      doPreExecute();

      boolean success = false;
      ResultT result = null;
      //noinspection OverlyBroadCatchBlock
      try {
        result = doDoInBackground();
        success = true;
      } catch (RuntimeException e) {
        doRuntimeException(e);
      } catch (final Exception e) {
        doException((E) e);
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

  protected void doPreExecute() {
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
  protected ResultT doDoInBackground() throws E { // hihi doodoo
    //noinspection OverlyBroadCatchBlock
    try {
      return mParent.doInBackground();
    } catch (Exception e) {
      throw (E) e;
    }
  }

  protected void doSuccess(@Nullable final ResultT r) {
    postToUiThreadAndWait(
        new Runnable() {
          @Override
          public void run() {
            mParent.onSuccess(r);
          }
        }
    );
  }

  protected void doCancel() {
    postToUiThreadAndWait(
        new Runnable() {
          @Override
          public void run() {
            mParent.onCancelled();
          }
        }
    );
  }

  protected void doException(@NonNull final E e) {
    fixStackTrace(e);
    postToUiThreadAndWait(
        new Runnable() {
          @Override
          public void run() {
            mParent.onException(e);
          }
        }
    );
  }

  protected void doRuntimeException(@NonNull final RuntimeException e) {
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

  private void fixStackTrace(@NonNull final Exception e) {
    StackTraceElement[] launchLocation = mParent.getLaunchLocation();
    if (launchLocation != null) {
      final ArrayList<StackTraceElement> stack = new ArrayList<>(Arrays.asList(e.getStackTrace()));
      stack.addAll(Arrays.asList(launchLocation));
      e.setStackTrace(stack.toArray(new StackTraceElement[stack.size()]));
    }
  }

  protected void doFinally() {
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
  protected void postToUiThreadAndWait(@NonNull final Runnable runnable) {
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
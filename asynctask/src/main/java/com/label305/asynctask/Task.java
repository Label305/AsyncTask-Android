package com.label305.asynctask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

class Task<ResultT, E extends Exception> extends SimpleTask<ResultT> {

  @NonNull
  private final AsyncTask<ResultT, E> mParent;

  Task(@NonNull final AsyncTask<ResultT, E> parent) {
    super(parent);
    mParent = parent;
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

  @Nullable
  private ResultT doDoInBackground() throws E { // hihi doodoo
    //noinspection OverlyBroadCatchBlock
    try {
      return mParent.doInBackground();
    } catch (Exception e) {
      throw (E) e;
    }
  }

  private void doException(@NonNull final E e) {
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
}
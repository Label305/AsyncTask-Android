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
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

/**
 * A class similar but unrelated to Android's {@link android.os.AsyncTask},
 * loosely based on RoboGuice's AsyncTask.
 *
 * Unlike AsyncTask, this class properly propagates exceptions, and unlike
 * RoboGuice's AsyncTask, does not catch RuntimeExceptions.
 *
 * @param <T> the type of the result.
 * @param <E> the type of the Exception {@link #doInBackground()} may throw.
 *
 * @author Niek Haarman <niek@label305.com>
 */
public abstract class AsyncTask<T, E extends Exception> {

  @Nullable
  private StackTraceElement[] mLaunchLocation;

  @Nullable
  private FutureTask<Void> mFutureTask;

  /**
   * Executes this AsyncTask, using {@link AsyncTaskExecutor#DEFAULT_EXECUTOR}.
   *
   * @return this instance.
   */
  @NonNull
  public <A extends AsyncTask<T, E>> A execute() {
    return (A) AsyncTaskExecutor.DEFAULT_EXECUTOR.execute(this);
  }

  /**
   * Executes this AsyncTask, using given Executor and Handler.
   *
   * @param executor The Executor to perform background operations on.
   * @param handler The Handler to perform main thread callbacks on.
   *
   * @return this instance.
   */
  @NonNull
  public <A extends AsyncTask<T, E>> A execute(@NonNull final Executor executor,
                                               @NonNull final Handler handler) {
    mLaunchLocation = Thread.currentThread().getStackTrace();
    mFutureTask = new FutureTask<>(new Task<>(this, handler), null);
    executor.execute(mFutureTask);
    return (A) this;
  }

  /**
   * Executes this AsyncTask, using given Executor and Handler.
   *
   * @param executor The Executor to perform background operations on.
   * @return this instance.
   */
  @NonNull
  <A extends AsyncTask<T, E>> A execute(@NonNull final Executor executor,
                                        @NonNull final FutureTask<Void> futureTask) {
    mLaunchLocation = Thread.currentThread().getStackTrace();
    mFutureTask = futureTask;
    executor.execute(futureTask);
    return (A) this;
  }

  /**
   * Attempts to cancel execution of this task.  This attempt will
   * fail if the task has already completed, has already been cancelled,
   * or could not be cancelled for some other reason. If successful,
   * and this task has not started when {@code cancel} is called,
   * this task should never run.  If the task has already started,
   * then the {@code mayInterruptIfRunning} parameter determines
   * whether the thread executing this task should be interrupted in
   * an attempt to stop the task.
   *
   * After this method returns, subsequent calls to {@link #isCancelled}
   * will always return {@code true} if this method returned {@code true}.
   *
   * Use this method if in-progress tasks are allowed
   * to complete. Otherwise, use {@link #cancelInterrupt()}.
   *
   * @return {@code false} if the task could not be cancelled,
   * typically because it has already completed normally;
   * {@code true} otherwise
   */
  public boolean cancel() {
    return cancel(false);
  }

  /**
   * Attempts to cancel execution of this task.  This attempt will
   * fail if the task has already completed, has already been cancelled,
   * or could not be cancelled for some other reason. If successful,
   * and this task has not started when {@code cancel} is called,
   * this task should never run.  If the task has already started,
   * then the {@code mayInterruptIfRunning} parameter determines
   * whether the thread executing this task should be interrupted in
   * an attempt to stop the task.
   *
   * After this method returns, subsequent calls to {@link #isCancelled}
   * will always return {@code true} if this method returned {@code true}.
   *
   * Use this method if the thread executing this
   * task should be interrupted. Otherwise, use {@link #cancel()}.
   *
   * @return {@code false} if the task could not be cancelled,
   * typically because it has already completed normally;
   * {@code true} otherwise
   */
  public boolean cancelInterrupt() {
    return cancel(true);
  }

  private boolean cancel(final boolean mayInterruptIfRunning) {
    if (mFutureTask == null) {
      throw new IllegalStateException("You cannot cancel this task before calling execute()");
    }

    return mFutureTask.cancel(mayInterruptIfRunning);
  }

  /**
   * Returns whether this task was cancelled.
   */
  public boolean isCancelled() {
    if (mFutureTask == null) {
      throw new IllegalStateException("You cannot cancel this task before calling execute()");
    }

    return mFutureTask.isCancelled();
  }

  @Nullable
  StackTraceElement[] getLaunchLocation() {
    if (mLaunchLocation == null) {
      return null;
    }

    StackTraceElement[] result = new StackTraceElement[mLaunchLocation.length];
    System.arraycopy(mLaunchLocation, 0, result, 0, mLaunchLocation.length);
    return result;
  }

  /**
   * Called before the asynchronous {@link #doInBackground()} method, on the main thread.
   */
  @MainThread
  protected void onPreExecute() {
  }

  /**
   * Override this method to do heavy work on a background thread.
   *
   * @return A result, passed to {@link #onSuccess(T)}.
   *
   * @throws E
   */
  @WorkerThread
  protected abstract T doInBackground() throws E;

  /**
   * Override this method to perform a computation on a background thread. The
   * specified parameters are the parameters passed to {@link #execute}
   * by the caller of this task.
   *
   * This method can call {@link #publishProgress} to publish updates
   * on the UI thread.
   *
   * @param params The parameters of the task.
   *
   * @return A result, defined by the subclass of this task.
   *
   * @see #onPreExecute()
   * @see #onPostExecute
   * @see #publishProgress
   */

  /**
   * Called after the asynchronous {@link #doInBackground()} method, on the original thread, iff
   * {@link #doInBackground()} didn't throw an Exception, and this AsyncTask wasn't cancelled.
   * Will be called on the main thread.
   *
   * @param t the result of {@link #doInBackground()}
   */
  @MainThread
  protected void onSuccess(final T t) {
  }

  /**
   * Called when the task has been cancelled, on the main thread.
   */
  @MainThread
  protected void onCancelled() {
  }

  /**
   * Called when the thread has been interrupted, likely because the task was
   * cancelled.
   * <p/>
   * Will be called on the main thread.
   *
   * @param e an InterruptedException
   */
  @MainThread
  protected void onInterrupted(@NonNull final InterruptedException e) {
    //noinspection ProhibitedExceptionThrown
    throw new RuntimeException("Thread was interrupted. Override onInterrupted(InterruptedException) to manage behavior.", e);
  }

  /**
   * Called when {@link #doInBackground()} has thrown an Exception.
   *
   * Will be called on the main thread.
   *
   * @param e The Exception thrown.
   */
  @MainThread
  protected abstract void onException(@NonNull final E e);

  /**
   * Guaranteed to be called after all other methods, on the main thread.
   */
  @MainThread
  protected void onFinally() {
  }

  /**
   * Called when any method threw a RuntimeException. By default, rethrows the RuntimeException,
   * causing your app to crash.
   * Be very careful when overriding this method!
   * Will be called on the main thread.
   *
   * @param e the RuntimeException thrown.
   */
  @MainThread
  protected void onRuntimeException(final RuntimeException e) {
    //noinspection ProhibitedExceptionThrown
    throw e;
  }
}
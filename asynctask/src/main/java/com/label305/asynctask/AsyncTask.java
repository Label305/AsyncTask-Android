package com.label305.asynctask;

import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.concurrent.Executor;

/**
 * A class similar but unrelated to Android's {@link android.os.AsyncTask},
 * loosely based on RoboGuice's OldAsyncTask.
 * <p/>
 * Unlike OldAsyncTask, this class properly propagates exceptions, and unlike
 * RoboGuice's OldAsyncTask, does not catch RuntimeExceptions.
 * <p/>
 * If using your own mExecutor, you must call mFutureTask() to get a runnable you can
 * execute.
 *
 * @param <T> the type of the result.
 * @param <E> the type of the Exception that may be thrown.
 */
public abstract class AsyncTask<T, E extends Exception> extends SimpleAsyncTask<T> {

  /**
   * A Runnable that can be set to execute on exception.
   */
  @Nullable
  private ExceptionRunnable<E> mOnExceptionRunnable;

  /**
   * Create a new OldAsyncTask.
   * Sets mExecutor to Executors.newFixedThreadPool(DEFAULT_POOL_SIZE) and
   * Handler to new Handler()
   */
  protected AsyncTask() {
  }

  /**
   * Create a new OldAsyncTask with given Handler.
   * Sets mExecutor to Executors.newFixedThreadPool(DEFAULT_POOL_SIZE)
   */
  protected AsyncTask(@Nullable final Handler handler) {
    super(handler);
  }

  /**
   * Create a new OldAsyncTask with given Executor.
   * Sets Handler to new Handler()
   */
  protected AsyncTask(@NonNull final Executor executor) {
    super(executor);
  }

  /**
   * Create a new OldAsyncTask. with given Handler and Executor.
   */
  protected AsyncTask(@Nullable final Handler handler, @NonNull final Executor executor) {
    super(handler, executor);
  }

  @NonNull
  @Override
  public <A extends SimpleAsyncTask<T>> A execute() {
    return execute(new Task<>(this));
  }

  @Override
  protected final T doInBackgroundSimple() {
    throw new UnsupportedOperationException("Call doInBackground");
  }

  protected abstract T doInBackground() throws E;

  /**
   * Called when the {@link #doInBackgroundSimple()} threw an Exception, on the main thread.
   *
   * @param e the exception thrown from {@link #doInBackground()}.
   */
  @MainThread
  protected void onException(@NonNull final E e) {
    if (mOnExceptionRunnable != null) {
      mOnExceptionRunnable.onException(e);
    }
  }

  /**
   * Sets a {@link ExceptionRunnable} that is executed when the task goes through its exception phase.
   * This has the same effect as overriding {@link #onException(E)}.
   *
   * @return this instance.
   */
  @NonNull
  public AsyncTask<T, E> onException(@NonNull final ExceptionRunnable<E> runnable) {
    mOnExceptionRunnable = runnable;
    return this;
  }

  public interface ExceptionRunnable<E extends Throwable> {

    @MainThread
    void onException(@NonNull E e);
  }
}
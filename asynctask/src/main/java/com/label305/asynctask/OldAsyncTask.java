package com.label305.asynctask;

import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

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
public abstract class OldAsyncTask<T, E extends Exception> {

  private static final int DEFAULT_POOL_SIZE = 25;

  private static final Executor DEFAULT_EXECUTOR = Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);

  private static final String CANCEL_EXCEPTION = "You cannot cancel this task before calling mFutureTask()";

  @Nullable
  private Handler mHandler;

  @NonNull
  private Executor mExecutor;

  @Nullable
  private StackTraceElement[] mLaunchLocation;

  @Nullable
  private FutureTask<Void> mFutureTask;

  /**
   * A Runnable that can be set to execute on preexecute.
   */
  @Nullable
  private PreExecuteRunnable mOnPreExecuteRunnable;

  /**
   * A Runnable that can be set to execute on success.
   */
  @Nullable
  private SuccessRunnable<T, E> mOnSuccessRunnable;

  /**
   * A Runnable that can be set to execute on cancelled.
   */
  @Nullable
  private CancelledRunnable mOnCancelledRunnable;

  /**
   * A Runnable that can be set to execute on interrupted.
   */
  @Nullable
  private InterruptedRunnable mOnInterruptedRunnable;

  /**
   * A Runnable that can be set to execute on exception.
   */
  @Nullable
  private ExceptionRunnable<E> mOnExceptionRunnable;

  /**
   * A Runnable that can be set to execute on finally.
   */
  @Nullable
  private FinallyRunnable mOnFinallyRunnable;

  /**
   * Create a new OldAsyncTask.
   * Sets mExecutor to Executors.newFixedThreadPool(DEFAULT_POOL_SIZE) and
   * Handler to new Handler()
   */
  protected OldAsyncTask() {
    mExecutor = DEFAULT_EXECUTOR;
  }

  /**
   * Create a new OldAsyncTask with given Handler.
   * Sets mExecutor to Executors.newFixedThreadPool(DEFAULT_POOL_SIZE)
   */
  protected OldAsyncTask(@Nullable final Handler handler) {
    mHandler = handler;
    mExecutor = DEFAULT_EXECUTOR;
  }

  /**
   * Create a new OldAsyncTask with given Executor.
   * Sets Handler to new Handler()
   */
  protected OldAsyncTask(@NonNull final Executor executor) {
    mExecutor = executor;
  }

  /**
   * Create a new OldAsyncTask. with given Handler and Executor.
   */
  protected OldAsyncTask(@Nullable final Handler handler, @NonNull final Executor executor) {
    mHandler = handler;
    mExecutor = executor;
  }

  @NonNull
  public <A extends OldAsyncTask<T, E>> A execute() {
    return execute(new OldTask<>(this));
  }

  @NonNull
  public <A extends OldAsyncTask<T, E>> A execute(@NonNull final OldTask<T, E> task) {
    mLaunchLocation = Thread.currentThread().getStackTrace();
    mFutureTask = new FutureTask<>(task, null);
    mExecutor.execute(mFutureTask);
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
   * <p/>
   * <p>After this method returns, subsequent calls to {@link #isCancelled}
   * will always return {@code true} if this method returned {@code true}.
   * <p/>
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
   * <p/>
   * <p>After this method returns, subsequent calls to {@link #isCancelled}
   * will always return {@code true} if this method returned {@code true}.
   * <p/>
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
      throw new UnsupportedOperationException(CANCEL_EXCEPTION);
    }

    return mFutureTask.cancel(mayInterruptIfRunning);
  }

  /**
   * Returns whether this task was cancelled.
   */
  public boolean isCancelled() {
    if (mFutureTask == null) {
      throw new UnsupportedOperationException(CANCEL_EXCEPTION);
    }
    return mFutureTask.isCancelled();
  }

  @Nullable
  public Handler getHandler() {
    return mHandler;
  }

  public void setHandler(@Nullable final Handler handler) {
    mHandler = handler;
  }

  @Nullable
  public StackTraceElement[] getLaunchLocation() {
    if (mLaunchLocation == null) {
      return null;
    }

    StackTraceElement[] result = new StackTraceElement[mLaunchLocation.length];
    System.arraycopy(mLaunchLocation, 0, result, 0, mLaunchLocation.length);
    return result;
  }

  public void setLaunchLocation(@Nullable final StackTraceElement[] launchLocation) {
    mLaunchLocation = launchLocation;
  }

  @NonNull
  public Executor getExecutor() {
    return mExecutor;
  }

  @NonNull
  public OldAsyncTask<T, E> setExecutor(@NonNull final Executor executor) {
    mExecutor = executor;
    return this;
  }

  /**
   * Called before the asynchronous {@link #doInBackground()} method, on the main thread.
   */
  @MainThread
  protected void onPreExecute() {
    if (mOnPreExecuteRunnable != null) {
      mOnPreExecuteRunnable.onPreExecute();
    }
  }

  protected abstract T doInBackground() throws E;

  /**
   * Called after the asynchronous {@link #doInBackground()} method, on the original thread, iff
   * {@link #doInBackground()} didn't throw an Exception, and this OldAsyncTask wasn't cancelled.
   * Will be called on the main thread.
   *
   * @param t the result of {@link #doInBackground()}
   */
  @MainThread
  protected void onSuccess(@Nullable final T t) {
    if (mOnSuccessRunnable != null) {
      mOnSuccessRunnable.onSuccess(t);
    }
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
    if (mOnInterruptedRunnable != null) {
      mOnInterruptedRunnable.onInterrupted(e);
    } else {
      //noinspection ProhibitedExceptionThrown
      throw new RuntimeException("Thread was interrupted. Override onInterrupted(InterruptedException) to manage behavior.", e);
    }
  }

  /**
   * Called when the task has been cancelled, on the main thread.
   */
  @MainThread
  protected void onCancelled() {
    if (mOnCancelledRunnable != null) {
      mOnCancelledRunnable.onCancelled();
    }
  }

  /**
   * Called when the {@link #doInBackground()} threw an Exception, on the main thread.
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

  /**
   * Guaranteed to be called after all other methods, on the main thread.
   */
  @MainThread
  protected void onFinally() {
    if (mOnFinallyRunnable != null) {
      mOnFinallyRunnable.onFinally();
    }
  }

  /**
   * Sets a {@link PreExecuteRunnable} that is executed when the task goes through its pre execute phase.
   * This has the same effect as overriding {@link #onPreExecute()}.
   *
   * @return this instance.
   */
  @NonNull
  public OldAsyncTask<T, E> onPreExecute(@NonNull final PreExecuteRunnable runnable) {
    mOnPreExecuteRunnable = runnable;
    return this;
  }

  /**
   * Sets a {@link SuccessRunnable} that is executed when the task goes through its success phase.
   * This has the same effect as overriding {@link #onSuccess(Object)}.
   *
   * @return this instance.
   */
  @NonNull
  public OldAsyncTask<T, E> onSuccess(@NonNull final SuccessRunnable<T, E> runnable) {
    mOnSuccessRunnable = runnable;
    return this;
  }

  /**
   * Sets a {@link CancelledRunnable} that is executed when the task goes through its cancelled phase.
   * This has the same effect as overriding {@link #onCancelled()}.
   *
   * @return this instance.
   */
  @NonNull
  public OldAsyncTask<T, E> onCancelled(@NonNull final CancelledRunnable runnable) {
    mOnCancelledRunnable = runnable;
    return this;
  }

  /**
   * Sets a {@link InterruptedRunnable} that is executed when the task goes through its interrupted phase.
   * This has the same effect as overriding {@link #onInterrupted(InterruptedException)}.
   *
   * @return this instance.
   */
  @NonNull
  public OldAsyncTask<T, E> onInterrupted(@NonNull final InterruptedRunnable runnable) {
    mOnInterruptedRunnable = runnable;
    return this;
  }

  /**
   * Sets a {@link ExceptionRunnable} that is executed when the task goes through its exception phase.
   * This has the same effect as overriding {@link #onException(E)}.
   *
   * @return this instance.
   */
  @NonNull
  public OldAsyncTask<T, E> onException(@NonNull final ExceptionRunnable<E> runnable) {
    mOnExceptionRunnable = runnable;
    return this;
  }

  /**
   * Sets a {@link FinallyRunnable} that is executed when the task goes through its finally phase.
   * This has the same effect as overriding {@link #onFinally()}.
   *
   * @return this instance.
   */
  @NonNull
  public OldAsyncTask<T, E> onFinally(@NonNull final FinallyRunnable runnable) {
    mOnFinallyRunnable = runnable;
    return this;
  }

  public interface PreExecuteRunnable {

    @MainThread
    void onPreExecute();
  }

  public interface SuccessRunnable<T, E> {

    @MainThread
    void onSuccess(@Nullable T r);
  }

  public interface CancelledRunnable {

    @MainThread
    void onCancelled();
  }

  public interface InterruptedRunnable {

    @MainThread
    void onInterrupted(@NonNull InterruptedException e);
  }

  public interface ExceptionRunnable<E extends Throwable> {

    @MainThread
    void onException(@NonNull E e);
  }

  public interface FinallyRunnable {

    @MainThread
    void onFinally();
  }
}
package com.label305.stan.async;

import android.os.Handler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * A class similar but unrelated to Android's {@link android.os.AsyncTask},
 * loosely based on RoboGuice's AsyncTask.
 * <p/>
 * Unlike AsyncTask, this class properly propagates exceptions, and unlike
 * RoboGuice's AsyncTask, does not catch RuntimeExceptions.
 * <p/>
 * If you're familiar with AsyncTask and are looking for
 * {@link android.os.AsyncTask#doInBackground(Object[])}, we've named it
 * {@link #call()} here to conform with java 1.5's
 * {@link java.util.concurrent.Callable} interface.
 * <p/>
 * If using your own mExecutor, you must call mFutureTask() to get a runnable you can
 * execute.
 *
 * @param <ResultT> the type of the result.
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class AsyncTask<ResultT> implements Callable<ResultT> {

    private static final int DEFAULT_POOL_SIZE = 25;

    private static final Executor DEFAULT_EXECUTOR = Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);

    private static final String CANCEL_EXCEPTION = "You cannot cancel this task before calling mFutureTask()";

    @Nullable
    private Handler mHandler;

    @NotNull
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
    private SuccessRunnable<ResultT> mOnSuccessRunnable;

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
    private ExceptionRunnable mOnExceptionRunnable;

    /**
     * A Runnable that can be set to execute on finally.
     */
    @Nullable
    private FinallyRunnable mOnFinallyRunnable;

    /**
     * Create a new AsyncTask.
     * Sets mExecutor to Executors.newFixedThreadPool(DEFAULT_POOL_SIZE) and
     * Handler to new Handler()
     */
    protected AsyncTask() {
        mExecutor = DEFAULT_EXECUTOR;
    }

    /**
     * Create a new AsyncTask with given Handler.
     * Sets mExecutor to Executors.newFixedThreadPool(DEFAULT_POOL_SIZE)
     */
    protected AsyncTask(@Nullable final Handler handler) {
        mHandler = handler;
        mExecutor = DEFAULT_EXECUTOR;
    }

    /**
     * Create a new AsyncTask with given Executor.
     * Sets Handler to new Handler()
     */
    protected AsyncTask(@NotNull final Executor executor) {
        mExecutor = executor;
    }

    /**
     * Create a new AsyncTask. with given Handler and Executor.
     */
    protected AsyncTask(@Nullable final Handler handler, @NotNull final Executor executor) {
        mHandler = handler;
        mExecutor = executor;
    }

    @NotNull
    public AsyncTask<ResultT> execute() {
        return execute(new Task<>(this));
    }

    @NotNull
    public AsyncTask<ResultT> execute(@NotNull final Task<ResultT> task) {
        mLaunchLocation = Thread.currentThread().getStackTrace();
        mFutureTask = new FutureTask<>(task, null);
        mExecutor.execute(mFutureTask);
        return this;
    }

    @Override
    @Nullable
    public abstract ResultT call() throws Exception;

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
        return mLaunchLocation;
    }

    public void setLaunchLocation(@Nullable final StackTraceElement[] launchLocation) {
        mLaunchLocation = launchLocation;
    }

    @NotNull
    public Executor getExecutor() {
        return mExecutor;
    }

    @NotNull
    public AsyncTask<ResultT> setExecutor(@NotNull final Executor executor) {
        mExecutor = executor;
        return this;
    }

    /* Callback methods */

    /**
     * Called before the asynchronous {@link #call()} method, on the original thread.
     */
    protected void onPreExecute() {
        if (mOnPreExecuteRunnable != null) {
            mOnPreExecuteRunnable.onPreExecute();
        }
    }

    /**
     * Called after the asynchronous {@link #call()} method, on the original thread, iff
     * {@link #call()} didn't throw an Exception, and this AsyncTask wasn't cancelled.
     * Will be called on the original thread.
     *
     * @param t the result of {@link #call()}
     */
    protected void onSuccess(@Nullable final ResultT t) {
        if (mOnSuccessRunnable != null) {
            mOnSuccessRunnable.onSuccess(t);
        }
    }

    /**
     * Called when the thread has been interrupted, likely because the task was
     * cancelled.
     * <p/>
     * By default, calls {@link #onException(Exception)}, but this method may be
     * overridden to handle interruptions differently than other exceptions.
     * Will be called on the original thread.
     *
     * @param e an InterruptedException
     */
    protected void onInterrupted(@NotNull final InterruptedException e) {
        if (mOnInterruptedRunnable != null) {
            mOnInterruptedRunnable.onInterrupted(e);
        } else {
            onException(e);
        }
    }

    /**
     * Called when the task has been cancelled, on the original thread.
     */
    protected void onCancelled() {
        if (mOnCancelledRunnable != null) {
            mOnCancelledRunnable.onCancelled();
        }
    }

    /**
     * Called when the {@link #call()} threw an Exception, on the original thread.
     *
     * @param e the exception thrown from {@link #call()}.
     */
    protected void onException(@NotNull final Exception e) {
        if (mOnExceptionRunnable != null) {
            mOnExceptionRunnable.onException(e);
        }
    }

    /**
     * Called when any method threw a RuntimeException. By default, rethrows the RuntimeException,
     * causing your app to crash.
     * Be very careful when overriding this method!
     * Will be called on the original thread.
     *
     * @param e the RuntimeException thrown.
     */
    protected void onRuntimeException(final RuntimeException e) {
        //noinspection ProhibitedExceptionThrown
        throw e;
    }

    /**
     * Guaranteed to be called after all other methods, on the original thread.
     */
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
    @NotNull
    public AsyncTask<ResultT> onPreExecute(@NotNull final PreExecuteRunnable runnable) {
        mOnPreExecuteRunnable = runnable;
        return this;
    }

    /**
     * Sets a {@link SuccessRunnable} that is executed when the task goes through its success phase.
     * This has the same effect as overriding {@link #onSuccess(Object)}.
     *
     * @return this instance.
     */
    @NotNull
    public AsyncTask<ResultT> onSuccess(@NotNull final SuccessRunnable<ResultT> runnable) {
        mOnSuccessRunnable = runnable;
        return this;
    }

    /**
     * Sets a {@link CancelledRunnable} that is executed when the task goes through its cancelled phase.
     * This has the same effect as overriding {@link #onCancelled()}.
     *
     * @return this instance.
     */
    @NotNull
    public AsyncTask<ResultT> onCancelled(@NotNull final CancelledRunnable runnable) {
        mOnCancelledRunnable = runnable;
        return this;
    }

    /**
     * Sets a {@link InterruptedRunnable} that is executed when the task goes through its interrupted phase.
     * This has the same effect as overriding {@link #onInterrupted(InterruptedException)}.
     *
     * @return this instance.
     */
    @NotNull
    public AsyncTask<ResultT> onInterrupted(@NotNull final InterruptedRunnable runnable) {
        mOnInterruptedRunnable = runnable;
        return this;
    }

    /**
     * Sets a {@link ExceptionRunnable} that is executed when the task goes through its exception phase.
     * This has the same effect as overriding {@link #onException(Exception)}.
     *
     * @return this instance.
     */
    @NotNull
    public AsyncTask<ResultT> onException(@NotNull final ExceptionRunnable runnable) {
        mOnExceptionRunnable = runnable;
        return this;
    }

    /**
     * Sets a {@link FinallyRunnable} that is executed when the task goes through its finally phase.
     * This has the same effect as overriding {@link #onFinally()}.
     *
     * @return this instance.
     */
    @NotNull
    public AsyncTask<ResultT> onFinally(@NotNull final FinallyRunnable runnable) {
        mOnFinallyRunnable = runnable;
        return this;
    }

    public interface PreExecuteRunnable {

        void onPreExecute();
    }

    public interface SuccessRunnable<ResultT> {

        void onSuccess(@Nullable ResultT r);
    }

    public interface CancelledRunnable {

        void onCancelled();
    }

    public interface InterruptedRunnable {

        void onInterrupted(@NotNull InterruptedException e);
    }


    public interface ExceptionRunnable {

        void onException(@NotNull Exception e);
    }

    public interface FinallyRunnable {

        void onFinally();
    }
}
package com.label305.stan.asyncutils;

import android.os.Handler;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * A class similar but unrelated to Android's {@link android.os.AsyncTask},
 * loosely based on RoboGuice's AsyncTask.
 *
 * Unlike AsyncTask, this class properly propagates exceptions, and unlike
 * RoboGuice's AsyncTask, does not catch RuntimeExceptions.
 *
 * If you're familiar with AsyncTask and are looking for
 * {@link android.os.AsyncTask#doInBackground(Object[])}, we've named it
 * {@link #call()} here to conform with java 1.5's
 * {@link java.util.concurrent.Callable} interface.
 *
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

    private Handler mHandler;
    private Executor mExecutor;
    private StackTraceElement[] mLaunchLocation;
    private FutureTask<Void> mFutureTask;

    /**
     * Create a new AsyncTask.
     * Sets mExecutor to Executors.newFixedThreadPool(DEFAULT_POOL_SIZE) and
     * Handler to new Handler()
     */
    public AsyncTask() {
        mExecutor = DEFAULT_EXECUTOR;
    }

    /**
     * Create a new AsyncTask with given Handler.
     * Sets mExecutor to Executors.newFixedThreadPool(DEFAULT_POOL_SIZE)
     */
    public AsyncTask(Handler handler) {
        mHandler = handler;
        mExecutor = DEFAULT_EXECUTOR;
    }

    /**
     * Create a new AsyncTask with given Executor.
     * Sets Handler to new Handler()
     */
    public AsyncTask(Executor executor) {
        mExecutor = executor;
    }

    /**
     * Create a new AsyncTask. with given Handler and Executor.
     */
    public AsyncTask(Handler handler, Executor executor) {
        mHandler = handler;
        mExecutor = executor;
    }

    public AsyncTask<ResultT> execute() {
        return execute(new Task<ResultT>(this));
    }

    public AsyncTask<ResultT> execute(final Task<ResultT> task) {
        mLaunchLocation = Thread.currentThread().getStackTrace();
        mFutureTask = new FutureTask<Void>(task, null);
        mExecutor.execute(mFutureTask);
        return this;
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
     * <p>After this method returns, subsequent calls to {@link #isCancelled}
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
     * <p>After this method returns, subsequent calls to {@link #isCancelled}
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

    private boolean cancel(boolean mayInterruptIfRunning) {
        if (mFutureTask == null) {
            throw new UnsupportedOperationException(CANCEL_EXCEPTION);
        }

        return mFutureTask.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return mFutureTask.isCancelled();
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void setHandler(final Handler handler) {
        mHandler = handler;
    }

    public StackTraceElement[] getLaunchLocation() {
        return mLaunchLocation;
    }

    public void setLaunchLocation(final StackTraceElement[] launchLocation) {
        mLaunchLocation = launchLocation;
    }

    public AsyncTask<ResultT> setExecutor(Executor executor) {
        mExecutor = executor;
        return this;
    }

    public Executor getExecutor() {
        return mExecutor;
    }

    /* Callback methods */

    /**
     * Called before the asynchronous {@link #call()} method, on the original thread.
     */
    protected void onPreExecute() {
    }

    /**
     * Called after the asynchronous {@link #call()} method, on the original thread, iff
     * {@link #call()} didn't throw an Exception, and this AsyncTask wasn't cancelled.
     * Will be called on the original thread.
     *
     * @param t the result of {@link #call()}
     */
    protected void onSuccess(ResultT t) {
    }

    /**
     * Called when the thread has been interrupted, likely because the task was
     * cancelled.
     *
     * By default, calls {@link #onException(Exception)}, but this method may be
     * overridden to handle interruptions differently than other exceptions.
     * Will be called on the original thread.
     *
     * @param e an InterruptedException
     */
    protected void onInterrupted(InterruptedException e) {
        onException(e);
    }

    /**
     * Called when the task has been cancelled, on the original thread.
     */
    protected void onCancelled() {
    }

    /**
     * Called when the {@link #call()} threw an Exception, on the original thread.
     * @param e the exception thrown from {@link #call()}.
     */
    protected void onException(Exception e) {
    }

    /**
     * Called when any method threw a RuntimeException. By default, rethrows the RuntimeException,
     * causing your app to crash.
     * Be very careful when overriding this method!
     * Will be called on the original thread.
     *
     * @param e the RuntimeException thrown.
     */
    protected void onRuntimeException(RuntimeException e) {
        throw e;
    }

    /**
     * Guaranteed to be called after all other methods, on the original thread.
     */
    protected void onFinally() {
    }
}
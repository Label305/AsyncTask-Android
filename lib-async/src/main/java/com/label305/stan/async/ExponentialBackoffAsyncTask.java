package com.label305.stan.async;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * An AsyncTask which facilitates exponential backoff.
 */
public abstract class ExponentialBackoffAsyncTask<ResultT> extends AsyncTask<ResultT> {

    public static final int DEFAULT_MAX_TRY_COUNT = 1;

    private int mMaxTryCount;

    protected ExponentialBackoffAsyncTask() {
        mMaxTryCount = DEFAULT_MAX_TRY_COUNT;
    }

    /**
     * Returns whether we should retry the {@link #call()} method when given Exception occurred.
     * Defaults to {@code tryCount < getMaxTryCount()}.
     *
     * @param e        the Exception that occurred.
     * @param tryCount the number of tries already executed.
     *
     * @return true if we should retry.
     */
    protected boolean shouldRetry(@NotNull final Exception e, final int tryCount) {
        return e instanceof IOException && tryCount < mMaxTryCount;
    }

    public void setMaxTryCount(final int maxTryCount) {
        mMaxTryCount = maxTryCount;
    }

    @NotNull
    @SuppressWarnings("RefusedBequest")
    @Override
    public ExponentialBackoffAsyncTask<ResultT> execute() {
        return execute(new ExponentialBackoffTask<>(this));
    }

    @NotNull
    @Override
    public ExponentialBackoffAsyncTask<ResultT> execute(@NotNull final Task<ResultT> task) {
        return (ExponentialBackoffAsyncTask<ResultT>) super.execute(task);
    }
}

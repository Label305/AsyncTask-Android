package com.label305.stan.asyncutils;

/**
 * An AsyncTask which facilitates exponential backoff.
 */
public abstract class ExponentialBackoffAsyncTask<ResultT> extends AsyncTask<ResultT> {

    public static final int DEFAULT_MAX_TRY_COUNT = 3;

    /**
     * Returns whether we should retry the {@link #call()} method when given Exception occurred.
     * Defaults to {@code tryCount < getMaxTryCount()}.
     * @param e the Exception that occurred.
     * @param tryCount the number of tries already executed.
     * @return true if we should retry.
     */
    protected boolean shouldRetry(final Exception e, final int tryCount) {
        return tryCount < getMaxTryCount();
    }

    /**
     * Returns the maximum number of times this AsyncTask should try the {@link #call()} method. Defaults to {@value #DEFAULT_MAX_TRY_COUNT}.
     */
    protected int getMaxTryCount() {
        return DEFAULT_MAX_TRY_COUNT;
    }

    @SuppressWarnings("RefusedBequest")
    @Override
    public ExponentialBackoffAsyncTask<ResultT> execute() {
        return (ExponentialBackoffAsyncTask<ResultT>) execute(new ExponentialBackoffTask<ResultT>(this));
    }
}

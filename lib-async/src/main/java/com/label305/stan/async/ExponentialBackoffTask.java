package com.label305.stan.async;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ExponentialBackoffTask<ResultT> extends Task<ResultT> {

    private int mTryCount;

    private int mNextTryDelay;

    ExponentialBackoffTask(final ExponentialBackoffAsyncTask<ResultT> parent) {
        super(parent);
    }

    @Override
    public void run() {
        try {
            doPreExecute();

            try {
                ResultT result = executeCall();
                if (getParent().isCancelled()) {
                    doCancel();
                } else {
                    doSuccess(result);
                }
            } catch (CallExceptionThrownException ignored) {
                /* Exception handling is already done in executeCall */
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
    private ResultT executeCall() throws CallExceptionThrownException {
        try {
            Thread.sleep(getTryDelayMs());
        } catch (InterruptedException ignored) {
            /* We don't want our sleep method to be the cause of an unwanted exception, so we ignore this and continue */
        }
        mTryCount++;
        ResultT result = null;
        boolean failed = true;
        try {
            result = doCall();
            failed = false;
        } catch (RuntimeException e) {
            doRuntimeException(e);
        } catch (final Exception e) {
            if (getParent().shouldRetry(e, mTryCount)) {
                incrementTryDelay();
                executeCall();
                failed = false;
            } else {
                doException(e);
            }
        }

        if (failed) {
            throw new CallExceptionThrownException();
        }

        return result;
    }

    private long getTryDelayMs() {
        return mNextTryDelay;
    }

    private void incrementTryDelay() {
        mNextTryDelay = mNextTryDelay == 0 ? 500 : mNextTryDelay * 2;
    }

    @NotNull
    @Override
    protected ExponentialBackoffAsyncTask<ResultT> getParent() {
        return (ExponentialBackoffAsyncTask<ResultT>) super.getParent();
    }

    private static class CallExceptionThrownException extends Exception {

    }
}

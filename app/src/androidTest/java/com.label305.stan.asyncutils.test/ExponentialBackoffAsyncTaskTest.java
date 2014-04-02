package com.label305.safeasynctasktest.test;

import android.os.Looper;

import com.label305.stan.asyncutils.ExponentialBackoffAsyncTask;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CountDownLatch;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@SuppressWarnings({"MagicNumber", "AnonymousInnerClass", "AnonymousInnerClassWithTooManyMethods"})
public class ExponentialBackoffAsyncTaskTest extends TestCase {

    @Mock
    private ExponentialBackoffAsyncTaskCallback<Object> mExponentialBackoffAsyncTaskCallback;

    private CountDownLatch mCountDownLatch;

    private long mTestStartMillis;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        mCountDownLatch = new CountDownLatch(1);
        mTestStartMillis = System.currentTimeMillis();
    }

    public void testNormalExecution() throws InterruptedException {
        new TestExponentialBackoffAsyncTask(mExponentialBackoffAsyncTaskCallback, mCountDownLatch).execute();

        mCountDownLatch.await();

        verify(mExponentialBackoffAsyncTaskCallback, never()).onBackoffFailedException(any(Exception.class));
        verify(mExponentialBackoffAsyncTaskCallback, times(1)).call();

        assertMaxTestDuration(100L);
    }

    public void testDefaultExponentialBackoff() throws InterruptedException {
        final Exception e = new Exception();

        new TestExponentialBackoffAsyncTask(mExponentialBackoffAsyncTaskCallback, mCountDownLatch) {
            @Override
            public Object call() throws Exception {
                super.call();
                throw e;
            }
        }.execute();

        mCountDownLatch.await();

        verify(mExponentialBackoffAsyncTaskCallback).onBackoffFailedException(e);
        verify(mExponentialBackoffAsyncTaskCallback, times(ExponentialBackoffAsyncTask.DEFAULT_MAX_TRY_COUNT)).call();

        assertProperTestDuration(ExponentialBackoffAsyncTask.DEFAULT_MAX_TRY_COUNT);
    }

    public void testCustomTryCountExponentialBackoff() throws InterruptedException {
        final Exception e = new Exception();
        final int maxTryCount = 5;

        new TestExponentialBackoffAsyncTask(mExponentialBackoffAsyncTaskCallback, mCountDownLatch) {

            @Override
            public Object call() throws Exception {
                super.call();
                throw e;
            }

            @SuppressWarnings("RefusedBequest")
            @Override
            protected int getMaxTryCount() {
                return maxTryCount;
            }
        }.execute();

        mCountDownLatch.await();

        verify(mExponentialBackoffAsyncTaskCallback).onBackoffFailedException(e);
        verify(mExponentialBackoffAsyncTaskCallback, times(maxTryCount)).call();

        assertProperTestDuration(maxTryCount);
    }

    private void assertProperTestDuration(final int tryCount) {
        assertMinTestDuration(calculateMinimumDuration(tryCount));
        assertMaxTestDuration(calculateMinimumDuration(tryCount) + 100L);
    }

    private static int calculateMinimumDuration(final int tryCount) {
        return tryCount == 1 ? 0 : (int) (calculateMinimumDuration(tryCount - 1) + 500 * StrictMath.pow(2, tryCount - 2));
    }

    private void assertMinTestDuration(final long maxDurationMs) {
        assertThat(System.currentTimeMillis() - mTestStartMillis, greaterThan(maxDurationMs));
    }

    private void assertMaxTestDuration(final long maxDurationMs) {
        assertThat(System.currentTimeMillis() - mTestStartMillis, lessThan(maxDurationMs));
    }

    /**
     * A test AsyncTask which:
     * - Notifies a callback
     * - Counts down a CountDownLatch when finished
     * - Checks whether calls are made on the correct thread.
     */
    private static class TestExponentialBackoffAsyncTask extends ExponentialBackoffAsyncTask<Object> {

        private final ExponentialBackoffAsyncTaskCallback<Object> mExponentialBackoffAsyncTask;
        private final CountDownLatch mCountDownLatch;

        private TestExponentialBackoffAsyncTask(final ExponentialBackoffAsyncTaskCallback<Object> exponentialBackoffAsyncTask, final CountDownLatch countDownLatch) {
            mExponentialBackoffAsyncTask = exponentialBackoffAsyncTask;
            mCountDownLatch = countDownLatch;
        }

        @Override
        public Object call() throws Exception {
            mExponentialBackoffAsyncTask.call();
            assertThat(Looper.getMainLooper().getThread(), is(not(Thread.currentThread())));
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mExponentialBackoffAsyncTask.onPreExecute();

            assertThat(Looper.getMainLooper().getThread(), is(Thread.currentThread()));
        }

        @Override
        protected void onSuccess(final Object t) {
            assertThat(Looper.getMainLooper().getThread(), is(Thread.currentThread()));
            mExponentialBackoffAsyncTask.onSuccess(t);
        }

        @SuppressWarnings("RefusedBequest")
        @Override
        protected void onInterrupted(final InterruptedException e) {
            assertThat(Looper.getMainLooper().getThread(), is(Thread.currentThread()));
            mExponentialBackoffAsyncTask.onInterrupted(e);
        }

        @Override
        protected void onCancelled() {
            assertThat(Looper.getMainLooper().getThread(), is(Thread.currentThread()));
            mExponentialBackoffAsyncTask.onCancelled();
        }

        @Override
        protected void onException(final Exception e) {
            assertThat(Looper.getMainLooper().getThread(), is(Thread.currentThread()));
            mExponentialBackoffAsyncTask.onBackoffFailedException(e);
        }

        @Override
        protected void onRuntimeException(final RuntimeException e) {
            assertThat(Looper.getMainLooper().getThread(), is(Thread.currentThread()));
            mExponentialBackoffAsyncTask.onRuntimeException(e);
        }

        @Override
        protected void onFinally() {
            assertThat(Looper.getMainLooper().getThread(), is(Thread.currentThread()));
            mExponentialBackoffAsyncTask.onFinally();
            mCountDownLatch.countDown();
        }
    }

    @SuppressWarnings("InterfaceNeverImplemented")
    /** A callback interface to validate method calls using Mockito. */
    private interface ExponentialBackoffAsyncTaskCallback<T> {
        public void call();

        public void onPreExecute();

        public void onSuccess(T t);

        public void onInterrupted(Exception e);

        public void onCancelled();

        public void onBackoffFailedException(Exception e);

        public void onRuntimeException(RuntimeException e);

        public void onFinally();
    }
}

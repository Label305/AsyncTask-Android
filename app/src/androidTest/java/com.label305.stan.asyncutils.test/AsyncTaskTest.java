package com.label305.safeasynctasktest.test;

import android.os.Looper;

import com.label305.stan.asyncutils.AsyncTask;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CountDownLatch;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@SuppressWarnings({"AnonymousInnerClass", "AnonymousInnerClassWithTooManyMethods"})
public class AsyncTaskTest extends TestCase {

    public static final long ONE_SECOND = 1000L;
    public static final long HALF_SECOND = 500L;
    public static final String EXPECTED_EXCEPTION_MESSAGE = "Expected";

    @Mock
    private AsyncTaskCallback<Object> mAsyncTaskCallback;
    private CountDownLatch mCountDownLatch;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        mCountDownLatch = new CountDownLatch(1);
    }

    /* Test normal behavior */

    public void testEmptyExecution() throws InterruptedException {
        new TestAsyncTask(mAsyncTaskCallback, mCountDownLatch).execute();

        mCountDownLatch.await();

        verify(mAsyncTaskCallback).onPreExecute();
        verify(mAsyncTaskCallback).call();
        verify(mAsyncTaskCallback).onSuccess(any());
        verify(mAsyncTaskCallback).onFinally();

        verify(mAsyncTaskCallback, never()).onInterrupted(any(Exception.class));
        verify(mAsyncTaskCallback, never()).onCancelled();
        verify(mAsyncTaskCallback, never()).onException(any(Exception.class));
        verify(mAsyncTaskCallback, never()).onRuntimeException(any(RuntimeException.class));
    }

    public void testSlowExecution() throws InterruptedException {
        new TestAsyncTask(mAsyncTaskCallback, mCountDownLatch) {
            @Override
            public Object call() throws Exception {
                super.call();
                Thread.sleep(ONE_SECOND);
                return null;
            }
        }.execute();

        mCountDownLatch.await();

        verify(mAsyncTaskCallback).onPreExecute();
        verify(mAsyncTaskCallback).call();
        verify(mAsyncTaskCallback).onSuccess(any());
        verify(mAsyncTaskCallback).onFinally();

        verify(mAsyncTaskCallback, never()).onInterrupted(any(Exception.class));
        verify(mAsyncTaskCallback, never()).onCancelled();
        verify(mAsyncTaskCallback, never()).onException(any(Exception.class));
        verify(mAsyncTaskCallback, never()).onRuntimeException(any(RuntimeException.class));
    }

    public void testResultExecution() throws InterruptedException {
        final Object result = new Object();
        new TestAsyncTask(mAsyncTaskCallback, mCountDownLatch) {
            @Override
            public Object call() throws Exception {
                super.call();
                return result;
            }
        }.execute();

        mCountDownLatch.await();

        verify(mAsyncTaskCallback).onPreExecute();
        verify(mAsyncTaskCallback).call();
        verify(mAsyncTaskCallback).onSuccess(result);
        verify(mAsyncTaskCallback).onFinally();

        verify(mAsyncTaskCallback, never()).onCancelled();
        verify(mAsyncTaskCallback, never()).onInterrupted(any(Exception.class));
        verify(mAsyncTaskCallback, never()).onException(any(Exception.class));
        verify(mAsyncTaskCallback, never()).onRuntimeException(any(RuntimeException.class));
    }

    public void testCancelledExecution() throws InterruptedException {
        AsyncTask<Object> task = new TestAsyncTask(mAsyncTaskCallback, mCountDownLatch) {
            @Override
            public Object call() throws Exception {
                super.call();
                Thread.sleep(ONE_SECOND);
                return null;
            }
        }.execute();

        Thread.sleep(HALF_SECOND);
        task.cancel();
        mCountDownLatch.await();

        verify(mAsyncTaskCallback).onPreExecute();
        verify(mAsyncTaskCallback).call();
        verify(mAsyncTaskCallback).onCancelled();
        verify(mAsyncTaskCallback).onFinally();

        verify(mAsyncTaskCallback, never()).onInterrupted(any(Exception.class));
        verify(mAsyncTaskCallback, never()).onSuccess(any());
        verify(mAsyncTaskCallback, never()).onException(any(Exception.class));
        verify(mAsyncTaskCallback, never()).onRuntimeException(any(RuntimeException.class));
    }

    public void testCallExceptionThrown() throws InterruptedException {
        final Exception ex = new Exception(EXPECTED_EXCEPTION_MESSAGE);
        new TestAsyncTask(mAsyncTaskCallback, mCountDownLatch) {

            @Override
            public Object call() throws Exception {
                super.call();
                throw ex;
            }

        }.execute();

        mCountDownLatch.await();

        verify(mAsyncTaskCallback).onPreExecute();
        verify(mAsyncTaskCallback).call();
        verify(mAsyncTaskCallback).onException(any(Exception.class));
        verify(mAsyncTaskCallback).onFinally();

        verify(mAsyncTaskCallback, never()).onRuntimeException(any(RuntimeException.class));
        verify(mAsyncTaskCallback, never()).onCancelled();
        verify(mAsyncTaskCallback, never()).onSuccess(any());
        verify(mAsyncTaskCallback, never()).onInterrupted(any(InterruptedException.class));
    }

     /* Test whether RuntimeExceptions are actually thrown, and not caught */

    public void testCancelledInterruptedExecution() throws InterruptedException {
        AsyncTask<Object> task = new TestAsyncTask(mAsyncTaskCallback, mCountDownLatch) {
            @Override
            public Object call() throws Exception {
                super.call();
                Thread.sleep(ONE_SECOND);
                return null;
            }
        }.execute();

        Thread.sleep(HALF_SECOND);
        task.cancelInterrupt();
        mCountDownLatch.await();

        verify(mAsyncTaskCallback).onPreExecute();
        verify(mAsyncTaskCallback).call();
        verify(mAsyncTaskCallback).onInterrupted(any(InterruptedException.class));
        verify(mAsyncTaskCallback).onFinally();

        verify(mAsyncTaskCallback, never()).onCancelled();
        verify(mAsyncTaskCallback, never()).onSuccess(any());
        verify(mAsyncTaskCallback, never()).onException(any(Exception.class));
        verify(mAsyncTaskCallback, never()).onRuntimeException(any(RuntimeException.class));
    }

    public void testPreExecuteRuntimeExceptionThrown() throws InterruptedException {
        final RuntimeException rte = new RuntimeException(EXPECTED_EXCEPTION_MESSAGE);
        new TestAsyncTask(mAsyncTaskCallback, mCountDownLatch) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                throw rte;
            }
        }.execute();

        mCountDownLatch.await();
        verify(mAsyncTaskCallback).onRuntimeException(rte);
    }

    public void testSuccessRuntimeExceptionThrown() throws InterruptedException {
        final RuntimeException rte = new RuntimeException(EXPECTED_EXCEPTION_MESSAGE);
        new TestAsyncTask(mAsyncTaskCallback, mCountDownLatch) {

            @Override
            protected void onSuccess(final Object o) {
                super.onSuccess(o);
                throw rte;
            }
        }.execute();

        mCountDownLatch.await();
        verify(mAsyncTaskCallback).onPreExecute();
        verify(mAsyncTaskCallback).call();
        verify(mAsyncTaskCallback).onSuccess(any());
        verify(mAsyncTaskCallback).onRuntimeException(rte);
        verify(mAsyncTaskCallback).onFinally();

        verify(mAsyncTaskCallback, never()).onException(any(Exception.class));
        verify(mAsyncTaskCallback, never()).onCancelled();
    }

    public void testCancelledRuntimeExceptionThrown() throws InterruptedException {
        final RuntimeException rte = new RuntimeException(EXPECTED_EXCEPTION_MESSAGE);
        AsyncTask<Object> task = new TestAsyncTask(mAsyncTaskCallback, mCountDownLatch) {

            @Override
            public Object call() throws Exception {
                super.call();
                Thread.sleep(ONE_SECOND);
                return null;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                throw rte;
            }
        }.execute();

        Thread.sleep(HALF_SECOND);
        task.cancel();
        mCountDownLatch.await();

        verify(mAsyncTaskCallback).onPreExecute();
        verify(mAsyncTaskCallback).call();
        verify(mAsyncTaskCallback).onCancelled();
        verify(mAsyncTaskCallback).onRuntimeException(rte);
        verify(mAsyncTaskCallback).onFinally();

        verify(mAsyncTaskCallback, never()).onSuccess(any());
        verify(mAsyncTaskCallback, never()).onException(any(Exception.class));
        verify(mAsyncTaskCallback, never()).onInterrupted(any(InterruptedException.class));
    }


    public void testExceptionRuntimeExceptionThrown() throws InterruptedException {
        final RuntimeException rte = new RuntimeException(EXPECTED_EXCEPTION_MESSAGE);
        new TestAsyncTask(mAsyncTaskCallback, mCountDownLatch) {

            @Override
            public Object call() throws Exception {
                super.call();
                throw new Exception();
            }

            @Override
            protected void onException(final Exception e) {
                super.onException(e);
                throw rte;
            }
        }.execute();

        mCountDownLatch.await();

        verify(mAsyncTaskCallback).onPreExecute();
        verify(mAsyncTaskCallback).call();
        verify(mAsyncTaskCallback).onException(any(Exception.class));
        verify(mAsyncTaskCallback).onRuntimeException(rte);
        verify(mAsyncTaskCallback).onFinally();

        verify(mAsyncTaskCallback, never()).onCancelled();
        verify(mAsyncTaskCallback, never()).onSuccess(any());
        verify(mAsyncTaskCallback, never()).onInterrupted(any(InterruptedException.class));
    }

    public void testFinallyRuntimeExceptionThrown() throws InterruptedException {
        final RuntimeException rte = new RuntimeException(EXPECTED_EXCEPTION_MESSAGE);
        new TestAsyncTask(mAsyncTaskCallback, mCountDownLatch) {

            @SuppressWarnings("RefusedBequest")
            @Override
            protected void onFinally() {
                assertThat(Looper.getMainLooper().getThread(), is(Thread.currentThread()));
                mAsyncTaskCallback.onFinally();
                throw rte;
            }

            @Override
            protected void onRuntimeException(final RuntimeException e) {
                super.onRuntimeException(e);
                mCountDownLatch.countDown();
            }
        }.execute();

        mCountDownLatch.await();

        verify(mAsyncTaskCallback).onPreExecute();
        verify(mAsyncTaskCallback).call();
        verify(mAsyncTaskCallback).onSuccess(any());
        verify(mAsyncTaskCallback).onFinally();
        verify(mAsyncTaskCallback).onRuntimeException(rte);

        verify(mAsyncTaskCallback, never()).onException(any(Exception.class));
        verify(mAsyncTaskCallback, never()).onCancelled();
        verify(mAsyncTaskCallback, never()).onInterrupted(any(InterruptedException.class));
    }

    public void testCallRuntimeExceptionThrown() throws InterruptedException {
        final RuntimeException rte = new RuntimeException(EXPECTED_EXCEPTION_MESSAGE);
        new TestAsyncTask(mAsyncTaskCallback, mCountDownLatch) {

            @Override
            public Object call() throws Exception {
                super.call();
                throw rte;
            }

        }.execute();

        mCountDownLatch.await();

        verify(mAsyncTaskCallback).onPreExecute();
        verify(mAsyncTaskCallback).call();
        verify(mAsyncTaskCallback).onRuntimeException(rte);
        verify(mAsyncTaskCallback).onFinally();

        verify(mAsyncTaskCallback, never()).onException(any(Exception.class));
        verify(mAsyncTaskCallback, never()).onCancelled();
        verify(mAsyncTaskCallback, never()).onSuccess(any());
        verify(mAsyncTaskCallback, never()).onInterrupted(any(InterruptedException.class));
    }

    /**
     * A test AsyncTask which:
     * - Notifies a callback
     * - Counts down a CountDownLatch when finished
     * - Checks whether calls are made on the correct thread.
     */
    private static class TestAsyncTask extends AsyncTask<Object> {

        private final AsyncTaskCallback<Object> mAsyncTaskCallback;
        private final CountDownLatch mCountDownLatch;

        private TestAsyncTask(final AsyncTaskCallback<Object> asyncTaskCallback, final CountDownLatch countDownLatch) {
            mAsyncTaskCallback = asyncTaskCallback;
            mCountDownLatch = countDownLatch;
        }

        @Override
        public Object call() throws Exception {
            mAsyncTaskCallback.call();
            assertThat(Looper.getMainLooper().getThread(), is(not(Thread.currentThread())));
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mAsyncTaskCallback.onPreExecute();

            assertThat(Looper.getMainLooper().getThread(), is(Thread.currentThread()));
        }

        @Override
        protected void onSuccess(final Object t) {
            assertThat(Looper.getMainLooper().getThread(), is(Thread.currentThread()));
            mAsyncTaskCallback.onSuccess(t);
        }

        @SuppressWarnings("RefusedBequest")
        @Override
        protected void onInterrupted(final InterruptedException e) {
            assertThat(Looper.getMainLooper().getThread(), is(Thread.currentThread()));
            mAsyncTaskCallback.onInterrupted(e);
        }

        @Override
        protected void onCancelled() {
            assertThat(Looper.getMainLooper().getThread(), is(Thread.currentThread()));
            mAsyncTaskCallback.onCancelled();
        }

        @Override
        protected void onException(final Exception e) {
            assertThat(Looper.getMainLooper().getThread(), is(Thread.currentThread()));
            mAsyncTaskCallback.onException(e);
        }

        @Override
        protected void onRuntimeException(final RuntimeException e) {
            assertThat(Looper.getMainLooper().getThread(), is(Thread.currentThread()));
            mAsyncTaskCallback.onRuntimeException(e);
        }

        @Override
        protected void onFinally() {
            assertThat(Looper.getMainLooper().getThread(), is(Thread.currentThread()));
            mAsyncTaskCallback.onFinally();
            mCountDownLatch.countDown();
        }
    }

    @SuppressWarnings("InterfaceNeverImplemented")
    /** A callback interface to validate method calls using Mockito. */
    private interface AsyncTaskCallback<T> {
        public void call();

        public void onPreExecute();

        public void onSuccess(T t);

        public void onInterrupted(Exception e);

        public void onCancelled();

        public void onException(Exception e);

        public void onRuntimeException(RuntimeException e);

        public void onFinally();
    }
}
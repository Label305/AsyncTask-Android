package com.label305.asynctask;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;
import com.label305.asynctask.SimpleAsyncTask;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class AsyncTaskTest extends AndroidTestCase {

  private static final long ONE_SECOND = 100;

  private static final long HALF_SECOND = 50L;

  private static final String EXPECTED_EXCEPTION_MESSAGE = "Expected";

  private AsyncTaskCallback<Object> mAsyncTaskCallback;

  private CountDownLatch mCountDownLatch;

  /* Test normal behavior */

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    mAsyncTaskCallback = mock(AsyncTaskCallback.class);

    mCountDownLatch = new CountDownLatch(1);
  }

  @Test
  public void emptyExecution() throws InterruptedException {
    new TestAsyncTask(mAsyncTaskCallback, mCountDownLatch).execute();

    mCountDownLatch.await();

    verify(mAsyncTaskCallback).onPreExecute();
    verify(mAsyncTaskCallback).doInBackground();
    verify(mAsyncTaskCallback).onSuccess(any());
    verify(mAsyncTaskCallback).onFinally();

    verify(mAsyncTaskCallback, never()).onInterrupted(any(Exception.class));
    verify(mAsyncTaskCallback, never()).onCancelled();
    verify(mAsyncTaskCallback, never()).onException(any(Exception.class));
    verify(mAsyncTaskCallback, never()).onRuntimeException(any(RuntimeException.class));
  }

  @Test
  public void slowExecution() throws InterruptedException {
    new TestAsyncTask<InterruptedException>(mAsyncTaskCallback, mCountDownLatch) {
      @Override
      public Object doInBackground() throws InterruptedException {
        super.doInBackground();
        Thread.sleep(ONE_SECOND);
        return null;
      }
    }.execute();

    mCountDownLatch.await();

    verify(mAsyncTaskCallback).onPreExecute();
    verify(mAsyncTaskCallback).doInBackground();
    verify(mAsyncTaskCallback).onSuccess(any());
    verify(mAsyncTaskCallback).onFinally();

    verify(mAsyncTaskCallback, never()).onInterrupted(any(Exception.class));
    verify(mAsyncTaskCallback, never()).onCancelled();
    verify(mAsyncTaskCallback, never()).onException(any(Exception.class));
    verify(mAsyncTaskCallback, never()).onRuntimeException(any(RuntimeException.class));
  }

  @Test
  public void resultExecution() throws InterruptedException {
    final Object result = new Object();
    new SimpleTestAsyncTask(mAsyncTaskCallback, mCountDownLatch) {
      @Override
      public Object doInBackgroundSimple() {
        super.doInBackgroundSimple();
        return result;
      }
    }.execute();

    mCountDownLatch.await();

    verify(mAsyncTaskCallback).onPreExecute();
    verify(mAsyncTaskCallback).doInBackground();
    verify(mAsyncTaskCallback).onSuccess(result);
    verify(mAsyncTaskCallback).onFinally();

    verify(mAsyncTaskCallback, never()).onCancelled();
    verify(mAsyncTaskCallback, never()).onInterrupted(any(Exception.class));
    verify(mAsyncTaskCallback, never()).onException(any(Exception.class));
    verify(mAsyncTaskCallback, never()).onRuntimeException(any(RuntimeException.class));
  }

  @Test
  public void cancelledExecution() throws InterruptedException {
    AsyncTask<Object, InterruptedException> task = new TestAsyncTask<InterruptedException>(mAsyncTaskCallback, mCountDownLatch) {
      @Override
      public Object doInBackground() throws InterruptedException {
        super.doInBackground();
        Thread.sleep(ONE_SECOND);
        return null;
      }
    }.execute();

    Thread.sleep(HALF_SECOND);
    task.cancel();
    mCountDownLatch.await();

    verify(mAsyncTaskCallback).onPreExecute();
    verify(mAsyncTaskCallback).doInBackground();
    verify(mAsyncTaskCallback).onCancelled();
    verify(mAsyncTaskCallback).onFinally();

    verify(mAsyncTaskCallback, never()).onInterrupted(any(Exception.class));
    verify(mAsyncTaskCallback, never()).onSuccess(any());
    verify(mAsyncTaskCallback, never()).onException(any(Exception.class));
    verify(mAsyncTaskCallback, never()).onRuntimeException(any(RuntimeException.class));
  }

  @Test
  public void callExceptionThrown() throws InterruptedException {
    final Exception ex = new Exception(EXPECTED_EXCEPTION_MESSAGE);
    new TestAsyncTask(mAsyncTaskCallback, mCountDownLatch) {

      @Override
      public Object doInBackground() throws Exception {
        super.doInBackground();
        throw ex;
      }
    }.execute();

    mCountDownLatch.await();

    verify(mAsyncTaskCallback).onPreExecute();
    verify(mAsyncTaskCallback).doInBackground();
    verify(mAsyncTaskCallback).onException(any(Exception.class));
    verify(mAsyncTaskCallback).onFinally();

    verify(mAsyncTaskCallback, never()).onRuntimeException(any(RuntimeException.class));
    verify(mAsyncTaskCallback, never()).onCancelled();
    verify(mAsyncTaskCallback, never()).onSuccess(any());
    verify(mAsyncTaskCallback, never()).onInterrupted(any(InterruptedException.class));
  }

  @Test
  public void preExecuteRuntimeExceptionThrown() throws InterruptedException {
    final RuntimeException rte = new RuntimeException(EXPECTED_EXCEPTION_MESSAGE);
    new SimpleTestAsyncTask(mAsyncTaskCallback, mCountDownLatch) {
      @Override
      protected void onPreExecute() {
        super.onPreExecute();
        throw rte;
      }
    }.execute();

    mCountDownLatch.await();
    verify(mAsyncTaskCallback).onRuntimeException(rte);
  }

  @Test
  public void successRuntimeExceptionThrown() throws InterruptedException {
    final RuntimeException rte = new RuntimeException(EXPECTED_EXCEPTION_MESSAGE);
    new SimpleTestAsyncTask(mAsyncTaskCallback, mCountDownLatch) {

      @Override
      protected void onSuccess(final Object o) {
        super.onSuccess(o);
        throw rte;
      }
    }.execute();

    mCountDownLatch.await();
    verify(mAsyncTaskCallback).onPreExecute();
    verify(mAsyncTaskCallback).doInBackground();
    verify(mAsyncTaskCallback).onSuccess(any());
    verify(mAsyncTaskCallback).onRuntimeException(rte);
    verify(mAsyncTaskCallback).onFinally();

    verify(mAsyncTaskCallback, never()).onException(any(Exception.class));
    verify(mAsyncTaskCallback, never()).onCancelled();
  }

  @Test
  public void cancelledRuntimeExceptionThrown() throws InterruptedException {
    final RuntimeException rte = new RuntimeException(EXPECTED_EXCEPTION_MESSAGE);
    AsyncTask<Object, InterruptedException> task = new TestAsyncTask<InterruptedException>(mAsyncTaskCallback, mCountDownLatch) {

      @Override
      public Object doInBackground() throws InterruptedException {
        super.doInBackground();
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
    verify(mAsyncTaskCallback).doInBackground();
    verify(mAsyncTaskCallback).onCancelled();
    verify(mAsyncTaskCallback).onRuntimeException(rte);
    verify(mAsyncTaskCallback).onFinally();

    verify(mAsyncTaskCallback, never()).onSuccess(any());
    verify(mAsyncTaskCallback, never()).onException(any(Exception.class));
    verify(mAsyncTaskCallback, never()).onInterrupted(any(InterruptedException.class));
  }

  @Test
  public void exceptionRuntimeExceptionThrown() throws InterruptedException {
    final RuntimeException rte = new RuntimeException(EXPECTED_EXCEPTION_MESSAGE);
    new TestAsyncTask<IOException>(mAsyncTaskCallback, mCountDownLatch) {

      @Override
      public Object doInBackground() throws IOException {
        super.doInBackground();
        throw new IOException();
      }

      @Override
      protected void onException(@NonNull final IOException e) {
        super.onException(e);
        throw rte;
      }
    }.execute();

    mCountDownLatch.await();

    verify(mAsyncTaskCallback).onPreExecute();
    verify(mAsyncTaskCallback).doInBackground();
    verify(mAsyncTaskCallback).onException(any(Exception.class));
    verify(mAsyncTaskCallback).onRuntimeException(rte);
    verify(mAsyncTaskCallback).onFinally();

    verify(mAsyncTaskCallback, never()).onCancelled();
    verify(mAsyncTaskCallback, never()).onSuccess(any());
    verify(mAsyncTaskCallback, never()).onInterrupted(any(InterruptedException.class));
  }

  @Test
  public void finallyRuntimeExceptionThrown() throws InterruptedException {
    final RuntimeException rte = new RuntimeException(EXPECTED_EXCEPTION_MESSAGE);
    new SimpleTestAsyncTask(mAsyncTaskCallback, mCountDownLatch) {

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
    verify(mAsyncTaskCallback).doInBackground();
    verify(mAsyncTaskCallback).onSuccess(any());
    verify(mAsyncTaskCallback).onFinally();
    verify(mAsyncTaskCallback).onRuntimeException(rte);

    verify(mAsyncTaskCallback, never()).onException(any(Exception.class));
    verify(mAsyncTaskCallback, never()).onCancelled();
    verify(mAsyncTaskCallback, never()).onInterrupted(any(InterruptedException.class));
  }

  @Test
  public void doInBackgroundRuntimeExceptionThrown() throws InterruptedException {
    final RuntimeException rte = new RuntimeException(EXPECTED_EXCEPTION_MESSAGE);
    new TestAsyncTask(mAsyncTaskCallback, mCountDownLatch) {

      @Override
      public Object doInBackground() throws Exception {
        super.doInBackground();
        throw rte;
      }
    }.execute();

    mCountDownLatch.await();

    verify(mAsyncTaskCallback).onPreExecute();
    verify(mAsyncTaskCallback).doInBackground();
    verify(mAsyncTaskCallback).onRuntimeException(rte);
    verify(mAsyncTaskCallback).onFinally();

    verify(mAsyncTaskCallback, never()).onException(any(Exception.class));
    verify(mAsyncTaskCallback, never()).onCancelled();
    verify(mAsyncTaskCallback, never()).onSuccess(any());
    verify(mAsyncTaskCallback, never()).onInterrupted(any(InterruptedException.class));
  }

  /** A callback interface to validate method calls using Mockito. */
  private interface AsyncTaskCallback<T> {

    void doInBackground();

    void onPreExecute();

    void onSuccess(T t);

    void onInterrupted(Exception e);

    void onCancelled();

    void onException(Exception e);

    void onRuntimeException(RuntimeException e);

    void onFinally();
  }

  /**
   * A test AsyncTask which:
   * - Notifies a callback
   * - Counts down a CountDownLatch when finished
   * - Checks whether calls are made on the correct thread.
   */
  private static class TestAsyncTask<E extends Exception> extends AsyncTask<Object, E> {

    private final AsyncTaskCallback<Object> mAsyncTaskCallback;

    private final CountDownLatch mCountDownLatch;

    private TestAsyncTask(final AsyncTaskCallback<Object> asyncTaskCallback, final CountDownLatch countDownLatch) {
      mAsyncTaskCallback = asyncTaskCallback;
      mCountDownLatch = countDownLatch;
    }

    @Override
    public Object doInBackground() throws E {
      mAsyncTaskCallback.doInBackground();
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
    protected void onInterrupted(@NonNull final InterruptedException e) {
      assertThat(Looper.getMainLooper().getThread(), is(Thread.currentThread()));
      mAsyncTaskCallback.onInterrupted(e);
    }

    @Override
    protected void onCancelled() {
      assertThat(Looper.getMainLooper().getThread(), is(Thread.currentThread()));
      mAsyncTaskCallback.onCancelled();
    }

    @Override
    protected void onException(@NonNull final E e) {
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

  /**
   * A test AsyncTask which:
   * - Notifies a callback
   * - Counts down a CountDownLatch when finished
   * - Checks whether calls are made on the correct thread.
   */
  private static class SimpleTestAsyncTask extends SimpleAsyncTask<Object> {

    private final AsyncTaskCallback<Object> mAsyncTaskCallback;

    private final CountDownLatch mCountDownLatch;

    private SimpleTestAsyncTask(final AsyncTaskCallback<Object> asyncTaskCallback, final CountDownLatch countDownLatch) {
      mAsyncTaskCallback = asyncTaskCallback;
      mCountDownLatch = countDownLatch;
    }

    @Override
    public Object doInBackgroundSimple() {
      mAsyncTaskCallback.doInBackground();
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
    protected void onInterrupted(@NonNull final InterruptedException e) {
      assertThat(Looper.getMainLooper().getThread(), is(Thread.currentThread()));
      mAsyncTaskCallback.onInterrupted(e);
    }

    @Override
    protected void onCancelled() {
      assertThat(Looper.getMainLooper().getThread(), is(Thread.currentThread()));
      mAsyncTaskCallback.onCancelled();
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
}
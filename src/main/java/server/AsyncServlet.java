package server;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import static com.google.common.base.Preconditions.checkState;

public class AsyncServlet extends SyncServlet {

  public AsyncServlet() {
    super();
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    final ParsedRequest parsed = parseRequest(request, response);
    if (null == parsed) {
      return;
    }

    if (processor == null) {
      log.error("Misconfiguration: AsyncServlet.processor is null");
      respondOrThrow(response, "error");
      return;
    }

    AsyncContext asyncContext = request.startAsync();
    try {
      doHandle(asyncContext, parsed);
    } catch (RuntimeException e) {
      log.error("Method {} failed ", parsed.method, e);
      respondWithResult(response, "error");
      asyncContext.complete();
    }

  }

  private void doHandle(AsyncContext asyncContext, @Nonnull ParsedRequest parsed) {

    final String method = parsed.method;

    ListenableFuture<String> future = processor.handleAsync(method);

    if (null != future) {
      asyncContext.addListener(new TimeoutListener(future, String.format("%s for ???", method))); // if timeout
      Futures.addCallback(future, new RespondingCallback(asyncContext), MoreExecutors.directExecutor());
      return;
    }

    log.error("Failed to create a future - do nothing");
    asyncContext.complete();
  }



  private class RespondingCallback implements FutureCallback<String> {

    protected final AsyncContext asyncContext;

    RespondingCallback(AsyncContext asyncContext) {
      this.asyncContext = asyncContext;
    }

    private void respond(String value) {
      respondWithResult(asyncContext.getResponse(), value);
      asyncContext.complete();
    }

    public void onSuccess(@Nullable String value) {
      log.trace("async call succeeded");
      respond(value == null ? "" : value);
    }

    public void onFailure(Throwable throwable) {
      log.error("Problem during asynchronous processing: {}", throwable.getMessage());
      respond("async error");
    }
  }

  private class TimeoutListener implements AsyncListener {

    final ListenableFuture<String> future;
    final String idForLog;

    TimeoutListener(ListenableFuture<String> future, @Nonnull String idForLog) {
      this.future = future;
      this.idForLog = idForLog;
    }

    public void onComplete(AsyncEvent event) throws IOException {
      // do nothing
    }

    public void onTimeout(AsyncEvent event) throws IOException {
      log.warn("Cancel future({}) because of timeout", idForLog);
      future.cancel(false);
    }

    public void onError(AsyncEvent event) throws IOException {
      // do nothing
    }

    public void onStartAsync(AsyncEvent event) throws IOException {
      // do nothing
    }
  }
}


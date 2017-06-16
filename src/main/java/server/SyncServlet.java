package server;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.io.CharStreams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.google.common.base.Preconditions.checkState;

public class SyncServlet extends HttpServlet {

  protected final Logger log = LogManager.getLogger(this.getClass());

  protected final Processor processor;

  public SyncServlet() {
    super();
    this.processor = buildProcessor();
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    final ParsedRequest parsed = parseRequest(request, response);
    if (null == parsed) {
      return;
    }

    if (processor == null) {
      log.error("Misconfiguration: SyncServlet.processor is null");
      respondOrThrow(response, "error");
      return;
    }

    try {
      doHandle(response, parsed);
    } catch (RuntimeException e) {
      log.error("Method {} failed ", parsed.method, e);
      respondWithResult(response, "error");
    }

  }

  private void doHandle(ServletResponse response, @Nonnull ParsedRequest parsed) {

    final String method = parsed.method;

    respondWithResult(response, "");
  }

  /**
   * Разбирает и валидирует данные запроса.
   * В случае проблем, пишет ошибку в response и возвращает null
   */
  @Nullable
  protected ParsedRequest parseRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String messageJson = CharStreams.toString(request.getReader());

    log.debug("jsonrpc--> {}", messageJson);

    // validate method field
    final String method = "method name";

    return new ParsedRequest(method);
  }

  protected void respondWithResult(ServletResponse response, @Nonnull String result) {
    try {
      respondOrThrow(response, result);
    } catch (IOException e) {
      log.error("Problem while responding with " + result, e);
    }
  }

  protected void respondOrThrow(ServletResponse response, @Nonnull String result) throws IOException {
    log.debug("response<-- {}", result);
    response.setContentType("text/html; charset=utf-8");
    response.getWriter().print(result);
  }

  protected Processor buildProcessor() {
    return new Processor(){};
  }

}


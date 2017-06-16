package server;

import javax.annotation.Nullable;

import com.google.common.util.concurrent.ListenableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Processor {

    final Logger log = LogManager.getLogger(Processor.class);

    default ListenableFuture<String> handleAsync(String methodName) {
        log.debug("Async handle is called");
        return null;
    }

    @Nullable
    default String handle(String methodName) {
        log.debug("Sync handle is called");
        return null;
    }
}

package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WorkerPool {

    Logger log = LogManager.getLogger(WorkerPool.class);

    private final ThreadPoolExecutor executor;
    private final BlockingQueue<Runnable> blockingQueue;

    public WorkerPool(int core, int max, int keepAlive, int queueLength) {
        blockingQueue = queueLength == -1 ? new LinkedBlockingQueue<Runnable>()
                                          : new LinkedBlockingQueue<Runnable>(queueLength);

        executor = new ThreadPoolExecutor(core, max, keepAlive, TimeUnit.SECONDS, blockingQueue);
    }

    public void execute(Runnable task) {
        executor.execute(new Runnable() {
            public void run() {
                try {
                    task.run();
                } catch (Throwable t) {
                    log.error("Uncaught exception", t);
                }
            }
        });
    }

    public void shutdown(int timeout) throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(timeout, TimeUnit.MILLISECONDS);
    }
}

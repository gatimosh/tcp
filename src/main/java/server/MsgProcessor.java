/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tcp.TaskMsg;
import tcp.TaskRes;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.Socket;

import static tcp.TCPTransport.receive;
import static tcp.TCPTransport.send;

/**
 * to process messages for a socket until it is closed
 */
public class MsgProcessor implements Runnable {

    private static final Logger log = LogManager.getLogger(MsgProcessor.class);

    private Socket socket;
    private WorkerPool pool;

    public MsgProcessor(Socket socket) {
        log.trace("get socket " + socket.getPort());
        this.socket = socket;
    }

    public void run() {

        pool = new WorkerPool(1, 16, 10, 64);

        while(!socket.isClosed() && socket.isConnected() && !socket.isInputShutdown()) {
            final TaskMsg msg;

            try {
                Object obj = receive(socket);
                if (!(obj instanceof TaskMsg)) {
                    log.error(String.format("request (%d) brings not a TaskMsg but %s", socket.getPort(), obj));
                    return;
                }
                msg = (TaskMsg) obj;
            } catch (Exception e) {
                log.error("Error while receiving request, closing socket.");
                try {
                    socket.close();
                } catch (IOException e1) {
                    log.error("Error while closing socket: ", e1);
                }
                return;
            }

            pool.execute(() -> dispatchMsg(msg));
        }

        try {
            pool.shutdown(10);
        } catch (InterruptedException e) {
            log.error("Error during shutting down thread pool: ", e.getMessage());
        }
    }

    private void dispatchMsg(@Nonnull TaskMsg msg) {

        log.debug(String.format("request(%d) <-- %s", socket.getPort(), msg));

        Service service = ServiceFactory.get(msg.service);
        if (null == service) {
            respond(new TaskRes(msg.id, TaskRes.Error.SERVICE_NOT_FOUND));
            return;
        }

        Service.ExecutionResult result = service.execute(msg);

        TaskRes resp = result.error != TaskRes.Error.OK ? new TaskRes(msg.id, result.error) : new TaskRes(msg.id, result.value);
        respond(resp);
    }

    private void sendFault(TaskMsg msg, Exception fault) {
        log.error("Error while processing request " + msg, fault);
        if (msg != null) {
            respond(new TaskRes(msg.id, TaskRes.Error.INTERNAL));
        }
    }

    private void respond(TaskRes res) {
        try {
            log.debug(String.format("respond(%d) --> %s", socket.getPort(), res));
            // this is actually an out-stream lock, as MsgProcessor is the only reader of input-stream
            synchronized(socket) {
                send(res, socket);
            }
        } catch (IOException e) {
            log.error(String.format("Error while sending the response[%s]", res), e);
        }
    }
}

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

import java.io.*;
import java.net.Socket;

import static tcp.TCPTransport.receive;
import static tcp.TCPTransport.send;

public class MsgProcessor implements Runnable {

    private static final Logger log = LogManager.getLogger(MsgProcessor.class);

    private Socket socket;

    public MsgProcessor(Socket socket) {
        log.trace("get socket " + socket.getPort());
        this.socket = socket;
    }

    public void run() {

        TaskMsg msg = null;

        try {
            Object  obj = receive(socket);
            if (!(obj instanceof TaskMsg)) {
                log.error(String.format("request (%d) brings not a TaskMsg but %s", socket.getPort(), obj));
                return;
            }
            msg = (TaskMsg) obj;

            log.debug(String.format("request(%d) <-- %s", socket.getPort(), msg));

            TaskRes resp = new TaskRes(msg.id, "SUCCESS: " + msg);
            send(resp, socket);

            log.debug(String.format("respond(%d) --> %s", socket.getPort(), resp));

        } catch (Exception e) {
            sendFault(msg, e);
        } finally {
            try {
                log.trace("closing socket " + socket.getPort());
                socket.close();
            } catch (IOException e) {
                sendFault(msg, e);
            }
        }
    }

    private void sendFault(TaskMsg msg, Exception fault) {
        log.error("Error while processing request " + msg, fault);
        if (msg != null) {
            try {
                send(new TaskRes(msg.id, TaskRes.Error.INTERNAL), socket);
            } catch (IOException e) {
                log.error("Error while sending the fault response", e);
            }
        }
    }
}

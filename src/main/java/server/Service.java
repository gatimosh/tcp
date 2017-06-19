package server;

import com.google.common.base.Preconditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tcp.TaskMsg;
import tcp.TaskRes;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class Service {

    private static ExecutionResult METHOD_NOT_FOUND = new ExecutionResult(TaskRes.Error.METHOD_NOT_FOUND, null);
    private static ExecutionResult EXECUTION_ERROR = new ExecutionResult(TaskRes.Error.METHOD_EXECUTION_ERROR, null);

    private static final Logger log = LogManager.getLogger(Service.class);

    public static class ExecutionResult {

        public final TaskRes.Error error;
        public final Optional<Object> value;

        private ExecutionResult(TaskRes.Error error, @Nullable Optional<Object> value) {
            this.error = error;
            this.value = value;
        }

        public ExecutionResult(@Nullable Optional<Object> value) {
            this(TaskRes.Error.OK, value);
        }
    }

    public final String name;
    private Class implClass;
    private Object impl;

    public Service(String name, String classname) {
        this.name = name;
        try {
            implClass = Class.forName(classname);
        } catch (ClassNotFoundException e) {
            log.error(String.format("Service %s unavailable: %s", name, e.getMessage()));
            implClass = null;
        }
        try {
            impl = implClass == null ? null : implClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error(String.format("Failed to instantiate service %s : %s", name, e.getMessage()));
            impl = null;
        }
    }

    public boolean isReady() {
        return null != impl;
    }

    public ExecutionResult execute(TaskMsg msg) {
        Preconditions.checkState(name.equals(msg.service));

        Class[] argTypes = Arrays.stream(msg.args).map(Object::getClass).collect(toList()).toArray(new Class[msg.args.length]);

        Method m;
        try {
            m = implClass.getDeclaredMethod(msg.method, argTypes);
        } catch (NoSuchMethodException e) {
            log.error(String.format("Method %s.%s not found", name, msg.method));
            return METHOD_NOT_FOUND;
        }

        Object result = null;
        try {
            result = m.invoke(impl, msg.args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(String.format("Error while method %s.%s execution: %s", name, msg.method, e.getMessage()));
            return EXECUTION_ERROR;
        }

        boolean isVoid = m.getReturnType().equals(Void.TYPE);
        return isVoid ? new ExecutionResult(null) : new ExecutionResult(Optional.ofNullable(result));
    }
}

package tcp;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TaskMsg implements Serializable {

    public final Integer id;
    public final String service;
    public final String method;
    public final Object[] args;

    public TaskMsg(@Nonnull Integer id, @Nonnull String service, @Nonnull String method, @Nonnull Object[] args) {
        this.id = id;
        this.service = service;
        this.method = method;
        this.args = args;
    }

    @Override
    public String toString() {
        final String argsS = Arrays.stream(args).map(Object::toString).collect(Collectors.joining(", "));
        return String.format("[%d]task:%s.%s(%s)", id, service, method, argsS);
    }
}

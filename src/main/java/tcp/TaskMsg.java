package tcp;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TaskMsg implements Serializable {

    public final String service;
    public final String method;
    public final Object[] args;

    public TaskMsg(@Nonnull String service, @Nonnull String method, @Nonnull Object[] args) {
        this.service = service;
        this.method = method;
        this.args = args;
    }

    @Override
    public String toString() {
        final String argsS = Arrays.stream(args).map(i -> i.toString()).collect(Collectors.joining(", "));
        return String.format("%s.%s(%s)", service, method, argsS);
    }
}

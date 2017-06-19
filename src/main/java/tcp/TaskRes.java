package tcp;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Optional;

public class TaskRes implements Serializable {

    public enum Error {
        OK,
        INTERNAL,
        SERVICE_NOT_FOUND,
        METHOD_NOT_FOUND,
        METHOD_EXECUTION_ERROR
    }

    public final Integer id;
    public final boolean isVoid;
    public final Object result;
    public final Error error;

    public TaskRes(@Nonnull Integer id, @Nullable Optional<Object> result) {
        this.id = id;
        this.isVoid = result == null;
        this.result = isVoid ? null : result.orElse(null);
        this.error = null;
    }

    public TaskRes(@Nonnull Integer id, Error error) {
        this.id = id;
        this.isVoid = true;
        this.result = null;
        this.error = error;
    }

    public boolean isError() {
        return error != null && error != Error.OK;
    }

    @Override
    public String toString() {
        if (isError()) {
            return String.format("[%d]err:%s", id, error);
        } else {
            return String.format("[%d]res:%s", id, result == null ? "void" : result);
        }
    }
}

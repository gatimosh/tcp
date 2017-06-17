package tcp;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

public class TaskRes implements Serializable {

    public enum Error {
        OK,
        INTERNAL
    }

    public final Integer id;
    public final Object result;
    public final Error error;

    public TaskRes(@Nonnull Integer id) {
        this.id = id;
        this.result = null;
        this.error = null;
    }

    public TaskRes(@Nonnull Integer id, @Nullable Object result) {
        this.id = id;
        this.result = result;
        this.error = null;
    }

    public TaskRes(@Nonnull Integer id, Error error) {
        this.id = id;
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

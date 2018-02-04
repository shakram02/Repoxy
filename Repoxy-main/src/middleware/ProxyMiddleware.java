package middleware;

import org.jetbrains.annotations.NotNull;
import utils.ConnectionId;
import utils.events.SocketDataEventArg;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Splits packet processing pipeline into manageable operations
 * <p>
 * Each middleware class acts upon a SINGLE {@link utils.ConnectionId}. this
 * makes the data management in a central location (before the start
 * of the pipeline) and simplifies the logic inside the middleware
 */
public abstract class ProxyMiddleware implements Cloneable {
    protected final LinkedBlockingDeque<SocketDataEventArg> input;
    protected final LinkedBlockingDeque<SocketDataEventArg> output;
    protected final LinkedBlockingDeque<SocketDataEventArg> error;
    protected ConnectionId id;

    public ProxyMiddleware() {
        input = new LinkedBlockingDeque<>();
        output = new LinkedBlockingDeque<>();
        error = new LinkedBlockingDeque<>();
    }

    public final void addInput(SocketDataEventArg input) {
        this.input.add(input);
    }

    public abstract void execute();

    @NotNull
    public final SocketDataEventArg getOutput() {
        return this.output.poll();
    }

    @NotNull
    public final SocketDataEventArg getError() {
        return this.error.poll();
    }

    public boolean hasOutput() {
        return !this.output.isEmpty();
    }

    public boolean hasError() {
        return !this.error.isEmpty();
    }

    public abstract ProxyMiddleware clone(ConnectionId id);
}

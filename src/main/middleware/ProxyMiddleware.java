package middleware;

import org.jetbrains.annotations.NotNull;
import utils.events.SocketDataEventArg;

import java.util.concurrent.LinkedTransferQueue;

/**
 * Splits packet processing pipeline into manageable operations
 * <p>
 * Each middleware class acts upon a SINGLE {@link utils.ConnectionId}. this
 * makes the data management in a central location (before the start
 * of the pipeline) and simplifies the logic inside the middleware
 */
public abstract class ProxyMiddleware {
    protected LinkedTransferQueue<SocketDataEventArg> input;
    protected LinkedTransferQueue<SocketDataEventArg> output;
    protected LinkedTransferQueue<SocketDataEventArg> error;

    public ProxyMiddleware() {
        input = new LinkedTransferQueue<>();
        output = new LinkedTransferQueue<>();
        error = new LinkedTransferQueue<>();
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

    public boolean isEmpty() {
        return this.output.isEmpty();
    }

    public boolean isError() {
        return !this.error.isEmpty();
    }
}

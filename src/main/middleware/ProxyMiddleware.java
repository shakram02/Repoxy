package middleware;

import org.jetbrains.annotations.NotNull;
import utils.events.SocketDataEventArg;

import java.util.PriorityQueue;

/**
 * Splits packet processing pipeline into manageable operations
 * Each middleware class acts upon a single connection ID. this
 * makes the data management in a central location (before the start
 * of the pipeline) and simplifies the logic inside the middleware
 */
public abstract class ProxyMiddleware {
    protected PriorityQueue<SocketDataEventArg> input;
    protected PriorityQueue<SocketDataEventArg> output;

    public ProxyMiddleware() {
        input = new PriorityQueue<>();
        output = new PriorityQueue<>();
    }

    public final void addInput(SocketDataEventArg input) {
        this.input.add(input);
    }

    public abstract void execute();

    @NotNull
    public final SocketDataEventArg getOutput() {
        return this.output.poll();
    }

    public boolean hasOutput() {
        return !this.output.isEmpty();
    }
}

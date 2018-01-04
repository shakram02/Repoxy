package middleware;

import utils.ConnectionId;
import utils.PacketBuffer;
import utils.SenderType;
import utils.events.SocketDataEventArg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class MiddlewareManager {
    private ArrayList<ProxyMiddleware> registeredMiddleware;
    private PacketBuffer inputBuffer;
    private Queue<SocketDataEventArg> outputBuffer;
    private HashMap<ConnectionId, ArrayList<ProxyMiddleware>> connectionMiddleware;
    private boolean isMainControllerAlive;

    public MiddlewareManager() {
        registeredMiddleware = new ArrayList<>();
        inputBuffer = new PacketBuffer();
        outputBuffer = new LinkedBlockingQueue<>();
        connectionMiddleware = new HashMap<>();
    }

    /**
     * For every connection ID that we have, run the packets for that ID
     * through all the middleware
     */
    private void cycle() {
        for (ConnectionId id : connectionMiddleware.keySet()) {
            if (inputBuffer.getPacketQueue(id).isEmpty()) continue;

            Queue<SocketDataEventArg> middlewareOutput = runThroughMiddleware(id);

            // TODO: how to order the output?
            outputBuffer.addAll(middlewareOutput);
        }
    }

    /**
     * Registers a new middleware into the pipeline, note that the object
     * passed as parameter is just used from cloning, and will never have
     * a changed state.
     * <p>
     * Users shouldn't obtain a reference to the objects they pass to this
     * method. As that reference won't hold any useful information
     *
     * @param middleware Instantiated object for cloning purposes
     */
    public void addMiddleware(ProxyMiddleware middleware) {
        registeredMiddleware.add(middleware);
    }

    public void addToPipeline(SocketDataEventArg packet) {
        if (!connectionMiddleware.containsKey(packet.getId())) {
            // Create a new set of middleware object for the new connection
            // since the middleware are stateful objects and can't be reused on
            // different connections
            registerConnection(packet.getId());
        }
        this.inputBuffer.addPacket(packet);
        this.cycle();
    }

    private Queue<SocketDataEventArg> runThroughMiddleware(ConnectionId id) {

        Queue<SocketDataEventArg> packetQueue = inputBuffer.getPacketQueue(id);
        // Input to the next stage
        Queue<SocketDataEventArg> stageOutput = new LinkedBlockingQueue<>();
        ArrayList<ProxyMiddleware> middlewares = connectionMiddleware.get(id);

        // Handle the case that no middleware is present
        // In case the main controller is dead, pass the packets
        // of the backup controller, which is now manages as ControllerRegion not ReplicaRegion
        if (middlewares.isEmpty() || !isMainControllerAlive) {
            // Consume the packet queue
            while (!packetQueue.isEmpty()) {
                SocketDataEventArg p = packetQueue.poll();

                // Packets from the backup controller are only used for matching
                // if no middleware is present, we don't need to pass those
                // packets to the switches
                if (p.getSenderType() == SenderType.ControllerRegion) {
                    stageOutput.add(p);
                }
            }
            return stageOutput;
        }

        // Normal case, we have middleware
        for (ProxyMiddleware middleware : middlewares) {
            // In the first run, use the middleware manager input queue
            if (!packetQueue.isEmpty()) {
                stageOutput = processByMiddleware(packetQueue, middleware);
            } else {
                stageOutput = processByMiddleware(stageOutput, middleware);
            }

            if (middleware.hasError()) {
                throw new IllegalStateException();
            }
        }

        return stageOutput;
    }

    private Queue<SocketDataEventArg> processByMiddleware(final Queue<SocketDataEventArg> inputPackets,
                                                          final ProxyMiddleware middleware) {

        LinkedBlockingQueue<SocketDataEventArg> output = new LinkedBlockingQueue<>();

        while (!inputPackets.isEmpty()) {
            SocketDataEventArg packet = inputPackets.poll();

            middleware.addInput(packet);
            middleware.execute();

            // Consume all output
            while (middleware.hasOutput()) {
                output.add(middleware.getOutput());
            }
        }

        return output;
    }

    private void registerConnection(ConnectionId id) {
        ArrayList<ProxyMiddleware> mw = createNewMiddlewarePipeline(id);
        this.connectionMiddleware.put(id, mw);
    }

    private ArrayList<ProxyMiddleware> createNewMiddlewarePipeline(ConnectionId id) {
        ArrayList<ProxyMiddleware> middleWareForId = new ArrayList<>(this.registeredMiddleware.size());

        for (ProxyMiddleware mw : this.registeredMiddleware) {
            middleWareForId.add(mw.clone(id));
        }

        return middleWareForId;
    }

    public boolean hasOutput() {
        return !this.outputBuffer.isEmpty();
    }

    public SocketDataEventArg getOutput() {
        return this.outputBuffer.poll();
    }

    public void setMainControllerAlive(boolean mainControllerAlive) {
        isMainControllerAlive = mainControllerAlive;
    }
}

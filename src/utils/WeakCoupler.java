package utils;

import java.util.ArrayList;

/**
 * Used to couple mediator and controller region subscribers
 */
public class WeakCoupler {
    private final Class<?> input;
    private final Class<?> output;
    private ArrayList<SocketEventObserver> inputObservers;
    private ArrayList<SocketEventObserver> outputObservers;

    /*
        Use event buffer, and users query the coupler, each new event
        a copy of the event queue is created and given for each registrar when
        it asks for it. ??

        after all registrars have had their copy of the events, this event
        will be removed from the queue

        this implies that no registrar can get 2 copies of the same event
        we can use a bit vector?
     */
    private EventBuffer inputEventBuffer;
    private EventBuffer outputEventBuffer;

    WeakCoupler(Class<?> input, Class<?> output) {

        this.input = input;
        this.output = output;
        this.inputObservers = new ArrayList<>();
        this.outputObservers = new ArrayList<>();
    }

    public void post(SocketEventObserver who, SocketEventArg what) {
        Class<?> target = who.getClass();
        // TODO Add to event buffer or directly notify?
        if (target == input) {
            for (SocketEventObserver o : this.inputObservers) {
                o.update(what);
            }
        } else if (target == output) {
            for (SocketEventObserver o : this.outputObservers) {
                o.update(what);
            }
        } else {
            throw new IllegalArgumentException("Target not listed");
        }
    }

    public void register(SocketEventObserver o) {
        Class<?> observerClass = o.getClass();

        if (observerClass == this.input) {
            this.outputObservers.add(o);
        } else if (observerClass == this.output) {
            this.inputObservers.add(o);
        } else {
            throw new IllegalArgumentException("Observer not listed");
        }
    }

    public void deregister(SocketEventObserver o) {
        Class<?> observerClass = o.getClass();

        if (observerClass == this.input) {
            this.outputObservers.remove(o);
        } else if (observerClass == this.output) {
            this.inputObservers.remove(o);
        } else {
            throw new IllegalArgumentException("Observer not listed");
        }
    }

}

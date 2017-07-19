package utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class WeakCouplerTest {

    class DummyPub implements SocketEventObserver {
        public int count;

        @Override
        public void update(SocketEventArg arg) {
            count++;
        }
    }

    class DummySub implements SocketEventObserver {
        public int count;

        @Override
        public void update(SocketEventArg arg) {
            count++;
        }
    }

    class DummyInvalid implements SocketEventObserver {

        @Override
        public void update(SocketEventArg arg) {
            throw new RuntimeException("I'm not supposed to be called");
        }
    }

    @Test
    public void testCoupling() throws Exception {
        WeakCoupler coupler = new WeakCoupler(DummyPub.class, DummySub.class);
        DummyPub publisher = new DummyPub();
        DummySub subscriber = new DummySub();

        coupler.register(subscriber);
        coupler.register(publisher);

        coupler.post(publisher,
                new SocketEventArg(SenderType.Socket,
                        EventType.Connection, ConnectionId.CreateForTesting("a")));

        assertEquals(1, subscriber.count);

        coupler.deregister(subscriber);

        coupler.post(publisher,
                new SocketEventArg(SenderType.Socket,
                        EventType.Connection, ConnectionId.CreateForTesting("a")));

        // Assert that the count hasn't changes
        assertEquals(1, subscriber.count);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRegister() throws Exception {
        WeakCoupler coupler = new WeakCoupler(DummyPub.class, DummySub.class);

        DummyInvalid invalid = new DummyInvalid();
        coupler.register(invalid);
    }

}
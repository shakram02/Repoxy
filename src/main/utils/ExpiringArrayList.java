package utils;

import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import utils.events.SocketDataEventArg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class ExpiringArrayList implements Iterable<SocketDataEventArg> {

    private final int threshold;
    private final Consumer<SocketDataEventArg> expiryHandler;
    private final ArrayList<SocketDataEventArg> data;
    private Timer expirationAlarm;

    public ExpiringArrayList(int threshold, Consumer<SocketDataEventArg> expiryHandler) {
        this.threshold = threshold;
        this.expiryHandler = expiryHandler;

        expirationAlarm = new Timer(true);
        expirationAlarm.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ExpiringArrayList.this.setTimer();
            }
        },0, (threshold / 2));

        data = new ArrayList<>();
    }

    public boolean add(SocketDataEventArg item) {
        return data.add(item);
    }

    private void setTimer() {

        if (data.isEmpty()) {
            return;
        }

        SocketDataEventArg oldest = data.get(0);
        long delay = Math.abs(oldest.getTimestamp() - System.currentTimeMillis());

        if (delay > threshold) {
            expiryHandler.accept(oldest);
        }
    }

    @NotNull
    @Override
    public Iterator<SocketDataEventArg> iterator() {
        return this.data.iterator();
    }
}
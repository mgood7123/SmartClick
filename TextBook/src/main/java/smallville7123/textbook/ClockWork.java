package smallville7123.textbook;

// TODO: should we abandon this?

import android.util.Log;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

/**
 * a simple utility for timing information
 */
public class ClockWork {
    private static final String TAG = "ClockWork";

    ArrayList<Data> data = new ArrayList();
    
    class Data {

        String key;
        ArrayList<Instant> start;
        Instant lastStart;
        ArrayList<Instant> end;
        Instant lastEnd;
        boolean protect;

        public Data() {
            start = new ArrayList<>();
            end = new ArrayList<>();
        }

        public Data(String key) {
            this();
            this.key = key;
        }

        public void start() {
            lastStart = Instant.now();
            start.add(lastStart);
        }

        public void end() {
            lastEnd = Instant.now();
            end.add(lastEnd);
        }

        public Duration getDurationOfLast() {
            return Duration.between(lastStart, lastEnd);
        }

        public Duration getDurationTotal() {
            int startSize = start.size();
            int endSize = end.size();
            if (startSize != endSize) {
                throw new RuntimeException("start/end balance mismatch: startSize = [" + startSize + "], endSize = [" + endSize + "]");
            } else if (startSize == 0 && endSize == 0) {
                return Duration.ZERO;
            }

            Duration total = Duration.between(start.get(0), end.get(0));

            for (int i = 1; i < start.size(); i++) {
                Duration duration = Duration.between(start.get(i), end.get(i));
                total = total.plus(duration);
            }
            return total;
        }

        public void reset() {
            if (!protect) {
                start.clear();
                lastStart = null;
                end.clear();
                lastEnd = null;
            } else {
                Log.w(TAG, "trying to reset a protected key: " + key + ", to reset this key, please invoke clearProtection(\"" + key + "\") and try to reset this key again");
            }
        }

        public void protect() {
            protect = true;
        }

        public void clearProtection() {
            protect = false;
        }

        public boolean isProtect() {
            return protect;
        }
    }

    static String key = "Clockwork Internal";
    Data clockworkInternal = new Data(key);


    boolean haskey(String key) {
        clockworkInternal.start();
        for (Data data : data) {
            if (data.key.contentEquals(key)) {
                clockworkInternal.end();
                return true;
            }
        }
        clockworkInternal.end();
        return false;
    }

    Data getData(String key) {
        clockworkInternal.start();
        for (Data data : data) {
            if (data.key.contentEquals(key)) {
                clockworkInternal.end();
                return data;
            }
        }
        clockworkInternal.end();
        return null;
    }

    private Data getDataAndCreateIfDataDoesNotExist(String key) {
        Data data = getData(key);
        return data != null ? data : addData(key);
    }

    private Data addData(String key) {
        clockworkInternal.start();
        Data data = new Data(key);
        this.data.add(data);
        clockworkInternal.end();
        return data;
    }

    public void start(String key) {
        clockworkInternal.start();
        Data data = getDataAndCreateIfDataDoesNotExist(key);
        data.start();
        clockworkInternal.end();
    }

    public void end(String key) {
        clockworkInternal.start();
        Data data = getDataAndCreateIfDataDoesNotExist(key);
        data.end();
        clockworkInternal.end();
    }

    public void protect(String key) {
        clockworkInternal.start();
        Data data = getData(key);
        if (data != null) data.protect();
        clockworkInternal.end();
    }

    public void clearProtection(String key) {
        clockworkInternal.start();
        Data data = getData(key);
        if (data != null) data.clearProtection();
        clockworkInternal.end();
    }
    
    public void reset(String key) {
        clockworkInternal.start();
        Data data = getData(key);
        if (data != null) data.reset();
        clockworkInternal.end();
    }

    public void reset() {
        for (Data data : data) data.reset();
        clockworkInternal.reset();
    }

    public void stats() {
        StringBuilder log = new StringBuilder();
        clockworkInternal.start();
        for (Data data : data) {
            log.append(
                    data.key +
                            ": completed in " +
                            data.getDurationTotal().toMillis() +
                            " milliseconds\n"
            );
        }
        clockworkInternal.end();
        log.append(
                clockworkInternal.key +
                        ": completed in " +
                        clockworkInternal.getDurationTotal().toMillis() +
                        " milliseconds\n"
        );
        Log.d(TAG, "ClockWork Statistics:\n" +
                log);
    }
}

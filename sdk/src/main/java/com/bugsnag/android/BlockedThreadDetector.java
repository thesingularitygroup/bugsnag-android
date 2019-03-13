package com.bugsnag.android;

import android.os.Handler;
import android.os.Looper;

/**
 * Detects whether a given thread is blocked by continuously posting a {@link Runnable} to it
 * from a watcher thread, invoking a delegate if the message is not processed within
 * a configured interval.
 */
final class BlockedThreadDetector {

    interface Delegate {

        /**
         * Invoked when a given thread has been unable to execute a {@link Runnable} within
         * the {@link #blockedThresholdMs}
         *
         * @param thread the thread being monitored
         */
        void onThreadBlocked(Thread thread);
    }

    final Looper looper;
    final long blockedThresholdMs;
    final Handler handler;
    final Delegate delegate;

    volatile int tick = 0;
    volatile boolean isAlreadyBlocked = false;

    BlockedThreadDetector(long blockedThresholdMs,
                          Looper looper,
                          Delegate delegate) {
        if (blockedThresholdMs <= 0 || looper == null || delegate == null) {
            throw new IllegalArgumentException();
        }
        this.blockedThresholdMs = blockedThresholdMs;
        this.looper = looper;
        this.delegate = delegate;
        this.handler = new Handler(looper);
    }

    void start() {
        watcherThread.start();
    }

    final Runnable livenessCheck = new Runnable() {
        @Override
        public void run() {
            tick = tick + 1;
        }
    };

    final Thread watcherThread = new Thread() {
        @Override
        public void run() {
            while (!isInterrupted()) {
                long prevTick = tick;
                handler.post(livenessCheck);

                try {
                    Thread.sleep(blockedThresholdMs); // throttle checks to the configured threshold
                } catch (InterruptedException exc) {
                    interrupt();
                }

                if (tick == prevTick) { // thread has not processed runnable and is blocked
                    if (!isAlreadyBlocked) {
                        delegate.onThreadBlocked(looper.getThread());
                    }
                    isAlreadyBlocked = true; // prevents duplicate reports for the same ANR
                } else {
                    isAlreadyBlocked = false;
                }
            }
        }
    };
}

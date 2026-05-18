package com.fathzer.soft.ajlib.swing.worker;

import static org.junit.jupiter.api.Assertions.*;
import static org.awaitility.Awaitility.await;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

class WorkerTest {
    private static class TestWorker extends Worker<String, Void> {
        private final CountDownLatch latch;
        private final Supplier<String> doProcessing;
        private final WorkerWaitSpy waitSpy;

        TestWorker(Supplier<String> doProcessing) {
            this.doProcessing = doProcessing;
            this.latch =  new CountDownLatch(1);
            this.waitSpy = new WorkerWaitSpy(this);
        }

        @Override
        protected String doProcessing() throws Exception {
            latch.await();
            return doProcessing.get();
        }

        void launch(Runnable onStarted) {
            waitSpy.start(1000);
            super.execute();
            await().until(() -> getState() == StateValue.STARTED);
            onStarted.run();
            unlockDoProcessing();
        }
        
        private void unlockDoProcessing() {
        // Open the latch. The background thread can continue processing right away.
            latch.countDown();
        }

        WorkerWaitSpy getWaitSpy() {
            return waitSpy;
        }
    }

    private static class WorkerWaitSpy {
        private final Worker<?,?> worker;
        private final AtomicBoolean notified = new AtomicBoolean(false);

        WorkerWaitSpy(Worker<?,?> worker) {
            this.worker = worker;
        }

        void start(long timeout) {
            Runnable r = () -> {
                try {
                    synchronized (worker) {
                        while (!worker.isFinished()) {
                            worker.wait(timeout);
                        }
                    }
                    notified.set(true);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.start();
        }

        void awaitWaitAndDone(Worker<?, ?> worker) {
            await()
                .atMost(500, TimeUnit.MILLISECONDS)
                .until(() -> notified.get() && worker.isDone());
        }
    }


    @Test
    void testOk() throws Exception {
        TestWorker worker = new TestWorker(() -> "result") ;
        
        worker.launch(() -> {});

        worker.getWaitSpy().awaitWaitAndDone(worker);

        assertTrue(worker.isDone());
        assertFalse(worker.isCancelled());
        assertEquals("result", worker.get());
        assertTrue(worker.isFinished());
    }

    @Test
    void testFailure() {
        TestWorker worker = new TestWorker(() -> {
            throw new UnsupportedOperationException("Unimplemented method 'doProcessing'");
        });

        worker.launch(() -> {});
        worker.getWaitSpy().awaitWaitAndDone(worker);
        assertTrue(worker.isDone());
        assertFalse(worker.isCancelled());
        assertThrows(ExecutionException.class, worker::get);
        assertTrue(worker.isFinished());
    }

    @Test
    void testCancellation() {
        TestWorker worker = new TestWorker(() -> "result");

        worker.launch(() -> worker.cancel(false));
        worker.getWaitSpy().awaitWaitAndDone(worker);
        assertTrue(worker.isDone());
        assertTrue(worker.isCancelled());
        assertThrows(CancellationException.class, worker::get);
        assertTrue(worker.isFinished());
    }
}

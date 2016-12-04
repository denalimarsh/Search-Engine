import java.util.LinkedList;

public class WorkQueue {

    /**
     * Pool of worker threads that will wait in the background until work is
     * available.
     */
    private final PoolWorker[] workers;

    /** Queue of pending work requests. */
    private final LinkedList<Runnable> queue;

    /** Used to signal the queue should be shutdown. */
    private volatile boolean shutdown;

    /** The default number of threads to use when not specified. */
    public static final int DEFAULT = 5;    
    
    private int pending;

    /**
     * Starts a work queue with the default number of threads.
     * 
     * @see #WorkQueue(int)
     */
    public WorkQueue() {
        this(DEFAULT);
    }

    /**
     * Starts a work queue with the specified number of threads.
     *
     * @param threads
     *            number of worker threads; should be greater than 1
     */
    public WorkQueue(int threads) {
        this.queue = new LinkedList<Runnable>();
        this.workers = new PoolWorker[threads];
        this.pending = 0;
        this.shutdown = false;

        for (int i = 0; i < threads; i++) {
            workers[i] = new PoolWorker();
            workers[i].start();
        }
    }

    /**
     * Adds a work request to the queue. A thread will process this request when
     * available.
     *
     * @param r
     *            work request (in the form of a {@link Runnable} object)
     */
    public void execute(Runnable r) {
        synchronized (queue) {
        	increase();
            queue.addLast(r);
            queue.notifyAll();
        }
    }

    public void finish() {
    	try {
    		synchronized(queue){
				while (pending > 0) {
					queue.wait();
				}
    		}
		}
		catch (InterruptedException e) {
		
		}
		
    }

    /**
     * Asks the queue to shutdown. Any unprocessed work will not be finished,
     * but threads in-progress will not be interrupted.
     */
    public void shutdown() {

    	finish();
        shutdown = true;

        synchronized (queue) {
            queue.notifyAll();
        }
    }

    /**
     * Returns the number of worker threads being used by the work queue.
     *
     * @return number of worker threads
     */
    public int size() {
        return workers.length;
    }
    
    
	private void increase() {
		synchronized(queue){
			pending++;
		}
	}

	private void decrease() {
		synchronized(queue){
			assert pending > 0;
			pending--;
			if (pending <= 0) {
				queue.notifyAll();
			}
		}
	}
	

    /**
     * Waits until work is available in the work queue. When work is found, will
     * remove the work from the queue and run it. If a shutdown is detected,
     * will exit instead of grabbing new work from the queue. These threads will
     * continue running in the background until a shutdown is requested.
     */
    private class PoolWorker extends Thread {

        @Override
        public void run() {
        	
            Runnable r = null;

            while (true) {
                synchronized (queue) {
                    while (queue.isEmpty() && !shutdown) {
                        try {
                            queue.wait();
                        }
                        catch (InterruptedException ex) {
                            System.err.println("Warning: Work queue interrupted.");
                            Thread.currentThread().interrupt();
                        }
                    }

                    if (shutdown) {
                        break;
                    }
                    else {
                        r = queue.removeFirst();
                    }
                }

                try {
                    r.run();
                }
                catch (RuntimeException ex) {
                    System.err.println("Warning: Work queue encountered an "
                            + "exception while running.");
                }finally{
                	decrease();
                }
            }
        }
    }
}
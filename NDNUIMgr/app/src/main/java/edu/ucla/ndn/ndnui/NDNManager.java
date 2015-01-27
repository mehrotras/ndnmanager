package edu.ucla.ndn.ndnui;

import android.os.Handler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by SMehrotra on 1/10/2015.
 */
public class NDNManager {
    /*
         * Status indicators
         */
    static final int DOWNLOAD_FAILED = -1;
    static final int DOWNLOAD_STARTED = 1;
    static final int DOWNLOAD_COMPLETE = 2;
    static final int DECODE_STARTED = 3;
    static final int TASK_COMPLETE = 4;
    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    // Sets the initial threadpool size to 8
    private static final int CORE_POOL_SIZE = 8;

    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 8;

    /**
     * NOTE: This is the number of total available cores. On current versions of
     * Android, with devices that use plug-and-play cores, this will return less
     * than the total number of cores. The total number of cores is not
     * available in current Android implementations.
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    // An object that manages Messages in a Thread
    private Handler mHandler;

    // A single instance of NDNManager, used to implement the singleton pattern
    private static NDNManager sInstance = null;
    // A managed pool of background download threads
    private final ThreadPoolExecutor mNDNThreadPool;
    // A queue of Runnables for the NDN pool
    private final BlockingQueue<Runnable> mNDNWorkQueue;

    // A static block that sets class fields
    static {

        // The time unit for "keep alive" is in seconds
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

        // Creates a single static instance of PhotoManager
        sInstance = new NDNManager();
    }

    private NDNManager() {
        /*
         * Creates a work queue for the pool of Thread objects used for downloading, using a linked
         * list queue that blocks when the queue is empty.
         */
        mNDNWorkQueue = new LinkedBlockingQueue<Runnable>();
        /*
         * Creates a new pool of Thread objects for the download work queue
         */
        mNDNThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mNDNWorkQueue);

    }

    /**
     * Returns the NDNManager object
     *
     * @return The global NDNManager object
     */
    public static NDNManager getInstance() {

        return sInstance;
    }

}

package de.verism.server.json;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The temporary content of {@link FileContentProvider#fileContent} is only deleted if retrieved by a key from the client side.
 * If the client uploads a file, but then interrupts the connection, the uploaded file will never be deleted from the server cache.
 * 
 * This cleanup service ensures periodic deletions of the file content hash on the server.
 * Caution: this does not work on a GAE server!
 * @author Daniel Kotyk
 *
 */
public class CleanupJobTask {
	//periodic cleanup of the filecache every 10 mins
    private static final int CLEANUP_MINS = 10;
    
    //the cleanup task
    private static ScheduledExecutorService cleanupJob = Executors.newSingleThreadScheduledExecutor();
    
    //provides access to future actions on the task
    private static ScheduledFuture<?> futureHandle = initCleanup();
    
	/**
	 * Creates the delayed cleanup job.
	 * @return the future handle of the job
	 */
    private static ScheduledFuture<?> initCleanup() {
    	return cleanupJob.scheduleWithFixedDelay(new Runnable() {
		    public void run() {
		    	FileContentProvider.clearFileCache();
		     }
		}, CLEANUP_MINS, CLEANUP_MINS, TimeUnit.MINUTES);
	}

    /**
     * Restarts the cleanup timer.
     */
	public static void reset() {
		futureHandle.cancel(false);
		futureHandle = initCleanup();
	}
}

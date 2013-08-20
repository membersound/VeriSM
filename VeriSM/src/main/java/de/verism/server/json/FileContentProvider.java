package de.verism.server.json;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.verism.client.rpc.JSONService;
import de.verism.server.file.FileDownloadServlet;

/**
 * Maintains a temporary list containing the whole project file content.
 * Used for saving between the {@link JSONService} RPC call and the creating of download file trough {@link FileDownloadServlet}.
 * @author Daniel Kotyk
 *
 */
public class FileContentProvider {
	//use concurrent hashmap to avoid race conditions during periodic cleanup
    private static Map<Long, String> fileCache = new ConcurrentHashMap<Long, String>();

	/**
     * Add a new key-value entry to the fileContent.
     * @param content the content to be converted into a downloadable file
     * @return the current time in millis identifying the requested file content
     */
	public static Long addFileContent(String content) {
		//first reset the cleanup so that the client has enough time to fetch the file content
		delayCleanup = true;
		CleanupJobTask.reset();
		
		Long key = generateFileId();
		fileCache.put(key, content);
		return key;
	}
	
	/**
     * Returns an entry from the fileContent.
     * Also by beeing the only accesor to the private {@link #fileCache}
     * it ensures that content is cleared once the downloadable project file is created by the {@link FileDownloadServlet}.
     * @param id
     * @return
     */
    public static String getFileContent(Long id) {
    	String content = fileCache.get(id);
    	fileCache.remove(id);
    	return content;
    }
    
	/**
	 * Create an unique id for the file to be exported.
	 * @return the id as 'Long' in ms
	 */
	public static Long generateFileId() {
		return System.currentTimeMillis();
	}

	/**
	 * Provides reset access to the file content cache.
	 */
	public static void clearFileCache() {
		if (!delayCleanup) {
			fileCache.clear();
		}
		
		delayCleanup = false;
	}
	
	//cleanup may be skipped if a file has been transfered shortly, to ensure that the content is not deleted between upload requests
	private static boolean delayCleanup = false;
}

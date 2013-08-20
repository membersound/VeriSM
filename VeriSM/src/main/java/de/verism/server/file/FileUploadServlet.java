package de.verism.server.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import de.verism.server.json.FileContentProvider;

/**
 * Servlet processing project files the user has uploaded to the application.
 * @author Daniel Kotyk
 *
 */
public class FileUploadServlet extends HttpServlet {
    // sizeMax - The maximum allowed size, in bytes.
    //The default value of -1 indicates, that there is no limit.
    private static final int MAX_FILESIZE_MB = 10;
    //indicates the end of the id string
	public static final String ID_DELIMITER = "-";
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //create file upload handler
        ServletFileUpload uploadHandler = new ServletFileUpload();
       
        //1048576 bytes = 1 mb
        uploadHandler.setSizeMax(1048576 * MAX_FILESIZE_MB);
 
        //ensure access through file upload
        if (!ServletFileUpload.isMultipartContent(request)) {
            response.getWriter().write("File upload request invalid. No file content transfered.");
            return;
        }

        //enables the client side to receive the file content
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String responseText = null;
        
		try {
            FileItemIterator iterator = uploadHandler.getItemIterator(request);
            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();

                //exclude all form fields to only get content of the upload field
                if (!item.isFormField()) {
                	String fileContent = getDataFromStream(item);
                	
            		//they key is to be transmitted back to the client,
            		//which then issues a new request to receive the deserialized file using this key as reference.
                	Long key = FileContentProvider.addFileContent(fileContent);
            		responseText = key.toString() + ID_DELIMITER;
                    break;
                }
            }
            
            if (responseText == null || responseText.isEmpty()) {
            	responseText = "The file could not be uploaded.";
            }

		} catch (SizeLimitExceededException e) {
        	responseText = "File size exceeds the limit of " + MAX_FILESIZE_MB + " MB." ;
        } catch (Exception e) {
            e.printStackTrace();
            responseText = "File Upload Error:\n" + e.toString();
        } finally {
            out.println(responseText);
			out.flush();
            out.close();
        }
    }

	private String getDataFromStream(FileItemStream item) throws IOException {
		//read the file content from stream
    	//http://commons.apache.org/proper/commons-fileupload/streaming.html
        InputStream stream = item.openStream();
        String fileContent = IOUtils.toString(stream, "UTF-8");
        stream.close();
        
		return fileContent;
	}
}

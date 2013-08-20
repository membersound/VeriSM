package de.verism.server.file;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.verism.server.json.FileContentProvider;
import de.verism.shared.file.FileType;

/**
 * Servlet for providing file download.
 * @author Daniel Kotyk
 *
 */
public class FileDownloadServlet extends HttpServlet {
	public static final String SERVLET_NAME = "FileDownloadServlet";
	
	//filename for downloaded project files
	public static final String FILE_NAME = "filename";
	
	//query string property for transfering data to the servlet (?content=...)
	public static final String CONTENT_PROP = "content";

	public static final String TYPE_PROP = "contentType";

	
    /**
     * {@inheritDoc}
     */
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	FileType fileType = FileType.valueOf(request.getParameter(TYPE_PROP).toUpperCase());
    	String filename = request.getParameter(FILE_NAME);
    	
    	//set response properties to provide file download dialog
        response.setHeader("Content-Disposition", "attachment; filename=" + filename.replaceAll("\\s+", "_") + "." + fileType.getFileExt());
        response.setContentType(fileType.getContentType());
        
    	//get the file content from url request
    	Long id = Long.valueOf(request.getParameter(CONTENT_PROP));
    	String content = FileContentProvider.getFileContent(id);
        
    	//write the content
    	writeFileContent(response.getOutputStream(), content);
    }
    
    /**
     * Generate the downloadable project file.
     * @param outputStream
     * @param content
     * @throws IOException
     */
    private void writeFileContent(ServletOutputStream outputStream, String content) throws IOException {
		for (int i = 0; i < content.length(); ++i) {
			outputStream.write(content.charAt(i));
		}
		
		//cleanup
		outputStream.flush();
		outputStream.close();
	}
}

package de.verism.shared.file;

/**
 * Own file formats for different export mechanisms.
 * To be transmitted to the servlet as query string.
 * @author Daniel Kotyk
 *
 */
public enum FileType {
	PROJECT("verism", "application"), VERILOG("v", "application"), PICTURE("png", "image");
	
	private String fileExt, contentType;
	
	/**
	 * Constructor for initialization of the FileType enum representation.
	 * @param fileExt
	 * @param contentType
	 */
	private FileType(String fileExt, String contentType) {
		this.fileExt = fileExt;
		this.contentType = contentType;
	}
	
	/**
	 * @return the file extension of the file type.
	 */
	public String getFileExt() { return fileExt; }
	
	/**
	 * @return the content type for the attachment, eg: 'image/png'
	 */
	public String getContentType() { return contentType + "/" + fileExt; }
}
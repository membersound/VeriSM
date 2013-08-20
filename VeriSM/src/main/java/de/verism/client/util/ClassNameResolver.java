package de.verism.client.util;

/**
 * Class targeting GWT Issue #3404 (Class.getSimpleName() error).
 * @author Daniel Kotyk
 *
 */
public class ClassNameResolver {
	/**
	 * As GWT cannot handle the {@link #getSimpleName(String)} yet, get the fully qualified path and trim after the last dot.
	 * @param className the fully qualified classname
	 * @return
	 */
	public static String getSimpleName(Object obj) {
		String className = obj.getClass().getName();
		return className.substring(className.lastIndexOf(".") + 1);
	}
}

package de.verism.server;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Adds cache control to not cache the gwt files.
 * Otherwise application update is only shown on the user side when browser cache is cleared.
 * 
 * @author Daniel Kotyk
 */
public class CacheFilter implements Filter {
	//one minute
	private static final long MS_MIN = 60000;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {

		String uri = ((HttpServletRequest) req).getRequestURI();

		if (uri.contains(".nocache.")) {
			Date today = new Date();
			HttpServletResponse httpResponse = (HttpServletResponse) resp;
			httpResponse.setDateHeader("Date", today.getTime());
			
			//one day
			httpResponse.setDateHeader("Expires", today.getTime() - MS_MIN);
			httpResponse.setHeader("Pragma", "no-cache");
			httpResponse.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
		}

		chain.doFilter(req, resp);
	}

	@Override
	public void destroy() {}

	@Override
	public void init(FilterConfig arg0) throws ServletException {}
}
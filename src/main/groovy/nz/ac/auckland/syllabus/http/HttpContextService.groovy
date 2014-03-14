package nz.ac.auckland.syllabus.http

import nz.ac.auckland.common.stereotypes.UniversityComponent
import org.springframework.web.context.request.ServletRequestAttributes

import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.RequestAttributes

/**
 * User: marnix
 * Date: 3/04/13
 * Time: 2:48 PM
 *
 * The HTTP context service allows the developer to access httprequest and httpresponse objects.
 * This abstraction has been put in place because there will be no request and response when the
 * syllabus moves to websockets.
 */
@UniversityComponent
class HttpContextService {

	/**
	 * Key where servlet request is stored
	 */
	private static final String KEY_REQUEST = "_servlet_request"

	/**
	 * Key where servlet response is stored
	 */
	private static final String KEY_RESPONSE = "_servlet_response"

	/**
	 * Setup the request and response instances for this thread
	 *
	 * @param request instance for this request
	 * @param response instance for this response
	 */
	public void setup(HttpServletRequest request, HttpServletResponse response) {
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		RequestContextHolder.requestAttributes.setAttribute(KEY_REQUEST, request, RequestAttributes.SCOPE_REQUEST)
		RequestContextHolder.requestAttributes.setAttribute(KEY_RESPONSE, response, RequestAttributes.SCOPE_REQUEST)
	}

	/**
	 * @return the request instance for this thread
	 */
	public HttpServletRequest getRequest() {
		return RequestContextHolder
			.requestAttributes
			.getAttribute(KEY_REQUEST, RequestAttributes.SCOPE_REQUEST) as HttpServletRequest
	}

	/**
	 * @return the response instance for this thread
	 */
	public HttpServletResponse getResponse() {
		return RequestContextHolder
			.requestAttributes
			.getAttribute(KEY_RESPONSE, RequestAttributes.SCOPE_REQUEST) as HttpServletResponse
	}

}

package nz.ac.auckland.syllabus.http

import net.stickycode.stereotype.Configured
import org.springframework.util.StringUtils
import nz.ac.auckland.syllabus.events.Event

/**
 * User: marnix
 * Date: 26/03/13
 * Time: 2:47 PM
 *
 * Class that is able to extract useful request information from httpservletrequest.pathInfo
 */
class RequestInformation {

	/**
	 * Default version.
	 */
	public static final String DEFAULT_VERSION = "1"

	/**
	 * Request
	 */
	private String pathInfo;

	/**
	 * Initialize data-members
	 *
	 * @param pathInfo is the path information to extract the request information from
	 */
	public RequestInformation(String pathInfo) {
		this.pathInfo = pathInfo
	}

	/**
	 * Get the namespace information, or null when not present
	 */
	protected String getNamespace() {
		return getUrlPartIfPresent(pathInfo, 1, 3) ?:
			getUrlPartIfPresent(pathInfo, 0, 2) ?:
				Event.DEFAULT_NAMESPACE
	}

	/**
	 * Get action name, or null when not present
	 */
	protected String getAction() {
		return getUrlPartIfPresent(pathInfo, 2, 3) ?:
			getUrlPartIfPresent(pathInfo, 1, 2) ?:
				getUrlPartIfPresent(pathInfo, 0, 1)
	}

	/**
	 * Get version information or null when not present
	 */
	protected String getVersion() {
		String versionStr = getUrlPartIfPresent(pathInfo, 0, 3)

		if (versionStr) {
			return versionStr;
		} else {
			return DEFAULT_VERSION;
		}

	}

	/**
	 * Return a certain index if there are `isLength` or more url parts
	 *
	 * @param pathInfo is the path information
	 * @param atIndex
	 * @param isLength
	 * @return
	 */
	protected String getUrlPartIfPresent(String pathInfo, int atIndex, int isLength) {
		if (StringUtils.isEmpty(pathInfo)) {
			return null
		}

		// strip first char if it is '/'
		if (pathInfo.charAt(0) == '/') {
			pathInfo = pathInfo.substring(1);
		}

		String[] uriParts = pathInfo.split("/")
		if (uriParts.length >= isLength) {
			return uriParts[atIndex];
		}
		return null
	}
}

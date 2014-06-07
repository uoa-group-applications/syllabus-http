package nz.ac.auckland.syllabus.http

import groovy.transform.CompileStatic
import net.stickycode.stereotype.configured.PostConfigured
import nz.ac.auckland.common.jsresource.ApplicationResource
import nz.ac.auckland.common.jsresource.ResourceScope
import nz.ac.auckland.common.stereotypes.UniversityComponent
import nz.ac.auckland.lmz.common.AppVersion
import nz.ac.auckland.syllabus.events.EventHandlerCollection
import nz.ac.auckland.syllabus.generator.EventHandlerConfig

import javax.inject.Inject

/**
 * Author: Marnix
 *
 * Gathers all endpoints and creates a JSON string from them. This is a HTTP implementation specific service
 * as the websockets implementation will most likely introduce a different addressing schema.
 */
@CompileStatic
@UniversityComponent
class EndpointMapService implements ApplicationResource {

	/**
	 * System property that contains web app context
	 */
	private static final String WEBAPP_CONTEXT = "webapp.context";

	/**
	 * Collection holder
	 */
	@Inject
	private EventHandlerCollection eventCollection;

	/**
	 * Json Endpoints
	 */
	private String jsonEndpoints;


	@Inject
	AppVersion appVersion

	/**
	 * Endpoints
	 */
	private Map<String, Map<String, String>> endpoints;

	/**
	 * Create a map of endpoints
	 */
	@PostConfigured
	public void createEndpointMap() {
		// endpoints map
		endpoints = [:].withDefault { key -> return [:]}

		eventCollection.findAll().each { EventHandlerConfig handlerConfig ->
			endpoints[handlerConfig.namespace][handlerConfig.name] =
				String.format("%s/api/%s/%s/%s", this.contextPath, appVersion.version, handlerConfig.namespace, handlerConfig.name)
		}
	}

	/**
	 * @return the context path
	 */
	protected String getContextPath() {
		String path = System.getProperty(WEBAPP_CONTEXT);
		if (path == "/" || path == null) {
			return ""
		} else {
			return path;
		}
	}

	/**
	 * @return this is a global application resource
	 */
	@Override
	ResourceScope getResourceScope() {
		return ResourceScope.Global;
	}

	/**
	 * @return the map of endpoints
	 */
	@Override
	Map<String, Object> getResourceMap() {
		return ['endpoints': this.endpoints]
	}
}

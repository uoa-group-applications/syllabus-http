package nz.ac.auckland.syllabus.http

import groovy.transform.CompileStatic
import net.stickycode.stereotype.Configured
import net.stickycode.stereotype.configured.PostConfigured
import nz.ac.auckland.common.config.ConfigKey
import nz.ac.auckland.common.jsresource.ApplicationResource
import nz.ac.auckland.common.jsresource.ResourceScope
import nz.ac.auckland.common.stereotypes.UniversityComponent
import nz.ac.auckland.syllabus.events.EventHandlerCollection
import nz.ac.auckland.syllabus.events.EventHandler
import nz.ac.auckland.util.JacksonHelper

import javax.annotation.PostConstruct
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

	/**
	 * @see nz.ac.auckland.common.config.JarManifestConfigurationSource#KEY_IMPLEMENTATION_VERSION
	 */
	@ConfigKey("Implementation-Version")
	protected String version = 'unknown';

	@Configured
	String meh = "meh.";

	/**
	 * Endpoints
	 */
	private Map<String, Map<String, String>> endpoints;

	/**
	 * Create a map of endpoints
	 */
	@PostConfigured
	public void createEndpointMap() {

		// get all events
		Map<String, Map<String, EventHandler>> nsEventMap = eventCollection.eventMap

		// endpoints map
		endpoints = [:];

		// iterate through namespaces
		nsEventMap?.each { String namespace, Map<String, EventHandler> eventMap ->

			// iterate through event names
			eventMap?.each { String eventName, EventHandler eventHandler ->

				if (!endpoints[namespace]) {
					endpoints[namespace] = [:]
				}

				endpoints[namespace][eventName] = String.format("%s/api/%s/%s/%s", this.contextPath, version, namespace, eventName)
			}
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

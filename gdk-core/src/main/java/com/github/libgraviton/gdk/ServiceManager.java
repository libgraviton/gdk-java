package com.github.libgraviton.gdk;

import com.github.libgraviton.gdk.exception.NoCorrespondingServiceException;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages all available services and associates them with a corresponding POJO class.
 */
public class ServiceManager {

    /**
     * The POJO class -> service association.
     */
    protected Map<String, Service> services = new HashMap<>();

    /**
     * Adds a service and associates it with a given POJO class.
     *
     * @param className The POJO class name.
     * @param service The service.
     *
     * @return The number of currently added services.
     */
    public int addService(String className, Service service) {
        services.put(className, service);
        return services.size();
    }

    /**
     * Tells whether the service manager is aware of a service for a given POJO class.
     *
     * @param className The POJO class name.
     *
     * @return true when the service manager is aware of a service of the given POJO class, otherwise false.
     */
    public boolean hasService(String className) {
        return services.containsKey(className);
    }

    /**
     * Gets the service associated to the given class.
     *
     * @param className The class name.
     *
     * @return The associated service.
     *
     * @throws NoCorrespondingServiceException When the service manager is not aware of a service associated to
     * the given POJO class.
     */
    public Service getService(String className) throws NoCorrespondingServiceException {
        if (!hasService(className)) {
            throw new NoCorrespondingServiceException(className);
        }
        return services.get(className);
    }

}

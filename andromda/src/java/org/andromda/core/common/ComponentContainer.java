package org.andromda.core.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * <p/> This handles all registration and retrieval of components within the
 * framework. The purpose of this container is so that we can register default
 * services in a consistent manner by creating a component interface and then
 * placing the file which defines the default implementation in the
 * 'META-INF/services/' directory found on the classpath.
 * </p>
 * <p/> In order to create a new component that can be registered/found through
 * this container you must perform the following steps:
 * <ol>
 * <li>Create the component interface (i.e.
 * org.andromda.core.repository.RepositoryFacade)</li>
 * <li>Create the component implementation (i.e.
 * org.andromda.repositories.mdr.MDRepositoryFacade)</li>
 * <li>Create a file with the exact same name as the fully qualified name of
 * the component (i.e. org.andromda.core.repository.RepositoryFacade) that
 * contains the name of the implementation class (i.e.
 * org.andromda.repositories.mdr.MDRepostioryFacade) and place this in the
 * META-INF/services/ directory within the core.</li>
 * </ol>
 * After you perform the above steps, the component can be found by the methods
 * within this class. See each below method for more information on how each
 * performs lookup/retrieval of the components.
 * </p>
 *
 * @author Chad Brandon
 */
public class ComponentContainer
{
    private static Logger logger = Logger.getLogger(ComponentContainer.class);

    /**
     * Where all component default implementations are found.
     */
    private static final String SERVICES = "META-INF/services/";

    /**
     * The container instance
     */
    private final Map container = new LinkedHashMap();

    /**
     * The shared instance.
     */
    private static ComponentContainer instance = null;

    /**
     * Gets the shared instance of this ComponentContainer.
     *
     * @return PluginDiscoverer the static instance.
     */
    public final static ComponentContainer instance()
    {
        if (instance == null)
        {
            instance = new ComponentContainer();
        }
        return instance;
    }

    /**
     * Finds the component with the specified <code>key</code>.
     *
     * @param key the unique key of the component as an Object.
     * @return Object the component instance.
     */
    public Object findComponent(final Object key)
    {
        return this.container.get(key);
    }

    /**
     * Creates a new component of the given <code>implementation</code> (if it
     * isn't null or empty), otherwise attempts to find the default
     * implementation of the given <code>type</code> by searching the
     * <code>META-INF/services</code> directory for the default
     * implementation.
     *
     * @return a new instance of the given <code>type</code>
     */
    public Object newComponent(
        String implementation,
        final Class type)
    {
        Object component = null;
        implementation = implementation != null ? implementation.trim() : "";
        if (implementation.length() == 0)
        {
            component = this.newDefaultComponent(type);
        }
        else
        {
            component = ClassUtils.newInstance(implementation);
        }
        return component;
    }

    /**
     * Creates a new component of the given <code>type</code> by searching the
     * <code>META-INF/services</code> directory and finding its default
     * implementation.
     *
     * @return a new instance of the given <code>type</code>
     */
    public Object newDefaultComponent(final Class type)
    {
        final String methodName = "Component.newComponent";
        ExceptionUtils.checkNull(methodName, "type", type);
        Object component = null;
        try
        {
            final String implementation = this.getDefaultImplementation(type);
            if (implementation.trim().length() == 0)
            {
                throw new ComponentContainerException(
                    "Default configuration file '" + this.getComponentDefaultConfigurationPath(type) +
                    "' could not be found");
            }
            component = ClassUtils.loadClass(implementation).newInstance();
        }
        catch (final Throwable throwable)
        {
            throw new ComponentContainerException(throwable);
        }
        return component;
    }

    /**
     * Returns the expected path to the component's default configuration file.
     *
     * @param type the component type.
     * @return the path to the component configuration file.
     */
    protected final String getComponentDefaultConfigurationPath(final Class type)
    {
        final String methodName = "ComponentContainer.getComponentConfigurationPath";
        ExceptionUtils.checkNull(methodName, "type", type);
        return SERVICES + type.getName();
    }

    /**
     * Finds the component with the specified Class <code>key</code>. If the
     * component wasn't explicitly registered then the META-INF/services
     * directory on the classpath will be searched in order to find the default
     * component implementation.
     *
     * @param key the unique key as a Class.
     * @return Object the component instance.
     */
    public Object findComponent(final Class key)
    {
        final String methodName = "ComponentContainer.findComponent";
        ExceptionUtils.checkNull(methodName, "key", key);
        return this.findComponent(null, key);
    }

    /**
     * Attempts to Find the component with the specified <code>type</code>,
     * throwing a {@link ComponentContainerException} exception if one can not
     * be found.
     *
     * @param key the unique key of the component as an Object.
     * @return Object the component instance.
     */
    public Object findRequiredComponent(final Class key)
    {
        final Object component = this.findComponent(key);
        if (component == null)
        {
            throw new ComponentContainerException(
                "No implementation could be found for component '" + key.getName() +
                "', please make sure you have a '" + this.getComponentDefaultConfigurationPath(key) +
                "' file on your classpath");
        }
        return component;
    }

    /**
     * Attempts to find the component with the specified unique <code>key</code>,
     * if it can't be found, the default of the specified <code>type</code> is
     * returned, if no default is set, null is returned. The default is the
     * service found within the META-INF/services directory on your classpath.
     *
     * @param key the unique key of the component.
     * @param type the default type to retrieve if the component can not be
     *        found.
     * @return Object the component instance.
     */
    public Object findComponent(
        final String key,
        final Class type)
    {
        final String methodName = "ComponentContainer.findComponent";
        ExceptionUtils.checkNull(methodName, "type", type);
        try
        {
            Object component = this.findComponent(key);
            if (component == null)
            {
                final String typeName = type.getName();
                component = this.findComponent(typeName);

                // if the component doesn't have a default already
                // (i.e. component == null), then see if we can find the default
                // configuration file.
                if (component == null)
                {
                    final String defaultImplementation = this.getDefaultImplementation(type);
                    if (defaultImplementation.trim().length() > 0)
                    {
                        component =
                            this.registerDefaultComponent(
                                ClassUtils.loadClass(typeName),
                                ClassUtils.loadClass(defaultImplementation));
                    }
                    else
                    {
                        logger.warn(
                            "WARNING! Component's default configuration file '" +
                            getComponentDefaultConfigurationPath(type) + "' could not be found");
                    }
                }
            }
            return component;
        }
        catch (final Throwable throwable)
        {
            throw new ComponentContainerException(throwable);
        }
    }

    /**
     * Attempts to find the default configuration file from the
     * <code>META-INF/services</code> directory. Returns an empty String if
     * none is found.
     *
     * @param typeName the name of the type (i.e.
     *        org.andromda.core.templateengine.TemplateEngine)
     * @return
     */
    private final String getDefaultImplementation(final Class type)
    {
        final String contents = ResourceUtils.getContents(this.getComponentDefaultConfigurationPath(type));
        return contents != null ? contents.trim() : "";
    }

    /**
     * Finds all components having the given <code>type</code>.
     *
     * @param type the component type.
     * @return Collection all components
     */
    public Collection findComponentsOfType(final Class type)
    {
        final Collection components = new ArrayList(this.container.values());
        final Collection containerInstances = this.container.values();
        for (final Iterator iterator = containerInstances.iterator(); iterator.hasNext();)
        {
            final Object component = iterator.next();
            if (component instanceof ComponentContainer)
            {
                components.addAll(((ComponentContainer)component).container.values());
            }
        }
        final Collection componentsOfType = new ArrayList();
        for (final Iterator iterator = components.iterator(); iterator.hasNext();)
        {
            final Object component = iterator.next();
            if (type.isInstance(component))
            {
                componentsOfType.add(component);
            }
        }
        return componentsOfType;
    }

    /**
     * Unregisters the component in this container with a unique (within this
     * container) <code>key</code>.
     *
     * @param key the unique key.
     * @return Object the registered component.
     */
    public Object unregisterComponent(final String key)
    {
        final String methodName = "ComponentContainer.unregisterComponent";
        ExceptionUtils.checkEmpty(methodName, "key", key);
        if (logger.isDebugEnabled())
        {
            logger.debug("unregistering component with key --> '" + key + "'");
        }
        return container.remove(key);
    }

    /**
     * Finds a component in this container with a unique (within this container)
     * <code>key</code> registered by the specified <code>namespace</code>.
     *
     * @param namespace the namespace for which to search.
     * @param key the unique key.
     * @return the found component, or null.
     */
    public Object findComponentByNamespace(
        final String namespace,
        final Object key)
    {
        final String methodName = "findComponentByNamespace";
        ExceptionUtils.checkEmpty(methodName, "namespace", namespace);
        ExceptionUtils.checkNull(methodName, "key", key);

        Object component = null;
        final ComponentContainer namespaceContainer = this.getNamespaceContainer(namespace);
        if (namespaceContainer != null)
        {
            component = namespaceContainer.findComponent(key);
        }
        return component;
    }
    
    /**
     * Gets an instance of the container for the given <code>namespace</code>
     * or returns null if one can not be found.
     * 
     * @param namespace the name of the namespace.
     * @return the namespace container.
     */
    private final ComponentContainer getNamespaceContainer(final String namespace)
    {
        return (ComponentContainer)this.findComponent(namespace); 
    }

    /**
     * Registers true (false otherwise) if the component in this container with
     * a unique (within this container) <code>key</code> is registered by the
     * specified <code>namespace</code>.
     *
     * @param namespace the namespace for which to register the component.
     * @param key the unique key.
     * @return boolean true/false depending on whether or not it is registerd.
     */
    public boolean isRegisteredByNamespace(
        final String namespace,
        final Object key)
    {
        final String methodName = "ComponentContainer.isRegisteredByNamespace";
        ExceptionUtils.checkEmpty(methodName, "namespace", namespace);
        ExceptionUtils.checkNull(methodName, "key", key);
        final ComponentContainer namespaceContainer = this.getNamespaceContainer(namespace);
        return namespaceContainer != null && namespaceContainer.isRegistered(key);
    }

    /**
     * Registers true (false otherwise) if the component in this container with
     * a unique (within this container) <code>key</code> is registered.
     *
     * @param key the unique key.
     * @return boolean true/false depending on whether or not it is registerd.
     */
    public boolean isRegistered(final Object key)
    {
        return this.findComponent(key) != null;
    }

    /**
     * Registers the component in this container with a unique (within this
     * container) <code>key</code> by the specified <code>namespace</code>.
     *
     * @param namespace the namespace for which to register the component.
     * @param key the unique key.
     * @return Object the registered component.
     */
    public void registerComponentByNamespace(
        final String namespace,
        final Object key,
        final Object component)
    {
        final String methodName = "ComponentContainer.registerComponentByNamespace";
        ExceptionUtils.checkEmpty(methodName, "namespace", namespace);
        ExceptionUtils.checkNull(methodName, "component", component);
        if (logger.isDebugEnabled())
        {
            logger.debug("registering component '" + component + "' with key --> '" + key + "'");
        }
        ComponentContainer namespaceContainer = this.getNamespaceContainer(namespace);
        if (namespaceContainer == null)
        {
            namespaceContainer = new ComponentContainer();
            this.registerComponent(namespace, namespaceContainer);
        }
        namespaceContainer.registerComponent(key, component);
    }

    /**
     * Registers the component in this container with a unique (within this
     * container) <code>key</code>.
     *
     * @param key the unique key.
     * @return Object the registered component.
     */
    public Object registerComponent(
        final Object key,
        final Object component)
    {
        final String methodName = "ComponentContainer.registerComponent";
        ExceptionUtils.checkNull(methodName, "component", component);
        if (logger.isDebugEnabled())
        {
            logger.debug("registering component '" + component + "' with key --> '" + key + "'");
        }
        return this.container.put(key, component);
    }

    /**
     * Registers the "default" for the specified componentInterface.
     *
     * @param componentInterface the interface for the component.
     * @param defaultTypeName the name of the "default" type of the
     *        implementation to use for the componentInterface. Its expected
     *        that this is the name of a class.
     * @return Object the registered component.
     */
    public void registerDefaultComponent(
        final Class componentInterface,
        final String defaultTypeName)
    {
        final String methodName = "ComponentContainer.registerDefaultComponent";
        ExceptionUtils.checkNull(methodName, "componentInterface", componentInterface);
        ExceptionUtils.checkEmpty(methodName, "defaultTypeName", defaultTypeName);
        try
        {
            this.registerDefaultComponent(
                componentInterface,
                ClassUtils.loadClass(defaultTypeName));
        }
        catch (final Throwable throwable)
        {
            throw new ComponentContainerException(throwable);
        }
    }

    /**
     * Registers the "default" for the specified componentInterface.
     *
     * @param componentInterface the interface for the component.
     * @param defaultType the "default" implementation to use for the
     *        componentInterface.
     * @return Object the registered component.
     */
    public Object registerDefaultComponent(
        final Class componentInterface,
        final Class defaultType)
    {
        final String methodName = "ComponentContainer.registerDefaultComponent";
        ExceptionUtils.checkNull(methodName, "componentInterface", componentInterface);
        ExceptionUtils.checkNull(methodName, "defaultType", defaultType);
        if (logger.isDebugEnabled())
        {
            logger.debug(
                "registering default for component '" + componentInterface + "' as type --> '" + defaultType + "'");
        }
        try
        {
            final String interfaceName = componentInterface.getName();

            // check and unregister the component if its registered
            // so that we can register a new default component.
            if (this.isRegistered(interfaceName))
            {
                this.unregisterComponent(interfaceName);
            }
            final Object component = defaultType.newInstance();
            this.container.put(interfaceName, component);
            return component;
        }
        catch (final Throwable throwable)
        {
            throw new ComponentContainerException(throwable);
        }
    }

    /**
     * Registers the component of the specified <code>type</code>.
     *
     * @param type the type Class.
     * @return Object an instance of the type registered.
     */
    public void registerComponentType(final Class type)
    {
        final String methodName = "ComponentContainer.registerComponent";
        ExceptionUtils.checkNull(methodName, "type", type);
        try
        {
            this.container.put(
                type,
                type.newInstance());
        }
        catch (final Throwable throwable)
        {
            throw new ComponentContainerException(throwable);
        }
    }

    /**
     * Registers the components of the specified <code>type</code>.
     *
     * @param type the name of a type (must have be able to be instantiated into
     *        a Class instance)
     * @return Object an instance of the type registered.
     */
    public Object registerComponentType(final String type)
    {
        final String methodName = "ComponentContainer.registerComponent";
        ExceptionUtils.checkNull(methodName, "type", type);
        try
        {
            return this.registerComponent(
                type,
                ClassUtils.loadClass(type).newInstance());
        }
        catch (final Throwable throwable)
        {
            throw new ComponentContainerException(throwable);
        }
    }

    /**
     * Shuts down this container instance.
     */
    public void shutdown()
    {
        this.container.clear();
        instance = null;
    }
}
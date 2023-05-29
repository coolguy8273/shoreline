package com.caspian.client.api.event.listener;

import com.caspian.client.Caspian;
import com.caspian.client.api.Invoker;
import com.caspian.client.api.event.Event;
import com.caspian.client.api.event.handler.EventHandler;

import java.lang.invoke.*;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link Event} Listener that creates an {@link Invoker} and runs {@link #invoke(Event)}
 * when the event is dispatched by the {@link EventHandler}.
 *
 * <p>The invoker is created using {@link LambdaMetafactory} which is nearly
 * as fast as direct access; This method of invocation allows for blazing
 * fast event handling. Event Listeners can be created with the
 * {@link EventListener} annotation, for example:<p/>
 * <pre>{@code
 * @EventListener
 * public void onEvent(Event e)
 * {
 *     // your code ...
 * }
 * }</pre>
 *
 * @author linus
 * @since 1.0
 *
 * @see Event
 * @see EventHandler
 * @see EventListener
 */
public class Listener
{

    // subscriber invoker cache for each listener method
    private static final Map<Method, Invoker<Object>> invokableCache = new HashMap<>();

    // the MethodHandler lookup
    private static final Lookup LOOKUP = MethodHandles.lookup();

    // The EventListener method which contains the code to invoke when the
    // listener is invoked.
    private final Method method;
    // The object that contains the EventListener. This object must be
    // subscribed to the EventHandler in order for this Listener to be invoked.
    private final Object subscriber;
    // The Listener invoker created by the LambdaMetaFactory which invokes the
    // code from the Listener method.
    private Invoker<Object> invoker;

    /**
     *
     *
     * @param method
     * @param subscriber
     */
    public Listener(Method method, Object subscriber)
    {
        this.method = method;
        this.subscriber = subscriber;
        // lambda at runtime to call the method
        try
        {
            if (!invokableCache.containsKey(method))
            {
                // i love bush bus
                MethodType methodType = MethodType.methodType(Invoker.class);
                CallSite callSite = LambdaMetafactory.metafactory(
                        LOOKUP,
                        "invoke",
                        methodType.appendParameterTypes(subscriber.getClass()),
                        MethodType.methodType(void.class, Object.class),
                        LOOKUP.unreflect(method),
                        MethodType.methodType(void.class, method.getParameterTypes()[0])
                );
                invoker = (Invoker<Object>) callSite.getTarget().invoke(subscriber);
                invokableCache.put(method, invoker);
            }
            else
            {
                invoker = invokableCache.get(method);
            }
        }
        catch (Throwable e)
        {
            Caspian.error("Failed to build invoker for %s", method.getName());
            e.printStackTrace();
        }
    }

    /**
     *
     *
     * @return
     */
    public Method getMethod()
    {
        return method;
    }

    /**
     *
     *
     * @return
     */
    public Object getSubscriber()
    {
        return subscriber;
    }

    /**
     *
     *
     * @param event
     *
     * @see Invoker#invoke(Object)
     */
    public void invoke(Event event)
    {
        invoker.invoke(event);
    }
}
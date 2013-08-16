/*
 * Copyright 2013 TeamNexus
 *
 * TeamNexus Licenses this file to you under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License
 */

package com.nexus.event;

/**
 * TODO: Edit description
 *
 * @author jk-5
 */

import com.google.common.reflect.TypeToken;
import com.nexus.logging.NexusLog;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class EventBus{

    private static int maxID = 0;

    private final ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners = new ConcurrentHashMap<Object, ArrayList<IEventListener>>();
    private final int busID = maxID++;

    public EventBus(){
        ListenerList.resize(busID + 1);
    }

    public void register(Object target){
        if(this.listeners.containsKey(target)) return;
        Set<? extends Class<?>> supers = TypeToken.of(target.getClass()).getTypes().rawTypes();
        for(Method method : target.getClass().getMethods()){
            for(Class<?> cls : supers){
                try{
                    Method real = cls.getDeclaredMethod(method.getName(), method.getParameterTypes());
                    if(real.isAnnotationPresent(EventListener.class)){
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if(parameterTypes.length != 1){
                            throw new IllegalArgumentException("Method " + method + " has @EventListener annotation, but provides " + parameterTypes.length + " arguments.  Event handler methods must require a single argument.");
                        }

                        Class<?> eventType = parameterTypes[0];

                        if(!Event.class.isAssignableFrom(eventType)){
                            throw new IllegalArgumentException("Method " + method + " has @EventListener annotation, but provides an argument that is not an Event (" + eventType + ")");
                        }

                        register(eventType, target, method);
                        break;
                    }
                }catch(NoSuchMethodException e){

                }
            }
        }
    }

    private void register(Class<?> eventType, Object target, Method method){
        if(Modifier.isAbstract(eventType.getModifiers())){
            NexusLog.getLogger().warning("Tried to register an event listener for an abstract event!");
            NexusLog.getLogger().warning("This is bad! The event listener will not be registered.");
            NexusLog.getLogger().warning(String.format("Remove the abstract modifier from the event %s to fix this", eventType.getSimpleName()));
            return;
        }
        try{
            Constructor<?> ctr = eventType.getConstructor();
            ctr.setAccessible(true);
            Event event = (Event) ctr.newInstance();
            ASMEventHandler listener = new ASMEventHandler(target, method);
            event.getListenerList().register(busID, listener.getPriority(), listener);

            ArrayList<IEventListener> others = listeners.get(target);
            if(others == null){
                others = new ArrayList<IEventListener>();
                listeners.put(target, others);
            }
            others.add(listener);
        }catch(Exception e){
            NexusLog.getLogger().log(Level.SEVERE, "Error while registering an event listener", e);
        }
    }

    public void register(Class<?> eventType, EventPriority priority, IEventListener listener){
        if(Modifier.isAbstract(eventType.getModifiers())){
            NexusLog.getLogger().warning("Tried to register an event listener for an abstract event!");
            NexusLog.getLogger().warning("This is bad! The event listener will not be registered.");
            NexusLog.getLogger().warning(String.format("Remove the abstract modifier from the event %s to fix this", eventType.getSimpleName()));
            return;
        }
        try{
            Constructor<?> ctr = eventType.getConstructor();
            ctr.setAccessible(true);
            Event event = (Event) ctr.newInstance();
            event.getListenerList().register(busID, priority, listener);

            ArrayList<IEventListener> others = listeners.get(listener);
            if(others == null){
                others = new ArrayList<IEventListener>();
                listeners.put(listener, others);
            }
            others.add(listener);
        }catch(Exception e){
            NexusLog.getLogger().log(Level.SEVERE, "Error while registering an event listener", e);
        }
    }

    public void unregister(Object object){
        ArrayList<IEventListener> list = listeners.remove(object);
        for(IEventListener listener : list){
            ListenerList.unregisterAll(busID, listener);
        }
    }

    /**
     * Posts the event to the eventbus
     *
     * @param event - The event to post
     * @return true when canceled. Otherwise false.
     */
    public boolean post(Event event){
        IEventListener[] listeners = event.getListenerList().getListeners(busID);
        for(IEventListener listener : listeners){
            listener.invoke(event);
        }
        return event.isCancelable() && event.isCanceled();
    }

    /**
     * Posts the event to an specific listener in the eventbus
     *
     * @param event - The event to post
     * @return true when canceled. Otherwise false.
     */
    public boolean post(Event event, Object object){
        IEventListener[] listeners = event.getListenerList().getListeners(busID);
        for(IEventListener listener : listeners){
            if(listener == object){
                listener.invoke(event);
            }else if(listener instanceof IGeneratedEventListener){
                IGeneratedEventListener handler = (IGeneratedEventListener) listener;
                if(handler.getTarget() instanceof IGeneratedEventListener){
                    IGeneratedEventListener hand = (IGeneratedEventListener) handler.getTarget();
                    if(hand.getTarget() == object){
                        hand.invoke(event);
                    }
                }
            }
        }
        return event.isCancelable() && event.isCanceled();
    }
}
package com.github.cuter44.nyafx.event;

import java.util.Map;
import java.util.HashMap;

public class EventHub
{
    protected HashMap<String, ListenerList> listenerMap;

  // CONSTRUCT
    public EventHub()
    {
        this.listenerMap = new HashMap<String, ListenerList>();

        return;
    }

    private static final class Singleton
    {
        public static EventHub instance = new EventHub();
    }

    public static EventHub getInstance()
    {
        return(Singleton.instance);
    }

  // LISTENER
    public void addListener(String eventType, EventSink l)
    {
        synchronized(this.listenerMap)
        {
            ListenerList list = this.listenerMap.get(eventType);
            if (list == null)
            {
                list = new ListenerList();
                this.listenerMap.put(eventType, list);
            }

            list.add(l);
        }
    }


  // DISPATCH
    public boolean dispatch(String eventType, Event<Object> ev)
    {
        ListenerList<Object> list = this.listenerMap.get(eventType);
        if (list == null)
            return(false);

        // else
        return(
            list.dispatch(ev)
        );
    }
}

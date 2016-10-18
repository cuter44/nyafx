package com.github.cuter44.nyafx.event;

import java.util.Vector;

public class ListenerList<T> extends Vector<EventSink<T>>
    implements EventSink<T>
{
    @Override
    public boolean dispatch(Event<T> ev)
    {
        for (EventSink<T> sink:this)
            try
            {
                sink.dispatch(ev);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

        return(true);
    }

    public void addListener(EventSink<T> sink)
    {
        this.add(sink);

        return;
    }
}

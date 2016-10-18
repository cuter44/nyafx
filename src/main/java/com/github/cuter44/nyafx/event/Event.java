package com.github.cuter44.nyafx.event;

/**
 * Where T means context type, usually a PO or VO.
 */
public class Event<T>
{
    /**
     * context was passed to the listeners to aware the listeners what to do.
     */
    public T context;
    /**
     * handled was passed to the listeners to aware if it is handled, i.e. avoiding handling redundantly.
     */
    public boolean handled;

    public T getContext()
    {
        return(this.context);
    }

    public void setContext(T newContext)
    {
        this.context = newContext;
        return;
    }

    public boolean isHandled()
    {
        return(this.handled);
    }

    public void setHandled(boolean newHandled)
    {
        this.handled = newHandled;
        return;
    }

    public Event()
    {
        return;
    }

    public Event(T context)
    {
        this.setContext(context);

        return;
    }
}

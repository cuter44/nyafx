package com.github.cuter44.nyafx.event;

public interface EventSink<T>
{
    /**
     * Event slot to be invoked.
     * Where T is refered to the context type. A EventSink<T> instance indicate it can haanld Event<T>.
     * @return Pipe-like component should return true to indicate event is passed-by. End-handler should return true to indicate event is handled.
     */
    public abstract boolean dispatch(Event<T> ev);
}

package com.github.cuter44.nyafx.event;

import java.util.concurrent.LinkedBlockingQueue;

/** EventQueue which is suitable for nyafx
 * It extends LinkedBlockingQueue, which means you can also treat it like a LBQ.
 */
public class EventQueue<T> extends LinkedBlockingQueue<Event<T>>
    implements EventSink<T>
{
    /** ENQUEUE event, just invoke LBQ.offer().
     * @return true if enqueue successfully
     */
    @Override
    public boolean dispatch(Event<T> ev)
    {
        return(
            super.offer(ev)
        );
    }

    /** DEQUEUE event, just invoke LBQ.take().
     */
    @Override
    public Event<T> take()
        throws InterruptedException
    {
        return(
            super.take()
        );
    }
}

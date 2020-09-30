package com.wave.notify;

import com.wave.notify.event.Event;

/**
 * publisher
 */
public interface EventPublisher {
    
    void addSubscriber(Subscriber sub);
    
    void publish(Event event) throws Exception;
}

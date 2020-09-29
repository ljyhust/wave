package com.wave.notify;

import com.wave.notify.event.Event;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class DefaultPublisher implements EventPublisher{
    
    private Set<Subscriber> subscriberSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
    
    @Override
    public void addSubscriber(Subscriber sub) {
        this.subscriberSet.add(sub);
    }
    
    @Override
    public void publish(Event event) {
        for (Subscriber subscriber : subscriberSet) {
            subscriber.onEvent(event);
        }
    }
}

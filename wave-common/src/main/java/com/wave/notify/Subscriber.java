package com.wave.notify;

import com.wave.notify.event.Event;

public interface Subscriber<T extends Event> {

    void onEvent(T event) throws Exception;
    
    Class<? extends Event> subscribeType();
    
}

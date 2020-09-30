package com.wave.blog.notify;

import com.wave.notify.Subscriber;
import com.wave.notify.event.Event;

public class BlogChangeSubscriber implements Subscriber {
    
    @Override
    public void onEvent(Event event) throws Exception{
    
    }
    
    @Override
    public Class<? extends Event> subscribeType() {
        return null;
    }
}

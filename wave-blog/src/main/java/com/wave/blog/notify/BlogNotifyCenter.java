package com.wave.blog.notify;

import com.wave.notify.DefaultPublisher;
import com.wave.notify.EventPublisher;
import com.wave.notify.Subscriber;
import com.wave.notify.event.Event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 帖子通知中心
 */
public class BlogNotifyCenter {
    
    /**
     * topic and publisher
     */
    private static final Map<String, EventPublisher> publisherMap = new ConcurrentHashMap<>();
    
    private BlogNotifyCenter(){}
    
    public static void registerSubscriber(Subscriber consumer) {
        Class clazz = consumer.subscribeType();
        addSubscriber(consumer, clazz);
    }
    
    public static boolean publishEvent(Event e) {
        Class<? extends Event> eventClass = e.getClass();
        String topic = eventClass.getCanonicalName();
        if (!publisherMap.containsKey(topic)) {
            return false;
        }
        publisherMap.get(topic).publish(e);
        return true;
    }
    
    private static void addSubscriber(Subscriber consumer, Class<? extends Event> subscribeType) {
        String topic = subscribeType.getCanonicalName();
        publisherMap.computeIfAbsent(topic, key ->{
            return new DefaultPublisher();
        });
        publisherMap.get(topic).addSubscriber(consumer);
    }
}

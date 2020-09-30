package com.wave.consistency.id;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IdGeneratorManager {

    private static Map<String, ServiceIdGenerator>  manager = new ConcurrentHashMap<>();
    
    public static void registerGenerator(String resource, ServiceIdGenerator idGenerator) {
        ServiceIdGenerator serviceIdGenerator = manager.get(resource);
        if (null != serviceIdGenerator) {
            throw new IllegalArgumentException(resource + " has id generator");
        }
        manager.put(resource, idGenerator);
    }
    
    public static ServiceIdGenerator getGenerator(String resource) {
        ServiceIdGenerator serviceIdGenerator = manager.get(resource);
        if (null == serviceIdGenerator) {
            throw new IllegalArgumentException("no id generator");
        }
        return serviceIdGenerator;
    }
}

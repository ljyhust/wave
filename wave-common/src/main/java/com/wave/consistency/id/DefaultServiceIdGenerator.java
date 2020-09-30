package com.wave.consistency.id;

public class DefaultServiceIdGenerator implements ServiceIdGenerator{
    
    private final IdGenerator idGenerator;
    
    private long maxCustomServiceId = 1L;
    
    public DefaultServiceIdGenerator(long customServiceIdBits) {
        idGenerator = new SnowFlowerIdGenerator(customServiceIdBits);
        this.maxCustomServiceId = (long) Math.pow(2, customServiceIdBits);
    }
    
    @Override
    public long nextId(long customServiceId) {
        if (customServiceId < 0L) {
            throw new IllegalArgumentException(customServiceId + " error: custom service id must be positive");
        }
        long id = customServiceId % (this.maxCustomServiceId);
        long nextId = idGenerator.nextId();
        return nextId | id;
    }
}

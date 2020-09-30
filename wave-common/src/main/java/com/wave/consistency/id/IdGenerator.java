package com.wave.consistency.id;

import java.util.Map;

public interface IdGenerator {

    long nextId();
    
    long currentId();
    
}

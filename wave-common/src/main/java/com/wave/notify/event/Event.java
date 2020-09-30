package com.wave.notify.event;

import java.io.Serializable;

public abstract class Event implements Serializable {
    
    private static final long serialVersionUID = 5679284270779834472L;
    
    private String message;
}

package com.wave.blog.notify.event;

import com.wave.blog.dto.BlogDto;
import com.wave.notify.event.Event;
import lombok.Data;

/**
 * create blog event.
 */
@Data
public class BlogEditEvent extends Event {
    
    private static final long serialVersionUID = 6747827714090210461L;
    
    private BlogDto blogDto;
}

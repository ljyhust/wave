package com.wave.blog.config;

public class WaveBlogConstants {
    
    public final static String WAVE_BLOG_NAME = "t_blog";
    
    public final static String WAVE_BLOG_MSG_TYPE_NEW = "BLOG_MSG_NEW";
    
    public final static String WAVE_BLOG_MSG_TYPE_DELELTE = "BLOG_MSG_DELETE";
    
    public final static String MQ_TAG_WAVE_BLOG_MSG_NEW = WAVE_BLOG_MSG_TYPE_NEW;
    
    public final static String MQ_TAG_WAVE_BLOG_MSG_DELETE = WAVE_BLOG_MSG_TYPE_DELELTE;
    
    public final static String WAVE_BLOG_MQ_TOPIC = "topic_blog";
    
    public final static String WAVE_BLOG_MQ_CONFIG_PRODUCER_GROUP = "blog_transaction_group";
    
    public final static String WAVE_BLOG_MQ_CONFIG_CONSUMER_GROUP = "blog_transaction_consumer_group";
    
}

package com.wave.demo.algorithm;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * LeetCode 232  Implement Queue using Stacks
 * 通过Stacks实现FIFO队列
 */
public class FIFObyStack {
    
    class MyQueue {
    
        /**
         * 两个队列，in 和 out
         */
        private Stack<Integer> inStack = new Stack<>();
        private Stack<Integer> outStack = new Stack<>();
        
        /** Initialize your data structure here. */
        public MyQueue() {
        
        }
        
        /** Push element x to the back of queue. */
        public void push(int x) {
            inStack.push(x);
        }
        
        /** Removes the element from in front of queue and returns that element. */
        public int pop() {
            if (outStack.isEmpty()) {
                while(!inStack.isEmpty()) {
                    outStack.push(inStack.pop());
                }
            }
            return outStack.pop();
        }
        
        /** Get the front element. */
        public int peek() {
            if (outStack.isEmpty()) {
                while(!inStack.isEmpty()) {
                    outStack.push(inStack.pop());
                }
            }
            return outStack.peek();
        }
        
        /** Returns whether the queue is empty. */
        public boolean empty() {
            return inStack.isEmpty() && outStack.isEmpty();
        }
    }
    
}

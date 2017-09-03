package com.oop.calculator;

/**
 * Author: Evin Darling (C00144257)
 * Date: March 2017
 * Description: A stack implementation for storing Strings.
 */
public class Stack {
    // Global variables
    private int top;
    private String stack[];

    /**
     * construct Stack object, initialize stack with empty array of size capacity
     * initialize top to -1
     * @param capacity size of array/stack
     */
    public Stack(int capacity)
    {
        stack = new String[capacity];
        top = -1;
    }

    // methods
    /**
     * insert a value to the top of the stack, if stack is not full
     * @param val the value to be pushed
     */
    public void push(String val)
    {
        // check if stack is full and throw exception
        if (top == stack.length-1) { throw new StackException("Stack is full"); }
        else {
            top++;                  // increment top of stack
            stack[top] = val;
        }
    }

    /**
     * return value at top of stack, then remove it. if stack is not empty
     * @return current value at top of stack that will be removed
     */
    public String pop()
    {
        // check if stack is empty, throw exception if it is
        if (top == -1) { throw new StackException("Stack is empty"); }
        else {
            top--;                  // decrement top
            return stack[top + 1];  // return relevant top value
        }
    }

    /**
     * return value at top of stack, if stack is not empty
     * @return value at top of stack
     */
    public String peek()
    {
        // check if stack is empty, throw exception if it is
        if (top == -1) { throw new StackException("Stack is empty"); }

        return stack[top];      // return top value
    }

    /**
     * check whether or not the stack is empty
     * @return true of the stack is empty, false if not
     */
    public boolean isEmpty()
    {
        return (top == -1);     // return true if stack is empty (equal -1)
    }

    /**
     * get the length of the array and return it
     * @return array length
     */

    public int getTop()
    {
        return top;
    }

    void clearStack()
    {
        top = -1;
    }

    /**
     * class to handle RuntimeExceptions for the stack ie.
     * whether stack is full or empty
     */
    public class StackException extends  RuntimeException
    {
        // constructor
        public StackException(String message)
        {
            super(message);
        }
    }
}

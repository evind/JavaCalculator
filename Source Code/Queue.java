package com.oop.calculator;

/**
 * Author: Evin Darling (C00144257)
 * Date: March 2017
 * Description: A circular queue implementation to store Strings.
 *              Will be used as a normal queue in this context.
 */
public class Queue {
    // Global variables
    private String[] queue;
    private int front;
    private int rear;
    private int size;
    private int qty;

    // Constructor
    /**
     * Constuct the circular queue of size capacity, initialise head and tail -1
     * @param capacity size of array
     */
    public Queue(int capacity)
    {
        queue = new String[capacity];
        size = capacity;
        front = -1;
        rear = -1;
    }

    // Methods
    /**
     * Check if the queue is empty. If front & read = -1 the queue is empty
     * @return true if queue is empty, false otherwise
     */
    public boolean isEmpty()
    {
        return (front == -1 && rear == -1);
    }

    /**
     * Insert a value into the rear of the queue. If the queue is full throw exception.
     * If the queue is empty increment front and rear to 0.
     * @param val value to be enqueued
     */
    public void enqueue(String val)
    {
        if ((rear + 1) % size == front)     // if the queue is full throw exception
        {
            throw new IllegalStateException("Queue is full. Cannot perform enqueue.");
        }
        else if (isEmpty())                 // if the queue is empty inc front and rear to 0
        {                                   // then insert value x to the rear of the queue
            front++;
            rear++;
            queue[rear] = val;
            qty++;
        }
        else                                // else increment the rear appropriately
        {                                   // then set rear of queue to value x
            rear = (rear + 1) % size;
            queue[rear] = val;
            qty++;
        }
    }

    /**
     * Pop the value at the front of the queue and increment the front
     * @return the value at the front of the queue
     */
    public String dequeue()
    {
        String value;
        if (isEmpty())              // if queue is empty throw exception
        {
            throw new IllegalStateException("Queue is empty. Cannot perform dequeue.");
        }
        else if (front == rear)     // if front and rear are equal, reset both to -1
        {
            value = queue[front];
            queue[front] = null;
            front = -1;
            rear = -1;
            qty--;
        }
        else                        // else set value to what is at front of queue
        {                           // increment the front appropriately
            value = queue[front];
            queue[front] = null;
            front = (front + 1) % size;
            qty--;
        }
        return value;
    }

    /**
     * set all elements of the queue to null and reset rear and front
     */
    public void clearQueue()
    {
        for (int i = 0; i < queue.length; i++) {
            queue[i] = null;
        }
        front = -1;
        rear = -1;
        qty = 0;
    }

    /**
     * Get the value currently at the rear of the queue
     * @return - value at rear of queue
     */
    public String getRearValue() {
        if (isEmpty()) {
            return null;
        } else {
            return queue[rear];
        }
    }

    /**
     * Dump contents linearly. Warning: This method will not
     * function as expected when this queue is used as a
     * circular queue. Implemented to be used with Calculator.java
     * in which there are no dequeue operations until all
     * elements are to be dequeue'd in series.
     * @return - a formatted string containing enqueue'd elements
     *           or an empty string if queue is empty.
     */
    public String dumpElements()
    {
        String elements = "";

        if (qty != 0) {
            for (int i = 0; i < qty; i++) {
                elements += queue[i];
            }
            return elements;
        } else {
            return "";
        }
    }
}

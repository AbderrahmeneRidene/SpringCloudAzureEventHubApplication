package com.exemple.demo.model;

public class EventMessage {
    private String partition;
    private long sequence;
    private String body;
    private String enqueuedTime;

    public EventMessage(String partition, long sequence, String body, String enqueuedTime) {
        this.partition = partition;
        this.sequence = sequence;
        this.body = body;
        this.enqueuedTime = enqueuedTime;
    }

    // Getters
    public String getPartition() { return partition; }
    public long getSequence() { return sequence; }
    public String getBody() { return body; }
    public String getEnqueuedTime() { return enqueuedTime; }
}
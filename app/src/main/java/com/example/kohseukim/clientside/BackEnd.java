package com.example.kohseukim.clientside;

public interface BackEnd {
    /*
    Start the driving session
     */
    public void start(String id);

    /*
    acknowledge the current alert
     */
    public void acknowledgeAlert();

    /*
    Stop the driving session
     */
    public void stop();
}
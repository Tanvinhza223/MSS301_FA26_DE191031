package com.mss301.watch.saga;
public record Compensation(String stepName, Runnable action) {}


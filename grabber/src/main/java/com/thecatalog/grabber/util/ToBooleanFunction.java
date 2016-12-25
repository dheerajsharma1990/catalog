package com.thecatalog.grabber.util;

@FunctionalInterface
public interface ToBooleanFunction<T> {
    boolean apply(T value);
}

package com.thecatalog.grabber;

@FunctionalInterface
public interface DataParser<T> {

    T parse(byte[] bytes);
}

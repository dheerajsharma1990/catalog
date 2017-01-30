package com.findmymovie.query.tree;

public class HaltState implements State {

    public static final HaltState HALT_STATE = new HaltState();

    @Override
    public State accept(String string, int index) {
        char character = string.charAt(index);
        if (character == ' ') {
            return this;
        }
        throw new RuntimeException("Unexpected character [" + character + "].");
    }
}

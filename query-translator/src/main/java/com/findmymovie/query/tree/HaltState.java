package com.findmymovie.query.tree;

public class HaltState implements State {

    public static final HaltState HALT_STATE = new HaltState();

    @Override
    public State accept(String string, int index) {
        if(index == string.length() || string.charAt(index) == ' ') {
            return this;
        }
        throw new RuntimeException("Unexpected character [" + string.charAt(index) + "].");
    }
}

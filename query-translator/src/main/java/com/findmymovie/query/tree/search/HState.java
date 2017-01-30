package com.findmymovie.query.tree.search;

import com.findmymovie.query.tree.State;

import static com.findmymovie.query.tree.HaltState.HALT_STATE;

public class HState implements State {

    public static final HState H_STATE = new HState();

    @Override
    public State accept(String string, int index) {
        char character = string.charAt(index);
        if (character == ' ') {
            return HALT_STATE;
        }
        throw new RuntimeException("Character [" + character + "] not expected.");
    }
}

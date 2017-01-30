package com.findmymovie.query.tree.search;

import com.findmymovie.query.tree.State;

import static com.findmymovie.query.tree.search.HState.H_STATE;

public class CState implements State {

    public static final CState C_STATE = new CState();

    @Override
    public State accept(String string, int index) {
        char character = string.charAt(index);
        if (character == 'h' || character == 'H') {
            return H_STATE;
        }
        throw new RuntimeException("Character [" + character + "] not expected.");
    }
}

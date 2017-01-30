package com.findmymovie.query.tree.search;

import com.findmymovie.query.tree.State;

import static com.findmymovie.query.tree.search.AState.A_STATE;

public class EState implements State {

    public static final EState E_STATE = new EState();

    @Override
    public State accept(String string, int index) {
        char character = string.charAt(index);
        if (character == 'a' || character == 'A') {
            return A_STATE;
        }
        throw new RuntimeException("Character [" + character + "] not expected.");
    }
}

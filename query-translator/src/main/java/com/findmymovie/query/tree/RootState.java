package com.findmymovie.query.tree;

import static com.findmymovie.query.tree.search.SearchState.SEARCH_STATE;

public class RootState implements State {

    public State accept(String string, int index) {
        char character = string.charAt(index);
        if (character == ' ') {
            return this.accept(string, index + 1);
        }
        return SEARCH_STATE.accept(string, index);
    }
}

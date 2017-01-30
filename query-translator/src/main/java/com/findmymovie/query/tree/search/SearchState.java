package com.findmymovie.query.tree.search;

import com.findmymovie.query.tree.State;

import static com.findmymovie.query.tree.HaltState.HALT_STATE;

public class SearchState implements State {

    private static final String SEARCH = "search";
    public static final SearchState SEARCH_STATE = new SearchState();

    @Override
    public State accept(String string, int index) {
        if (string.length() >= index + SEARCH.length()) {
            String substring = string.substring(index, index + SEARCH.length());
            if (substring.equalsIgnoreCase(SEARCH)) {
                return HALT_STATE.accept(string, index + SEARCH.length());
            }
        }
        throw new RuntimeException("Expected [" + SEARCH + "].");
    }
}

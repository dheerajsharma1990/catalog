package com.findmymovie.query.tree;

public class Validator {

    private final RootState rootState = new RootState();

    public boolean validate(String string) {
        State state = rootState;
        for (int i = 0; i < string.length(); i++) {
            state = state.accept(string, i);
        }
        return true;
    }


}

package com.findmymovie.query.tree.states;

import com.findmymovie.query.tree.State;

import static com.findmymovie.query.tree.HaltState.HALT_STATE;

public class ForState implements State {

    private static final String FOR = "for";
    public static final ForState FOR_STATE = new ForState();

    @Override
    public State accept(String string, int index) {
        if (string.length() >= index + FOR.length()) {
            if (string.charAt(index) == ' ') {
                return this.accept(string, index + 1);
            }
            String substring = string.substring(index, index + FOR.length());
            if (substring.equalsIgnoreCase(FOR)) {
                return HALT_STATE.accept(string, index + FOR.length());
            }
        }
        throw new RuntimeException("Expected [" + FOR + "].");
    }
}

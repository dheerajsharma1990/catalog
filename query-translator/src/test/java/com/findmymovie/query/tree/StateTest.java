package com.findmymovie.query.tree;

import com.findmymovie.query.tree.states.SearchState;
import org.testng.annotations.Test;

import static org.testng.Assert.fail;

public class StateTest {

    private final SearchState rootState = new SearchState();

    @Test
    public void shouldPassForValidSyntax() {
        rootState.accept(" search  for  ", 0);
        rootState.accept("search for", 0);
        rootState.accept("search FoR ", 0);
        rootState.accept(" search FOR", 0);
        rootState.accept("search   for ", 0);
        rootState.accept(" seArch  FOR ", 0);
        rootState.accept(" seArcH  for ", 0);
        rootState.accept(" seArCH FOR  ", 0);
        rootState.accept("   SeArCH FoR  ", 0);
        rootState.accept("SEARCH  FOR ", 0);
        rootState.accept("SEARCH FOR", 0);
        rootState.accept("SEARCH FOR ", 0);
    }

    @Test
    public void shouldFailForInvalidSyntax() {
        try {
            rootState.accept(" sech ", 0);
            fail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

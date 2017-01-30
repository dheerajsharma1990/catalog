package com.findmymovie.query.tree;

import org.testng.annotations.Test;

import static org.testng.Assert.fail;

public class SearchTokenTest {

    private final RootState rootState = new RootState();

    @Test
    public void shouldAcceptVariousWaysToEnterSearch() {
        rootState.accept(" search ", 0);
        rootState.accept("search ", 0);
        rootState.accept("search ", 0);
        rootState.accept(" search ", 0);
        rootState.accept("search ", 0);
        rootState.accept(" seArch ", 0);
        rootState.accept(" seArcH   ", 0);
        rootState.accept(" seArCH   ", 0);
        rootState.accept("   SeArCH   ", 0);
        rootState.accept("SEARCH   ", 0);
        rootState.accept("SEARCH ", 0);
        rootState.accept("SEARCH", 0);
    }

    @Test
    public void shouldFailOnWrongInputs() {
        try {
            rootState.accept(" sech ", 0);
            fail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

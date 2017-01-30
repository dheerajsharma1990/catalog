package com.findmymovie.query.tree;

import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SearchTokenTest {

    private final Validator validator = new Validator();

    @Test
    public void shouldAcceptVariousWaysToEnterSearch() {
        assertThat(validator.validate(" search "), is(true));
        assertThat(validator.validate("search "), is(true));
        assertThat(validator.validate(" seArch "), is(true));
        assertThat(validator.validate(" seArcH   "), is(true));
        assertThat(validator.validate(" seArCH   "), is(true));
        assertThat(validator.validate("   SeArCH   "), is(true));
        assertThat(validator.validate("SEARCH   "), is(true));
        assertThat(validator.validate("SEARCH "), is(true));
    }
}

package com.findmymovie.query.tree;

import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.fail;

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
        assertThat(validator.validate("SEARCH"), is(true));
    }

    @Test
    public void shouldFailOnWrongInputs() {
        try {
            assertThat(validator.validate(" sarch "), is(false));
            fail();
        } catch (Exception e) {

        }
    }
}

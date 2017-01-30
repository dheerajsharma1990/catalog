package com.findmymovie.query.analyzer;

import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SyntaxValidatorTest {

    private final SyntaxValidator syntaxValidator = new SyntaxValidator(SyntaxTree.getTree());

    @Test
    public void shouldValidateVariousQueries() {
        assertThat(syntaxValidator.validate("  search for   movies whose 'title' is \"Casino Royale\"   "), is(true));
        assertThat(syntaxValidator.validate("  search for movies whose \"title\" is \"Casino Royale\""), is(true));
        assertThat(syntaxValidator.validate("  search for   movies whose \"title\" is \"Casino Royale\""), is(true));
        assertThat(syntaxValidator.validate("  search for   movies whose \"title\" is \"Casino Royale\"   "), is(true));
        assertThat(syntaxValidator.validate("search for movies whose \"title\" is \"Casino Royale\""), is(true));
        assertThat(syntaxValidator.validate("search for movies whose \"title\" is and \"budget\" is \"4000000\""), is(false));
        assertThat(syntaxValidator.validate("search movies whose \"title\" is \"Something\""), is(false));
        assertThat(syntaxValidator.validate("movies whose \"title\" is \"Something\""), is(false));
        assertThat(syntaxValidator.validate("whose \"title\" is \"Something\""), is(false));
        assertThat(syntaxValidator.validate("search for movies whose \"title\" is \"My Name\" or \"title\" is \"nothing\""), is(true));
    }

}

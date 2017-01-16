package com.findmymovie.query.translator;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TranslatorTest {

    abstract class Operator {
        abstract public String apply();
    }

    class IsOperator extends Operator {

        private final String leftHandSide;
        private final String rightHandSide;

        public IsOperator(String leftHandSide, String rightHandSide) {
            this.leftHandSide = leftHandSide;
            this.rightHandSide = rightHandSide;
        }

        @Override
        public String apply() {
            return "filter(movie -> movie.get" + leftHandSide + "().equals(" + rightHandSide + "))";
        }
    }

    private final String TRANSLATED_QUERY = "movies.stream().filter(movie -> movie.getName().equals(\"Casino Royale\")).collect(Collectors.asList())";

    private List<String> getTokens(String query) {
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(query);
        List<String> tokens = new ArrayList<>();
        while (m.find()) {
            tokens.add(m.group(1));
        }
        return tokens;
    }

    private String getTranslatedQuery(List<Operator> operators) {
        StringBuilder builder = new StringBuilder();
        builder.append("movies")
                .append(".stream()");
        for (Operator operator : operators) {
            builder.append(".").append(operator.apply());
        }

        return builder.append(".collect(Collectors.asList())").toString();
    }

    @Test
    public void shouldBreakIntoTokens() {
        //given
        String query = "search movies whose name is \"Casino Royale\"";

        //when
        List<String> tokens = getTokens(query);

        //then
        assertThat(tokens.size(), is(6));
        assertThat(tokens.get(0), is("search"));
        assertThat(tokens.get(1), is("movies"));
        assertThat(tokens.get(2), is("whose"));
        assertThat(tokens.get(3), is("name"));
        assertThat(tokens.get(4), is("is"));
        assertThat(tokens.get(5), is("\"Casino Royale\""));

        String translatedQuery = getTranslatedQuery(Arrays.asList(new IsOperator("Name", "\"Casino Royale\"")));
        assertThat(translatedQuery, is(TRANSLATED_QUERY));
    }
}

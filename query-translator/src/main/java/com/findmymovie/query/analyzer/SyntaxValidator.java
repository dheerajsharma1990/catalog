package com.findmymovie.query.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxValidator {

    private final Tree root;

    public SyntaxValidator(Tree root) {
        this.root = root;
    }

    public boolean validate(String query) {
        Matcher matcher = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(query);
        List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            tokens.add(matcher.group(1));
        }
        return root.validate(tokens, 0);
    }


}

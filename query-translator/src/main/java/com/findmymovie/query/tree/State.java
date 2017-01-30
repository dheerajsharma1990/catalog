package com.findmymovie.query.tree;

public interface State {

    State accept(String string, int index);

}

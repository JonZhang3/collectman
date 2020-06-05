package com.collectman.connection;

import com.tuples.Tuple;

public interface Connection {

    void connect();

    void close();

    Object execute(final Tuple values);

    default Object update(final Tuple values) {
        return execute(values);
    }

}

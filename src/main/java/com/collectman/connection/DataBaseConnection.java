package com.collectman.connection;

import com.collectman.config.DataBaseProperties;
import com.queryflow.accessor.Accessor;
import com.queryflow.accessor.AccessorFactory;
import com.queryflow.accessor.AccessorFactoryBuilder;
import com.queryflow.common.ResultMap;
import com.tuples.Tuple;

import java.util.List;

public class DataBaseConnection implements Connection {

    private final AccessorFactoryBuilder builder;
    private Accessor accessor;
    private final String sql;

    public DataBaseConnection(DataBaseProperties properties) {
        this.sql = properties.getSql();
        builder = new AccessorFactoryBuilder();
        builder.addDatabase(properties);
    }

    @Override
    public void connect() {
        AccessorFactory accessorFactory = builder.build(false);
        accessor = accessorFactory.getAccessor();
    }

    @Override
    public void close() {
        if(accessor != null) {
            accessor.close();
        }
    }

    @Override
    public Object execute(final Tuple values) {
        if(accessor != null) {
            List<ResultMap> result = accessor.query(sql).listMap();
            accessor.close();
            return result;
        }
        return null;
    }

}

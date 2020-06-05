package com.collectman.config;

import com.queryflow.config.DatabaseConfig;

public class DataBaseProperties extends DatabaseConfig {

    private String sql;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return "DataBaseProperties{" +
            "sql='" + sql + '\'' +
            '}';
    }
}

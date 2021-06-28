package com.december.friends.database;

import java.sql.SQLException;

@FunctionalInterface
public interface ExceptionCallback {

    void accept(SQLException ex);
}

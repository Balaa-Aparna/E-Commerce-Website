package com.eazydeals.helper;

import java.sql.Connection;

public interface IConnectionProvider {
    Connection getConnection();
}
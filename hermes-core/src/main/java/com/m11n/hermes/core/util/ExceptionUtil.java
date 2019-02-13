package com.m11n.hermes.core.util;

import org.xml.sax.SAXException;

import java.sql.SQLException;

public class ExceptionUtil {
    /**
     * Looks up and returns the root cause of an exception. If none is found, returns
     * supplied Throwable object unchanged. If root is found, recursively "unwraps" it,
     * and returns the result to the user.
     */
    public static Throwable unwindException(Throwable th) {
        if (th instanceof SAXException) {
            SAXException sax = (SAXException) th;
            if (sax.getException() != null) {
                return unwindException(sax.getException());
            }
        }
        else if (th instanceof SQLException) {
            SQLException sql = (SQLException) th;
            if (sql.getNextException() != null) {
                return unwindException(sql.getNextException());
            }
        }
        else if (th.getCause() != null) {
            return unwindException(th.getCause());
        }

        return th;
    }
}

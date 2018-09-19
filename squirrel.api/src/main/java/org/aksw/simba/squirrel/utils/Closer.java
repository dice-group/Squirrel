package org.aksw.simba.squirrel.utils;

import java.io.Closeable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple class offering methods to close other classes either quitely or with
 * logging errors.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class Closer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Closer.class);

    /**
     * Closes the given {@link Closeable} while ignoring all exception that may
     * occur.
     * 
     * @param closeable
     *            the object which should be closed.
     */
    public static void closeQuietly(Closeable closeable) {
        close(closeable, null, false);
    }

    /**
     * Closes the given {@link Closeable} and logs occuring exceptions with the
     * {@link Logger} of this utility class.
     * 
     * <b>Note</b> that it is recommended to use {@link #close(Closeable, Logger)}
     * instead.
     * 
     * @param closeable
     *            the object which should be closed.
     */
    public static void close(Closeable closeable) {
        close(closeable, LOGGER, false);
    }

    /**
     * Closes the given {@link Closeable} and logs occuring exceptions with the
     * given {@link Logger}.
     * 
     * <b>Note</b> that exceptions are logged without their stack trace (i.e., like
     * you would call {@link #close(Closeable, Logger, boolean)} with {@code false}
     * for the stack trace flag.
     * 
     * @param closeable
     *            the object which should be closed.
     * @param logger
     *            the logger used for logging occurring exceptions
     */
    public static void close(Closeable closeable, Logger logger) {
        close(closeable, logger, false);
    }

    /**
     * Closes the given {@link Closeable} and logs occuring exceptions with the
     * given {@link Logger}. Depending on the given flag, exceptions are logged with
     * their stack trace or not.
     * 
     * @param closeable
     *            the object which should be closed.
     * @param logger
     *            the logger used for logging occurring exceptions
     * @param withStackTrace
     *            if this flag is {@code true} the exception will be logged together
     *            with its stack trace. Otherwise, only the exception message will
     *            be logged.
     */
    public static void close(Closeable closeable, Logger logger, boolean withStackTrace) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                if (logger != null) {
                    if (withStackTrace) {
                        logger.error("Exception while closing object.", e);
                    } else {
                        logger.error(e.getMessage());
                    }
                }
            }
        }
    }
}

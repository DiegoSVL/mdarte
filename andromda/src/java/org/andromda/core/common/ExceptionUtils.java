package org.andromda.core.common;

import org.apache.commons.lang.StringUtils;


/**
 * Contains Exception handling utilities.
 *
 * @author Chad Brandon
 */
public class ExceptionUtils
{
    /**
     * Checks if the argument is null, and if so, throws an IllegalArgumentException, does nothing if not.
     *
     * @param methodExecuteName the name of the method we are currently executing
     * @param argumentName the name of the argument we are checking for null
     * @param argument the argument we are checking
     */
    public static void checkNull(
        final String methodExecuteName,
        final String argumentName,
        final Object argument)
    {
        final String methodName = "ExceptionUtils.checkNull";
        if (StringUtils.isEmpty(methodExecuteName))
        {
            throw new IllegalArgumentException(methodName + " - methodExecuteName can not be null or an empty String");
        }
        if (StringUtils.isEmpty(argumentName))
        {
            throw new IllegalArgumentException(
                "methodName: '" + methodName + "' - argumentName can not be null or an empty String");
        }

        // this is what the method is actually for
        if (argument == null)
        {
            throw new IllegalArgumentException(
                "methodName: " + methodExecuteName + " - '" + argumentName + "' can not be null");
        }
    }

    /**
     * Checks if the argument is null or an empty String throws an IllegalArgumentException if it is, does nothing if
     * not.
     *
     * @param methodExecuteName the name of the method we are currently executing
     * @param argumentName the name of the argument we are checking for null
     * @param argument the argument we are checking
     */
    public static void checkEmpty(
        final String methodExecuteName,
        final String argumentName,
        final String argument)
    {
        final String methodName = "ExceptionUtils.checkEmpty";
        if (StringUtils.isEmpty(methodExecuteName))
        {
            throw new IllegalArgumentException(methodName + " - methodExecuteName can not be null or an empty String");
        }
        if (StringUtils.isEmpty(argumentName))
        {
            throw new IllegalArgumentException(methodName + " - argumentName can not be null or an empty String");
        }

        // this is what the method is actually for
        if (StringUtils.isEmpty(argument))
        {
            throw new IllegalArgumentException(
                "methodName: " + methodExecuteName + " - '" + argumentName + "' can not be null or an empty String");
        }
    }

    /**
     * Checks if the argumentClass is assignable to assignableToClass, and if not throws an IllegalArgumentException,
     * otherwise does nothing.
     *
     * @param methodExecuteName the method name of the method, this method is being executed within
     * @param assignableToClass the Class that argumentClass must be assignable to
     * @param argumentClass the argumentClass we are checking
     * @param argumentName the name of the argument we are checking
     */
    public static void checkAssignable(
        final String methodExecuteName,
        final Class assignableToClass,
        final String argumentName,
        final Class argumentClass)
    {
        final String methodName = "ExceptionUtils.checkAssignable";
        if (StringUtils.isEmpty(methodExecuteName))
        {
            throw new IllegalArgumentException(methodName + " - methodExecuteName can not be null or an empty String");
        }
        if (assignableToClass == null)
        {
            throw new IllegalArgumentException(methodName + " - assignableToClass can not be null");
        }
        if (argumentClass == null)
        {
            throw new IllegalArgumentException(methodName + " - argumentClass can not be null");
        }
        if (StringUtils.isEmpty(argumentName))
        {
            throw new IllegalArgumentException(methodName + " - argumentName can not be null or an empty String");
        }

        // this is what the method is for
        if (!assignableToClass.isAssignableFrom(argumentClass))
        {
            throw new IllegalArgumentException(
                "methodName: " + methodExecuteName + " - '" + argumentName + "' class --> '" + argumentClass +
                "' must be assignable to class --> '" + assignableToClass + "'");
        }
    }

    /**
     * Attempts to retrieve the root cause of the exception, if it can not be
     * found, the <code>throwable</code> itself is returned.
     *
     * @param throwable the exception from which to retrieve the root cause.
     * @return the root cause of the exception
     */
    public static Throwable getRootCause(Throwable throwable)
    {
        final Throwable root = org.apache.commons.lang.exception.ExceptionUtils.getRootCause(throwable);
        if (root != null)
        {
            throwable = root;
        }
        return throwable;
    }
}
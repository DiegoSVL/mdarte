package org.andromda.utils.beans.comparators;

import java.io.Serializable;

import java.util.Comparator;


/**
 * Used to sort Number values
 *
 * @author Chad Brandon
 */
class NumberComparator
    implements Comparator,
        Serializable
{
    /**
     * Used to sort Number values, both objects are assumed
     * to be Number instances.
     */
    public int compare(
        final Object objectA,
        final Object objectB)
    {
        final Number aAsNumber = (Number)objectA;
        final Number bAsNumber = (Number)objectB;
        int result = 0;
        if (aAsNumber.doubleValue() > bAsNumber.doubleValue())
        {
            result = 1;
        }
        else if (aAsNumber.doubleValue() < bAsNumber.doubleValue())
        {
            result = -1;
        }
        return result;
    }
}
package com.crowdcoin.networking.sqlcom.Generation;

/**
 * Defines a generator class. Used to generate objects and values
 * @param <T> the type of the value of generate
 * @param <S> the type of a parameter to take in. This will influence generation in some manner which is decided by the implementing class
 */
public interface Generator<T,S> {

    /**
     * Generate a value
     * @param parameter a parameter to influence the generation process
     * @return the value generated
     */
    T generateValue(S parameter);

}

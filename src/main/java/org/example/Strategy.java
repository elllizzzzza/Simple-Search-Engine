package org.example;

/**
 * Enum used for strategy type.
 */
public enum Strategy {
    /** Match if any term is found. */
    ANY,

    /** Match only if all terms are found. */
    ALL,

    /** Match if none of the terms are found. */
    NONE
}

package com.artyommameev.faststudynotes.domain;

import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;

/**
 * Encapsulates a correction of bad OCRed text.
 *
 * @author Artyom Mameev
 */
public class Correction {

    @Getter
    private final String expression;
    @Getter
    private final String correction;
    @Getter
    private final TYPE type;

    /**
     * Instantiates a new Correction object.
     *
     * @param expression an expression that should be corrected.
     * @param correction a correction of the expression.
     * @param type       a type of the correction.
     * @throws NullPointerException     if any parameter is null.
     * @throws IllegalArgumentException if the expression is empty.
     */
    public Correction(@NonNull String expression, @NonNull String correction,
                      @NonNull Correction.TYPE type) {
        if (expression.isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be empty!");
        }

        this.expression = expression;
        this.correction = correction;
        this.type = type;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return the string representation of the object in the following format:
     * <p>
     * "expression" -{@literal >} "correction" (type)
     */
    @Override
    public String toString() {
        return "\"" + Objects.requireNonNull(expression) + "\" -> \"" +
                Objects.requireNonNull(correction) + "\" (" +
                Objects.requireNonNull(type) + ")";
    }

    /**
     * Determines the type of correction.
     * <p>
     * <i>TEXT</i> - correction of a simple text.<br>
     * <i>CODE</i> - correction of a code text.
     */
    public enum TYPE {TEXT, CODE}
}
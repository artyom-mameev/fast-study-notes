package com.artyommameev.faststudynotes.domain;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("ConstantConditions")
public class CorrectionTests {


    @Test
    public void constructorThrowsNullPointerExceptionIfExpressionIsNull() {
        assertThrows(NullPointerException.class, () ->
                new Correction(null, "correction",
                        Correction.TYPE.TEXT));
    }

    @Test
    public void constructorThrowsNullPointerExceptionIfCorrectionIsNull() {
        assertThrows(NullPointerException.class, () ->
                new Correction("expression", null,
                        Correction.TYPE.TEXT));
    }

    @Test
    public void constructorThrowsNullPointerExceptionIfTypeIsNull() {
        assertThrows(NullPointerException.class, () ->
                new Correction("expression", "condition",
                        null));
    }

    @Test
    public void constructorThrowsIllegalArgumentExceptionIfExpressionIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                new Correction("", "correction",
                        Correction.TYPE.TEXT));
    }

    @Test
    public void constructorProperlyConstructs() {
        val correction = new Correction("expression",
                "correction", Correction.TYPE.TEXT);

        assertEquals(correction.getExpression(), "expression");
        assertEquals(correction.getCorrection(), "correction");
        assertEquals(correction.getType(), Correction.TYPE.TEXT);
    }

    @Test
    public void toStringWorksProperly() {
        val correction = new Correction("expression",
                "correction", Correction.TYPE.TEXT);

        assertEquals(correction.toString(), "\"expression\" -> " +
                "\"correction\" (TEXT)");
    }
}

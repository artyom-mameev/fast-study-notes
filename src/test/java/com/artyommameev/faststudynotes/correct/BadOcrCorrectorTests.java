package com.artyommameev.faststudynotes.correct;

import com.artyommameev.faststudynotes.domain.Correction;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("ConstantConditions")
public class BadOcrCorrectorTests {

    @Test
    void correctThrowsNullPointerExceptionIfParametersIsNull() {
        assertThrows(NullPointerException.class, () ->
                BadOcrCorrector.correct(null,
                        Correction.TYPE.TEXT, new ArrayList<>()));

        assertThrows(NullPointerException.class, () ->
                BadOcrCorrector.correct("null",
                        null, new ArrayList<>()));

        assertThrows(NullPointerException.class, () ->
                BadOcrCorrector.correct("null",
                        Correction.TYPE.TEXT, null));
    }

    @Test
    void correctThrowsIllegalArgumentExceptionIfStringParametersIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                BadOcrCorrector.correct("",
                        Correction.TYPE.TEXT, new ArrayList<>()));

        assertThrows(IllegalArgumentException.class, () ->
                BadOcrCorrector.addPeriodAtTheEnd(""));

        assertThrows(IllegalArgumentException.class, () ->
                BadOcrCorrector.capitalize(""));
    }

    @Test
    void correctReplacesLineBreaksIntoWhitespacesIfCorrectionTypeIsText() {
        val testString = "Line\nBreaks";
        val correctedString = BadOcrCorrector.correct(testString,
                Correction.TYPE.TEXT, new ArrayList<>());

        assertEquals("Line Breaks", correctedString);
    }

    @Test
    void correctFixesDoubleWhitespacesIfCorrectionTypeIsText() {
        val testString = "Double  Whitespaces";
        val correctedString = BadOcrCorrector.correct(testString,
                Correction.TYPE.TEXT, new ArrayList<>());

        assertEquals("Double Whitespaces", correctedString);
    }

    @Test
    void correctRemovesLineBreaksWithSoftHyphenIfCorrectionTypeIsText() {
        val testString = "Soft Hy\u00AD\nphen";
        val correctedString = BadOcrCorrector.correct(testString,
                Correction.TYPE.TEXT, new ArrayList<>());

        assertEquals("Soft Hyphen", correctedString);
    }

    @Test
    void correctRemovesHyphenatedLineBreaksIfCorrectionTypeIsText() {
        val testString = "Hyphena-\nted Line Breaks";
        val correctedString = BadOcrCorrector.correct(testString,
                Correction.TYPE.TEXT, new ArrayList<>());

        assertEquals("Hyphenated Line Breaks", correctedString);
    }

    @Test
    void correctProperlyCorrectsTextWhenCorrectionTypeIsText() {
        val correctionTestCorrection = new Correction("Correotion",
                "Correction", Correction.TYPE.TEXT);

        val objectsTestCorrection = new Correction("Objeots",
                "Objects", Correction.TYPE.TEXT);

        val corrections = new ArrayList<>(Arrays.asList(
                correctionTestCorrection, objectsTestCorrection));

        val testString = "Correo-\ntion  Ob\u00AD\njeots\n";
        val correctedString = BadOcrCorrector.correct(
                testString, Correction.TYPE.TEXT, corrections);

        assertEquals("Correction Objects ", correctedString);
    }

    @Test
    void correctProperlyCorrectsTextWhenCorrectionTypeIsCode() {
        val publicTestCorrection = new Correction(
                "puЬlic", "public", Correction.TYPE.CODE);

        val voidTestCorrection = new Correction(
                "uoid", "void", Correction.TYPE.CODE);

        val corrections = new ArrayList<>(Arrays.asList(
                publicTestCorrection, voidTestCorrection));

        val testString = "   puЬlic uoid   ";
        val correctedString = BadOcrCorrector.correct(testString,
                Correction.TYPE.CODE, corrections);

        assertEquals("   public void", correctedString);
    }

    @Test
    void correctRemovesEndWhitespacesIfCorrectionTypeIsCode() {
        val testString = "   Code   ";
        val correctedString = BadOcrCorrector.correct(testString,
                Correction.TYPE.CODE, new ArrayList<>());

        assertEquals("   Code", correctedString);
    }

    @Test
    void correctDoesMultiplyCorrections() {
        val correctionTestCorrection1 = new Correction("Cor3ction",
                "Corection", Correction.TYPE.TEXT);

        val correctionTestCorrection2 = new Correction("Corection",
                "Correction", Correction.TYPE.TEXT);

        val corrections = new ArrayList<>(Arrays.asList(
                correctionTestCorrection1, correctionTestCorrection2));

        val testString = "Cor3ction";
        val correctedString = BadOcrCorrector.correct(testString,
                Correction.TYPE.TEXT, corrections);

        assertEquals("Correction", correctedString);
    }

    @Test
    void capitalizeWorksProperly() {
        String testString = BadOcrCorrector.capitalize("t");

        assertEquals("T", testString);

        testString = BadOcrCorrector.capitalize("test");

        assertEquals("Test", testString);
    }

    @Test
    void addPointAtTheEndWorksProperly() {
        String testString = BadOcrCorrector.addPeriodAtTheEnd("t");

        assertEquals("t.", testString);

        testString = BadOcrCorrector.addPeriodAtTheEnd("test");

        assertEquals("test.", testString);
    }

    @Test
    void addPointAtTheEndDoesNotAddPointAtTheEndIfLastCharacterIsAPunctuationMark() {
        val testString = BadOcrCorrector.addPeriodAtTheEnd(
                "test!");

        assertEquals("test!", testString);
    }

    @Test
    void addPointAtTheEndAddsPointAtTheEndIfLastCharacterIsABracketOrAQuotationMark() {
        String testString = BadOcrCorrector.addPeriodAtTheEnd(
                "(test)");

        assertEquals("(test).", testString);

        testString = BadOcrCorrector.addPeriodAtTheEnd("\"test\"");

        assertEquals("\"test\".", testString);

        testString = BadOcrCorrector.addPeriodAtTheEnd("«test»");

        assertEquals("«test».", testString);
    }

    @Test()
    void addPointAtTheEndThrowsNullPointerExceptionIfParametersIsNull() {
        assertThrows(NullPointerException.class, () ->
                BadOcrCorrector.addPeriodAtTheEnd(null));
    }

    @Test
    void makeUppercaseThrowsNullPointerExceptionIfParametersIsNull() {
        assertThrows(NullPointerException.class, () ->
                BadOcrCorrector.capitalize(null));
    }
}
package com.artyommameev.faststudynotes.correct;

import com.artyommameev.faststudynotes.domain.Correction;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A set of utilities for correction of typical errors and inconvenient
 * formatting of bad OCRed text.
 *
 * @author Artyom Mameev
 */
@UtilityClass
public class BadOcrCorrector {

    /**
     * Corrects a text using the data from {@link Correction}s list.
     * <p>
     * If the given correction type is {@link Correction.TYPE#TEXT}, the text
     * goes through the following procedures:
     * <p>
     * 1. All line breaks in the text replaces by whitespaces;<br>
     * 2. Removes a soft hyphen if it exists;<br>
     * 3. Double whitespaces (byproduct of the first operation)
     * replaces by normal whitespaces;<br>
     * 4. Removes a normal hyphen if it used to break lines;<br>
     * 5. Replaces each substring of the text that matches the expressions
     * with the specific corrections according to {@link Correction} objects
     * in the given {@link Correction}s list with the {@link Correction.TYPE#TEXT}
     * type.
     * <p>
     * If the given correction type is {@link Correction.TYPE#TEXT}, this method
     * removes line breaks from the original text, because they are become
     * irrelevant as the paragraph formatting in poorly OCR'ed text is already
     * lost and must be set manually by the user using the application GUI.
     * <p>
     * If the given correction type is {@link Correction.TYPE#CODE}, text goes
     * through the following procedures:
     * <p>
     * 1. Removes all whitespaces at the end of the text;<br>
     * 2. Replaces each substring of the text that matches the expressions
     * with the specific corrections according to {@link Correction} objects
     * in the given {@link Correction}s list with the {@link Correction.TYPE#CODE}
     * type.
     *
     * @param text        the text to correct.
     * @param type        the type of the correction.
     * @param corrections the list of the {@link Correction} objects.
     * @return the text, corrected according to the above rules.
     * @throws NullPointerException     if any parameter is null.
     * @throws IllegalArgumentException if the text is empty.
     */

    public static String correct(@NonNull String text, @NonNull Correction.TYPE
            type, @NonNull List<Correction> corrections) {
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be empty");
        }

        if (type.equals(Correction.TYPE.TEXT)) {
            text = doBasicTextCorrection(text);
        }

        if (type.equals(Correction.TYPE.CODE)) {
            text = removeEndWhitespaces(text);
        }

        for (val correction : corrections) {
            if (correction.getType().equals(type)) {
                text = text.replaceAll(Pattern.quote(correction.getExpression()),
                        Matcher.quoteReplacement(correction.getCorrection()));
            }
        }

        return text;
    }

    /**
     * Makes a string begin with a capital letter.
     *
     * @param toCorrect the string to correct.
     * @return the given string starting with a capital letter.
     * @throws IllegalArgumentException if the string is empty.
     * @throws NullPointerException     if the string is null.
     */
    public static String capitalize(@NonNull String toCorrect) {
        if (toCorrect.isEmpty()) {
            throw new IllegalArgumentException("String to correct cannot be " +
                    "empty");
        }

        if (toCorrect.length() == 1) {
            return toCorrect.toUpperCase();
        }

        val firstCharacterOfString = toCorrect.substring(0, 1);

        val otherPartOfString = toCorrect.substring(1);

        return firstCharacterOfString.toUpperCase() + otherPartOfString;
    }

    /**
     * Adds a period at the end of the given string.
     *
     * @param toCorrect the string to correct.
     * @return the given string with period at the end if the last character is
     * not a punctuation mark (except for bracket ')' and quotation marks
     * '"' and '»'), otherwise returns the same string.
     * @throws IllegalArgumentException if the string is empty.
     * @throws NullPointerException     if the string is null.
     */
    public static String addPeriodAtTheEnd(@NonNull String toCorrect) {
        if (toCorrect.isEmpty()) {
            throw new IllegalArgumentException("String to correct cannot be " +
                    "empty");
        }

        val lastCharacter = toCorrect.substring(toCorrect.length() - 1);

        boolean isNotPunctuationMark = !Pattern.matches("\\p{Punct}",
                lastCharacter);
        boolean isBracket = lastCharacter.equals(")");
        boolean isQuotationMark = lastCharacter.equals("\"");
        boolean isDoubleAngleQuotationMark = lastCharacter.equals("»");

            /*don't add period if the last character is a punctuation mark
            (except for bracket and quotation marks)*/
        if (isNotPunctuationMark |
                isBracket | isQuotationMark | isDoubleAngleQuotationMark) {
            toCorrect += ".";
        }

        return toCorrect;
    }

    private static String doBasicTextCorrection(String toCorrect) {
        toCorrect = toCorrect
                // change line breaks into whitespaces
                .replaceAll("\\r\\n|\\r|\\n", " ")
                // remove soft hyphen
                .replaceAll("\u00AD\\s", "")
                // fix double whitespaces
                .replaceAll(" {2}", " ");

        // e.g. "t- " in "pat- tern"
        val pattern = Pattern.compile("\\S-\\s");
        val matcher = pattern.matcher(toCorrect);

        // remove hyphenated line breaks
        while (matcher.find()) {
            int foundStart = matcher.start();
            int foundEnd = matcher.end();

            toCorrect = toCorrect.substring(0, foundStart + 1) +
                    toCorrect.substring(foundEnd);
        }

        return toCorrect;
    }

    private static String removeEndWhitespaces(String toCorrect) {
        return toCorrect.replaceFirst("\\s++$", "");
    }
}

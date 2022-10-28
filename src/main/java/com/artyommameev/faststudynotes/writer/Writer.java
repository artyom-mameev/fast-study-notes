package com.artyommameev.faststudynotes.writer;

import javax.naming.OperationNotSupportedException;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * An interface for objects that can create documents with simple formatting.
 *
 * @author Artyom Mameev
 * @see DocWriter
 * @see TextFlowWriter
 * @see MultiWriter
 */
public interface Writer {

    /**
     * Adds a headline to the document.
     *
     * @param headline the headline text that should be added to the document.
     */
    void addHeadline(String headline);

    /**
     * Adds a normal text to the document.
     *
     * @param text the normal text that should be added to the document.
     */
    void addText(String text);

    /**
     * Adds a text formatted as a code to the document.
     *
     * @param code the code text that should be added to the document.
     */
    void addCode(String code);

    /**
     * Adds an image to the document.
     *
     * @param bufferedImage the image that should be added to the document.
     */
    void addImage(BufferedImage bufferedImage);

    /**
     * Adds a line break to the document.
     */
    void addLineBreak();

    /**
     * Adds a paragraph break to the document.
     */
    void addParagraphBreak();

    /**
     * Removes last added element from the document.
     */
    void undo();

    /**
     * Removes a white space occurrences at the end of the document.
     */
    void trimEnd();

    /**
     * Checks whether the document is empty.
     *
     * @return true if the document is empty, otherwise false.
     */
    boolean isEmpty();

    /**
     * Always returns false indicating that the document is not supports saving.
     *
     * @return false.
     */
    default boolean isSavingSupported() {
        return false;
    }

    /**
     * Just throws {@link OperationNotSupportedException} indicating that
     * the document is not supports saving.
     */
    default Map<String, byte[]> save() throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }
}

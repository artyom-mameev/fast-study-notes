package com.artyommameev.faststudynotes.writer;

import lombok.NonNull;
import lombok.val;

import javax.naming.OperationNotSupportedException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The utility for adding elements to a several {@link Writer} objects at once.
 *
 * @author Artyom Mameev
 * @see Writer
 */
public class MultiWriter implements Writer {

    private final List<Writer> writers = new ArrayList<>();

    /**
     * Instantiates a new MultiWriter object.
     *
     * @param writers the {@link Writer} objects to which new elements should be
     *                added.
     * @throws NullPointerException if any {@link Writer} object is null.
     */
    public MultiWriter(Writer... writers) {
        for (val writer : writers) {
            if (writer == null) {
                throw new NullPointerException("Writer cannot be null");
            }

            this.writers.add(writer);
        }
    }

    /**
     * Delegates adding a headline to the provided {@link Writer} objects.
     *
     * @param headline the headline that should be added to the provided
     *                 {@link Writer} objects.
     * @throws NullPointerException     if the headline is null.
     * @throws IllegalArgumentException if the headline is empty.
     */
    @Override
    public void addHeadline(@NonNull String headline) {
        if (headline.isEmpty()) {
            throw new IllegalArgumentException("Headline cannot be empty");
        }

        for (val writer : writers) {
            writer.addHeadline(headline);
        }
    }

    /**
     * Delegates adding a normal text to the provided {@link Writer} objects.
     *
     * @param text the text that should be added the provided {@link Writer}
     *             objects.
     * @throws NullPointerException     if the text is null.
     * @throws IllegalArgumentException if the text is empty.
     */
    @Override
    public void addText(@NonNull String text) {
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be empty");
        }

        for (val writer : writers) {
            writer.addText(text);
        }
    }

    /**
     * Delegates adding a text formatted as code to the provided {@link Writer}
     * objects.
     *
     * @param code the code text that should be added to the provided
     *             {@link Writer} objects.
     * @throws NullPointerException     if the code text is null.
     * @throws IllegalArgumentException if the code text is empty.
     */
    @Override
    public void addCode(@NonNull String code) {
        if (code.isEmpty()) {
            throw new IllegalArgumentException("Code cannot be empty");
        }

        for (val writer : writers) {
            writer.addCode(code);
        }
    }

    /**
     * Delegates adding an image to to the provided {@link Writer} objects.
     *
     * @param bufferedImage the image that should be added to the provided
     *                      {@link Writer} objects
     * @throws NullPointerException if the bufferedImage is null.
     */
    @Override
    public void addImage(@NonNull BufferedImage bufferedImage) {
        for (val writer : writers) {
            writer.addImage(bufferedImage);
        }
    }

    /**
     * Delegates adding a line break to the provided {@link Writer} objects.
     */
    @Override
    public void addLineBreak() {
        for (val writer : writers) {
            writer.addLineBreak();
        }
    }

    /**
     * Delegates adding a paragraph break to the provided {@link Writer} objects.
     */
    @Override
    public void addParagraphBreak() {
        for (val writer : writers) {
            writer.addParagraphBreak();
        }
    }

    /**
     * Delegates removing a last added element to the provided {@link Writer}
     * objects.
     */
    @Override
    public void undo() {
        for (val writer : writers) {
            writer.undo();
        }
    }

    /**
     * Delegates removing a white space occurrences at the end of the document
     * to the provided {@link Writer} objects.
     */
    @Override
    public void trimEnd() {
        for (val writer : writers) {
            writer.trimEnd();
        }
    }

    /**
     * Returns a boolean value indicating whether the provided {@link Writer}
     * objects have no elements.
     *
     * @return true if the first provided {@link Writer} object have no elements,
     * otherwise false.
     */
    @Override
    public boolean isEmpty() {
        return writers.get(0).isEmpty();
    }

    /**
     * Returns a boolean value indicating whether the saving is supported for
     * any provided {@link Writer} object.
     *
     * @return true if any provided {@link Writer} object supports saving,
     * otherwise false.
     */
    @Override
    public boolean isSavingSupported() {
        for (val writer : writers) {
            if (writer.isSavingSupported()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns saved documents of the provided {@link Writer} objects in a Map.
     * <p>
     *
     * @return the saved documents of the provided {@link Writer} objects in a
     * Map the keys in which is a format identifiers, and the values is a saved
     * documents as an array of bytes; if no {@link Writer} objects support
     * saving, returns an empty Map.
     */
    @Override
    public Map<String, byte[]> save() {
        Map<String, byte[]> savedDocuments = new HashMap<>();

        for (val writer : writers) {
            if (!writer.isSavingSupported()) {
                continue;
            }

            try {
                savedDocuments.putAll(writer.save());
            } catch (OperationNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        return savedDocuments;
    }
}

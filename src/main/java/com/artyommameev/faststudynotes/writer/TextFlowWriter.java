package com.artyommameev.faststudynotes.writer;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.NonNull;
import lombok.val;

import java.awt.image.BufferedImage;

/**
 * An adapter that allows to add new elements to the JavaFX TextFlow using the
 * {@link Writer} interface.
 *
 * @author Artyom Mameev
 * @see Writer
 */
public class TextFlowWriter implements Writer {

    private static final String PARAGRAPH_BREAK = "\n\n";
    private static final String LINE_BREAK = "\n";
    private static final String DEFAULT_FONT = "Dialog";
    private static final String CODE_FONT = "Monospaced";

    private final TextFlow textFlow;

    /**
     * Instantiates a new TextFlowWriter object.
     *
     * @param textFlow the TextFlow object to add new elements.
     * @throws NullPointerException if the textFlow object is null.
     */
    public TextFlowWriter(@NonNull TextFlow textFlow) {
        this.textFlow = textFlow;
    }

    /**
     * Adds a headline to the TextFlow.
     *
     * @param headline the headline text that should be added to the TextFlow.
     * @throws NullPointerException     if the headline text is null.
     * @throws IllegalArgumentException if the headline text is empty.
     */
    public void addHeadline(@NonNull String headline) {
        if (headline.isEmpty()) {
            throw new IllegalArgumentException("Headline cannot be empty");
        }

        val headText = createText(headline, DEFAULT_FONT, FontWeight.BOLD,
                16);

        headText.setFill(Color.BLUE);

        Platform.runLater(() ->
                textFlow.getChildren().add(headText));
    }

    /**
     * Adds a normal text to the TextFlow.
     *
     * @param text the text that should be added to the TextFlow.
     * @throws NullPointerException     if the text is null.
     * @throws IllegalArgumentException if the text is empty.
     */
    public void addText(@NonNull String text) {
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be empty");
        }

        val normalText = createText(text, DEFAULT_FONT, FontWeight.NORMAL,
                14);

        Platform.runLater(() ->
                textFlow.getChildren().add(normalText));
    }

    /**
     * Adds a text formatted as code to the TextFlow.
     *
     * @param code the code text that should be added to the TextFlow.
     * @throws NullPointerException     if the code text is null.
     * @throws IllegalArgumentException if the code text is empty.
     */
    public void addCode(@NonNull String code) {
        if (code.isEmpty()) {
            throw new IllegalArgumentException("Code cannot be empty");
        }

        val codeText = createText(code, CODE_FONT, FontWeight.NORMAL,
                14);

        Platform.runLater(() ->
                textFlow.getChildren().add(codeText));
    }

    /**
     * Adds an image to the TextFlow.
     *
     * @param bufferedImage the image that should be added to the TextFlow.
     * @throws NullPointerException if the image is null.
     */
    public void addImage(@NonNull BufferedImage bufferedImage) {
        val imageView = new ImageView(SwingFXUtils.toFXImage(bufferedImage,
                null));

        Platform.runLater(() ->
                textFlow.getChildren().add(imageView));
    }

    /**
     * Adds a line break to the TextFlow.
     */
    @Override
    public void addLineBreak() {
        val lineBreak = new Text(LINE_BREAK);

        Platform.runLater(() ->
                textFlow.getChildren().add(lineBreak));
    }

    /**
     * Adds a paragraph break to the TextFlow.
     */
    @Override
    public void addParagraphBreak() {
        val paragraphBreak = new Text(PARAGRAPH_BREAK);

        Platform.runLater(() ->
                textFlow.getChildren().add(paragraphBreak));
    }

    /**
     * Removes last added element from the TextFlow.
     */
    @Override
    public void undo() {
        if (isEmpty()) {
            return;
        }

        val lastElementIndex = textFlow.getChildren().size() - 1;

        Platform.runLater(() ->
                textFlow.getChildren().remove(lastElementIndex));
    }

    /**
     * Removes a white space occurrences at the end of the TextFlow.
     */
    @Override
    public void trimEnd() {
        if (isEmpty()) {
            return;
        }

        val lastElementIndex = textFlow.getChildren().size() - 1;

        val lastElement = textFlow.getChildren().get(lastElementIndex);

        if (!(lastElement instanceof Text)) {
            return;
        }

        val lastTextElement = (Text) lastElement;

        if (!lastTextElement.getText().equals(PARAGRAPH_BREAK) ||
                !lastTextElement.getText().equals(LINE_BREAK)) {
            return;
        }

        Platform.runLater(() ->
                textFlow.getChildren().remove(lastElementIndex));
    }

    /**
     * Returns a boolean value indicating whether the TextFlow is empty.
     *
     * @return true if the TextFlow is empty, otherwise false.
     */
    @Override
    public boolean isEmpty() {
        return textFlow.getChildren().size() == 0;
    }

    private Text createText(String text, String family,
                            FontWeight fontWeight, int size) {
        val textFlowText = new Text(text);

        textFlowText.setFont(Font.font(family, fontWeight, size));

        return textFlowText;
    }
}

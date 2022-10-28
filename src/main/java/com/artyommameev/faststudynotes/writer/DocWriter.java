package com.artyommameev.faststudynotes.writer;

import lombok.Cleanup;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import word.api.interfaces.IDocument;
import word.w2004.Document2004;
import word.w2004.elements.BreakLine;
import word.w2004.elements.Heading1;
import word.w2004.elements.Paragraph;
import word.w2004.elements.ParagraphPiece;
import word.w2004.style.Font;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility for creating Microsoft Office .doc documents.
 *
 * @author Artyom Mameev
 * @see Writer
 */
public class DocWriter implements Writer {

    private final List<DocElement> docElements;
    private IDocument doc;

    public DocWriter() {
        doc = new Document2004();

        doc.encoding(Document2004.Encoding.UTF_8);

        docElements = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException     if the headline is null.
     * @throws IllegalArgumentException if the headline is empty.
     */
    @Override
    public void addHeadline(@NonNull String headline) {
        if (headline.isEmpty()) {
            throw new IllegalArgumentException("Headline cannot be empty");
        }

        val docElement = new DocElement(DocElementType.HEADLINE);

        docElement.setText(headline);

        docElements.add(docElement);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException     if the text is null.
     * @throws IllegalArgumentException if the text is empty.
     */
    @Override
    public void addText(@NonNull String text) {
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be empty");
        }

        val docElement = new DocElement(DocElementType.TEXT);

        docElement.setText(text);

        docElements.add(docElement);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException     if the code is null.
     * @throws IllegalArgumentException if the code is empty.
     */
    @Override
    public void addCode(@NonNull String code) {
        if (code.isEmpty()) {
            throw new IllegalArgumentException("Code cannot be empty");
        }

        val docElement = new DocElement(DocElementType.CODE);

        docElement.setText(code);

        docElements.add(docElement);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if the bufferedImage is null.
     */
    @Override
    public void addImage(@NonNull BufferedImage bufferedImage) {
        val docElement = new DocElement(DocElementType.IMAGE);

        docElement.setImage(bufferedImage);

        docElements.add(docElement);
    }

    /**
     * Does nothing, because the line breaks is adding in the document
     * after each element automatically.
     */
    @Override
    public void addLineBreak() {
        // line breaks is adding after each element automatically
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addParagraphBreak() {
        val docElement = new DocElement(DocElementType.PARAGRAPH_BREAK);

        docElements.add(docElement);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        if (docElements.isEmpty()) {
            return;
        }

        docElements.remove(docElements.size() - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trimEnd() {
        if (docElements.isEmpty()) {
            return;
        }

        if (docElements.get(docElements.size() - 1).getType().equals(
                DocWriter.DocElementType.PARAGRAPH_BREAK)) {
            undo();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return docElements.size() == 0;
    }

    /**
     * Indicating whether this {@link Writer} implementation can save documents.
     * Always returns true.
     *
     * @return true.
     */
    @Override
    public boolean isSavingSupported() {
        return true;
    }

    /**
     * Returns saved document in a Map.
     * <p>
     * After saving, a new document is created and the old document
     * becomes unavailable for editing.
     *
     * @return the saved document in a Map, the key in which is a format
     * identifier (doc), and the value is a saved document as an array of bytes.
     */
    @Override
    public Map<String, byte[]> save() {
        Map<String, byte[]> savedDocument = new HashMap<>();

        savedDocument.put("doc", makeDoc().getBytes());

        return savedDocument;
    }

    private String makeDoc() {
        for (val docElement : docElements) {
            switch (docElement.getType()) {
                case HEADLINE:
                    makeHeadline(docElement);
                    break;

                case TEXT:
                    makeText(docElement);
                    break;

                case CODE:
                    makeCode(docElement);
                    break;

                case PARAGRAPH_BREAK:
                    makeParagraphBreak();
                    break;

                case IMAGE:
                    makeImage(docElement);
                    makeParagraphBreak();
                    break;
            }
        }

        String docContent = doc.getContent();

        doc = new Document2004();

        return docContent;
    }

    private void makeHeadline(DocElement docElement) {
        doc.addEle(Heading1.with(
                escapeXML(docElement.getText()))
                .create());
    }

    private void makeText(DocElement docElement) {
        doc.addEle(Paragraph.withPieces(
                ParagraphPiece.with(
                        escapeXML(docElement.getText()))
                        .create()));
    }

    private void makeCode(DocElement docElement) {
        val codeArray = docElement.getText()
                .split("\\r\\n|\\r|\\n");

        for (val codeElements : codeArray) {
            doc.addEle(Paragraph.withPieces(
                    ParagraphPiece.with((
                            escapeXML(codeElements)))
                            .withStyle().font(Font.COURIER)
                            .create()));
        }
    }

    private void makeParagraphBreak() {
        doc.addEle(BreakLine.times(1)
                .create());
    }

    private void makeImage(DocElement docElement) {
        val bufferedImage = docElement.getImage();

        val convertedImage = new BufferedImage(
                bufferedImage.getWidth(), bufferedImage.getHeight(),
                BufferedImage.TYPE_USHORT_565_RGB);

        convertedImage.getGraphics().drawImage(bufferedImage,
                0, 0, null);

        val graphics = convertedImage.createGraphics();

        graphics.drawImage(convertedImage, null, null);

        try {
            @Cleanup val outputStream = new ByteArrayOutputStream();

            ImageIO.write(convertedImage, "png", outputStream);

            @Cleanup val inputStream = new ByteArrayInputStream(
                    outputStream.toByteArray());

            doc.addEle(word.w2004.elements.Image.from_STREAM(
                    "filename.jpg", inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String escapeXML(String s) {
        return s.replaceAll("&", "&amp;")
                .replaceAll("\"", "&quot;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("'", "&apos;");
    }

    private enum DocElementType {HEADLINE, TEXT, CODE, PARAGRAPH_BREAK, IMAGE}

    private static class DocElement {

        @Getter
        private final DocElementType type;
        @Getter
        private String text;
        @Getter
        private BufferedImage image;

        private DocElement(DocElementType type) {
            this.type = type;
        }

        private void setText(String text) {
            if (type == DocElementType.HEADLINE ||
                    type == DocElementType.TEXT ||
                    type == DocElementType.CODE)
                this.text = text;
            else throw new UnsupportedOperationException("Type is " + type +
                    ", but trying to add text");
        }

        private void setImage(BufferedImage image) {
            if (type == DocElementType.IMAGE) {
                this.image = image;
            } else throw new UnsupportedOperationException("Type is " + type +
                    ", but trying to add image");
        }
    }
}
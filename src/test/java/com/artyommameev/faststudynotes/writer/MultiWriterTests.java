package com.artyommameev.faststudynotes.writer;

import javafx.scene.text.TextFlow;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("ConstantConditions")
public class MultiWriterTests {

    private MultiWriter multiWriter;
    private DocWriter docWriter;
    private TextFlowWriter textFlowWriter;

    @BeforeEach
    void setUp() {
        docWriter = Mockito.mock(DocWriter.class);

        when(docWriter.isSavingSupported()).thenReturn(true);

        textFlowWriter = Mockito.mock(TextFlowWriter.class);

        multiWriter = new MultiWriter(docWriter, textFlowWriter);
    }

    @Test
    void constructorThrowsNullPointerExceptionIfSomeOrAllWritersIsNull() {
        assertThrows(NullPointerException.class, () ->
                new MultiWriter(null,
                        new TextFlowWriter(new TextFlow())));

        assertThrows(NullPointerException.class, () ->
                new MultiWriter(new DocWriter(), null));

        assertThrows(NullPointerException.class, () ->
                new MultiWriter(null, null));
    }

    @Test
    void addHeadlineThrowsNullPointerExceptionIfHeadlineIsNull() {
        assertThrows(NullPointerException.class, () ->
                multiWriter.addHeadline(null));
    }

    @Test
    void addTextThrowsNullPointerExceptionIfTextIsNull() {
        assertThrows(NullPointerException.class, () ->
                multiWriter.addText(null));
    }

    @Test
    void addCodeThrowsNullPointerExceptionIfCodeIsNull() {
        assertThrows(NullPointerException.class, () ->
                multiWriter.addCode(null));
    }

    @Test
    void addImageThrowsNullPointerExceptionIfImageIsNull() {
        assertThrows(NullPointerException.class, () ->
                multiWriter.addImage(null));
    }

    @Test
    void addHeadlineThrowsIllegalArgumentExceptionIfHeadlineIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                multiWriter.addHeadline(""));
    }

    @Test
    void addTextThrowsIllegalArgumentExceptionIfTextIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                multiWriter.addText(""));
    }

    @Test
    void addCodeThrowsIllegalArgumentExceptionIfCodeIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                multiWriter.addCode(""));
    }

    @Test
    void addHeadlineCallsSameMethodInWriters() {
        multiWriter.addHeadline("Test");

        verify(docWriter).addHeadline("Test");
        verify(textFlowWriter).addHeadline("Test");
    }

    @Test
    void addTextCallsSameMethodInWriters() {
        multiWriter.addText("Test");

        verify(docWriter).addText("Test");
        verify(textFlowWriter).addText("Test");
    }

    @Test
    void addCodeCallsSameMethodInWriters() {
        multiWriter.addCode("Test");

        verify(docWriter).addCode("Test");
        verify(textFlowWriter).addCode("Test");
    }

    @Test
    void addImageCallsSameMethodInWriters() {
        val bufferedImage = new BufferedImage(1, 1, 1);
        multiWriter.addImage(bufferedImage);

        verify(docWriter).addImage(bufferedImage);
        verify(textFlowWriter).addImage(bufferedImage);
    }

    @Test
    void addLineBreakCallsSameMethodInWriters() {
        multiWriter.addLineBreak();

        verify(docWriter).addLineBreak();
        verify(textFlowWriter).addLineBreak();
    }

    @Test
    void addParagraphBreakCallsSameMethodInWriters() {
        multiWriter.addParagraphBreak();

        verify(docWriter).addParagraphBreak();
        verify(textFlowWriter).addParagraphBreak();
    }

    @Test
    void undoCallsSameMethodInWriters() {
        multiWriter.undo();

        verify(docWriter).undo();
        verify(textFlowWriter).undo();
    }

    @Test
    void trimEndCallsSameMethodInWriters() {
        multiWriter.trimEnd();

        verify(docWriter).trimEnd();
        verify(textFlowWriter).trimEnd();
    }

    @Test
    void isEmptyReturnsTrueIfFirstWriterIsEmpty() {
        when(docWriter.isEmpty()).thenReturn(true);

        assertTrue(multiWriter.isEmpty());
    }

    @Test
    void isSavingSupportedReturnsTrueIfSomeOfWritersSupportsSaving() {
        assertTrue(multiWriter.isSavingSupported());
    }

    @Test
    void saveCallsSameMethodInWritersIfSavingIsSupported() {
        multiWriter.save();

        verify(docWriter).isSavingSupported();
        verify(docWriter).save();

        verify(textFlowWriter).isSavingSupported();
    }
}
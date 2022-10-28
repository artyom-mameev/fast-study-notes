package com.artyommameev.faststudynotes.writer;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.naming.OperationNotSupportedException;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SuppressWarnings("ConstantConditions")
public class TextFlowWriterTests {

    private static MockedStatic<Platform> platformMockedStatic;
    private TextFlowWriter textFlowWriter;
    private TextFlow textFlow;

    @BeforeAll
    static void setUp() {
        platformMockedStatic = Mockito.mockStatic(Platform.class);

        platformMockedStatic.when(() -> Platform.runLater(any()))
                .then(invocationOnMock -> {
                    val runnable = invocationOnMock.getArgument(
                            0, Runnable.class);

                    runnable.run();

                    return invocationOnMock;
                });
    }

    @AfterAll
    static void clearPlatformStaticMock() {
        platformMockedStatic.close();
    }

    @BeforeEach
    public void clearTextFlow() {
        textFlow = new TextFlow();

        textFlowWriter = new TextFlowWriter(textFlow);

        new JFXPanel();
    }

    @Test
    void constructorThrowsNullPointerExceptionIfTextFlowIsNull() {
        assertThrows(NullPointerException.class, () ->
                new TextFlowWriter(null));
    }

    @Test
    void addHeadlineThrowsNullPointerExceptionIfHeadlineIsNull() {
        assertThrows(NullPointerException.class, () ->
                textFlowWriter.addHeadline(null));
    }

    @Test
    void addTextThrowsNullPointerExceptionIfTextIsNull() {
        assertThrows(NullPointerException.class, () ->
                textFlowWriter.addText(null));
    }

    @Test
    void addCodeThrowsNullPointerExceptionIfCodeIsNull() {
        assertThrows(NullPointerException.class, () ->
                textFlowWriter.addCode(null));
    }

    @Test
    void addImageThrowsNullPointerExceptionIfImageIsNull() {
        assertThrows(NullPointerException.class, () ->
                textFlowWriter.addImage(null));
    }

    @Test
    void addHeadlineThrowsIllegalArgumentExceptionIfHeadlineIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                textFlowWriter.addHeadline(""));
    }

    @Test
    void addTextThrowsIllegalArgumentExceptionIfTextIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                textFlowWriter.addText(""));
    }

    @Test
    void addCodeThrowsIllegalArgumentExceptionIfCodeIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                textFlowWriter.addCode(""));
    }

    @Test
    void addHeadlineAddsHeadline() {
        textFlowWriter.addHeadline("Test");

        val node = textFlow.getChildren().get(0);

        assertTrue(node instanceof Text);
        assertEquals("Bold", ((Text) node).getFont().getStyle());
        assertEquals("Test", ((Text) node).getText());
    }

    @Test
    void addTextAddsText() {
        textFlowWriter.addText("Test");

        isThisATestText(textFlow.getChildren().get(0));
    }

    @Test
    void addCodeAddsCode() {
        textFlowWriter.addCode("Test");

        val node = textFlow.getChildren().get(0);

        assertTrue(node instanceof Text);
        assertEquals(Font.font("Monospaced", FontWeight.NORMAL,
                14), ((Text) node).getFont());
        assertEquals("Test", ((Text) node).getText());
    }

    @Test
    void addImageAddsImage() {
        val bufferedImage = new BufferedImage(1, 1, 1);

        textFlowWriter.addImage(bufferedImage);

        val node = textFlow.getChildren().get(0);

        assertTrue(node instanceof ImageView);

        val imageView = (ImageView) textFlow.getChildren().get(0);
        val image = imageView.getImage();

        assertEquals(1, image.getWidth());
        assertEquals(1, image.getHeight());
    }

    @Test
    void addLineBreakAddsLineBreak() {
        textFlowWriter.addLineBreak();

        val node = textFlow.getChildren().get(0);

        assertTrue(node instanceof Text);
        assertEquals("\n", ((Text) node).getText());
    }

    @Test
    void addParagraphBreakAddsParagraphBreak() {
        textFlowWriter.addParagraphBreak();

        val node = textFlow.getChildren().get(0);

        assertTrue(node instanceof Text);
        assertEquals("\n\n", ((Text) node).getText());
    }

    @Test
    void addsEveryElement() {
        textFlowWriter.addHeadline("Headline");
        textFlowWriter.addText("Text");
        textFlowWriter.addCode("Code");
        textFlowWriter.addImage(new BufferedImage(
                1, 1, 1));
        textFlowWriter.addLineBreak();
        textFlowWriter.addParagraphBreak();

        assertEquals("Headline", ((Text) textFlow.getChildren()
                .get(0)).getText());
        assertEquals("Text", ((Text) textFlow.getChildren()
                .get(1)).getText());
        assertEquals("Code", ((Text) textFlow.getChildren()
                .get(2)).getText());
        assertEquals(1, ((ImageView) textFlow.getChildren()
                .get(3)).getImage().getWidth());
        assertEquals("\n", ((Text) textFlow.getChildren()
                .get(4)).getText());
        assertEquals("\n\n", ((Text) textFlow.getChildren()
                .get(5)).getText());
    }

    @Test
    void undoDoesUndo() {
        textFlowWriter.addText("Test");
        textFlowWriter.undo();

        assertTrue(textFlowWriter.isEmpty());

        textFlowWriter.addText("Test");
        textFlowWriter.addText("Test 2");
        textFlowWriter.undo();

        isThisATestText(textFlow.getChildren().get(0));
    }

    @Test
    void trimEndTrimsEnd() {
        textFlowWriter.addText("Test");
        textFlowWriter.addParagraphBreak();
        textFlowWriter.trimEnd();

        isThisATestText(textFlow.getChildren().get(0));

        textFlowWriter.addLineBreak();
        textFlowWriter.trimEnd();

        isThisATestText(textFlow.getChildren().get(0));
    }

    @Test
    void isEmptyReturnsTrueIfEmpty() {
        assertTrue(textFlowWriter.isEmpty());
    }

    @Test
    void isEmptyReturnsFalseIfNotEmpty() {
        textFlowWriter.addHeadline("Test");

        assertFalse(textFlowWriter.isEmpty());
    }

    @Test
    void isSavingSupportedReturnsFalse() {
        assertFalse(textFlowWriter.isSavingSupported());
    }

    @Test
    void saveThrowsOperationNotSupportedException() {
        assertThrows(OperationNotSupportedException.class, () ->
                textFlowWriter.save());
    }

    private void isThisATestText(Node node) {
        assertTrue(node instanceof Text);
        assertEquals(Font.font("Dialog", FontWeight.NORMAL, 14),
                ((Text) node).getFont());
        assertEquals("Test", ((Text) node).getText());
    }
}
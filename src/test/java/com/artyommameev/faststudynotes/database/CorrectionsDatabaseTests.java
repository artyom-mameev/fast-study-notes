package com.artyommameev.faststudynotes.database;

import com.artyommameev.faststudynotes.domain.Correction;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static com.artyommameev.faststudynotes.database.CorrectionsDatabase.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings({"ConstantConditions", "SqlDialectInspection",
        "SqlNoDataSourceInspection"})
public class CorrectionsDatabaseTests {

    @BeforeAll
    static void setUpTestDbName() throws Throwable {
        val dbNameField = CorrectionsDatabase.class
                .getDeclaredField("DB_NAME");

        setFinalStatic(dbNameField, "test");

        val dbUrlField = CorrectionsDatabase.class
                .getDeclaredField("DB_URL");

        setFinalStatic(dbUrlField, "jdbc:sqlite:" +
                System.getProperty("user.dir") + "\\" + "test" + ".db");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void deleteDbFile() {
        val dbFile = new File(System.getProperty("user.dir") + "\\" +
                "test.db");

        dbFile.delete();
    }

    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    @BeforeEach
    void initDb() throws Throwable {
        init();
    }

    @AfterEach
    void deleteDb() {
        deleteDbFile();
    }

    @Test
    void insertThrowsNullPointerExceptionIfCorrectionIsNull() {
        assertThrows(NullPointerException.class, () ->
                insert(null));
    }

    @Test
    void insertThrowsCorrectionAlreadyExistsExceptionIfCorrectionAlreadyExists()
            throws Throwable {
        val textCorrection = new Correction("1", "2",
                Correction.TYPE.TEXT);

        insert(textCorrection);

        assertThrows(CorrectionsDatabase.CorrectionAlreadyExistsException.class, () -> {
            val correction2 = new Correction("1", "2",
                    Correction.TYPE.TEXT);

            insert(correction2);
        });

        assertThrows(CorrectionsDatabase.CorrectionAlreadyExistsException.class, () -> {
            val correction2 = new Correction("1", "3",
                    Correction.TYPE.TEXT);

            insert(correction2);
        });

        val codeCorrection = new Correction("1", "2",
                Correction.TYPE.CODE);

        insert(codeCorrection);

        assertThrows(CorrectionsDatabase.CorrectionAlreadyExistsException.class, () -> {
            val correction2 = new Correction("1", "2",
                    Correction.TYPE.CODE);

            insert(correction2);
        });

        assertThrows(CorrectionsDatabase.CorrectionAlreadyExistsException.class, () -> {
            val correction2 = new Correction("1", "3",
                    Correction.TYPE.CODE);

            insert(correction2);
        });
    }

    @Test
    void insertInsertsAndGetsAllCorrections() throws Throwable {
        val correction = new Correction("1", "2",
                Correction.TYPE.TEXT);

        val correction2 = new Correction("3", "4",
                Correction.TYPE.TEXT);

        insert(correction);
        insert(correction2);

        val corrections = getAll();

        assertEquals(2, corrections.size());
        assertEquals(correction.getExpression(),
                corrections.get(0).getExpression());
        assertEquals(correction.getCorrection(),
                corrections.get(0).getCorrection());
        assertEquals(correction.getType(), corrections.get(0).getType());

        assertEquals(correction2.getExpression(),
                corrections.get(1).getExpression());
        assertEquals(correction2.getCorrection(),
                corrections.get(1).getCorrection());
        assertEquals(correction2.getType(), corrections.get(1).getType());
    }

    @Test
    void updateUpdatesCorrection() throws Throwable {
        val correction = new Correction("1", "2",
                Correction.TYPE.TEXT);

        val correction2 = new Correction("3", "4",
                Correction.TYPE.TEXT);

        insert(correction);
        update(correction, correction2);

        val corrections = getAll();

        assertEquals(correction2.getExpression(),
                corrections.get(0).getExpression());
        assertEquals(correction2.getCorrection(),
                corrections.get(0).getCorrection());
        assertEquals(correction2.getType(), corrections.get(0).getType());
    }

    @Test
    void updateThrowsNullPointerExceptionIfOldCorrectionIsNull() {
        assertThrows(NullPointerException.class, () ->
                update(null, new Correction(
                        "1", "2", Correction.TYPE.TEXT)));
    }

    @Test
    void updateThrowsNullPointerExceptionIfUpdatedCorrectionIsNull() {
        assertThrows(NullPointerException.class, () ->
                update(new Correction("1", "2",
                        Correction.TYPE.TEXT), null));
    }

    @Test
    void removeThrowsNullPointerExceptionIfCorrectionIsNull() {
        assertThrows(NullPointerException.class, () ->
                remove(null));
    }

    @Test
    void removeRemovesCorrection() throws Throwable {
        val correction = new Correction("1", "2",
                Correction.TYPE.TEXT);

        val correction2 = new Correction("3", "4",
                Correction.TYPE.TEXT);

        insert(correction);
        insert(correction2);
        remove(correction2);

        val corrections = getAll();

        assertEquals(1, corrections.size());
        assertEquals(correction.getExpression(),
                corrections.get(0).getExpression());
        assertEquals(correction.getCorrection(),
                corrections.get(0).getCorrection());
        assertEquals(correction.getType(), corrections.get(0).getType());
    }

    @Test
    void getAllReturnsEmptyListIfDatabaseIsEmpty() throws Throwable {
        val corrections = getAll();

        assertEquals(0, corrections.size());
    }
}
package com.artyommameev.faststudynotes.database;

import com.artyommameev.faststudynotes.domain.Correction;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstraction of simple JDBC database for storing, querying, removing
 * and updating {@link Correction}s.
 *
 * @author Artyom Mameev
 */
@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
@UtilityClass
public class CorrectionsDatabase {

    private static final int SQLITE_CONSTRAINT_PRIMARYKEY_ERROR_CODE = 19;

    private static final String DB_NAME = "corrections";
    private static final String DB_URL = "jdbc:sqlite:" +
            System.getProperty("user.dir") + "\\" + DB_NAME + ".db";


    /**
     * Creates the database if it does not exist.
     *
     * @throws CorrectionsDatabaseException if the connection with the database
     *                                      fails.
     */
    public static void init() throws CorrectionsDatabaseException {
        val createIfNotExistsSql =
                "CREATE TABLE IF NOT EXISTS " + DB_NAME + " (\n" +
                        "expression text NOT NULL,\n" +
                        "correction text NOT NULL,\n" +
                        "correction_type text NOT NULL,\n" +
                        "PRIMARY KEY (expression, correction_type)" + ");";

        try (val connection = connect();
             val preparedStatement = connection.createStatement()) {
            preparedStatement.execute(createIfNotExistsSql);
        } catch (SQLException e) {
            throw new CorrectionsDatabaseException(e);
        }
    }

    /**
     * Inserts a {@link Correction} into the database.
     *
     * @param correction the {@link Correction} to insert.
     * @throws NullPointerException         if the {@link Correction} is null.
     * @throws CorrectionsDatabaseException if the connection with the database
     *                                      fails.
     */
    public static void insert(@NonNull Correction correction)
            throws CorrectionsDatabaseException,
            CorrectionAlreadyExistsException {
        val insertSql = "INSERT INTO " + DB_NAME +
                "(expression,correction,correction_type) VALUES(?,?,?)";

        try (val connection = connect();
             val preparedStatement = connection
                     .prepareStatement(insertSql)) {
            preparedStatement.setString(1,
                    correction.getExpression());
            preparedStatement.setString(2,
                    correction.getCorrection());
            preparedStatement.setString(3,
                    correction.getType().toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == SQLITE_CONSTRAINT_PRIMARYKEY_ERROR_CODE) {
                throw new CorrectionAlreadyExistsException();
            } else throw new CorrectionsDatabaseException(e);
        }
    }

    /**
     * Queries and returns all {@link Correction} objects from the database.
     *
     * @return the list of all {@link Correction} objects from the database.
     * @throws CorrectionsDatabaseException if the connection with the database
     *                                      fails.
     */
    public static List<Correction> getAll() throws CorrectionsDatabaseException {
        val selectSql = "SELECT expression, correction, correction_type FROM " +
                DB_NAME;

        List<Correction> corrections = new ArrayList<>();

        try (val connection = connect();
             val statement = connection.createStatement();
             val resultSet = statement.executeQuery(selectSql)) {
            while (resultSet.next()) {
                corrections.add(new Correction(
                        resultSet.getString("expression"),
                        resultSet.getString("correction"),
                        Correction.TYPE.valueOf(resultSet.getString(
                                "correction_type"))));
            }
        } catch (SQLException e) {
            throw new CorrectionsDatabaseException(e);
        }

        return corrections;
    }

    /**
     * Removes a {@link Correction} object from the database.
     *
     * @param correction the {@link Correction} object to remove.
     * @throws NullPointerException         if the {@link Correction} object is
     *                                      null.
     * @throws CorrectionsDatabaseException if the connection with the database
     *                                      fails.
     */
    public static void remove(@NonNull Correction correction)
            throws CorrectionsDatabaseException {
        val removeSql = "DELETE FROM " + DB_NAME + " WHERE expression = ?";

        try (val connection = connect();
             val preparedStatement = connection
                     .prepareStatement(removeSql)) {
            preparedStatement.setString(1,
                    correction.getExpression());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new CorrectionsDatabaseException(e);
        }
    }

    /**
     * Updates a {@link Correction} object in the database.
     *
     * @param oldCorrection the {@link Correction} object to update.
     * @param newCorrection the updated {@link Correction} object.
     * @throws NullPointerException         if any parameter is null.
     * @throws CorrectionsDatabaseException if the connection with the database
     *                                      fails.
     */
    public static void update(@NonNull Correction oldCorrection,
                              @NonNull Correction newCorrection)
            throws CorrectionsDatabaseException {
        val updateSql = "UPDATE " + DB_NAME + " SET expression = ? , " +
                "correction = ? , " + "correction_type = ? " +
                "WHERE expression = ?";

        try (val connection = connect();
             val preparedStatement = connection
                     .prepareStatement(updateSql)) {
            preparedStatement.setString(1,
                    newCorrection.getExpression());
            preparedStatement.setString(2,
                    newCorrection.getCorrection());
            preparedStatement.setString(3,
                    newCorrection.getType().toString());
            preparedStatement.setString(4,
                    oldCorrection.getExpression());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new CorrectionsDatabaseException(e);
        }
    }

    private static Connection connect() throws CorrectionsDatabaseException {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            throw new CorrectionsDatabaseException(e);
        }
    }

    /**
     * An exception indicating that interaction with the Corrections Database
     * fails.
     */
    public static class CorrectionsDatabaseException extends Throwable {

        /**
         * Instantiates a new Corrections Database Exception.
         *
         * @param t the cause of the exception.
         */
        public CorrectionsDatabaseException(Throwable t) {
            super(t);
        }
    }

    /**
     * An exception indicating that a certain {@link Correction} already exists
     * in the database.
     */
    public static class CorrectionAlreadyExistsException extends Throwable {
        /**
         * Instantiates a new Corrections Already Exists Exception.
         */
        public CorrectionAlreadyExistsException() {
            super();
        }
    }
}

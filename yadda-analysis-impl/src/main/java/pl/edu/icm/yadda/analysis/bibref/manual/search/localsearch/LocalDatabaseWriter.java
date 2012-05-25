package pl.edu.icm.yadda.analysis.bibref.manual.search.localsearch;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import pl.edu.icm.yadda.tools.bibref.model.AuthorSimpleMetadata;
import pl.edu.icm.yadda.tools.bibref.model.DocSimpleMetadata;

/**
 * Writes documents metadata to the database.
 * 
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 *
 */
public class LocalDatabaseWriter implements DatabaseWriter {
    private Connection connection = null;

    public LocalDatabaseWriter() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/nlm", "postgres", "postgres");
        connection.setAutoCommit(false);
        clearDatabase();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pl.edu.icm.yadda.analysis.bibref.manual.search.localsearch.DatabaseWriter
     * #writeToDatabase(pl.edu.icm.yadda.analysis.bibref.manual.search.
     * DocSimpleMetadata)
     */
    @Override
    public void writeToDatabase(DocSimpleMetadata metadata) throws SQLException {
        final String sql = "INSERT INTO publications(id, bibref_source, bibref_position, journal_name, journal_hash, volume, issue, date_published_year, def_name)"
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        final String authorSql = "INSERT INTO authors(publication_id, author_coauthor_normalized, author_coauthor_surname) VALUES(?, ?, ?)";
        PreparedStatement statement = null;
        PreparedStatement authorStatement = null;
        try {
            statement = connection.prepareStatement(sql);
            authorStatement = connection.prepareStatement(authorSql);
            statement.setString(1, metadata.getDocId());
            statement.setString(2, null);
            statement.setString(3, Long.toString(metadata.getPosition()));
            statement.setString(4, metadata.getJournal());
            statement.setString(5, metadata.getJournalHash());
            statement.setString(6, metadata.getVolume());
            statement.setString(7, metadata.getIssue());
            statement.setString(8, metadata.getYear());
            statement.setString(9, metadata.getTitle());
            statement.execute();

            for (AuthorSimpleMetadata author : metadata.getAuthors()) {
                authorStatement.setString(1, metadata.getDocId());
                authorStatement.setString(2, author.getNormalized());
                authorStatement.setString(3, author.getSurname());
                authorStatement.execute();
            }
            connection.commit();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (authorStatement != null) {
                    authorStatement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void clearDatabase() throws SQLException {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            final String authorsSql = "DELETE FROM authors";
            final String publicationsSql = "DELETE FROM publications";
            statement.execute(authorsSql);
            statement.execute(publicationsSql);
            connection.commit();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws IOException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}

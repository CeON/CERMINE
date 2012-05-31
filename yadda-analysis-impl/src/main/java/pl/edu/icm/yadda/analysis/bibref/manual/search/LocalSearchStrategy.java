package pl.edu.icm.yadda.analysis.bibref.manual.search;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pl.edu.icm.yadda.analysis.bibref.manual.AuthorSimpleMetadataSupernormalized;
import pl.edu.icm.yadda.analysis.bibref.manual.MyIndexFields;
import pl.edu.icm.yadda.client.indexing.IndexFields;
import pl.edu.icm.yadda.service.search.searching.ResultField;
import pl.edu.icm.yadda.service.search.searching.SearchResult;
import pl.edu.icm.yadda.tools.bibref.model.AuthorSimpleMetadata;
import pl.edu.icm.yadda.tools.bibref.model.SimpleMetadata;

/**
 * Local database-based search strategy.
 * 
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 * 
 */
public class LocalSearchStrategy implements SearchStrategy {

    @Override
    public List<SearchResult> searchByAuthorJournalYear(SimpleMetadata metadata) {
        return search(metadata, true);
    }
    
    @Override
    public List<SearchResult> searchByAuthorYear(SimpleMetadata metadata) {
        return search(metadata, false);
    }
    
    private List<SearchResult> search(SimpleMetadata metadata, boolean filterJournal) {
        List<SearchResult> results = null;
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/nlm", "postgres", "postgres");

            PreparedStatement statement = preparePublicationStatement(connection, metadata, filterJournal);
            PreparedStatement authorStatement = prepareAuthorStatement(connection);

            results = queryPublications(statement, authorStatement);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return results != null ? results : new ArrayList<SearchResult>();
    }

    /**
     * @param publicationStatement
     * @param authorStatement
     * @return
     * @throws SQLException
     */
    private List<SearchResult> queryPublications(PreparedStatement publicationStatement, PreparedStatement authorStatement) throws SQLException {
        List<SearchResult> results = new ArrayList<SearchResult>();
        ResultSet rs = publicationStatement.executeQuery();
        while (rs.next()) {
            try {
                SearchResult result = new SearchResult(rs.getString("id"));
                List<ResultField> fields = new ArrayList<ResultField>();
                fields.add(new ResultField(IndexFields.F_BIBREF_SOURCE, new String[] { rs.getString("bibref_source") }, null));
                fields.add(new ResultField(IndexFields.F_JOURNAL_NAME, new String[] { rs.getString("journal_name") }, null));
                fields.add(new ResultField(IndexFields.F_JOURNAL_HASH, new String[] { rs.getString("journal_hash") }, null));
                fields.add(new ResultField(IndexFields.F_VOLUME, new String[] { rs.getString("volume") }, null));
                fields.add(new ResultField(IndexFields.F_ISSUE, new String[] { rs.getString("issue") }, null));
                fields.add(new ResultField(IndexFields.F_DATE_PUBLISHED_YEAR, new String[] { rs.getString("date_published_year") }, null));
                fields.add(new ResultField(IndexFields.F_DEF_NAME, new String[] { rs.getString("def_name") }, null));
                fields.add(new ResultField(IndexFields.F_BIBREF_POSITION, new String[] { rs.getString("bibref_position") }, null));

                authorStatement.setString(1, rs.getString("id"));
                ResultSet authorRs = authorStatement.executeQuery();
                List<String> surnames = new ArrayList<String>();
                List<String> lastSurnamesParts = new ArrayList<String>();
                List<String> normalized = new ArrayList<String>();
                List<String> supernormalized = new ArrayList<String>();
                while (authorRs.next()) {
                    surnames.add(authorRs.getString("author_coauthor_surname"));
                    lastSurnamesParts.add(authorRs.getString("author_coauthor_last_surname_part"));
                    normalized.add(authorRs.getString("author_coauthor_normalized"));
                    supernormalized.add(authorRs.getString("author_coauthor_supernormalized"));
                }
                fields.add(new ResultField(IndexFields.F_AUTHOR_COAUTHOR_SURNAME, surnames.toArray(new String[surnames.size()]), null));
                fields.add(new ResultField(MyIndexFields.F_AUTHOR_COAUTHOR_LAST_SURNAME_PART, lastSurnamesParts.toArray(new String[lastSurnamesParts.size()]), null));
                fields.add(new ResultField(IndexFields.F_AUTHOR_COAUTHOR_NORMALIZED, normalized.toArray(new String[normalized.size()]), null));
                fields.add(new ResultField(MyIndexFields.F_AUTHOR_COAUTHOR_SUPERNORMALIZED, supernormalized.toArray(new String[supernormalized.size()]), null));
                result.setFields(fields);
                results.add(result);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    /**
     * @param connection
     * @return
     * @throws SQLException
     */
    private PreparedStatement prepareAuthorStatement(Connection connection) throws SQLException {
        final String authorSql = "SELECT author_coauthor_surname, author_coauthor_normalized, author_coauthor_last_surname_part, author_coauthor_supernormalized" 
                + " FROM authors WHERE publication_id = ?";
        PreparedStatement authorStatement = connection.prepareStatement(authorSql);
        return authorStatement;
    }

    /**
     * @param metadata
     * @param connection
     * @param filterJournal
     * @param sql
     * @return
     * @throws SQLException
     */
    private PreparedStatement preparePublicationStatement(Connection connection, SimpleMetadata metadata, boolean filterJournal) throws SQLException {
        String sql = "SELECT id, bibref_source, journal_name, journal_hash, volume, issue, date_published_year, def_name, bibref_position"
                + " FROM publications WHERE date_published_year = ?";
        int counter = 0;
        Map<Integer, String> params = new HashMap<Integer, String>();
        params.put(++counter, metadata.getYear());
        if (filterJournal && metadata.getJournal() != null) {
            sql += " AND journal_hash = ?";
            params.put(++counter, metadata.getJournalHash());
        }
        for (AuthorSimpleMetadata authorInner : metadata.getAuthors()) {
            AuthorSimpleMetadataSupernormalized author = new AuthorSimpleMetadataSupernormalized(authorInner); 
            sql += " AND EXISTS(SELECT 1 FROM authors WHERE publication_id = publications.id AND author_coauthor_last_surname_part = ?)";
            params.put(++counter, author.getLastSurnamePart());
        }
        PreparedStatement statement = connection.prepareStatement(sql);
        for (Entry<Integer, String> e : params.entrySet()) {
            statement.setString(e.getKey(), e.getValue());
        }
        return statement;
    }
}

package pl.edu.icm.yadda.analysis.bibref.manual.search.localsearch;

import java.io.Closeable;
import java.sql.SQLException;

import pl.edu.icm.yadda.tools.bibref.model.DocSimpleMetadata;

public interface DatabaseWriter extends Closeable {

    public abstract void writeToDatabase(DocSimpleMetadata metadata) throws SQLException;

}
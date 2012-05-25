package pl.edu.icm.yadda.analysis.bibref.manual.search.localsearch;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import pl.edu.icm.yadda.analysis.bibref.manual.MetadataReader;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;
import pl.edu.icm.yadda.tools.bibref.model.DocSimpleMetadata;

/**
 * Used to populate local database used by local search.
 * 
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 * 
 */
public class DatabasePopulatorMain {

    public static final String EXT_SCHEMA_DMLE = "bwmeta1.id-class.dmle-id";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        if (args.length != 1) {
            System.out.println("Usage: <nlms-dir>");
            return;
        }
        MetadataReader metadataReader = new MetadataReader(EXT_SCHEMA_DMLE);
        DatabaseWriter databaseWriter = null;
        try {
            databaseWriter = new LocalDatabaseWriter();

            for (File f : new File(args[0]).listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return (pathname.getName().toLowerCase().endsWith("xml"));
                }
            })) {
                try {
                    DocSimpleMetadata meta = metadataReader.readFromNLMFile(f);
                    databaseWriter.writeToDatabase(meta);
                } catch (TransformationException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            try {
                if (databaseWriter != null) {
                    databaseWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

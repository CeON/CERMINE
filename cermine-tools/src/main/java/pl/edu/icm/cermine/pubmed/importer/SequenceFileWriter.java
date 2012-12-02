package pl.edu.icm.cermine.pubmed.importer;

import com.google.protobuf.ByteString;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.SequenceFile;
import pl.edu.icm.cermine.pubmed.importer.model.DocumentProtos;
import pl.edu.icm.cermine.pubmed.importer.model.DocumentProtos.Document;

public class SequenceFileWriter {

    private SequenceFileWriter() {
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.out.println("Usage: <in_dir> <out_file>");
            System.exit(1);
        }

        String inputDir = args[0];
        String outputSequenceFile = args[1];

        checkPaths(inputDir, outputSequenceFile);
        generateSequenceFile(inputDir, outputSequenceFile);

    }

    private static void checkPaths(String inputDir, String outputSequenceFile) throws IOException {
        File input = new File(inputDir);
        if (!input.exists()) {
            System.err.println("<Input dir> does not exist: " + inputDir);
            System.exit(1);
        }
        if (!input.isDirectory()) {
            System.err.println("<Input dir> is not a directory:" + inputDir);
            System.exit(1);
        }
        File outf = new File(outputSequenceFile);
        if (!outf.getParentFile().exists()) {
            outf.getParentFile().mkdirs();
        }

    }

    private static void generateSequenceFile(String inputDir, String outputSequenceFile) throws IOException {

        SequenceFile.Writer writer = createSequenceFileWriter(outputSequenceFile, byte[].class, byte[].class);
        //writer.

        PubmedCollectionIterator iter = new PubmedCollectionIterator(inputDir);
        for (PubmedEntry item : iter) {

            InputStream nlm_fis = new FileInputStream(item.getNlm());
            InputStream pdf_fis = new FileInputStream(item.getPdf());

            DocumentProtos.Document.Builder db = DocumentProtos.Document.newBuilder();
            db.setNlm(ByteString.copyFrom(IOUtils.toByteArray(nlm_fis)));
            db.setPdf(ByteString.copyFrom(IOUtils.toByteArray(pdf_fis)));
            db.setKey(item.getKey());
            Document d = db.build();

            byte[] rowBytes = item.getKey().getBytes();
            byte[] docBytes = d.toByteArray();

            try {
                BytesWritable rowKeyBytesWritable = new BytesWritable();
                BytesWritable documentBytesWritable = new BytesWritable();
                Document.Builder dw = Document.newBuilder();


                rowKeyBytesWritable.set(rowBytes, 0, rowBytes.length);
                documentBytesWritable.set(docBytes, 0, docBytes.length);
                writer.append(rowKeyBytesWritable, documentBytesWritable);
            } finally {
                writer.close();
            }
        }

    }

    private static SequenceFile.Writer createSequenceFileWriter(String uri, Class keyClass, Class valueClass) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(uri), conf);
        Path path = new Path(uri);

        SequenceFile.Writer writer = SequenceFile.createWriter(conf,
                SequenceFile.Writer.file(path),
                SequenceFile.Writer.keyClass(keyClass),
                SequenceFile.Writer.valueClass(valueClass));
        return writer;
    }
}
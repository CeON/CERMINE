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
//    	SequenceFile.Writer writer = createSequenceFileWriter(outputSequenceFile, byte[].class, byte[].class);
    	SequenceFile.Writer writer = createSequenceFileWriter(outputSequenceFile, BytesWritable.class, BytesWritable.class);
    	PubmedCollectionIterator iter = new PubmedCollectionIterator(inputDir);
    	try {
    		BytesWritable rowKeyBytesWritable = new BytesWritable();
    		BytesWritable documentBytesWritable = new BytesWritable();
    		Integer fileCounter = 0;
    		System.out.println(iter.size());
    		for(PubmedEntry item : iter) {
    			File nlm = item.getNlm();
    			File pdf = item.getPdf();
    			System.out.println(fileCounter + ": " + nlm.getName());

    			InputStream nlm_fis = null;
    			DocumentProtos.Document.Builder db = DocumentProtos.Document.newBuilder();
    			if(nlm != null) {
    				nlm_fis = new FileInputStream(item.getNlm());
    				db.setNlm(ByteString.copyFrom(IOUtils.toByteArray(nlm_fis)));
    			} else {
    				db.setPdf(ByteString.EMPTY);
    			}

    			InputStream pdf_fis = null;
    			if(pdf != null) {
    				pdf_fis = new FileInputStream(item.getPdf());
    				db.setPdf(ByteString.copyFrom(IOUtils.toByteArray(pdf_fis)));
    			} else {
    				db.setPdf(ByteString.EMPTY);
    			}

    			db.setKey(item.getKey());
    			Document d = db.build();

    			byte[] rowBytes = item.getKey().getBytes();
    			byte[] docBytes = d.toByteArray();

    			rowKeyBytesWritable.set(rowBytes, 0, rowBytes.length);
    			documentBytesWritable.set(docBytes, 0, docBytes.length);
    			writer.append(rowKeyBytesWritable, documentBytesWritable);
    			nlm_fis.close();
    			++fileCounter;
    		}
    	} finally {
    		writer.close();
    	}
    }

    private static <T1, T2> SequenceFile.Writer createSequenceFileWriter(String uri, Class<T1> keyClass, Class<T2> valueClass) throws IOException {
        Configuration conf = new Configuration();
        Path path = new Path(uri);

//        SequenceFile.Writer writer = SequenceFile.createWriter(conf,
//                SequenceFile.Writer.file(path),
//                SequenceFile.Writer.keyClass(keyClass),
//                SequenceFile.Writer.valueClass(valueClass));
        FileSystem fs = FileSystem.get(URI.create(uri), conf);
        SequenceFile.Writer writer = SequenceFile.createWriter(fs, conf, path, keyClass, valueClass);
        return writer;
    }
}
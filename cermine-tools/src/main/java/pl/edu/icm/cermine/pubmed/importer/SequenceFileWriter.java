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
import pl.edu.icm.cermine.pubmed.pig.PubmedCollectionIterator;
import pl.edu.icm.cermine.pubmed.pig.PubmedEntry;

public class SequenceFileWriter {

    public static void main(String[] args) throws IOException {

        if (args.length != 2 && args.length != 4) {
            System.out.println("Usage: <in_dir> <out_file> [-max X]");
            System.exit(1);
        }

        String inputDir = args[0];
        String outputSequenceFile = args[1];
        Integer maximumPairs = 0;
        if (args.length == 4) {
        	maximumPairs = Integer.valueOf(args[3]);
        }
        checkPaths(inputDir, outputSequenceFile);
        generateSequenceFile(inputDir, outputSequenceFile, maximumPairs);

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

    private static void generateSequenceFile(String inputDir, String outputSequenceFile, Integer maximumPairs) throws IOException {
    	SequenceFile.Writer sequenceFileWriter = createSequenceFileWriter(outputSequenceFile, BytesWritable.class, BytesWritable.class);
    	PubmedCollectionIterator iter = new PubmedCollectionIterator(inputDir);
    	Integer fileCounter = 0;
    	Integer filesAfterDump = 0;
    	System.out.println(iter.size());

    	try {
    		for(PubmedEntry item : iter) {
    			if(maximumPairs != 0 && maximumPairs == fileCounter) {
    				break;
    			}
    			DocumentProtos.InputDocument.Builder documentProtosBuilder = DocumentProtos.InputDocument.newBuilder();
    			BytesWritable rowKeyBytesWritable = new BytesWritable();
    			BytesWritable documentBytesWritable = new BytesWritable();

    			++filesAfterDump;
    			File nlm = item.getNlm();
    			File pdf = item.getPdf();
    			System.out.println(fileCounter + ": " + item.getKey());

    			documentProtosBuilder.setKey(item.getKey());

    			InputStream nlm_fis = null;
    			if(nlm != null) {
    				nlm_fis = new FileInputStream(item.getNlm());
    				documentProtosBuilder.setNlm(ByteString.copyFrom(IOUtils.toByteArray(nlm_fis)));
    			} else {
    				documentProtosBuilder.setNlm(ByteString.EMPTY);
    			}
    			nlm_fis.close();

    			InputStream pdf_fis = null;
    			if(pdf != null) {
    				pdf_fis = new FileInputStream(item.getPdf());
    				documentProtosBuilder.setPdf(ByteString.copyFrom(IOUtils.toByteArray(pdf_fis)));
    			} else {
    				documentProtosBuilder.setPdf(ByteString.EMPTY);
    			}
    			pdf_fis.close();

    			DocumentProtos.InputDocument d = documentProtosBuilder.build();

    			byte[] rowBytes = item.getKey().getBytes();
    			byte[] docBytes = d.toByteArray();
    			rowKeyBytesWritable.set(rowBytes, 0, rowBytes.length);
    			documentBytesWritable.set(docBytes, 0, docBytes.length);
    			sequenceFileWriter.append(rowKeyBytesWritable, documentBytesWritable);
    			if(filesAfterDump == 256) {
    				sequenceFileWriter.syncFs();
    				filesAfterDump = 0;
    			}
    			++fileCounter;
    		}
    	} finally {
    		sequenceFileWriter.close();
    	}
    }

    private static <T1, T2> SequenceFile.Writer createSequenceFileWriter(String uri, Class<T1> keyClass, Class<T2> valueClass) throws IOException {
        Configuration conf = new Configuration();
        Path path = new Path(uri);

        SequenceFile.Writer writer = SequenceFile.createWriter(conf,
                SequenceFile.Writer.file(path),
                SequenceFile.Writer.keyClass(keyClass),
                SequenceFile.Writer.valueClass(valueClass),
                SequenceFile.Writer.bufferSize(1<<25));
//        FileSystem fs = FileSystem.get(URI.create(uri), conf);
//        SequenceFile.Writer writer = SequenceFile.createWriter(fs, conf, path, keyClass, valueClass);
        return writer;
    }
}
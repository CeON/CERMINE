package pl.edu.icm.cermine.pubmed.pig;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.pig.StoreFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.*;
import java.util.ArrayList;
import org.apache.pig.impl.io.NullableText;
import org.apache.pig.impl.io.NullableTuple;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileAsBinaryOutputFormat;

import com.google.protobuf.ByteString;

import pl.edu.icm.cermine.pubmed.importer.model.DocumentProtos;

public class SequenceFileStoreFunc extends StoreFunc {
        protected RecordWriter<NullableTuple, NullableTuple> writer;

        @Override
        public OutputFormat getOutputFormat() throws IOException {
                SequenceFileOutputFormat out = new SequenceFileOutputFormat<NullableTuple,NullableTuple>();
                return out;
        }

        @Override
        public void setStoreLocation(String location, Job job) throws IOException {
                FileOutputFormat.setOutputPath(job, new Path(location));
        }

        @SuppressWarnings("unchecked")
        @Override
        public void prepareToWrite(@SuppressWarnings("rawtypes") RecordWriter writer) throws IOException {
                this.writer = writer;
        }

        @Override
        public void putNext(Tuple t) throws IOException {
        	System.out.println("next tuple=====================================");
        	
    		if(t.size() != 4) {
    			throw new ExecException("Output tuple has wrong size: is ");
    		}
    		String keyString = (String) t.get(0);
    		System.out.println("KEY STRING " + keyString);
    		DataByteArray nlmBytes = (DataByteArray) t.get(1);
    		DataByteArray pdfBytes = (DataByteArray) t.get(2);
    		String returnDoc = (String) t.get(3);
    		
    		if (keyString == null || nlmBytes == null || pdfBytes == null || returnDoc == null) {
    			throw new ExecException("Output tuple contains null");
    		}
    		
    		DocumentProtos.OutputDocument.Builder odb = DocumentProtos.OutputDocument.newBuilder();
    		odb.setKey(keyString);
    		odb.setNlm(ByteString.copyFrom(nlmBytes.get()));
    		odb.setPdf(ByteString.copyFrom(pdfBytes.get()));
    		odb.setXml(ByteString.copyFrom(returnDoc.getBytes()));
    		
    		DataByteArray keyBytesArray = new DataByteArray(keyString.getBytes());
     		DataByteArray valueBytesArray = new DataByteArray(odb.build().toByteArray());
        	ArrayList keyArrayList = new ArrayList();
        	keyArrayList.add(keyBytesArray);
        	NullableTuple key = new NullableTuple(TupleFactory.getInstance().newTuple(keyArrayList));
        	ArrayList valueArrayList = new ArrayList();
        	valueArrayList.add(valueBytesArray);
        	NullableTuple val = new NullableTuple(TupleFactory.getInstance().newTuple(valueArrayList));
     		
        	try {
        		System.out.println("writing tuple=====================================");
        		writer.write(key, val);
        	} catch (InterruptedException e) {
        		throw new IOException(e);
        	}
        }
}

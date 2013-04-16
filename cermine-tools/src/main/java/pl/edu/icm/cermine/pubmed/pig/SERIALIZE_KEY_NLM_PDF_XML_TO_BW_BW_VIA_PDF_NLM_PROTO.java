/*
 * (C) 2010-2012 ICM UW. All rights reserved.
 */
package pl.edu.icm.cermine.pubmed.pig;

import java.io.IOException;
import java.util.Arrays;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.DataByteArray;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.apache.pig.impl.logicalLayer.FrontendException;
import org.apache.pig.impl.logicalLayer.schema.Schema;

import pl.edu.icm.cermine.pubmed.importer.model.DocumentProtos.OutputDocument;

import com.google.protobuf.ByteString;

/**
 *
 * @author pdendek
 */
public class SERIALIZE_KEY_NLM_PDF_XML_TO_BW_BW_VIA_PDF_NLM_PROTO extends EvalFunc<Tuple> {

	@Override
	public Schema outputSchema(Schema p_input){
		try{
			return Schema.generateNestedSchema(DataType.TUPLE,
					DataType.BYTEARRAY,DataType.BYTEARRAY);
		}catch(FrontendException e){
			throw new IllegalStateException(e);
		}
	}
    @Override
    public Tuple exec(Tuple input) throws IOException {
        try {
        	String key = (String) input.get(0);
        	DataByteArray nlm = (DataByteArray) input.get(1);
        	DataByteArray pdf = (DataByteArray) input.get(2);
        	DataByteArray xml = (DataByteArray) input.get(3);
        	
        	OutputDocument.Builder odb = OutputDocument.newBuilder();
        	odb.setKey(key);
        	odb.setNlm(ByteString.copyFrom(nlm.get()));
        	odb.setPdf(ByteString.copyFrom(pdf.get()));
        	odb.setXml(ByteString.copyFrom(xml.get()));
        	
	        Object[] to = new Object[]{new DataByteArray(key.getBytes()),new DataByteArray(odb.build().toByteArray())};
	        
	        Tuple t = TupleFactory.getInstance().newTuple(Arrays.asList(to));
//	        System.out.println("doc serialized");
	        return t;
        } catch (Exception e) {
            // Throwing an exception will cause the task to fail.
            throw new IOException("Caught exception processing input row:\n"
            		+ StackTraceExtractor.getStackTrace(e));
        }
    }
}

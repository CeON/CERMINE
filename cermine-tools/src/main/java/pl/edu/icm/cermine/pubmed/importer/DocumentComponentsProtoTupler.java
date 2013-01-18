/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.icm.cermine.pubmed.importer;

import java.io.IOException;
import org.apache.pig.EvalFunc;
import org.apache.pig.data.DataByteArray;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import pl.edu.icm.cermine.pubmed.importer.model.DocumentProtos;
import pl.edu.icm.cermine.pubmed.importer.model.DocumentProtos.Document;

/**
 *
 */
public class DocumentComponentsProtoTupler extends EvalFunc<Tuple> {
    
    private String rowId = new String();
    private DataByteArray nlm = new DataByteArray();
    private DataByteArray pdf = new DataByteArray();
    private Tuple output = TupleFactory.getInstance().newTuple(3);

    @Override
    public Tuple exec(Tuple input) throws IOException {
        byte[] documentProto = (byte[]) input.get(0);
        Document document = DocumentProtos.Document.parseFrom(documentProto);
        
        rowId = document.getKey();
        nlm.set(document.getNlm().toByteArray());
        pdf.set(document.getPdf().toByteArray());
        
        output.set(0, rowId);
        output.set(1, nlm);
        output.set(2, pdf);
        
        return output;
    }

    @Override
    public Schema outputSchema(Schema input) {
        try {
            Schema tupleSchema = new Schema();
            tupleSchema.add(input.getField(0));
            tupleSchema.add(input.getField(1));
            tupleSchema.add(input.getField(2));
            return new Schema(new Schema.FieldSchema(getSchemaName(this.getClass().getName().toLowerCase(), input), tupleSchema, DataType.TUPLE));
        } catch (Exception e) {
            return null;
        }
    }
}
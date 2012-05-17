package pl.edu.icm.yadda.bwmeta.doc;

import de.schlichtherle.io.FileInputStream;


public class SchemaDocGenerator {
	public static final void main(String[] args) throws Exception {
		SchemaReader sr = new SchemaReader();
		ReferenceWriter rw = new ReferenceWriter();
		
		if (args.length < 2) {
		    System.out.println("Need two file names in arguments: input XSD and output PDF");
		    System.exit(1);
		}
		
		XsdSchema schema = sr.parseSchema(new FileInputStream(args[0]));
		rw.writePDF(schema, args[1]);
	}
}

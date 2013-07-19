package pl.edu.icm.cermine.pubmed.pig;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public final class StackTraceExtractor {
	public static String getStackTrace(Throwable in) {
	    final Writer outputWriter = new StringWriter();
	    final PrintWriter auxilWriter = new PrintWriter(outputWriter);
	    in.printStackTrace(auxilWriter);
	    return outputWriter.toString();
	  }
}
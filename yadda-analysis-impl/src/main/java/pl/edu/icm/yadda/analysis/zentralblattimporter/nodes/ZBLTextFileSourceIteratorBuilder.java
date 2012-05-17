package pl.edu.icm.yadda.analysis.zentralblattimporter.nodes;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.imports.zentralblatt.reading.*;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.iterator.IIdExtractor;
import pl.edu.icm.yadda.process.iterator.ISourceIterator;
import pl.edu.icm.yadda.process.iterator.ISourceIteratorBuilder;

/**
 * Iterates over stream of ZentralBlattMATH text file with records.
 * 
 * Adapts ZentralBlattTextIterator class for use of yadda processing services.
 * 
 * @author tkusm
 *
 */
public class ZBLTextFileSourceIteratorBuilder implements
		ISourceIteratorBuilder<ZentralBlattRecord> {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Stream with input data (ZBL records).
	 */
	private InputStream inputStream = null;
	/**
	 * Says what is parameter name that contains path to the input file.
	 * 
	 * AUX parameter name from ProcessContext.
	 */
	private String inFilePathParameterName = null;

	public ZBLTextFileSourceIteratorBuilder() {
		
	}
	
	public ZBLTextFileSourceIteratorBuilder(InputStream is) {
		this.inputStream = is;
	}
	
	public ZBLTextFileSourceIteratorBuilder(String inFilePathParameterName) {
		this.inFilePathParameterName = inFilePathParameterName;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	

	public void setInFilePathParameterName(String inFilePathParameterName) {
		this.inFilePathParameterName = inFilePathParameterName;
	}



	static class IZentralBlattRecordSourceIterator 
			extends ZentralBlattTextIterator 
			implements ISourceIterator<ZentralBlattRecord> {

		public IZentralBlattRecordSourceIterator(InputStream is) {
			super(is);
		}

		@Override
		public int getEstimatedSize() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clean() {
			/*try {
				this.input.close();
			} catch (IOException e) {
			} */
		}

	}


	@Override
	public ISourceIterator<ZentralBlattRecord> build(ProcessContext ctx)
			throws Exception {
		checkIfStreamInitialized(ctx);

		log.info("[build] inputStream={}",inputStream);
		return new IZentralBlattRecordSourceIterator(inputStream);
	}
	
	private void checkIfStreamInitialized(ProcessContext ctx)
			throws FileNotFoundException {
		if (inputStream == null && inFilePathParameterName!=null) {
			String inputFilePath = (String) ctx.getAuxParam(inFilePathParameterName);
			inputStream = new FileInputStream(inputFilePath);
		}
	}

	@Override
	public IIdExtractor<ZentralBlattRecord> getIdExtractor() {
		return new IIdExtractor<ZentralBlattRecord>() {

			@Override
			public String getId(ZentralBlattRecord element) {
				return element.getField(ZentralBlattRecord.ID_FIELD_NAME);
			}			
		};
	}

}

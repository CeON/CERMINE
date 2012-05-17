package pl.edu.icm.yadda.analysis.zentralblatteudmlmixer;

import java.io.*;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.icm.yadda.analysis.zentralblatteudmlmixer.auxil.MixRecord;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.iterator.IIdExtractor;
import pl.edu.icm.yadda.process.iterator.ISourceIterator;
import pl.edu.icm.yadda.process.iterator.ISourceIteratorBuilder;

/**
 * Iterates over stream of file containing mapping from ZblId to EuDMLId in each line.
 * 
 * @author pdendek
 *
 */
public class MixFileIteratorBuilder implements
		ISourceIteratorBuilder<MixRecord> {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	protected String AUX_INPUT_FILE_PATH = "inputFile";
	protected File file;

	public MixFileIteratorBuilder(){
		
	}
	
	public MixFileIteratorBuilder(File file){
		this.file=file;
	}
	
	static class IMixFileIterator 
	extends MixFileIterator 
	implements ISourceIterator<MixRecord> {
		File file = null;
		Integer estimatedSize = null;
		
		public IMixFileIterator(File file) throws IOException {
			super(new FileInputStream(file));
			this.file=file;
		}
	
		@Override
		public int getEstimatedSize(){
			
			if(estimatedSize!=null) return estimatedSize;
			else{
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					int i=0;
					for(;br.readLine()!=null;i++);
					br.close();
					estimatedSize=i;
					return estimatedSize;
				} catch (FileNotFoundException e) {
					return -1;
				} catch (IOException e) {
					return -1;
				}
			}
		}
	
		@Override
		public void clean() {
//			throw new NotImplementedException();
		}
	}

	@Override
	public ISourceIterator<MixRecord> build(ProcessContext ctx)
			throws Exception {
		String inputFilePath = null;
		if(file==null){
			inputFilePath = (String) ctx.getAuxParam(AUX_INPUT_FILE_PATH);
			file = new File(inputFilePath);
		}
		log.info("[build] file={}",file);
		return new IMixFileIterator(file);
	}

	@Override
	public IIdExtractor<MixRecord> getIdExtractor() {
		return new IIdExtractor<MixRecord>() {
			@Override
			public String getId(MixRecord mr) {
				return mr.get10DigitId()+" "+mr.getDotId();
			}			
		};
	}

	
}

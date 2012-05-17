package pl.edu.icm.yadda.analysis.zentralblattimporter.nodes;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.imports.zentralblatt.ZentralBlattToYModelConverter;
import pl.edu.icm.yadda.imports.zentralblatt.reading.ZentralBlattRecord;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * Adapts ZentralBlattToYModelConverter for use of yadda processing services.
 * 
 * @author tkusm
 *
 */
public class ZBLtoYProcessingNode implements  IProcessingNode<ZentralBlattRecord, YElement> {

	private ZentralBlattToYModelConverter converter = new ZentralBlattToYModelConverter();
	
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public YElement process(ZentralBlattRecord input, ProcessContext ctx)
			throws Exception {		
		synchronized (converter) {
			YElement output = converter.convert(input);
			log.info("[process] input=[{}] output=[{}]", input, output);
			return output;
		}
	}

}

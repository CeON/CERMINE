package pl.edu.icm.yadda.analysis.relations.auxil.trash;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IWriterNode;
import pl.edu.icm.yadda.service2.CatalogObject;
import pl.edu.icm.yadda.service2.CatalogParamConstants;
import pl.edu.icm.yadda.service2.editor.EditResult;
import pl.edu.icm.yadda.service2.editor.impl.EditorFacade;


/***
 * Slight modification of @author tkusm EditorWritingNode 
 * @author pdendek
 */
public class _4MassiveEditorWritingNode implements IWriterNode<List<CatalogObject<String>>>  {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	protected EditorFacade<String> editorFacade;
	
	@Override
	public void store(List<CatalogObject<String>> colist, ProcessContext ctx)
			throws Exception {
		synchronized (this) {
			for(CatalogObject<String> data : colist){
				log.info("[store] data={}", (data!=null)?data:"null");
				log.info("[store] editorFacade={}", (editorFacade!=null)?editorFacade:"null");
				
				//TODO czemu to MetadataIndexConstants.T_BWMETA =  CatalogParamConstants.TYPE_BWMETA1 ???
				EditResult result = editorFacade.save(data, new String[] { CatalogParamConstants.TYPE_BWMETA2 });			
				log.info("[store] result.getEditId()=[{}] result.getStatus()=[{}]", result.getEditId(), result.getStatus());
			} 			
		}
	}
	

	public void setEditorFacade(EditorFacade<String> editorFacade) {
		this.editorFacade = editorFacade;
	}

}



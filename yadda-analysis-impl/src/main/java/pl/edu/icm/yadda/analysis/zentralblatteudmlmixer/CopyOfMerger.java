package pl.edu.icm.yadda.analysis.zentralblatteudmlmixer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import pl.edu.icm.yadda.analysis.zentralblatteudmlmixer.auxil.MixRecord;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.serialization.BwmetaReader;
import pl.edu.icm.yadda.common.YaddaException;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IWriterNode;
import pl.edu.icm.yadda.service2.CatalogObject;
import pl.edu.icm.yadda.service2.CatalogObjectMeta;
import pl.edu.icm.yadda.service2.YaddaObjectID;
import pl.edu.icm.yadda.service2.catalog.CatalogException;
import pl.edu.icm.yadda.service2.catalog.CountingIterator;
import pl.edu.icm.yadda.service2.catalog.impl.CatalogFacade;
import pl.edu.icm.yadda.service2.editor.impl.EditorFacade;
import pl.edu.icm.yadda.tools.relations.Statements;

/**
 * 
 * @author pdendek
 *
 */
public class CopyOfMerger implements IWriterNode<MixRecord>  {
	
	
	CountingIterator<CatalogObjectMeta> iterator;
	EditorFacade<String> editorFacade;
	CatalogFacade<String> catalogFacade;
	protected BwmetaReader bwmetaReader;
	
	
	public EditorFacade<String> getEditorFacade() {
		return editorFacade;
	}

	public void setEditorFacade(EditorFacade<String> editorFacade) {
		this.editorFacade = editorFacade;
	}

	public CatalogFacade<String> getCatalogFacade() {
		return catalogFacade;
	}

	public void setCatalogFacade(CatalogFacade<String> catalogFacade) {
		this.catalogFacade = catalogFacade;
	}

	@Override
	public void store(MixRecord data, ProcessContext ctx)
			throws Exception {
		String normalId = data.getDotId();
		String extId = data.get10DigitId();
		
		CatalogObject normalCO = catalogFacade.getObject(new YaddaObjectID("urn:zbl:"+normalId));
		CatalogObject extCO = catalogFacade.getObject(new YaddaObjectID());
		

		
		CountingIterator<CatalogObjectMeta> iterator = null;
        try {
            iterator = catalogFacade.iterateObjects(new String[]{}, null, null, null, false);
        } catch (CatalogException e) {
            throw new YaddaException("Cannot iterate objects", e);
        }
        System.out.println("Iterator acquired");
//        Iterator<String> iter=new CatalogStringIterator(iterator, catalogFacade);
//        transfer(iter);
	}

	public void transfer(Iterator<String> iter) throws YaddaException {

        while (iter.hasNext()) {
            String data = iter.next();
             Object t = bwmetaReader.read(data, null);
            List a;
            if (t instanceof List) {
                a = (List) t;
            } else {
                a = new ArrayList();
                a.add(t);
            }
            for (Object tmp : a) {
                List<Statements> statements = Collections.emptyList();
                if (tmp instanceof YElement) {
                    YElement ye = (YElement) tmp;
                    ye.getAttributes(YConstants.EXT_SCHEME_ZBL);
                } 
            }
        }

    }
	
	
	
}

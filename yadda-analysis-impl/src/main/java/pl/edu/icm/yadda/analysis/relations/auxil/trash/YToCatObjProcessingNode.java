package pl.edu.icm.yadda.analysis.relations.auxil.trash;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.bwmeta.model.YAttribute;
import pl.edu.icm.yadda.bwmeta.model.YContentEntry;
import pl.edu.icm.yadda.bwmeta.model.YDescription;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YExportable;
import pl.edu.icm.yadda.bwmeta.model.YName;
import pl.edu.icm.yadda.bwmeta.model.YRelation;
import pl.edu.icm.yadda.bwmeta.model.YRichText;
import pl.edu.icm.yadda.bwmeta.model.YRichText.Leaf;
import pl.edu.icm.yadda.bwmeta.model.YRichText.Node;
import pl.edu.icm.yadda.bwmeta.model.YRichText.Part;
import pl.edu.icm.yadda.bwmeta.transformers.YToBwmeta2_0Transformer;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;
import pl.edu.icm.yadda.service2.CatalogObject;
import pl.edu.icm.yadda.service2.CatalogObjectPart;
import pl.edu.icm.yadda.service2.CatalogParamConstants;
import pl.edu.icm.yadda.service2.YaddaObjectID;

/**
 * Slight modification of @author tkusm YModelToCatalogObjectProcessingNode
 * @author pdendek
 *
 */
public class YToCatObjProcessingNode implements IProcessingNode<List<YElement>, List<CatalogObject<String>>> {

	
	static LinkedList<String> ll = new LinkedList<String>();
	static{
		ll.add("reference-text");
		ll.add("reference-to");		
	}
	
	
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public List<CatalogObject<String>> process(List<YElement> yelist, 
			ProcessContext ctx) throws TransformationException{
		synchronized (this) {
			List<CatalogObject<String>> col = new LinkedList<CatalogObject<String>>();
			int i = 0;
			for(YElement yelement : yelist){
				i++;
				
					log.info("[process] yelement={}", yelement);
					
					String bwmetaStr;
					bwmetaStr = yElementToBwMetaString(yelement);
					log.info("[process] bwmetaStr={}", bwmetaStr);
					
					YaddaObjectID id = new YaddaObjectID(yelement.getId());
					CatalogObject<String> co = new  CatalogObject<String>(id);
					CatalogObjectPart<String> part = new CatalogObjectPart<String>(CatalogParamConstants.TYPE_BWMETA2, bwmetaStr);
					co.addPart(part);			
					log.info("[process] co={}", co);
					col.add(co);
			}
			return col;
		}
	}

	/**
	 * Converts YElement to BwMeta stored as a string.
	 * 
	 * @param yelement
	 * @return
	 * @throws TransformationException
	 */
	private static String yElementToBwMetaString(YElement yelement) throws TransformationException{
		YToBwmeta2_0Transformer transformer = new YToBwmeta2_0Transformer();
		List<YExportable> yelements = new LinkedList<YExportable>();
		
		boolean print = false;
		
//		System.out.println("-----------OLD--------------");
//		for(YRelation yr : yelement.getRelations()) System.out.println(yr);
		
		if(print) System.out.println("-----------NEW1--------------");
		
		yelement = correctNamesByScRemoval(yelement);
		
		if(print) for(YRelation yr : yelement.getRelations()) System.out.println(yr);
		if(print) System.out.println("-----------NEW2--------------");
		
		yelement = correctDescriptionsByScRemoval(yelement);
		
		if(print) for(YRelation yr : yelement.getRelations()) System.out.println(yr);
		if(print) System.out.println("-----------NEW3--------------");
		
		yelement = correctContentByPhantomRemoval(yelement);
		
		if(print) for(YRelation yr : yelement.getRelations()) System.out.println(yr);
		if(print) System.out.println("-----------NEW4--------------");
		
		yelement = putNameLeafsIntoNodes(yelement);
		
		if(print) for(YRelation yr : yelement.getRelations()) System.out.println(yr);
//		System.out.println("-----------NEW5--------------");
		
		yelement = putNameNodesIntoLeafs(yelement);
		
//		for(YRelation yr : yelement.getRelations()) System.out.println(yr);
//		System.out.println("-----------NEW6--------------");
//		yelement = correctRelations(yelement);
		yelement = putRelationReferenceToLeafsIntoNodes(yelement);
//		yelement = correctContentByPhantomRemoval(yelement);
//		for(YRelation yr : yelement.getRelations()) System.out.println(yr);
		yelements.add(yelement);
		String bwmetaStr = null;
		bwmetaStr = transformer.write(yelements);
		return bwmetaStr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static YElement correctContentByPhantomRemoval(YElement yelement) {
		LinkedList<YContentEntry> ycel = new LinkedList<YContentEntry>();
		int i =0;
		for(YContentEntry yce : yelement.getContents()){
			i++;
//			if(i==6){
//				System.out.println();
//			}
			LinkedList<YName> ynl = new LinkedList<YName>();
			for(YName yn : (List<YName>)(yce.getNames())){
				List<Part> lp = putNameNodesIntoLeafs(yn.getRichText().toParts());
				if(lp.size()>0){
					yn.setText(new YRichText(lp));
					ynl.add(yn);
				}
			}
			if(ynl.size()>0){
				yce.setNames(ynl);
				ycel.add(yce);
			}
		}
		yelement.setContents(ycel);
		return yelement;
	}

	private static YElement putNameNodesIntoLeafs(YElement yelement) {
		for(YName yn : yelement.getNames()){
			YRichText yrt = new YRichText(putNameNodesIntoLeafs(yn.getRichText().toParts()));
			yn.setText(yrt);
		}
		return yelement;
	}

	
	private static List<Part> putNameNodesIntoLeafs(List<Part> partlist) {
		LinkedList<Part> newpl = new LinkedList<Part>();			
		for(Part part : partlist){
			if(part instanceof Leaf){
//				if(((Leaf) part).getText()==null || ((Leaf) part).getText().matches("[\\s]+"));
//				else 
					newpl.add(part);
			}else if(part instanceof Node){
//				((Node) part).setParts(extractLeafs(((Node) part).getParts()));
//				newpl.add(part);
				newpl.addAll(extractLeafs(((Node) part).getParts()));
			}
		}
		return newpl;
	}
	
	private static List<Part> extractLeafs(List<Part> partlist) {
		LinkedList<Part> newpl = new LinkedList<Part>();			
		for(Part part : partlist){
			if(part instanceof Leaf){
//				if(((Leaf) part).getText()==null || ((Leaf) part).getText().matches("[\\s]+"));
//				else 
					newpl.add(part);
			}else if(part instanceof Node){
				newpl.addAll(extractLeafs(((Node) part).getParts()));
			}
		}
		return newpl;
	}
	
	private static YElement putNameLeafsIntoNodes(YElement yelement) {
		for(YName yn : yelement.getNames()){
			YRichText yrt = new YRichText(putLeafsIntoNodes(yn.getRichText().toParts()));
			yn.setText(yrt);
		}
		return yelement;
	}

	private static YElement putRelationReferenceToLeafsIntoNodes(YElement yelement) {
		for(YRelation yr : yelement.getRelations()){
//			LinkedList<YAttribute> lya = new LinkedList<YAttribute>();
			for(YAttribute ya : yr.getAttributes()){
				if(! ll.contains(ya.getKey()))continue;
				removeGivenTag("sc", ya.getRichValue().toParts());
				ya.setValue(new YRichText(extractLeafs(removeNodeWithGivenTag("ext-link", ya.getRichValue().toParts()))));
//				lya.add(ya);
			}
//			if(lya.size()>0)yr.setAttributes(lya);
		}
		return yelement;
	}
	
	
	private static List<Part> removeNodeWithGivenTag(String givenTag, List<Part> parts) {
		if(givenTag==null || givenTag.matches("[\\s]+")) return parts; 
		LinkedList<Part> partlist = new LinkedList<Part>();
		for(Part part : parts){
			if(part instanceof Node){
				Node node = (Node) part;
				if(!givenTag.equals(node.getTag())){
					partlist.add(part);
					removeNodeWithGivenTag(givenTag, node.getParts());
				}
			}else{
				partlist.add(part);
			}
		}
		return partlist;
	}

	private static List<Part> putLeafsIntoNodes(List<Part> partlist) {
		LinkedList<Part> newpl = new LinkedList<Part>();			
		for(Part part : partlist){
			if(part instanceof Leaf){
//				if(((Leaf) part).getText()==null || ((Leaf) part).getText().matches("[\\s]+"));
//				else{
					Node n = new Node();
					n.addPart(part);
					newpl.add(n);
//				}
			}else if(part instanceof Node){
				((Node) part).setParts(putLeafsIntoNodes(((Node) part).getParts()));				
				newpl.add(part);
			}
		}
		return newpl;
	}
	
//	private static YElement correctNamesByTrimming(YElement yelement) {
//		for(YName yn : yelement.getNames()){
//			YRichText yrt = new YRichText(correctNamesByTrimming(yn.getRichText().toParts()));
//			yn.setText(yrt);
//		}
//		return yelement;
//	}
//
//	private static List<Part> correctNamesByTrimming(List<Part> partlist) {
//		LinkedList<Part> newpl = new LinkedList<Part>();			
//		for(Part part : partlist){
//			if(part instanceof Leaf){
//				if(((Leaf) part).getText()==null || ((Leaf) part).getText().matches("[\\s]+"));
//				else newpl.add(part);
//			}else if(part instanceof Node){
//				((Node) part).setParts(correctNamesByTrimming(((Node)part).getParts()));
//				newpl.add(part);
//			}
//		}
//		return newpl;
//	}
	
	private static YElement correctDescriptionsByScRemoval(YElement yelement) {
		for(YDescription yd : yelement.getDescriptions()){
			removeGivenTag("sc", yd.getRichText().toParts());
		}
		return yelement;
	}
	
	private static YElement correctNamesByScRemoval(YElement yelement) {
		for(YName yn : yelement.getNames()){
			removeGivenTag("sc", yn.getRichText().toParts());
		}
		return yelement;
	}

	private static void removeGivenTag(String givenTag, List<Part> partList) {
//		if(givenTag==null || givenTag.matches("[\\s]+")) return; 
		for(Part part : partList){
			if(part instanceof Node){
				Node node = (Node) part;
				if(givenTag.equals(node.getTag())){
					node.setTag("");
				}
				removeGivenTag(givenTag, node.getParts());
			}
		}
	}
	
	private static YElement correctRelations(YElement yelement) {
		LinkedList<YRelation> yrl = new LinkedList<YRelation>();
		for(YRelation yr : yelement.getRelations()) yrl.add(transformRelation(yr));
		yelement.setRelations(yrl);
		
		
		return yelement;
	}

	private static YRelation transformRelation(YRelation yr) {
		LinkedList<YAttribute> yal = new LinkedList<YAttribute>(); 
		for(YAttribute ya : yr.getAttributes()) yal.add(extractOneReferenceTextAttributeScNode(ya));
		yr.setAttributes(yal);
		return yr;
	}

	private static YAttribute extractOneReferenceTextAttributeScNode(YAttribute ya) {
		if(! ll.contains(ya.getKey())) return ya;
		for(Part p:ya.getRichValue().toParts()){
			if(p instanceof Node){
				if("sc".equals(((Node)p).getTag()))
					return new YAttribute(ya.getKey(),new YRichText(((Node)p).getParts().get(0).toString()));
			}else if(p instanceof Leaf)
//			if(p.toPlainText()!=null && !p.toPlainText().matches("[\\s]+"))
					return new YAttribute(ya.getKey(),new YRichText(((Leaf)p).toPlainText()));
		}
			
		return ya;
	}
}

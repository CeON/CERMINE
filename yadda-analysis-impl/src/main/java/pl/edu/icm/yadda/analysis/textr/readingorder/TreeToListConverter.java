package pl.edu.icm.yadda.analysis.textr.readingorder;

import java.util.ArrayList;
import java.util.List;

import pl.edu.icm.yadda.analysis.textr.model.BxZone;

class TreeToListConverter {
	public List<BxZone> convertToList(BxZoneGroup obj) {
		List<BxZone> ret = new ArrayList<BxZone>();
		if (obj.getLeftChild() instanceof BxZone) {
			BxZone zone = (BxZone) obj.getLeftChild();
			ret.add(zone);
		} else { // obj.getLeftChild() instanceof BxZoneGroup
			ret.addAll(convertToList((BxZoneGroup)obj.getLeftChild()));
		}

		if (obj.getRightChild() instanceof BxZone) {
			BxZone zone = (BxZone) obj.getRightChild();
			ret.add(zone);
		} else { // obj.getRightChild() instanceof BxZoneGroup
			ret.addAll(convertToList((BxZoneGroup)obj.getRightChild()));
		}
		return ret;
	}

}

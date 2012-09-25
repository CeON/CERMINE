package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.tools;

import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;

public class LabelPair {
	public BxZoneLabel l1, l2;
	public LabelPair(BxZoneLabel l1, BxZoneLabel l2) {
		this.l1 = l1;
		this.l2 = l2;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LabelPair other = (LabelPair) obj;
		if (l1 != other.l1)
			return false;
		if (l2 != other.l2)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((l1 == null) ? 0 : l1.hashCode());
		result = prime * result + ((l2 == null) ? 0 : l2.hashCode());
		return result;
	}
	@Override
	public String toString() {
		return "("+ l1 + ", " + l2 + ")";
	}
}
package pl.edu.icm.cermine.affparse.model;

public enum AffiliationLabel implements Label {
	INSTITUTION ("institution"),
	ADDRESS ("addr-line"),
	COUNTRY ("country");
	
	private final String tag;
	
	private AffiliationLabel(String tag) {
		this.tag = tag;
	}
	
	@Override
	public String getTag() {
		return tag;
	}
}

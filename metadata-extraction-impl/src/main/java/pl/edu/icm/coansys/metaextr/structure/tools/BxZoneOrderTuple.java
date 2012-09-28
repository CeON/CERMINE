package pl.edu.icm.coansys.metaextr.structure.tools;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;

class BxZoneOrderTuple {
	private BxZone zone;
	private Integer order;

	public BxZoneOrderTuple(BxZone zone, Integer order) {
		this.zone = zone;
		this.order = order;
	}
	
	public BxZone getZone() {
		return zone;
	}
	public void setZone(BxZone zone) {
		this.zone = zone;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	
}

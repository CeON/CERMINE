package pl.edu.icm.cermine.structure.tools;
import pl.edu.icm.cermine.structure.model.BxZone;

public class BxZoneOrderTuple {
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

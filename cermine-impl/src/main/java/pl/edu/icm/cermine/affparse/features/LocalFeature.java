package pl.edu.icm.cermine.affparse.features;

public abstract class LocalFeature {
	
	// TODO find a way of whether a given feature was used during the model training phase.
	// This could be a text file along the serialized CRF model.
	
	public abstract String computeFeature(String text);
}

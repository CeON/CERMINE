package pl.edu.icm.cermine.parsing.features;

public abstract class LocalFeature {
	
	// TODO 
	// Find a way of indicating whether a given feature was used during the model training phase.
	// This could be a text file along the serialized CRF model.
	
	public abstract String computeFeature(String text);
}

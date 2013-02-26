package pl.edu.icm.cermine.evaluation.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;

public class PenaltyCalculator {
	private List<TrainingSample<BxZoneLabel>> samples;
	private List<BxZoneLabel> classes = null;
	public PenaltyCalculator(List<TrainingSample<BxZoneLabel>> samples) {
		this.samples = samples;
	}
	
	public Double getPenaltyWeigth(BxZoneLabel label) {
		Integer allSamples = samples.size();
		Integer thisSamples = 0;
		for(TrainingSample<BxZoneLabel> sample: samples) {
			if(sample.getLabel() == label) {
				++thisSamples;
			}
		}
		return (double)allSamples / thisSamples;
	}
	
	public List<BxZoneLabel> getClasses() {
		if(classes == null) {
			classes = new ArrayList<BxZoneLabel>();
			for(TrainingSample<BxZoneLabel> sample: samples) {
				if(!classes.contains(sample.getLabel())) {
					classes.add(sample.getLabel());
				}
			}
			Collections.sort(classes);
			return classes;
		} else {
			return classes;
		}
	}
	
}

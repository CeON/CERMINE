package pl.edu.icm.yadda.analysis.relations;

public interface DisambiguationInterpreter {

	public double interpretResult(Disambiguator disambiguator,
			double result);
	public String id();
}

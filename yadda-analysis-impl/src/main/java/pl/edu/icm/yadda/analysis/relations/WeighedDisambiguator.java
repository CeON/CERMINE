package pl.edu.icm.yadda.analysis.relations;

/**
 * A {@link Disambiguator} with a weight.
 * Used in configuring {@link PersonDirectoryCreator}.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public class WeighedDisambiguator {
    private final double weight;
    private final Disambiguator disambiguator;

    public WeighedDisambiguator(double weight, Disambiguator disambiguator) {
        this.weight = weight;
        this.disambiguator = disambiguator;
    }

    public double getWeight() {
        return weight;
    }

    public Disambiguator getDisambiguator() {
        return disambiguator;
    }
}

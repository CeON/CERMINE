package pl.edu.icm.yadda.analysis.jrlsimilarity.common;

/**
 * Encapsulator-class for a value of similarity between two journals.
 * The value is supposed to be double type.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class DoubleJournalSimilarity implements JournalSimilarity<Double> {
	
	
	private Double value;

	private JournalId journalid1;
	
	
	private JournalId journalid2;
	
	@Override
	public Double getValue() {
		return this.value;
	}

	@Override
	public void setValue(Double value) {
		this.value = value;
		
	}

	@Override
	public JournalId getJournalId1() {
		return this.journalid1;
	}

	@Override
	public JournalId getJournalId2() {
		return this.journalid2;
	}

	@Override
	public void setJournalId1(JournalId journalId1) {
		this.journalid1 = journalId1;
		
	}

	@Override
	public void setJournalId2(JournalId journalId2) {
		this.journalid2 = journalId2;		
	}

}

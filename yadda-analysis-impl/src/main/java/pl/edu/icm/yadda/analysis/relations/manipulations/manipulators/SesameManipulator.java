package pl.edu.icm.yadda.analysis.relations.manipulations.manipulators;

import org.openrdf.repository.Repository;

import pl.edu.icm.yadda.analysis.relations.manipulations.operations.Operation;


public class SesameManipulator extends Manipulator {

	public SesameManipulator(Object repository, Operation operation) {
		super(repository, operation);
		if(repository==null) return;
		if(!(repository instanceof Repository)) throw new ClassCastException();
	}

	protected Repository getRepository() {
		return (Repository) repository;
	}

	public void setRepository(Object repository) {
		this.repository = (Repository) repository;
	}

}

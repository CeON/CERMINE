package pl.edu.icm.yadda.analysis.relations.manipulations.manipulators;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.relations.manipulations.operations.Operation;

public class Manipulator {
	
	private static final Logger log = LoggerFactory.getLogger(Manipulator.class);
	
	protected Object repository;
	protected Operation operation;
	
	public Manipulator(Object repository, Operation operation){
		this.setRepository(repository);
		this.setOperation(operation);
	}

	public Manipulator(Operation operation){
		this.setRepository(null);
		this.setOperation(operation);
	}
	
	protected Object getRepository() {
		return repository;
	}

	protected void setRepository(Object repository) {
		this.repository = repository;
	}

	protected Operation getOperation() {
		return operation;
	}

	protected void setOperation(Operation operation) {
		this.operation = operation;
	}

	public Object execute(Map<String,Object> operationParam){
		return operation.execute(repository, operationParam);
	}
}

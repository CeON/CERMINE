package pl.edu.icm.yadda.analysis.relations.manipulations.operations;

import java.util.Map;

public interface Operation {
	public Object execute(Object repository, Map<String,Object> operationParam);
}

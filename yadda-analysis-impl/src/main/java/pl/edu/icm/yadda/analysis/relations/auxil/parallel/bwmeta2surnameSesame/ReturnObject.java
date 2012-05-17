package pl.edu.icm.yadda.analysis.relations.auxil.parallel.bwmeta2surnameSesame;

import java.util.List;

public class ReturnObject {
	public List getStatements() {
		return statements;
	}
	public void setStatements(List list) {
		this.statements = list;
	}
	public List getSurnames() {
		return surnames;
	}
	public void setSurnames(List list) {
		this.surnames = list;
	}
	List statements = null;
	List surnames = null;
}

package pl.edu.icm.yadda.analysis.relations.auxil;

import org.openrdf.query.TupleQuery;

public class ResultEntity{
	public String name;
	TupleQuery querry;
	public int occurence;
	
	public String getName() {
		return name;
	}
	public ResultEntity setName(String name) {
		this.name = name;
		return this;
	}
	public TupleQuery getQuerry() {
		return querry;
	}
	public ResultEntity setQuerry(TupleQuery querry) {
		this.querry = querry;
		return this;
	}
	public int getOccurence() {
		return occurence;
	}
	public ResultEntity setOccurence(int occurence) {
		this.occurence = occurence;
		return this;
	}
	
}

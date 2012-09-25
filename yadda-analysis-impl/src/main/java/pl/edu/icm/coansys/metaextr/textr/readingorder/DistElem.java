package pl.edu.icm.coansys.metaextr.textr.readingorder;

/** Tuple containing magic value c, object1, object2 and distance between them.
*
* @author Pawel Szostek (p.szostek@icm.edu.pl)
* @date 05.2012
* 
*/

class DistElem<E> implements Comparable<DistElem<E> >{
	boolean c;
	double dist;
	E obj1;
	E obj2;

	public boolean isC() {
		return c;
	}

	public void setC(boolean c) {
		this.c = c;
	}

	public double getDist() {
		return dist;
	}

	public void setDist(double dist) {
		this.dist = dist;
	}

	public E getObj1() {
		return obj1;
	}

	public void setObj1(E obj1) {
		this.obj1 = obj1;
	}

	public E getObj2() {
		return obj2;
	}

	public void setObj2(E obj2) {
		this.obj2 = obj2;
	}

	public DistElem(boolean c, double dist, E obj1, E obj2) {
		this.c = c; this.dist = dist; this.obj1 = obj1; this.obj2 = obj2;
	}
	
	@Override
	public int compareTo(DistElem<E> compareObject) {
		if(c == compareObject.c) {
			 return compareDouble(dist, compareObject.dist);
		} else {
			if(c == true)
				return -1;
			else //c == false
				return 1;
		}
	}
	
	private int compareDouble(Double d1, Double d2) {
		Double eps = new Double(1E-6);
		if(d1 > d2)
			return 1;
		else if(Math.abs(d1-d2) < eps)
				return 0;
		else 
			return -1;
	}
}
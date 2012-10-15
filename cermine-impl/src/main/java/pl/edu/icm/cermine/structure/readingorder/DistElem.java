package pl.edu.icm.cermine.structure.readingorder;

/** Tuple containing magic value c, object1, object2 and distance between them.
*
* @author Pawel Szostek (p.szostek@icm.edu.pl)
* @date 05.2012
* 
*/

public class DistElem<E> implements Comparable<DistElem<E> >{

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (c ? 1231 : 1237);
		long temp;
		temp = Double.doubleToLongBits(dist);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((obj1 == null) ? 0 : obj1.hashCode());
		result = prime * result + ((obj2 == null) ? 0 : obj2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DistElem other = (DistElem) obj;
		if (c != other.c) {
			return false;
		}
		if (Double.doubleToLongBits(dist) != Double
				.doubleToLongBits(other.dist)) {
			return false;
		}
		if (obj1 == null) {
			if (other.obj1 != null) {
				return false;
			}
		} else if (!obj1.equals(other.obj1)) {
			return false;
		}
		if (obj2 == null) {
			if (other.obj2 != null) {
				return false;
			}
		} else if (!obj2.equals(other.obj2)) {
			return false;
		}
		return true;
	}

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
			if(c == true) {
				return -1;
			} else { //c == false
				return 1;
			}
		}
	}
	
	private int compareDouble(Double d1, Double d2) {
		Double eps = new Double(1E-6);
		if(d1 > d2) {
			return 1;
		} else if(Math.abs(d1-d2) < eps) {
				return 0;
		} else {
			return -1;
		}
	}
}
package pl.edu.icm.yadda.analysis.datastructures;

import java.util.Set;

/**
 * Interface that represents simple matrix.
 * 
 * @author tkusm
 * 
 * @param <ROWT>
 *            Row key type
 * @param <COLT>
 *            Column key type
 * @param <VALT>
 *            Value type
 */
public interface AbstractMatrix<ROWT, COLT, VALT> {

	public static class PositionNotFound extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		protected String message = null;
		
		public PositionNotFound() {		  
		}
		
		public PositionNotFound(String msg) {
		    super(msg);
        }
		
		@Override
		public String toString(){
		    return message;
		}
	}

	/**
	 * @return value at row x col position.
	 */
	public VALT get(ROWT row, COLT col, VALT defaultValue);

	/**
	 * @return value at row x col position or throws PositionNotFound exception.
	 */
	public VALT get(ROWT row, COLT col);

	/**
	 * Sets value at row x col position.
	 */
	public void set(ROWT row, COLT col, VALT value);
	
	/** 
	 * @return newly created set that contains all the row keys.
	 */
	public Set<ROWT> getRows();
	
	/** 
     * @return newly created set that contains all the row keys for a given column.
     */
    public Set<ROWT> getRows(COLT col);
	
	 /** 
     * @return newly created set that contains all the columns keys.
     */
    public Set<COLT> getCols();
    
   /** 
    * @return newly created set that contains all the columns keys for a given row.
    */
   public Set<COLT> getCols(ROWT row);
   
   /**
    * 
    * @return what is total number of unique rows.
    */
   public int getNumRows();
   
   /**
    * 
    * @return what is total number of unique columns.
    */
   public int getNumCols();
   
}

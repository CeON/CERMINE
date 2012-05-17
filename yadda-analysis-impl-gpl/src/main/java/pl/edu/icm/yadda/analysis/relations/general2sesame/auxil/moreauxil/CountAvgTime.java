//package pl.edu.icm.yadda.analysis.relations.general2sesame.auxil.moreauxil;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.sql.Time;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.LinkedList;
//import java.util.Map;
//
//public class CountAvgTime {
//	
//	class TimeInfo implements Comparable{
//		Long sumOfTime;
//		Integer numberOfOccurences;
//		final Integer size;
//		
//		public TimeInfo(Long t, Integer noo, Integer s){
//			this.sumOfTime=t;
//			this.numberOfOccurences=noo;
//			this.size=s;
//		}
//		
//		@Override
//		public boolean equals(Object obj){
//			if(obj==null)return false;
//			if(obj instanceof Integer){
//				if(this.size.equals(obj)) return true;
//				else return false;
//			}else if(obj instanceof TimeInfo){
//				TimeInfo ti = (TimeInfo) obj;
//				if(this.size.equals(ti.size)) return true;
//			}
//			return false;
//		}
//		
//		@Override
//		public int hashCode() 
//		{
//		  int hash = 7;
//		  hash = 31 * hash + (null == this.size ? 0 : this.size);
//		  return hash;
//		}
//
//		@Override
//		public int compareTo(Object o) {
//			TimeInfo ti = (TimeInfo) o;
//			return this.size - ti.size;
//		}
//	}
//	
//	public static void main(String[] args) throws IOException{
//		
//		File f = new File("/home/pdendek/AND_FLOW_CIEMNIAK/PIERWSZE_108_SHARDOW/czas.txt");
//		BufferedReader br = new BufferedReader(new FileReader(f));
//		
//		HashMap<Integer,Integer> hm = new HashMap<Integer,Integer>();
//		
//		LinkedList<TimeInfo> ll = new LinkedList<TimeInfo>();
//		
//		String s = null;
//		while((s=br.readLine())!=null){
//			String[] part = s.split("\t");
//			String[] time = part[1].split(":");
//			
//			Long time = Time.UTC(0, 0, 0, Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
//			
//			int size = Integer.parseInt(part[0]);
//			int index;
//			if((index=ll.indexOf(size))!=-1){
//				TimeInfo ti = ll.get(index);
//				ti.sumOfTime.UTC(year, month, date, hrs, min, sec)  +=t;
//				
//			}
//			
//			TimeInfo ti = new TimeInfo(t,1,);
//			
//			
//			
//			try{
//				Integer size = Integer.parseInt(s);
//				Integer occNumber = hm.get(size);
//				if(occNumber==null){
//					hm.put(size, 1);
//				}else{
//					occNumber++;
//					hm.put(size, occNumber);
//				}
//			}catch(Exception e){
//				break;
//			}
//		}
//		
//		br.close();
//		br = null;
//		f = null;
//		
//		System.out.println("Group size" + "\t\t" + "Occurence Number");
//		for(Map.Entry<Integer, Integer> e : hm.entrySet())
//			System.out.println(e.getKey() + "\t\t\t" + e.getValue());
//		
//		hm.clear();
//		hm=null;
//	}
//}

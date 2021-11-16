package org.automation.util;

import java.util.Arrays;
import java.util.Comparator;

import org.automation.model.ProductResultHistory;

public final class AutomationConstant{
  
	public static String TRIGGER_TYPE_ALARM="ALARM";
	
    public static String TRIGGER_TYPE_OPERATE="OPERATE";
    
    public enum  SCHEDULER_STATUS{
    	IN_PROGRESS,STARTED,FAILURE,SUCCESS
    }
    
    public static boolean isEmpty(Object obj){
       return obj!=null?true:false;
    }
    
    public static String converTime(long timeDiff){
    	String time=null;
    	long seconds=timeDiff/1000%60;
    	long minutes=timeDiff/(60*1000)%60;
    	long hours=timeDiff/(60*60*1000)%24;
    	time=hours+":"+minutes+":"+seconds;
    	return time;
    }
    
    public static class SortByDate implements Comparator<ProductResultHistory>{
		@Override
		public int compare(ProductResultHistory o1, ProductResultHistory o2) {
			return o1.getEndDate().compareTo(o2.getEndDate());
		}
		
    }
}

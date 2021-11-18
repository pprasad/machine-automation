package org.automation.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

import org.automation.model.ProductResultHistory;
import org.automation.model.SchedulerConfig;

public final class AutomationConstant{
  
	public static String TRIGGER_TYPE_ALARM="ALARM";
	
    public static String TRIGGER_TYPE_OPERATE="OPERATE";
    
    public static String ERROR_RECORD_NOTFOUND="Active Record Not Found On ProductResultHistoryActive";
    
    public static String ERROR_WEAKUP_STATUS="Machine Still In Weakup Status";
    
    public static String TRIGGER_TYPE_OPR="OPR";
    
    public static final String ERROR_OBJECT_EMPTY="Object is Null";
    
    public static final String JOB_SCHDEULER_ID="JOB_SCHEDULER";
   
    private static final SimpleDateFormat TIME_FORMAT=new SimpleDateFormat("HH:mm:ss");
    
    
    public enum  SCHEDULER_STATUS{
    	IN_PROGRESS,STARTED,FAILURE,SUCCESS
    }
    
    
    public static boolean isEmpty(Object obj){
       return obj!=null?false:true;
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
    
    public static String convertDateTotime(Date date) {
    	return TIME_FORMAT.format(date);
    }
    
    public enum JOB_SCHEDULER_TIME{
    	D,W,M,Y,H
    }
    
    public static Date startDate(SchedulerConfig schedulerConfig) {
    	Date startDate=null;
    	Calendar calendar=Calendar.getInstance();
    	calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
    	if(schedulerConfig.getJobType().equals(JOB_SCHEDULER_TIME.D)) {
    		calendar.add(Calendar.DATE,-schedulerConfig.getJobDay());
    	}else if(schedulerConfig.getJobType().equals(JOB_SCHEDULER_TIME.M.toString())) {
    		calendar.add(Calendar.MONTH,-schedulerConfig.getJobDay());
    	}else if(schedulerConfig.getJobType().equals(JOB_SCHEDULER_TIME.Y.toString())) {
    		calendar.add(Calendar.YEAR,-schedulerConfig.getJobDay());
    	}else if(schedulerConfig.getJobType().equals(JOB_SCHEDULER_TIME.W.toString())) {
    		calendar.add(Calendar.WEEK_OF_MONTH,-schedulerConfig.getJobDay());
    	}else if(schedulerConfig.getJobType().equals(JOB_SCHEDULER_TIME.H.toString())) {
    		calendar.add(Calendar.HOUR,-schedulerConfig.getJobDay());
    	}
    	startDate=calendar.getTime();
    	return startDate;
    }
}

package usi.biz.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.ConnectionInfo;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class DiskInfoUtil {

	/**
	 * 日志.
	 */
	private static Logger _logger = LoggerFactory.getLogger(DiskInfoUtil.class);
	
	public static Map  DiskInfo(){
		Map<Object,String> map=new HashMap<Object,String>();
        try {  
        	File file=new File("/");
            File[] roots = file.listFiles();  
        	System.err.println("the count of roots is ： "+roots.length);  
        	double constm = 1024 * 1024 * 1024 ;  
        	        double total = 0d;  
        	        double free  = 0d;
        	        double used = 0d;
        	        for (File _file : roots) {
        	        	System.out.println("=============_file.getName():"+_file.getName());
        	        	if(_file.getName().equals("zzyf")){
	        	            System.out.println(_file.getPath());  
	        	            double total0 = _file.getTotalSpace()/constm,free0=_file.getFreeSpace()/constm,used0=total0-free0;  
	        	            System.out.println("totol space = " + total0+" G");  
	        	            System.out.print("the free space = " + free0+" G");  
	        	            System.out.println("---------- "+free0*100/total0+"% ----------");  
	        	            System.out.print("the used space = " + used0+" G");  
	        	            System.out.println("---------- "+used0*100/total0+"% ----------");  
	        	            System.out.println();  
	        	            total+=_file.getTotalSpace();  
	        	            free +=free0;
	        	            used +=used0;
        	        	}
        	        }  
        	        DecimalFormat df= new DecimalFormat("######0.00");  
        	        String totalSpace=df.format(total/constm)+"G";
        	        String freeSpace=df.format(free)+"G";
        	        String usedSpace=df.format(used)+"G";
        	        System.out.println("the total space of the machine = "+totalSpace); 
        	        System.out.println("the free space of the machine = "+freeSpace); 
        	        System.out.println("the used space of the machine = "+usedSpace); 
        	        map.put("totalSpace", totalSpace);
        	        map.put("freeSpace", freeSpace);
        	        map.put("usedSpace", usedSpace);
        } catch (Exception e) {  
            System.err.println(e.getMessage());  
        }
		return map;
	}
	public static void main(String[] args){
		DiskInfoUtil.DiskInfo();
	}
}

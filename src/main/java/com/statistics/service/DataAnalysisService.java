package com.statistics.service;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.statistics.dao.GetClicksDAO;
import com.statistics.repository.WareHouseRepository;

@Service
public class DataAnalysisService {
	@Autowired
	WareHouseRepository wr;
	boolean validfromdate=false;
	boolean validtodate=false;

	public String getClicks(String fromDate, String toDate, String dataSource)
	{
		if(fromDate!=null)
		{
			validfromdate= validateJavaDate(fromDate);
		}
		 if(toDate!=null)
		{
			validtodate =validateJavaDate(toDate);
		}
		if(validfromdate && validtodate)
		{   
			String total= wr.getValuesforClicks(fromDate,toDate,dataSource);	
			return total;
		}
		else
		{
			return "Invalid Date Format";
		}
	}
	
	public String calculateCTR(String campaign, String dataSource) {
		
		String total=null;
		total= wr.calculateCTR(campaign,dataSource);
		return total;
	}
	
	
	
	public static boolean validateJavaDate(String strDate)
	{
		boolean returnVal = false;
		if (strDate.trim().equals(""))
		{
		    return true;
		}
		else
		{
		   
			String[] permissFormats=new String[] {"MM-dd-yy","MM/dd/yy"};
			
			 for (int i = 0; i < permissFormats.length; i++) 
		        {
		            try
		            {
		                SimpleDateFormat sdfObj = new SimpleDateFormat(permissFormats[i]);
		                sdfObj.setLenient(false);
		                sdfObj.parse(strDate);
		                returnVal = true;
		                
		                System.out.println("Looks like a valid date for Date Value :"+strDate+": For Format:"+permissFormats[i]);
		                break;
		            }
		            catch(ParseException e)
		            {
		                System.out.println("Parse Exception Occured for Date Value :"+strDate+":And Format:"+permissFormats[i]);
		            }
		        }
		   
		    return returnVal;
		}
	}

	public ArrayList ClickThroughRate() {
	String datasource=null;
	String campaign=null;
	String ctr=null;
	ArrayList ctrValues=new ArrayList();
		Map<String, HashMap<String,String>> map1= wr.ClickThroughRate();
		for (Map.Entry<String, HashMap<String,String>> entry1:map1.entrySet())
		{
			 datasource=entry1.getKey();
			
			HashMap<String,String> hs=entry1.getValue();
		for (Map.Entry<String, String> entry:hs.entrySet())
        {		GetClicksDAO gcd1=new GetClicksDAO();
             campaign =entry.getKey();
             ctr = entry.getValue();
             gcd1.setDataSource(datasource);
             gcd1.setCampaign(campaign);
             gcd1.setCtr(ctr);
            ctrValues.add(gcd1);
        }
		
		}
		return ctrValues;
	}

	public ArrayList getCampaignClicks(String campaign) {
		// TODO Auto-generated method stub
		ArrayList campaignTotalclicks=new ArrayList();
		HashMap<String,String> campaignClicks= wr.getCampaignClicks(campaign);
		for (Map.Entry<String, String> entry:campaignClicks.entrySet())
        {	
			GetClicksDAO gd=new GetClicksDAO();
             String campaignKey =entry.getKey();
             String numberOfClicks = entry.getValue();
             gd.setCampaign(campaignKey);
             gd.setClicks(numberOfClicks);
             campaignTotalclicks.add(gd);
        }
		return campaignTotalclicks;
	}
	
		
}

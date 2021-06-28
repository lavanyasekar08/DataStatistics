package com.statistics.repository;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.statistics.dao.GetClicksDAO;
@Service
public class WareHouseRepository {

@Autowired
GetClicksDAO gc;
	
	static Connection conn = null;
	
	
public static Connection getConnection() throws SQLException, ClassNotFoundException
{
	Class.forName("org.relique.jdbc.csv.CsvDriver");
	File file = new File(new ClassPathResource("/src/main/resources").getPath());
	System.out.println(file);
	conn = DriverManager.getConnection("jdbc:relique:csv:"+file);
	return conn;
}

public GetClicksDAO getValuesforClicks(String fromDate,String toDate,String dataSource)
{
	PreparedStatement  statement = null;
	ResultSet rs = null;
    String total=null;
    gc.setFromDate(fromDate);
	gc.setToDate(toDate);
	gc.setDataSource(dataSource);
    String getClicksQuery="SELECT SUM(Clicks) As Total FROM adverity where (Daily  BETWEEN ? and ?) and Datasource= ?";
	try {
		try {
			conn = getConnection();
			statement = conn.prepareStatement(getClicksQuery);
			statement.setString(1,fromDate);
			statement.setString(2,toDate);
			statement.setString(3,dataSource);
			rs=statement.executeQuery();
			while(rs.next()) {
				total=rs.getString("Total");
				System.out.println(rs.getString("Total"));
				
			}
		} catch (ClassNotFoundException e) {
					e.printStackTrace();
		}
		

	}  catch (SQLException e) {
		e.printStackTrace();
	} finally {
		if (rs != null) try{rs.close();}catch(SQLException ex) {}
		if (statement != null) try{statement.close();}catch(SQLException ex) {}
		if (conn != null) try{conn.close();}catch(SQLException ex) {}
	}
	gc.setClicks(total);
	return gc;
	
}

public GetClicksDAO calculateCTR(String campaign, String dataSource)  {
	
	
	PreparedStatement  statement = null;
	ResultSet rs = null;
	String totalNumberOfClicks=null;
	String totalNumberOfImpressions=null;
	double clickThroughRate=0;
	String ctrQuery="Select sum(clicks)  total, sum(impressions) impressions from adverity where campaign= ? and Datasource= ? ";
	
		try {
			conn=getConnection();
		
			statement = conn.prepareStatement(ctrQuery);
			statement.setString(1,campaign);
			statement.setString(2,dataSource);
			rs=statement.executeQuery();
			while(rs.next()) {
			 totalNumberOfClicks=rs.getString("total");
			 totalNumberOfImpressions=rs.getString("impressions");
			}
		
	} 
		catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}catch (SQLException e) {
		
		e.printStackTrace();
	}
	finally {
		if (rs != null) try{rs.close();}catch(SQLException ex) {}
		if (statement != null) try{statement.close();}catch(SQLException ex) {}
		if (conn != null) try{conn.close();}catch(SQLException ex) {}
	}
				
				if(totalNumberOfClicks!=null)
				{
					double totalClicks=Integer.parseInt(totalNumberOfClicks);
					double totalImpressions=Integer.parseInt(totalNumberOfImpressions);
					
					 clickThroughRate=(totalClicks/totalImpressions)*100;
				
				}
				System.out.println(campaign);
				gc.setCampaign(campaign);
				gc.setDataSource(dataSource);
				gc.setCtr(Double.toString(Math.floor(clickThroughRate)));
				//ctrValues.add(gc);
				return 	 gc;


}

public Map<String, HashMap<String, String>> ClickThroughRate() {
	Statement statement = null;
	ResultSet rs = null;
	double totalNumberOfClicks=0;
	double totalNumberOfImpressions=0;
	String campaign=null;
	String datasource=null;
	HashMap<String, String> map = new HashMap<>();
	Map<String, HashMap<String,String>> map1 = new HashMap<>();
	
	try {
		
			
		conn=getConnection();
		statement = conn.createStatement();
		String Query="Select Campaign,Datasource,sum(clicks)  total, sum(impressions) impressions ,campaign from adverity group by Campaign,Datasource";
		rs=statement.executeQuery(Query);
		System.out.println("Test");
		while(rs.next()) {
			totalNumberOfClicks=Integer.parseInt(rs.getString("total"));
			totalNumberOfImpressions=Integer.parseInt(rs.getString("impressions"));
			campaign=rs.getString("Campaign");
			datasource=rs.getString("Datasource");
			double clickThroughRate=(totalNumberOfClicks/totalNumberOfImpressions)*100;
			String newValue = Double.toString(Math.floor(clickThroughRate));
			map.put(campaign, newValue);
			map1.put(datasource, map);
		}
		
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	catch (SQLException e) {
		e.printStackTrace();
	} finally {
		if (rs != null) try{rs.close();}catch(SQLException ex) {}
		if (statement != null) try{statement.close();}catch(SQLException ex) {}
		if (conn != null) try{conn.close();}catch(SQLException ex) {}
	}	// TODO Auto-generated method stub
	
	return map1;
	
}

public HashMap getCampaignClicks(String campaign) {
	// TODO Auto-generated method stub
	Connection conn = null;
	Statement statement = null;
	PreparedStatement  preStatement = null;
	ResultSet rs = null;
	Integer totalNumberOfClicks=0;
	HashMap<String,String> campaignClicks=new HashMap<String,String>();
	String campaignQuery="Select sum(clicks) TotalNumbeOfClicks,campaign from adverity where campaign= ? group by campaign";
	String Query="Select sum(clicks) TotalNumbeOfClicks,campaign from adverity group by campaign ";

	try {
		

		conn = getConnection();
		if(campaign==null)
		{
		statement = conn.createStatement();
		rs=statement.executeQuery(Query);
		}
		else
		{
			preStatement=conn.prepareStatement(campaignQuery);
			preStatement.setString(1,campaign);
		rs=preStatement.executeQuery();
		}

		while(rs.next()) {
			totalNumberOfClicks=Integer.parseInt(rs.getString("TotalNumbeOfClicks"));
			campaign=rs.getString("Campaign");
			campaignClicks.put(campaign, String.valueOf(totalNumberOfClicks));
			System.out.println("Campaign"+campaign+"totalNumberOfClicks"+totalNumberOfClicks);
		}
		 
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		if (rs != null) try{rs.close();}catch(SQLException ex) {}
		if (statement != null) try{statement.close();}catch(SQLException ex) {}
		if (conn != null) try{conn.close();}catch(SQLException ex) {}
	}
	return campaignClicks;
}
}

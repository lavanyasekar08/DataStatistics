package com.statistics.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.statistics.dao.GetClicksDAO;
import com.statistics.service.DataAnalysisService;

import io.swagger.annotations.Api;

@Controller
@RequestMapping("/DataAnalysis")
@Api(value="DataMetrices", description="Analysis of Marketing data")
public class DataAnalysisController {
	@Autowired
	DataAnalysisService ds;
	@Autowired
	GetClicksDAO gc;
	
	@GetMapping(value="/getclicks")
	public ModelAndView getClicks(@RequestParam(name="fromdate") String fromDate,@RequestParam(name="todate") String toDate,@RequestParam(name="datasource") String dataSource,Model model)
	{
		
		GetClicksDAO clicks=ds.getClicks(fromDate,toDate,dataSource);
		model.addAttribute("listvalues",clicks);
		return new ModelAndView("clicks"); 
		
	}
	
	@GetMapping(value="/calculatectr")
	public ModelAndView calculateCTR(@RequestParam(name="campaign") String campaign,@RequestParam(name="datasource") String dataSource,Model model)
	{
		GetClicksDAO gd=ds.calculateCTR(campaign,dataSource);
		model.addAttribute("listvalues",gd);
		return new ModelAndView("ctr");
		
	}
	
	@GetMapping(value="/ctr")
	public ModelAndView CTR(Model model)
	{
		
		List<GetClicksDAO> Al=ds.ClickThroughRate();
		model.addAttribute("listvalues",Al);
		for(int i=0;i<Al.size();i++)
		{
			gc=Al.get(i);
			
		}
		return new ModelAndView("ctr");	
	}
	@GetMapping(value="/getcampaignclicks")
	public ModelAndView getCampaignClicks(@RequestParam(required = false) String campaign,Model model)
	{
		
		ArrayList<GetClicksDAO> clicksValue=ds.getCampaignClicks(campaign);
		model.addAttribute("clicksValue",clicksValue);
		return new ModelAndView("campaignclicks"); 
		
	}
	
}

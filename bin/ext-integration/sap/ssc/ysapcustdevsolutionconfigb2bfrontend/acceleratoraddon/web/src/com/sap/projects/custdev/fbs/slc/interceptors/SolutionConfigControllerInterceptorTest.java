package com.sap.projects.custdev.fbs.slc.interceptors;

import static org.junit.Assert.assertEquals;

import com.sap.projects.custdev.fbs.slc.facades.impl.SolutionConfigurationItemData;
import com.sap.projects.custdev.fbs.slc.forms.SolutionUpdateQuantityForm;

import de.hybris.platform.sap.productconfig.facades.ConfigurationData;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;


public class SolutionConfigControllerInterceptorTest
{

	protected SolutionConfigControllerInterceptor solutionConfigControllerInterceptor;

	@Before
	public void setUp()
	{
		solutionConfigControllerInterceptor = new SolutionConfigControllerInterceptor();
	}

	@Test
	public void testPopulateUpdateSolutionForm()
	{
		ModelAndView modelAndView = new ModelAndView();
		List<SolutionConfigurationItemData> solutionItems = prepareModel(modelAndView);

		//Test Execution
		solutionConfigControllerInterceptor.populateUpdateSolutionItemForm(modelAndView);



		for (SolutionConfigurationItemData solutionItem : solutionItems)
		{
			modelAndView.getModelMap().get(
					"updateSolutionQuantityForm" + solutionItem.getEntryNumber());

		}




	}

	private List<SolutionConfigurationItemData> prepareModel(ModelAndView modelAndView)
	{
		//Preparation
		List<SolutionConfigurationItemData> solutionItems = new ArrayList<SolutionConfigurationItemData>();
		SolutionConfigurationItemData item1 = new SolutionConfigurationItemData();
		item1.setEntryNumber(1);
		item1.setUpdateable(true);
		Long item1Quantity = new Long(2);
		item1.setQuantity(item1Quantity);

		SolutionConfigurationItemData item2 = new SolutionConfigurationItemData();
		item2.setEntryNumber(2);
		item2.setUpdateable(false);
		Long item2Quantity = new Long(3);
		item2.setQuantity(item2Quantity);

		solutionItems.add(item1);
		solutionItems.add(item2);

		ConfigurationData configContent = new ConfigurationData();
		configContent.setSolutionItems(solutionItems);


		modelAndView.getModelMap().addAttribute("config", configContent);
		return solutionItems;
	}
}

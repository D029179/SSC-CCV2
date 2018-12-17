package com.sap.projects.custdev.fbs.slc.interceptors;

import com.sap.projects.custdev.fbs.slc.facades.SolutionConfigurationFacade;
import com.sap.projects.custdev.fbs.slc.facades.impl.SolutionConfigurationItemData;
import com.sap.projects.custdev.fbs.slc.forms.SolutionUpdateQuantityForm;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigaddonConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


public class SolutionConfigControllerInterceptor extends HandlerInterceptorAdapter
{
	static final Logger LOG = Logger.getLogger(SolutionConfigControllerInterceptor.class);

	@Resource(name = "sapcustdevSolutionConfigFacade")
	private SolutionConfigurationFacade solutionConfigFacade;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Override
	public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
			final ModelAndView modelAndView)
	{

		if (isValidRequest(modelAndView))
		{
			String viewName = modelAndView.getViewName();

			LOG.debug("Interceptor called: " + viewName);

			ConfigurationData producConfigData = (ConfigurationData) modelAndView.getModelMap().get(
					SapproductconfigaddonConstants.CONFIG_ATTRIBUTE);
			solutionConfigFacade.populateSolutionConfigData(producConfigData);

			populateUpdateSolutionItemForm(modelAndView);
			modelAndView.addObject("cpq.devmode.enabled", configurationService.getConfiguration().getBoolean("cpq.devmode.enabled", false));
		}

	}

	private boolean isValidRequest(final ModelAndView modelAndView)
	{
		return modelAndView != null
				&& modelAndView.getModelMap().get(SapproductconfigaddonConstants.CONFIG_ATTRIBUTE) != null;
	}

	protected SolutionConfigurationFacade getSolutionConfigFacade()
	{
		return solutionConfigFacade;
	}

	protected void setSolutionConfigFacade(SolutionConfigurationFacade solutionConfigFacade)
	{
		this.solutionConfigFacade = solutionConfigFacade;
	}

	public void populateUpdateSolutionItemForm(ModelAndView modelAndView)
	{
		ConfigurationData configData = (ConfigurationData) modelAndView.getModelMap().get(
				SapproductconfigaddonConstants.CONFIG_ATTRIBUTE);
		List<SolutionConfigurationItemData> solutionItems = configData.getSolutionItems();

		for (SolutionConfigurationItemData solutionItem : solutionItems)
		{
			final SolutionUpdateQuantityForm uqf = new SolutionUpdateQuantityForm();
			modelAndView.getModelMap().addAttribute("updateSolutionQuantityForm" + solutionItem.getEntryNumber(), uqf);

		}

	}



}

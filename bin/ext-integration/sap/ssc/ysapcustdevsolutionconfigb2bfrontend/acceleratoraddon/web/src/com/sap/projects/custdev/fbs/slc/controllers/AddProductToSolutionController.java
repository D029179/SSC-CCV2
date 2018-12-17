package com.sap.projects.custdev.fbs.slc.controllers;


import com.sap.projects.custdev.fbs.slc.facades.SolutionConfigurationFacade;
import com.sap.projects.custdev.fbs.slc.facades.impl.SolutionConfigurationModificationData;
import com.sap.projects.custdev.fbs.slc.forms.SolutionConfigForm;
import com.sap.projects.custdev.fbs.slc.helper.impl.SolutionMessageHandler;

import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigaddonConstants;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;

import javax.annotation.MatchesPattern;
import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import de.hybris.platform.sap.productconfig.facades.SessionAccessFacade;

@Controller
@Scope("tenant")
@RequestMapping()
public class AddProductToSolutionController extends AbstractPageController 
{
	@Resource(name = "sapcustdevSolutionConfigFacade")
	private SolutionConfigurationFacade solutionConfigFacade;

	@Resource(name = "sapcustdevSolutionConfigMessageHandler")
	private SolutionMessageHandler messageHandler;

	@Resource(name = "sapProductConfigSessionAccessFacade")
	private SessionAccessFacade sessionAccessFacade;

	private static final Logger LOG = Logger.getLogger(AddProductToSolutionController.class);

	@RequestMapping(value = "/**/{productCode:.*}/addRelatedProduct", method = RequestMethod.POST)
	public String addRelatedProduct(@ModelAttribute(SapproductconfigaddonConstants.CONFIG_ATTRIBUTE) @Valid @MatchesPattern(SapproductconfigaddonConstants.CONFIG_ATTRIBUTE) final ConfigurationData configData, @RequestParam("productToSearchForRelatedProducts") String productToSearchForRelatedProducts,
			@RequestParam("productToAddToSolution") String productToAddToSolution, @Valid final SolutionConfigForm form,
			final BindingResult bindingErrors, final Model model, final RedirectAttributes redirectModel)
	{
		final UiStatus uiStatus = sessionAccessFacade.getUiStatusForProduct(productToSearchForRelatedProducts);

		if (uiStatus != null)
		{
			final String configId = uiStatus.getConfigId();
			final SolutionConfigurationModificationData modificationData = solutionConfigFacade.addToSolution(configId,
					productToAddToSolution, 1);
			LOG.debug("Add product " + productToAddToSolution + " to solution " + productToSearchForRelatedProducts);

			configData.setConfigId(configId);
			final ConfigurationData latestConfiguration = solutionConfigFacade.getConfiguration(configData);
			model.addAttribute(SapproductconfigaddonConstants.CONFIG_ATTRIBUTE, latestConfiguration);

			messageHandler.populateModificationAsMessage(redirectModel, modificationData);

		}

		final String redirectURL = "redirect:/" + productToSearchForRelatedProducts + "/configuratorPage/findRelatedProducts";
		return redirectURL;

	}

	protected SolutionConfigurationFacade getSolutionConfigFacade()
	{
		return solutionConfigFacade;
	}

	protected void setSolutionConfigFacade(SolutionConfigurationFacade solutionConfigFacade)
	{
		this.solutionConfigFacade = solutionConfigFacade;
	}

	protected SolutionMessageHandler getMessageHandler()
	{
		return messageHandler;
	}

	protected void setMessageHandler(SolutionMessageHandler messageHandler)
	{
		this.messageHandler = messageHandler;
	}



}

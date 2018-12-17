package com.sap.projects.custdev.fbs.slc.controllers;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sap.projects.custdev.fbs.slc.facades.SolutionConfigurationFacade;
import com.sap.projects.custdev.fbs.slc.facades.impl.SolutionConfigurationModificationData;
import com.sap.projects.custdev.fbs.slc.forms.SolutionUpdateQuantityForm;
import com.sap.projects.custdev.fbs.slc.helper.impl.SolutionMessageHandler;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.sap.productconfig.facades.SessionAccessFacade;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;

@Controller
@Scope("tenant")
@RequestMapping()
public class ListProductOfSolutionController extends AbstractPageController
{
	@Resource(name = "sapcustdevSolutionConfigFacade")
	private SolutionConfigurationFacade solutionConfigFacade;

	@Resource(name = "sapcustdevSolutionConfigMessageHandler")
	private SolutionMessageHandler messageHandler;

	@Resource(name = "sapProductConfigSessionAccessFacade")
	private SessionAccessFacade sessionAccessFacade;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ListProductOfSolutionController.class);

	@RequestMapping(value = "/{productCode:.*}/configur*/config/updateSolution", method =
	{ RequestMethod.GET, RequestMethod.POST })
	public String updateSolution(@PathVariable("productCode") final String productCode,
			@RequestParam("entryNumber") final long entryNumber,
			@RequestParam("solutionComponentProductCode") final String solutionComponentProductCode,
			@RequestParam("solutionComponentInstanceId") final String solutionComponentInstanceId,
			final Model model,
			@Valid final SolutionUpdateQuantityForm form, final BindingResult bindingResult, final RedirectAttributes redirectModel)
	{
		if (bindingResult.hasErrors())
		{
			messageHandler.populateBindingResultAsMessage(redirectModel, bindingResult);
		}
		else
		{
			final UiStatus uiStatus = sessionAccessFacade.getUiStatusForProduct(productCode);

			if (uiStatus != null)
			{
				final String configId = uiStatus.getConfigId();

				SolutionConfigurationModificationData modificationData = solutionConfigFacade.removeFromSolution(configId,
								solutionComponentProductCode,
								solutionComponentInstanceId);
				messageHandler.populateModificationAsMessage(redirectModel, modificationData);
			}
		}

		final String redirectURL = "redirect:/" + productCode + "/configuratorPage/CPQCONFIGURATOR";
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

}

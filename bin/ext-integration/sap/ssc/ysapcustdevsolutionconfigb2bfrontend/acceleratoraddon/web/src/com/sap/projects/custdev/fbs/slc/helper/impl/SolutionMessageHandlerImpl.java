package com.sap.projects.custdev.fbs.slc.helper.impl;

import com.sap.projects.custdev.fbs.slc.constants.SolutionConfigurationModificationDataStatus;
import com.sap.projects.custdev.fbs.slc.facades.impl.SolutionConfigurationModificationData;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


public class SolutionMessageHandlerImpl implements SolutionMessageHandler
{

	@Override
	public void populateModificationAsMessage(RedirectAttributes model, SolutionConfigurationModificationData modificationData)
	{
		String[] params = new String[1];

		switch (modificationData.getStatusCode())
		{
			case SolutionConfigurationModificationDataStatus.SUCCESS_ADDED:

				params[0] = modificationData.getEntry().getProduct().getName();
				GlobalMessages.addFlashMessage(model, GlobalMessages.CONF_MESSAGES_HOLDER,
						"sapcustdev.solutionconfig.findrelatedproduct.message.success", params);
				break;

			case SolutionConfigurationModificationDataStatus.ERROR:

				params[0] = modificationData.getEntry().getProduct().getDescription();
				GlobalMessages.addFlashMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER,
						"sapcustdev.solutionconfig.findrelatedproduct.message.error", params);
				break;

			default:
				break;

		}

	}

	@Override
	public void populateBindingResultAsMessage(RedirectAttributes model, BindingResult result)
	{
		if (result.hasErrors())
		{
			for (final ObjectError error : result.getAllErrors())
			{
				String code = error.getCode();
				if (code.equals("typeMismatch"))
				{
					GlobalMessages.addFlashMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER,
							"sapcustdev.solutionconfig.error.quantity.invalid");
				}
				else
				{
					GlobalMessages.addFlashMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, error.getDefaultMessage());
				}
			}
		}

	}
}

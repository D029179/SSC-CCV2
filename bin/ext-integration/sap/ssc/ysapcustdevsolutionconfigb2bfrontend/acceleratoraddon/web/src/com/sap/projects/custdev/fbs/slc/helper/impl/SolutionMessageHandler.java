package com.sap.projects.custdev.fbs.slc.helper.impl;

import com.sap.projects.custdev.fbs.slc.facades.impl.SolutionConfigurationModificationData;

import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


public interface SolutionMessageHandler
{

	public void populateModificationAsMessage(RedirectAttributes model, SolutionConfigurationModificationData modificationData);

	public void populateBindingResultAsMessage(RedirectAttributes model, BindingResult result);

}

package com.sap.projects.custdev.fbs.slc.frontend.component.renderer;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.PageContext;

import de.hybris.platform.addonsupport.renderer.impl.DefaultAddOnCMSComponentRenderer;
import de.hybris.platform.sap.productconfig.frontend.model.ProductConfigurationMenuComponentModel;

public class SolutionConfigurationMenuComponentRenderer<C extends ProductConfigurationMenuComponentModel> extends DefaultAddOnCMSComponentRenderer<C> {


	@Override
	protected String getView(final C component)
	{
		return "/WEB-INF/views/addons/ysapcustdevsolutionconfigb2bfrontend/responsive/cms/solutionconfigurationmenucomponent.jsp";
	}
	
}

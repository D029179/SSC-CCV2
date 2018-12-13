package com.sap.projects.custdev.fbs.slc.frontend.component.renderer;

import de.hybris.platform.addonsupport.renderer.impl.DefaultAddOnCMSComponentRenderer;
import de.hybris.platform.sap.productconfig.frontend.model.ProductConfigurationFormComponentModel;

public class SolutionConfigurationFormComponentRenderer<C extends ProductConfigurationFormComponentModel> extends DefaultAddOnCMSComponentRenderer<C> {


	@Override
	protected String getView(final C component)
	{
		return "/WEB-INF/views/addons/ysapcustdevsolutionconfigb2bfrontend/responsive/cms/solutionconfigurationformcomponent.jsp";
	}
	
}

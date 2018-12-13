package com.sap.projects.custdev.fbs.slc.postprocessors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import com.sap.projects.custdev.fbs.slc.frontend.util.impl.SolutionUiStateHandler;
import de.hybris.platform.sap.productconfig.frontend.controllers.ConfigureProductController;
import de.hybris.platform.sap.productconfig.frontend.controllers.UpdateConfigureProductController;
import de.hybris.platform.sap.productconfig.frontend.controllers.AddConfigToCartController;
import de.hybris.platform.sap.productconfig.frontend.controllers.CartConfigureProductController;
import de.hybris.platform.sap.productconfig.frontend.controllers.ConfigurationOverviewController;
import de.hybris.platform.sap.productconfig.frontend.controllers.VariantOverviewController;

public class SolutionConfigPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(beanName.equals("configureProductController")){
			ConfigureProductController controller = (ConfigureProductController)bean;
			controller.setUiStateHandler(new SolutionUiStateHandler());
			return controller;
		}
		else if(beanName.equals("updateConfigureProductController")){
			UpdateConfigureProductController controller = (UpdateConfigureProductController)bean;
			controller.setUiStateHandler(new SolutionUiStateHandler());
			return controller;
		}
		else if(beanName.equals("addConfigToCartController")){
			AddConfigToCartController controller = (AddConfigToCartController)bean;
			controller.setUiStateHandler(new SolutionUiStateHandler());
			return controller;
		}
		else if(beanName.equals("cartConfigureProductController")){
			CartConfigureProductController controller = (CartConfigureProductController)bean;
			controller.setUiStateHandler(new SolutionUiStateHandler());
			return controller;
		}
		else if(beanName.equals("configurationOverviewController")){
			ConfigurationOverviewController controller = (ConfigurationOverviewController)bean;
			controller.setUiStateHandler(new SolutionUiStateHandler());
			return controller;
		}
		else if(beanName.equals("variantOverviewController")){
			VariantOverviewController controller = (VariantOverviewController)bean;
			controller.setUiStateHandler(new SolutionUiStateHandler());
			return controller;
		}
		else
			return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}

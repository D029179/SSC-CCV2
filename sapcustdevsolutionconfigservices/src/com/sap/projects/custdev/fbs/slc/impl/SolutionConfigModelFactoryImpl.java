package com.sap.projects.custdev.fbs.slc.impl;

import com.sap.projects.custdev.fbs.slc.model.impl.SolutionConfigModelImpl;

import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigModelFactoryImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;

public class SolutionConfigModelFactoryImpl extends ConfigModelFactoryImpl {

	@Override
	public ConfigModel createInstanceOfConfigModel() {
		final SolutionConfigModelImpl configModel = new SolutionConfigModelImpl();
                // TODO KB: Hardcoded version number is probably not the best solution
                configModel.setVersion("1");
		return configModel;
	}

}

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.sap.projects.custdev.fbs.slc.runtime.ssc.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigSessionClient;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;
import com.sap.projects.custdev.fbs.slc.model.intf.SolutionConfigModel;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.ssc.ConfigurationUpdateAdapter;
import de.hybris.platform.sap.productconfig.runtime.ssc.impl.ConfigurationUpdateAdapterImpl;


/**
 * Default implementation of {@link ConfigurationUpdateAdapter}
 */
public class SolutionConfigurationUpdateAdapterImpl extends ConfigurationUpdateAdapterImpl
{
	private static final Logger LOG = Logger.getLogger(SolutionConfigurationUpdateAdapterImpl.class);

	@Override
	public boolean updateConfiguration(final ConfigModel configModel, final String plainId, final IConfigSessionClient session)
	{
		boolean sscUpdated = super.updateConfiguration(configModel, plainId, session);
				
		if (configModel instanceof SolutionConfigModel) {
			SolutionConfigModel solutionConfigModel = (SolutionConfigModel) configModel;

			List<InstanceModel> solutionComponents = solutionConfigModel.getSolutionComponents();
			if (solutionComponents != null && solutionComponents.size() > 0) {
				for (InstanceModel instanceModel : solutionComponents) {
					try {
						sscUpdated = sscUpdated
								|| updateInstance(configModel.getId(), plainId, instanceModel, configModel, session);
					} catch (final IpcCommandException e) {
						throw new IllegalStateException("Could not update instance", e);
					}
				}
			}
		}
		
		return sscUpdated;
	}

}

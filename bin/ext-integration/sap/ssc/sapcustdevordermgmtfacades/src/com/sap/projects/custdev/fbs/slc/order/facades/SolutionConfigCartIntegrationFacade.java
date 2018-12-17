/**
 *
 */
package com.sap.projects.custdev.fbs.slc.order.facades;

import com.sap.projects.custdev.fbs.slc.model.intf.SolutionConfigModel;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.sapordermgmtcfgfacades.impl.DefaultCartIntegrationFacade;


/**
 * @author d051840
 *
 */
public class SolutionConfigCartIntegrationFacade extends DefaultCartIntegrationFacade
{

	@Override
	public String addConfigurationToCart(final ConfigurationData configuration) throws CommerceCartModificationException
	{

		final ConfigModel configModel = getConfigurationService().retrieveConfigurationModel(configuration.getConfigId());
		final String externalConfig = getConfigurationService().retrieveExternalConfiguration(configuration.getConfigId());

		if (configModel instanceof SolutionConfigModel)
		{
			final SolutionConfigModel solutionConfigModel = (SolutionConfigModel) configModel;
			solutionConfigModel.getExtensionMap().put("EXT_CONFIG", externalConfig);
		}

		return super.addConfigurationToCart(configuration);
	}
}

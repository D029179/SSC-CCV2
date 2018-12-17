package com.sap.projects.custdev.fbs.slc.facades;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.sap.projects.custdev.fbs.slc.model.intf.SolutionConfigModel;

import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicGroup;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.facades.populator.ConfigurationOverviewPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import org.springframework.beans.factory.annotation.Required;


/**
 * This class overrides the populator which populates the configuration overview data.
 * 
 * @author I307479
 *
 */
public class SolutionConfigurationOverviewPopulator extends ConfigurationOverviewPopulator {

	private SessionAccessService sessionAccessService;

	@Override
	public void populate(final ConfigModel source, final ConfigurationOverviewData target)
	{
		if (source != null && source instanceof SolutionConfigModel) {
			final SolutionConfigModel solutionConfigModel = (SolutionConfigModel) source;
			final List<CharacteristicGroup> groups = new ArrayList<>();
			final Map<String, ClassificationSystemCPQAttributesContainer> nameMap = getSessionAccessService().getCachedNameMap();
			final Collection<Map> options = fillOptions(target, nameMap);	
			target.setId(solutionConfigModel.getId());			
			
			getConfigurationOverviewInstancePopulator().populate(solutionConfigModel.getRootInstance(), groups, options);
			
			List<InstanceModel> solutionComponents = solutionConfigModel.getSolutionComponents();			
			if (solutionComponents != null && solutionComponents.size() > 0) {
				for (InstanceModel instanceModel : solutionComponents) {
					getConfigurationOverviewInstancePopulator().populate(instanceModel, groups, options);
				}
			}
			target.setGroups(groups);
		} else {
			super.populate(source, target);
		}
	}

	public SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

	@Required
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}
}

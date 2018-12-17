package com.sap.projects.custdev.fbs.slc;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;

import java.util.List;

import com.sap.custdev.projects.fbs.slc.cfg.client.IInstanceTypeData;


/**
 * One Component groups all instances with the same InstanceType (ComponentCode)
 */
public interface SolutionConfigurationService extends ProductConfigurationService
{

	public void addSolutionComponent(ConfigModel configModel, IInstanceTypeData instanceType);

	public List<InstanceModel> getSolutionComponents(ConfigModel configModel);

	public void removeSolutionComponent(ConfigModel configModel, String instanceId);

	public void updateSolutionComponent(ConfigModel configModel, IInstanceTypeData instanceType, long quantity);

	public List<IInstanceTypeData> getRelevantProducts(ConfigModel configModel, String nameOfProduct);
	
	public IInstanceTypeData getTypeDataForProductName(ConfigModel configModel, String nameOfProduct);

}

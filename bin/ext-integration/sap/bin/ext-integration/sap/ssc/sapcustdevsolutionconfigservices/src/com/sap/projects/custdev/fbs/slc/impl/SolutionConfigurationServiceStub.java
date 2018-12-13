package com.sap.projects.custdev.fbs.slc.impl;

import com.sap.custdev.projects.fbs.slc.cfg.client.IInstanceTypeData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.InstanceTypeData;
import com.sap.projects.custdev.fbs.slc.SolutionConfigurationService;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.services.impl.ProductConfigurationServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;


public class SolutionConfigurationServiceStub extends ProductConfigurationServiceImpl implements SolutionConfigurationService
{

	private static Map<String, ConfigModel> stubSolutionInstances = new HashMap<String, ConfigModel>();

	@Override
	public void addSolutionComponent(ConfigModel configModel, IInstanceTypeData instanceType)
	{
		InstanceModel newComponent = new InstanceModelImpl();
		newComponent.setName(instanceType.getName());
		String randomId = String.valueOf(RandomUtils.nextLong());
		newComponent.setId(randomId);
		//newComponent.setQuantity(1);


		String configId = configModel.getId();

		if (!stubSolutionInstances.containsKey(configId))
		{
			stubSolutionInstances.put(configId, configModel);
		}
		

		configModel.getRootInstance().getSubInstances().add(newComponent);

	}

	@Override
	public List<InstanceModel> getSolutionComponents(ConfigModel configModel)
	{
		List<InstanceModel> solutionComponents  = configModel.getRootInstance().getSubInstances();
		return solutionComponents;
	}

	@Override
	public void removeSolutionComponent(ConfigModel configModel, String instanceId)
	{
		List<InstanceModel> solutionComponents = getSolutionComponents(configModel);

		Iterator<InstanceModel> iterSolutions = solutionComponents.iterator();

		while (iterSolutions.hasNext())
		{
			InstanceModel solutionInstance = iterSolutions.next();
			if (solutionInstance.getId().equals(instanceId))
			{
				iterSolutions.remove();
			}
		}

	}

	@Override
	@Deprecated
	public void updateSolutionComponent(ConfigModel configModel, IInstanceTypeData instanceType, long quantity)
	{
		if (quantity == 0)
		{
			//removeSolutionComponent(configModel, instanceType);
		}

		List<InstanceModel> solutionComponents = getSolutionComponents(configModel);

		Iterator<InstanceModel> iterSolutionComponents = solutionComponents.iterator();

		while (iterSolutionComponents.hasNext())
		{
			InstanceModel solutionComponent = iterSolutionComponents.next();
			if (solutionComponent.getName().equals(instanceType.getName()))
			{
				//solutionComponent.setQuantity(quantity);
			}
		}


	}

	@Override
	public List<IInstanceTypeData> getRelevantProducts(ConfigModel configModel, String productCode)
	{
		List<IInstanceTypeData> relevantProducts = new ArrayList<IInstanceTypeData>();
		IInstanceTypeData instanceType1 = new InstanceTypeData();
		IInstanceTypeData instanceType2 = new InstanceTypeData();
		IInstanceTypeData instanceType3 = new InstanceTypeData();
		
		instanceType1.setName("WCEM_CRM");
		instanceType2.setName("WCEM_PAYMENT");
		instanceType3.setName("WCEM_DEPENDENCY_PC");
		
		relevantProducts.add(instanceType1);
		relevantProducts.add(instanceType2);
		relevantProducts.add(instanceType3);

		return relevantProducts;
	}
	
	@Override
	public IInstanceTypeData getTypeDataForProductName(final ConfigModel configModel, final String nameOfProduct)
	{
		IInstanceTypeData instanceType = new InstanceTypeData();
		instanceType.setName(nameOfProduct);
		return instanceType;
	}

}

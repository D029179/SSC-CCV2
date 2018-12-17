package com.sap.projects.custdev.fbs.slc.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.sap.custdev.projects.fbs.slc.cfg.client.IInstanceTypeData;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;
import com.sap.projects.custdev.fbs.slc.SolutionConfigurationService;
import com.sap.projects.custdev.fbs.slc.intf.ConfigurationProviderSlc;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.services.impl.ProductConfigurationServiceImpl;


public class SolutionConfigurationServiceImpl extends ProductConfigurationServiceImpl implements SolutionConfigurationService
{

	private static final Logger LOG = Logger
			.getLogger(SolutionConfigurationServiceImpl.class);

	@Override
	public void addSolutionComponent(ConfigModel configModel, IInstanceTypeData instanceType)
	{
		final Object lock = getLock(configModel.getId());
		synchronized (lock)
		{
			ConfigurationProviderSlc configurationProvider = getConfigurationProviderSlc();
			try {
				configurationProvider.addSolutionComponent(configModel, instanceType);
			} catch (IpcCommandException e) {
				LOG.error("Could not add component to solution", e);
				throw new IllegalStateException(
						"Could not add component to solution",
						e);

			}
		}
		
	}
	
	@Override
	public List<InstanceModel> getSolutionComponents(ConfigModel configModel)
	{
		final Object lock = getLock(configModel.getId());
		synchronized (lock)
		{
			ConfigurationProviderSlc configurationProvider = getConfigurationProviderSlc();
			try {
				return configurationProvider.getSolutionComponents(configModel);
			} catch (IpcCommandException e) {
				LOG.error("Could read components from solution", e);
				throw new IllegalStateException("Could read components from solution", e);
			}
		}
	}


	@Override
	public void removeSolutionComponent(ConfigModel configModel, String instanceId)
	{
		final Object lock = getLock(configModel.getId());
		synchronized (lock)
		{
			ConfigurationProviderSlc configurationProvider = getConfigurationProviderSlc();
			try {
				configurationProvider.removeSolutionComponent(configModel, instanceId);
			} catch (IpcCommandException e) {
				throw new IllegalStateException("Could remove component from solution", e);
			}
		}

	}


	@Override
	public void updateSolutionComponent(ConfigModel configModel, IInstanceTypeData instanceType, long quantity)
	{
		final Object lock = getLock(configModel.getId());
		synchronized (lock)
		{
			ConfigurationProviderSlc configurationProvider = getConfigurationProviderSlc();
			try {
				configurationProvider.updateSolutionComponent(configModel, instanceType, quantity);
			} catch (IpcCommandException e) {
				LOG.error("Could update component", e);
				throw new IllegalStateException("Could update component", e);
			}
		}

	}

	@Override
	public List<IInstanceTypeData> getRelevantProducts(ConfigModel configModel, String productCode)
	{
		final Object lock = getLock(configModel.getId());
		synchronized (lock)
		{
			ConfigurationProviderSlc configurationProvider = getConfigurationProviderSlc();
			try {
				return configurationProvider.getRelatedProductsForProduct(configModel, productCode);
			} catch (IpcCommandException e) {
				LOG.error("Could read possible component/products for solution", e);
				throw new IllegalStateException("Could read possible component/products for solution", e);
			}
		}
	}

	@Override
	public IInstanceTypeData getTypeDataForProductName(ConfigModel configModel, String nameOfProduct)
	{
		final Object lock = getLock(configModel.getId());
		synchronized (lock)
		{
			ConfigurationProviderSlc configurationProvider = getConfigurationProviderSlc();
			try {
				return configurationProvider.getTypeDataForProductName(configModel, nameOfProduct);
			} catch (IpcCommandException e) {
				LOG.error("Could read type data for product name", e);
				throw new IllegalStateException("Could read type data for product name", e);
			}
		}
	}

	private ConfigurationProviderSlc getConfigurationProviderSlc()
	{
		try
		{
			ConfigurationProviderSlc configurationProvider = (ConfigurationProviderSlc) getConfigurationProvider();
			return configurationProvider;
		}
		catch(ClassCastException  e)
		{
			// This is a serious problem. This can only happen, if the bean alias sapProductConfigConfigurationProvider is not set to class
			// com.sap.projects.custdev.fbs.slc.impl.ConfigurationProviderSlcImpl or one of its child implementations.
			// This configuration is necessary to run the solution configuration extensions
			LOG.fatal("Bean sapProductConfigConfigurationProvider is not of type ConfigurationProviderSlcImpl. This is mandatory for the solution configuration extension. Please check the spring configuration");
			throw e;
		}
	}

}

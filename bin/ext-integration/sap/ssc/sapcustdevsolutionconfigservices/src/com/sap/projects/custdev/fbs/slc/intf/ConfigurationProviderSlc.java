package com.sap.projects.custdev.fbs.slc.intf;

import java.util.List;

import com.sap.custdev.projects.fbs.slc.cfg.client.IInstanceTypeData;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

public interface ConfigurationProviderSlc extends ConfigurationProvider{
	
	/**
	 * Retrieves related products (type MARA) for a given product Id
	 * @para, configuration model
	 * @param productId 
	 * @return List of product ids (implicit type ID = MARA)
	 * @throws IpcCommandException
	 */
	public List<IInstanceTypeData> getRelatedProductsForProduct(final ConfigModel model, final String nameOfProduct) throws IpcCommandException;
	
	/**
	 * Adds a new component to the configuration
	 * @param configModel
	 * @param nameOfComp 
	 * @param quantity How many times
	 * @throws IpcCommandException 
	 */
	public void addSolutionComponent(final ConfigModel configModel, final IInstanceTypeData instanceType) throws IpcCommandException;
	
	/**
	 * Retrieves list of all added solution components
	 * @param configModel
	 * @return List of solution components
	 * @throws IpcCommandException
	 */
	public List<InstanceModel> getSolutionComponents(final ConfigModel configModel) throws IpcCommandException;
	
	/**
	 * Removes a single component from the configuration
	 * @param configModel
	 * @param nameOfComp
	 * @throws IpcCommandException
	 */
	public void removeSolutionComponent(final ConfigModel configModel, final String instanceId) throws IpcCommandException;
	
	
	/**
	 * Removes or adds components to configuration until number of instances equals quantity
	 * @param configModel
	 * @param nameOfComp
	 * @param quantity
	 * @throws IpcCommandException
	 */
	public void updateSolutionComponent(final ConfigModel configModel, final IInstanceTypeData instanceType, final long quantity) throws IpcCommandException;
	
	/**
	 * Converts a product name into an appropiate InstanceTypeData
	 * @param configModel
	 * @param nameOfProduct
	 * @return
	 */
	public IInstanceTypeData getTypeDataForProductName(final ConfigModel configModel, final String nameOfProduct) throws IpcCommandException;
	
}

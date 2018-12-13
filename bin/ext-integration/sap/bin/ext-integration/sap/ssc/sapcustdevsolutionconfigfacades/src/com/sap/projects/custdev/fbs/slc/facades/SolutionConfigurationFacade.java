package com.sap.projects.custdev.fbs.slc.facades;

import com.sap.projects.custdev.fbs.slc.facades.impl.SolutionConfigurationModificationData;

import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationFacade;

import java.util.List;


public interface SolutionConfigurationFacade extends ConfigurationFacade
{
	/**
	 * @param configId
	 * @param productCode
	 * @param quantity
	 * @return SolutionConfigurationItemData
	 */
	public SolutionConfigurationModificationData addToSolution(String configId, String productCode, long quantity);

	/**
	 * @param configContent
	 * @param productCode
	 * @return
	 */
	public List<String> getRelevantProductsForSolution(String configId, String productCode);


	/**
	 * @param configContent
	 */
	public void populateSolutionConfigData(ConfigurationData producConfigData);

	/**
	 * @param configId
	 * @param productCode
	 * @param newQuantity
	 * @return SolutionConfigurationModificationData
	 */
	public SolutionConfigurationModificationData updateSolutionConfiguration(String configId, String productCode, long newQuantity);

	/**
	 * @param configId
	 * @param productCode
	 * @param instanceId
	 * @return SolutionConfigurationModificationData
	 */
	public SolutionConfigurationModificationData removeFromSolution(String configId,
			String productCode, String instanceId);


}

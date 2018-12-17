package com.sap.projects.custdev.fbs.slc.facades.impl;

import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigurationFacadeImpl;
import de.hybris.platform.sap.productconfig.facades.impl.UniqueUIKeyGeneratorImpl;
import de.hybris.platform.sap.productconfig.facades.UniqueUIKeyGenerator;
import de.hybris.platform.sap.productconfig.facades.populator.SolvableConflictPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import com.sap.custdev.projects.fbs.slc.cfg.client.IInstanceTypeData;
import com.sap.projects.custdev.fbs.slc.SolutionConfigurationService;
import com.sap.projects.custdev.fbs.slc.constants.SapcustdevsolutionconfigservicesConstants;
import com.sap.projects.custdev.fbs.slc.constants.SolutionConfigurationModificationDataStatus;
import com.sap.projects.custdev.fbs.slc.facades.SolutionConfigurationFacade;
import com.sap.projects.custdev.fbs.slc.facades.populator.SolutionConfigSolvableConflictPopulator;
import com.sap.projects.custdev.fbs.slc.model.intf.SolutionConfigModel;


public class SolutionConfigurationFacadeImpl extends ConfigurationFacadeImpl implements SolutionConfigurationFacade
{
	private static final Logger LOG = Logger.getLogger(SolutionConfigurationFacadeImpl.class);
	private ProductConfigurationService productConfigConfigurationService;
	private SolutionConfigurationService solutionConfigurationService;
	private ProductFacade productFacade;

	protected ConfigurationData populateSolutionConfigData(final ConfigurationData productConfigData,
			final ConfigModel configModel)
	{
		final List<SolutionConfigurationItemData> solutionItems = new ArrayList<SolutionConfigurationItemData>();
		final SolutionConfigModel solutionConfigModel = (SolutionConfigModel) configModel;
		final List<InstanceModel> solutionComponents = solutionConfigModel.getSolutionComponents();
		int entryNumber = 0;
		for (final InstanceModel solutionComponent : solutionComponents)
		{
			// ignore system generated item
			// TODO check where to put the INST_AUTHOR info
			//	if (CsticValueModel.AUTHOR_SYSTEM.equals(solutionComponent.getExtensionData(SapcustdevsolutionconfigservicesConstants.INST_AUTHOR))) {
			//	continue;
			//	}

			final SolutionConfigurationItemData solutionItemData = new SolutionConfigurationItemData();
			populateSolutionConfigurationItemData(solutionItemData, solutionComponent, entryNumber);
			solutionItems.add(solutionItemData);
			entryNumber++;
		}
		productConfigData.setSolutionItems(solutionItems);
		// set classical model flag
		// TODO check where to put the IS_CLASSIC_MODEL model info
		//final String isClassicalModel = configModel.getExtensionData(SapcustdevsolutionconfigservicesConstants.IS_CLASSIC_MODEL);
		//TODO refactor this line
		//productConfigData.setClassicalModel(SapcustdevsolutionconfigservicesConstants.TRUE.equals(isClassicalModel) ? true : false);
		return productConfigData;
	}

	protected void populateSolutionConfigurationItemData(final SolutionConfigurationItemData solutionItemData,
			final InstanceModel solutionComponent, final Integer entryNumber)
	{
		final String productCode = solutionComponent.getName();
		final ProductData productData = productFacade.getProductForCodeAndOptions(productCode,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION,
						ProductOption.GALLERY, ProductOption.CATEGORIES, ProductOption.PROMOTIONS, ProductOption.VARIANT_FULL,
						ProductOption.STOCK, ProductOption.VOLUME_PRICES, ProductOption.PRICE_RANGE, ProductOption.VARIANT_MATRIX));
		solutionItemData.setEntryNumber(entryNumber);
		solutionItemData.setProduct(productData);
		solutionItemData.setInstanceId(solutionComponent.getId());
		solutionItemData.setUpdateable(true);
		solutionItemData.setBasePrice(productData.getPrice());
	}

	@Required
	public void setProductConfigConfigurationService(final ProductConfigurationService productConfigConfigurationService)
	{
		this.productConfigConfigurationService = productConfigConfigurationService;
	}

	protected ProductFacade getProductFacade()
	{
		return productFacade;
	}

	@Required
	public void setProductFacade(final ProductFacade productFacade)
	{
		this.productFacade = productFacade;
	}

	@Override
	public void populateSolutionConfigData(final ConfigurationData productConfigData)
	{
		final String configId = productConfigData.getConfigId();
		final ConfigModel configModel = productConfigConfigurationService.retrieveConfigurationModel(configId);
		populateSolutionConfigData(productConfigData, configModel);
	}

	@Override
	public SolutionConfigurationModificationData addToSolution(final String configId, final String productCode,
			final long quantity)
	{
		final ConfigModel configModel = productConfigConfigurationService.retrieveConfigurationModel(configId);
		final IInstanceTypeData instanceType = solutionConfigurationService.getTypeDataForProductName(configModel, productCode);
		solutionConfigurationService.addSolutionComponent(configModel, instanceType);
		return prepareModificationData(productCode, quantity, SolutionConfigurationModificationDataStatus.SUCCESS_ADDED);
	}

	@Override
	public List<String> getRelevantProductsForSolution(final String configId, final String productCode)
	{
		final ConfigModel configModel = productConfigConfigurationService.retrieveConfigurationModel(configId);
		final List<IInstanceTypeData> instanceTypes = solutionConfigurationService.getRelevantProducts(configModel, productCode);
		final List<String> returnData = new ArrayList<String>();
		if (instanceTypes != null)
		{
			for (final IInstanceTypeData instanceType : instanceTypes)
			{
				returnData.add(instanceType.getName());
			}
		}
		return returnData;
	}

	@Required
	public void setSolutionConfigurationService(final SolutionConfigurationService solutionConfigurationService)
	{
		this.solutionConfigurationService = solutionConfigurationService;
	}

	protected SolutionConfigurationService getSolutionConfigurationService()
	{
		return solutionConfigurationService;
	}

	@Override
	public SolutionConfigurationModificationData removeFromSolution(final String configId, final String productCode,
			final String instanceId)
	{
		final ConfigModel configModel = productConfigConfigurationService.retrieveConfigurationModel(configId);
		solutionConfigurationService.removeSolutionComponent(configModel, instanceId);
		return prepareModificationData(productCode, 0, SolutionConfigurationModificationDataStatus.SUCCESS_REMOVED);
	}

	@Override
	public SolutionConfigurationModificationData updateSolutionConfiguration(final String configId,
			final String productCode, final long newQuantity)
	{
		final ConfigModel configModel = productConfigConfigurationService.retrieveConfigurationModel(configId);
		final IInstanceTypeData instanceType = solutionConfigurationService.getTypeDataForProductName(configModel, productCode);
		solutionConfigurationService.updateSolutionComponent(configModel, instanceType, newQuantity);
		return prepareModificationData(productCode, newQuantity, SolutionConfigurationModificationDataStatus.SUCCESS);
	}

	private SolutionConfigurationModificationData prepareModificationData(final String productCode, final long newQuantity,
			final String status)
	{
		final ProductData productData = productFacade.getProductForCodeAndOptions(productCode,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION,
						ProductOption.GALLERY, ProductOption.CATEGORIES, ProductOption.PROMOTIONS, ProductOption.VARIANT_FULL,
						ProductOption.STOCK, ProductOption.VOLUME_PRICES, ProductOption.PRICE_RANGE, ProductOption.VARIANT_MATRIX));
		final SolutionConfigurationItemData entry = new SolutionConfigurationItemData();
		entry.setProduct(productData);
		final SolutionConfigurationModificationData modificationData = new SolutionConfigurationModificationData();
		modificationData.setQuantity(newQuantity);
		modificationData.setStatusCode(status);
		modificationData.setEntry(entry);
		return modificationData;
	}

	@Override
	protected List<UiGroupData> getCsticGroupsFromModel(final ConfigModel configModel,
			final List<UiGroupData> csticGroupsFlat)
	{
		final List<UiGroupData> csticGroups = super.getCsticGroupsFromModel(configModel, csticGroupsFlat);
		// Solution Configuration Specific: Retrieve Non-Part-Instances
		if (configModel instanceof SolutionConfigModel)
		{
			final SolutionConfigModel solutionConfigModel = (SolutionConfigModel) configModel;
			final List<InstanceModel> solutionComponents = solutionConfigModel.getSolutionComponents();
			if (solutionComponents != null)
			{
				for (final InstanceModel solutionComponent : solutionComponents)
				{
					final List<UiGroupData> solutionComponentCsticGroups = getGroupsFromSolutionComponent(solutionComponent);
					csticGroups.addAll(solutionComponentCsticGroups);
					//Change for fixing Non-Part-Instances not selected.
					populateCsticGroupsFlat(solutionComponentCsticGroups, csticGroupsFlat);
				}
			}
		}
		return csticGroups;
	}

	private void populateCsticGroupsFlat(final List<UiGroupData> csticGroups, List<UiGroupData> csticGroupsFlat)
	{
		for (final UiGroupData group : csticGroups)
		{
			if (group.getSubGroups() != null && group.getSubGroups().size() == 1)
			{
				populateCsticGroupsFlat(group.getSubGroups(), csticGroupsFlat);
			}
			else
			{
				final List<UiGroupData> groupsToAdd = (group.getSubGroups() == null) ? csticGroups : group.getSubGroups();
				csticGroupsFlat.addAll(groupsToAdd);
			}
		}
	}

	private List<UiGroupData> getGroupsFromSolutionComponent(final InstanceModel soutionComponent)
	{
		final List<UiGroupData> csticGroups = new ArrayList<>();
		final List<CsticGroup> csticModelGroups = soutionComponent.retrieveCsticGroupsWithCstics();
		final String prefix = getUiKeyGenerator().generateGroupIdForInstance(soutionComponent);
		for (final CsticGroup csticModelGroup : csticModelGroups)
		{
			final UiGroupData csticDataGroup = createCsticGroup(csticModelGroup, prefix);
			if (csticDataGroup.getCstics() == null || csticDataGroup.getCstics().size() == 0)
			{
				continue;
			}
			final UiGroupData uiGroup = createUiGroup(soutionComponent);
			//Fix for OSS 424837
			uiGroup.setCstics(csticDataGroup.getCstics());
			uiGroup.setCollapsed(true);
			csticGroups.add(uiGroup);
			break;//this line was added to remove triplication defect.Note#2456082
		}
		final List<InstanceModel> subInstances = soutionComponent.getSubInstances();
		for (final InstanceModel subInstance : subInstances)
		{
			final UiGroupData uiGroup = createUiGroup(subInstance);
			csticGroups.add(uiGroup);
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("get subgroups for instance [ID='" + soutionComponent.getId() + "';NAME='" + soutionComponent.getName()
					+ "';NUM_GROUPS='" + csticGroups.size() + "']");
		}
		return csticGroups;
	}

	// TODO: Unchanged, had to copy because of private methods used, check with
	// standard colleagues: change all private methods to protected in facade
	private List<UiGroupData> getGroupsFromInstance(final InstanceModel instance)
	{
		final List<UiGroupData> csticGroups = new ArrayList<>();
		final List<CsticGroup> csticModelGroups = instance.retrieveCsticGroupsWithCstics();
		final String prefix = getUiKeyGenerator().generateGroupIdForInstance(instance);
		for (final CsticGroup csticModelGroup : csticModelGroups)
		{
			final UiGroupData csticDataGroup = createCsticGroup(csticModelGroup, prefix);
			if (csticDataGroup.getCstics() == null || csticDataGroup.getCstics().size() == 0)
			{
				continue;
			}
			csticDataGroup.setConfigurable(true);
			csticGroups.add(csticDataGroup);
		}
		final List<InstanceModel> subInstances = instance.getSubInstances();
		for (final InstanceModel subInstance : subInstances)
		{
			final UiGroupData uiGroup = createUiGroup(subInstance);
			csticGroups.add(uiGroup);
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("get subgroups for instance [ID='" + instance.getId() + "';NAME='" + instance.getName() + "';NUM_GROUPS='"
					+ csticGroups.size() + "']");
		}
		return csticGroups;
	}

	// TODO: Unchanged, had to copy because of private methods used, check with
	// standard colleagues: change all private methods to protected in facade
	private UiGroupData createCsticGroup(final CsticGroup csticModelGroup, String prefix)
	{
		final UiGroupData uiGroupData = new UiGroupData();
		// cstic group name is unique (inside an instance), there is no cstic
		// group id
		// For ui groups we can use the cstic group name as ui group id as well
		// (additional to the ui group name)
		final String csticGroupName = csticModelGroup.getName();
		prefix = prefix + "." + csticGroupName;
		uiGroupData.setId(prefix);
		uiGroupData.setName(csticGroupName);
		uiGroupData.setDescription(csticModelGroup.getDescription());
		uiGroupData.setGroupType(GroupType.CSTIC_GROUP);
		uiGroupData.setCstics(getListOfCsticData(csticModelGroup.getCstics(), prefix));
		if (LOG.isDebugEnabled())
		{
			LOG.debug("create UI group for csticGroup [NAME='" + csticModelGroup.getName() + "';GROUP_PREFIX='" + prefix
					+ "';CSTICS_IN_GROUP='" + uiGroupData.getCstics().size() + "']");
		}
		return uiGroupData;
	}

	// TODO: Unchanged, had to copy because of private methods used, check with
	// standard colleagues: change all private methods to protected in facade
	private List<CsticData> getListOfCsticData(final List<CsticModel> csticModelList, final String prefix)
	{
		final List<CsticData> cstics = new ArrayList<>();
		for (final CsticModel model : csticModelList)
		{
			if (!model.isVisible())
			{
				continue;
			}
			//TODO getSessionAccessService not visible
			final Map<String, ClassificationSystemCPQAttributesContainer> hybrisNamesMap = null;//getSessionAccessService().getCachedNameMap();
			cstics.add(getCsticTypeMapper().mapCsticModelToData(model, prefix, hybrisNamesMap));
		}
		return cstics;
	}

	// TODO: Unchanged, had to copy because of private methods used, check with
	// standard colleagues: change all private methods to protected in facade
	private UiGroupData createUiGroup(final InstanceModel instance)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("create UI group for instance [ID='" + instance.getId() + "';NAME='" + instance.getName() + "']");
		}
		final UiGroupData uiGroup = new UiGroupData();
		final String groupId = getUiKeyGenerator().generateGroupIdForInstance(instance);
		final String groupName = instance.getName();
		uiGroup.setId(groupId);
		uiGroup.setName(groupName);
		uiGroup.setDescription(instance.getLanguageDependentName());
		// retrieve (sub)instance product description from catalog if available
		final List<ProductModel> products = getProductDao().findProductsByCode(groupName);
		if (products != null && products.size() == 1)
		{
			final ProductModel product = products.get(0);
			final String productName = product.getName();
			if (productName != null && !productName.isEmpty())
			{
				uiGroup.setDescription(productName);
			}
			final String summaryText = product.getSummary();
			uiGroup.setSummaryText(summaryText);
		}
		// if no group (subinstance) language dependent description available at
		// all, use the subinstance name
		if (uiGroup.getDescription() == null || uiGroup.getDescription().isEmpty())
		{
			uiGroup.setDescription("[" + groupName + "]");
		}
		uiGroup.setGroupType(GroupType.INSTANCE);
		final List<UiGroupData> subGroups = getGroupsFromInstance(instance);
		uiGroup.setSubGroups(subGroups);
		uiGroup.setCstics(new ArrayList<CsticData>());
		uiGroup.setConfigurable(isUiGroupConfigurable(subGroups));
		uiGroup.setOneConfigurableSubGroup(isOneSubGroupConfigurable(subGroups));
		return uiGroup;
	}

	// TODO: Unchanged, had to copy because of private methods used, check with
	// standard colleagues: change all private methods to protected in facade
	protected boolean isUiGroupConfigurable(final List<UiGroupData> subGroups)
	{
		if (subGroups == null || subGroups.isEmpty())
		{
			return false;
		}
		for (final UiGroupData uiGroup : subGroups)
		{
			if (uiGroup.isConfigurable())
			{
				return true;
			}
		}
		return false;
	}

	// TODO: Unchanged, had to copy because of private methods used, check with
	// standard colleagues: change all private methods to protected in facade
	protected boolean isOneSubGroupConfigurable(final List<UiGroupData> subGroups)
	{
		if (subGroups == null || subGroups.isEmpty())
		{
			return false;
		}
		int numberOfConfigurableGroups = 0;
		for (final UiGroupData uiGroup : subGroups)
		{
			if (uiGroup.isConfigurable() && ++numberOfConfigurableGroups > 1)
			{
				return false;
			}
		}
		return numberOfConfigurableGroups == 1;
	}

	@Override
	public void updateConfiguration(final ConfigurationData configContent)
	{
		final String configId = configContent.getConfigId();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("update configuration [CONFIG_ID='" + configId + "';PRODUCT_CODE='" + configContent.getKbKey().getProductCode()
					+ "']");
		}
		final ConfigModel configModel = getConfigurationService().retrieveConfigurationModel(configId);
		final PricingData pricingData = getConfigPricing().getPricingData(configModel);
		configContent.setPricing(pricingData);
		final InstanceModel rootInstance = configModel.getRootInstance();
		// Need to pass also the solution components models to the update,
		// otherwise for the UIGroups no corresponding model is found
		final SolutionConfigModel solutionConfigModel = (SolutionConfigModel) configModel;
		final List<InstanceModel> solutionComponents = solutionConfigModel.getSolutionComponents();
		for (final UiGroupData uiGroup : configContent.getGroups())
		{
			updateUiGroup(rootInstance, solutionComponents, uiGroup);
		}
		getConfigurationService().updateConfiguration(configModel);
	}

	private void updateUiGroup(final InstanceModel instance, final List<InstanceModel> solutionComponents,
			final UiGroupData uiGroup)
	{
		if (uiGroup.getGroupType() == GroupType.CSTIC_GROUP)
		{
			// cstic group
			updateCsticGroup(instance, uiGroup);
		}
		else if (uiGroup.getGroupType() == GroupType.CONFLICT_HEADER)
		{
			// Exception during calculating instanceID of ConlifctUIGroup
			// Do Nothing
		}
		else
		{
			// (sub)instance and solution components
			final InstanceModel subInstance = retrieveRelatedInstanceModel(instance, solutionComponents, uiGroup);
			final List<UiGroupData> uiSubGroups = uiGroup.getSubGroups();
			if (subInstance != null && uiSubGroups != null)
			{
				for (final UiGroupData uiSubGroup : uiSubGroups)
				{
					updateUiGroup(subInstance, solutionComponents, uiSubGroup);
				}
			}
		}
	}

	private InstanceModel retrieveRelatedInstanceModel(final InstanceModel instance, final List<InstanceModel> solutionComponents,
			final UiGroupData uiSubGroup)
	{
		final String uiGroupId = uiSubGroup.getId();
		if (uiGroupId != null)
		{
			final String instanceId = retrieveInstanceId(uiGroupId);
			final List<InstanceModel> subInstances = instance.getSubInstances();
			final List<InstanceModel> allInstances = new ArrayList<InstanceModel>();
			allInstances.addAll(subInstances);
			allInstances.addAll(solutionComponents);
			for (final InstanceModel instanceToCheck : allInstances)
			{
				if (instanceToCheck.getId().equals(instanceId))
				{
					return instanceToCheck;
				}
			}
		}
		return null;
	}

	protected String retrieveInstanceId(final String uiGroupId)
	{
		/**
		 * Classic product configuration models have instance ID pattern
		 * <INDEX_OF_INSTANCE> (e.g. 1,2,3) Advanced configuration models have
		 * instances ID pattern
		 * <ID_OF_KNOWLEDGEBASE>-<MATERIAL_ID>$<INDEX_OF_INSTANCE> (E.g.
		 * 1-FBS_OFFICE$1, 1-FBS_OFFICE$2, 1-FBS_OFFICE$3) For classic models
		 * that results in generation of IDs for UI Groups like
		 * 1-WCEM_SAPWEC.SAP_GENERAL_GROUP_NAME For advanced models this looks
		 * like 1-FBS_OFFICE$1-FBS_OFFICE.SAP_GENERAL_GROUP_NAME The standard
		 * logic to retrieve the instanceId from the UIGroups is: final String
		 * instanceId = uiGroupId.substring(0, uiGroupId.indexOf("-")); That
		 * does not work for advanced models, as we have multiple "-" in the
		 * IDs, so the below more complicated logic had to be introduced
		 *
		 * Example UI group structure for advanced models
		 *
		 * 1-FBS_OFFICE$1-FBS_OFFICE.SAP_GENERAL_GROUP_NAME
		 * 1-FBS_OFFICE$2-FBS_WORKPLACE
		 * 1-FBS_OFFICE$2-FBS_WORKPLACE.SAP_GENERAL_GROUP_NAME
		 * 1-FBS_OFFICE$3-FBS_LAPTOP
		 * 1-FBS_OFFICE$3-FBS_LAPTOP.SAP_GENERAL_GROUP_NAME
		 * 1-FBS_OFFICE$4-FBS_PROCESSOR
		 * 1-FBS_OFFICE$4-FBS_PROCESSOR.SAP_GENERAL_GROUP_NAME
		 * 1-FBS_OFFICE$5-FBS_MEMORY
		 * 1-FBS_OFFICE$5-FBS_MEMORY.SAP_GENERAL_GROUP_NAME
		 * 1-FBS_OFFICE$7-FBS_ADMINISTRATION
		 * 1-FBS_OFFICE$7-FBS_ADMINISTRATION.SAP_GENERAL_GROUP_NAME
		 *
		 *
		 * Example UI group structure for classic models
		 *
		 * 2-WCEM_SAPWEC 2-WCEM_SAPWEC.SAP_GENERAL_GROUP_NAME
		 */
		final String instanceIndexPrefix = "$";
		final String instanceIdPostFix = "-";
		if (uiGroupId.contains(instanceIndexPrefix))
		{
			// Advanced Model
			final int instanceIndexPrefixIndex = uiGroupId.indexOf(instanceIndexPrefix);
			final int instanceIdPostFixIndex = uiGroupId.indexOf(instanceIdPostFix, instanceIndexPrefixIndex);
			final String instanceId = uiGroupId.substring(0, instanceIdPostFixIndex);
			return instanceId;
		}
		else
		{
			// Classic model, use standard logic
			return getUiKeyGenerator().retrieveInstanceId(uiGroupId);
		}
	}

	protected SolvableConflictPopulator getConflictPopulator()
	{
		if (!(super
				.getConflictPopulator() instanceof com.sap.projects.custdev.fbs.slc.facades.populator.SolutionConfigSolvableConflictPopulator))
			setConflictPopulator(new SolutionConfigSolvableConflictPopulator());
		return super.getConflictPopulator();
	}
}

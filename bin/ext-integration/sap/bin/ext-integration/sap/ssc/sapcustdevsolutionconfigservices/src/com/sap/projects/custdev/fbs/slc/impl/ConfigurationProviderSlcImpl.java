package com.sap.projects.custdev.fbs.slc.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.ssc.impl.CommonConfigurationProviderSSCImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigInfoData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigSessionClient;
import com.sap.custdev.projects.fbs.slc.cfg.client.IDeltaBean;
import com.sap.custdev.projects.fbs.slc.cfg.client.IInstanceData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IInstanceTypeData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.InstanceTypeData;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;
import com.sap.custdev.projects.fbs.slc.kbo.local.OrchestratedCstic;
import com.sap.custdev.projects.fbs.slc.kbo.local.OrchestratedInstance;
import com.sap.custdev.projects.fbs.slc.kbo.util.ContainerUtil;
import com.sap.custdev.projects.fbs.slc.util.IIPCConstants;
import com.sap.projects.custdev.fbs.slc.constants.SapcustdevsolutionconfigservicesConstants;
import com.sap.projects.custdev.fbs.slc.intf.ConfigurationProviderSlc;
import com.sap.projects.custdev.fbs.slc.model.intf.SolutionConfigModel;
import com.sap.sce.front.base.Config;
import com.sap.sce.front.base.Instance;
import com.sap.sce.front.base.InstanceType;

public class ConfigurationProviderSlcImpl extends CommonConfigurationProviderSSCImpl implements ConfigurationProviderSlc {

	private static final Logger LOG = Logger.getLogger(ConfigurationProviderSlcImpl.class);

	private final String MARA = "MARA";

	@Override
	protected CsticModel createCsticModel(final OrchestratedCstic orchestratedCstic, final String instId)  {
		final CsticModel csticModel = getConfigModelFactory().createInstanceOfCsticModel();

		csticModel.setName(orchestratedCstic.getName());
		csticModel.setLanguageDependentName(orchestratedCstic.getLangDependentName());
		String description = orchestratedCstic.getDescription();
		description = getTextConverter().convertLongText(description);
		csticModel.setLongText(description);

		csticModel.setComplete(!orchestratedCstic.isRequired() || orchestratedCstic.hasValues());

		csticModel.setConsistent(!orchestratedCstic.isConflicting());

		csticModel.setConstrained(orchestratedCstic.isDomainConstrained());
		csticModel.setMultivalued(orchestratedCstic.isMultiValued());
		csticModel.setAllowsAdditionalValues(orchestratedCstic.getType().isAdditionalValuesAllowed());
		csticModel.setEntryFieldMask(orchestratedCstic.getType().getEntryFieldMask());
		csticModel.setIntervalInDomain(orchestratedCstic.isDomainAnInterval());
		csticModel.setReadonly(orchestratedCstic.isReadOnly());
		csticModel.setRequired(orchestratedCstic.isRequired());

		csticModel.setInstanceId(instId);

		final String csticAuthor = orchestratedCstic.isUserOwned() ? CsticModel.AUTHOR_USER : CsticModel.AUTHOR_SYSTEM;
		csticModel.setAuthor(csticAuthor);

		csticModel.setValueType(orchestratedCstic.getType().getValueType());

		final Integer numberScaleInt = orchestratedCstic.getType().getNumberScale();
		final int numberScale = (numberScaleInt != null) ? numberScaleInt.intValue() : 0;
		csticModel.setNumberScale(numberScale);

		final String[] staticDomain = orchestratedCstic.getStaticDomain();
		final int staticDomainLength = (staticDomain != null) ? staticDomain.length : 0;
		csticModel.setStaticDomainLength(staticDomainLength);

		// We do not support ADT CStics, so we hide ADT CStics
		// Performance reason we remove the call to getcsticData() line number
		// 81-90
		if (orchestratedCstic.getType() != null && orchestratedCstic.getType().isADT()) {
			csticModel.setVisible(false);
		} else {
			csticModel.setVisible(!orchestratedCstic.isInvisible());
		}

		//ADT cstics' typelength is null, which results in an error in Product Configuration code
		//where there is no null handling.
		final Integer csticTypeLength = orchestratedCstic.getType().getTypeLength();
		int intCsticTypeLength = 0;
		if (csticTypeLength != null) {
			intCsticTypeLength = csticTypeLength.intValue();
		}
		csticModel.setTypeLength(intCsticTypeLength);

		return csticModel;
	}

	/*
	 * Following method does not seem to be used anywhere. Will delete it.
	 *
	 *
	 *
	 * protected CsticModel createCsticModel(final IConfigSession session, final
	 * String configId, final ICsticData csticData, final ICsticHeader
	 * csticHeader) throws IpcCommandException {
	 *
	 * final CsticModel csticModel =
	 * getConfigModelFactory().createInstanceOfCsticModel();
	 * csticModel.setName(csticHeader.getCsticName());
	 * csticModel.setLanguageDependentName(csticHeader.getCsticLname());
	 *
	 * csticModel.setComplete(csticData.getCsticComplete().booleanValue());
	 * csticModel.setConsistent(csticData.getCsticConsistent().booleanValue());
	 * csticModel
	 * .setConstrained(csticHeader.getCsticConstrained().booleanValue());
	 * csticModel.setMultivalued(csticHeader.getCsticMulti().booleanValue());
	 * csticModel
	 * .setAllowsAdditionalValues(csticHeader.getCsticAdd().booleanValue());
	 * csticModel.setEntryFieldMask(csticHeader.getCsticEntryFieldMask());
	 * csticModel
	 * .setIntervalInDomain(csticData.getCsticDomainIsInterval().booleanValue
	 * ()); csticModel.setReadonly(csticData.getCsticReadonly().booleanValue());
	 * csticModel.setRequired(csticData.getCsticRequired().booleanValue());
	 *
	 * // We do not support ADT CStics, so we hide ADT CStics if
	 * (csticData.getIsCsticTypeADT() != null && csticData.getIsCsticTypeADT())
	 * { csticModel.setVisible(false); } else {
	 * csticModel.setVisible(csticData.getCsticVisible().booleanValue()); }
	 *
	 * if (csticData.getCsticAuthor() == null) {
	 * csticModel.setAuthor(CsticModel.AUTHOR_NOAUTHOR); } else {
	 * csticModel.setAuthor(csticData.getCsticAuthor()); }
	 *
	 * csticModel.setValueType(csticHeader.getCsticValueType().intValue());
	 * final Integer csticTypeLength = csticHeader.getCsticTypeLength(); int
	 * intCsticTypeLength = 0; if (csticTypeLength != null) { intCsticTypeLength
	 * = csticTypeLength.intValue(); }
	 *
	 * csticModel.setTypeLength(intCsticTypeLength); if
	 * (csticHeader.getCsticNumberScale() == null) {
	 * csticModel.setNumberScale(0); } else {
	 * csticModel.setNumberScale(csticHeader.getCsticNumberScale().intValue());
	 * }
	 *
	 * csticModel.setStaticDomainLength(calculateStaticDomainLength(session,
	 * configId, csticData.getInstanceId(), csticHeader.getCsticName()));
	 *
	 * return csticModel; }
	 */

	@Override
	public List<IInstanceTypeData> getRelatedProductsForProduct(final ConfigModel model, final String nameOfProduct) throws IpcCommandException {
		final List<IInstanceTypeData> returnList = new ArrayList<IInstanceTypeData>();

		IConfigSession session = null;

		try {
			session = retrieveConfigSession(model.getId());
		} catch (final IllegalStateException e) {
			// Fallback. Build your own session
			LOG.warn("Session not found for ConfigModel ID: " + model.getId());
			final KBKey kbKey = new KBKeyImpl(nameOfProduct);
			final ConfigModel tempModel = createDefaultConfiguration(kbKey);
			session = retrieveConfigSession(tempModel.getId());
		}

		final String configId = retrievePlainConfigId(model.getId());
		try {
			// final String rootConfigId = session.getRootInstance(configId,
			// false).getConfigId();

			final OrchestratedInstance orchInst = session.getRootInstanceLocal(configId);
			final Instance firstShared = orchInst.getFirstSharedInstance();
			final Config config = firstShared.getConfig();
			final InstanceType[] instanceTypes = config.getNonPartInstanceTypes();

			final List<InstanceTypeData> instanceTypeDataList = new ArrayList<InstanceTypeData>();
			ContainerUtil.createInstTypeDataList(instanceTypes, config.getKboManager(), config, instanceTypeDataList);
			// IInstanceTypeData[] instanceTypeData = session
			// .getNonPartInstanceTypes(rootConfigId);
			if (instanceTypeDataList != null) {
				for (int i = 0; i < instanceTypeDataList.size(); i++) {
					// Add to return list if type is MARA and exclude self
					// reference
					if (instanceTypeDataList.get(i).getExternalType().equals(MARA) && !instanceTypeDataList.get(i).getName().equals(nameOfProduct)) {
						returnList.add(instanceTypeDataList.get(i));
					}
				}
			}

		} catch (final IpcCommandException e) {
			LOG.error("Error reading instance type data for product: " + nameOfProduct);
			throw e;
		}

		return returnList;
	}

	@Override
	public void addSolutionComponent(final ConfigModel configModel, final IInstanceTypeData instanceType) throws IpcCommandException {
		// Get Session and configuration ID
		final IConfigSession session = retrieveConfigSession(configModel.getId());

		final String configId = retrievePlainConfigId(configModel.getId());

		OrchestratedInstance instance = null;
		final String uid = instanceType.getInstTypeId();

		try {
			instance = session.createNonPartInstanceLocal(configId, uid);
		} catch (final IpcCommandException e) {
			LOG.error("Could not create non part instance for component " + instanceType.getName());
			throw e;
		}
		// Check if something failed. createNonPartInstanceLocal hardly throws
		// any exceptions
		if (instance == null) {
			LOG.error("Could not create non part instance for component " + instanceType.getName());
			throw new IpcCommandException("Could not create non part instance for component");
		}

	}

	@Override
	public List<InstanceModel> getSolutionComponents(final ConfigModel configModel) throws IpcCommandException {
		// Get Session and configuration ID
		final List<InstanceModel> returnModels = new ArrayList<InstanceModel>();
		List<IInstanceData> instancesList = null;
		final IConfigSession session = retrieveConfigSession(configModel.getId());
		// Get Orchestraded Instance
		OrchestratedInstance orchestratedInstance = null;
		final String configId = retrievePlainConfigId(configModel.getId());
		final IInstanceData[] instances = session.getNonPartInstances(configId, false);
		if (instances != null) {
			// Convert to instance model and prepare return
			instancesList = Arrays.asList(instances);
			for (final IInstanceData data : instancesList) {
				final String instanceId = data.getInstId();
				orchestratedInstance = session.getInstanceLocal(configId, instanceId);
				final InstanceModel instanceModel = createInstance(data);

				final List<CsticGroupModel> goupModelList = prepareCsticGroups(orchestratedInstance); // prepareCsticGroups(configId,
																										// instanceId,
																										// session,
																										// csticsData);
				instanceModel.setCsticGroups(goupModelList);

				// Prepare external cstic value authors
				// final Map<String, String> csticValueAuthorMap =
				// prepareExternalCsticValueAuthors(session, configId);

				final List<CsticModel> csticModels = createCstics(orchestratedInstance, instanceModel.getId());
				instanceModel.setCstics(csticModels);

				// TODO: Check the impact of commenting the following method as
				// the following method does not exist anymore
				// retrieveConfigurationConflicts(configId, instanceModel,
				// session);

				returnModels.add(instanceModel);
			}
		}

		return returnModels;
	}

	@Override
	public void removeSolutionComponent(final ConfigModel configModel, final String instanceId) throws IpcCommandException {

		// Get Session and configuration ID
		final IConfigSession session = retrieveConfigSession(configModel.getId());

		final String configId = retrievePlainConfigId(configModel.getId());
		IDeltaBean instance = null;

		final IInstanceData[] instances = session.getNonPartInstances(configId, false);

		LOG.info(instances);
		try {
			instance = session.deleteInstance(instanceId, configId);
		} catch (final IpcCommandException e) {
			LOG.error("Could not delete non part instance for component");
			throw e;
		}
		if (instance == null) {
			LOG.error("Could not delete non part instance for component ");
			throw new IpcCommandException("Could not delete non part instance for component");
		}
	}

	@Override
	@Deprecated
	public void updateSolutionComponent(final ConfigModel configModel, final IInstanceTypeData instanceType, final long quantity)
			throws IpcCommandException {
		// Count number of currently existing instances
		final List<InstanceModel> components = getSolutionComponents(configModel);
		final long oldQuantity = 0;
		for (final InstanceModel component : components) {
			if (component.getName().equals(instanceType.getName())) {
				// oldQuantity = component.getQuantity();
			}
		}

		if (quantity > oldQuantity) {
			// Increase number of instances
			final long toDoQty = quantity - oldQuantity;
			for (long l = 0; l < toDoQty; l++) {
				addSolutionComponent(configModel, instanceType);
			}
		} else if (quantity < oldQuantity) {
			// Reduces number of instances
			final long toDoQty = oldQuantity - quantity;
			for (long l = 0; l < toDoQty; l++) {
				// removeSolutionComponent(configModel, instanceType);
			}
		}
		// if quantity == oldQuantity do nothing.
	}

	public IInstanceTypeData getTypeDataForProductName(final ConfigModel configModel, final String nameOfProduct) throws IpcCommandException {
		IInstanceTypeData returnData = null;
		IConfigSession session = null;

		try {
			session = retrieveConfigSession(configModel.getId());
		} catch (final IllegalStateException e) {
			// Fallback. Build your own session
			LOG.warn("Session not found for ConfigModel ID: " + configModel.getId());
			final KBKey kbKey = new KBKeyImpl(nameOfProduct);
			final ConfigModel tempModel = createDefaultConfiguration(kbKey);
			session = retrieveConfigSession(tempModel.getId());
		}

		final String configId = retrievePlainConfigId(configModel.getId());
		try {
			final OrchestratedInstance orchInst = session.getRootInstanceLocal(configId);
			final Instance firstShared = orchInst.getFirstSharedInstance();
			final Config config = firstShared.getConfig();
			final InstanceType[] instanceTypes = config.getNonPartInstanceTypes();

			final List<InstanceTypeData> instanceTypeDataList = new ArrayList<InstanceTypeData>();
			ContainerUtil.createInstTypeDataList(instanceTypes, config.getKboManager(), config, instanceTypeDataList);
			// IInstanceTypeData[] instanceTypeData = session
			// .getNonPartInstanceTypes(rootConfigId);
			if (instanceTypeDataList != null) {
				for (int i = 0; i < instanceTypeDataList.size(); i++) {
					// Add to return list if type is MARA and exclude self
					// reference
					if (instanceTypeDataList.get(i).getExternalType().equals(MARA) && instanceTypeDataList.get(i).getName().equals(nameOfProduct)) {
						returnData = instanceTypeDataList.get(i);
						break;
					}
				}
			}

			if (returnData == null) {
				throw new IpcCommandException("Product is not part of the model and cannot be add to the solution");
			}

		} catch (final IpcCommandException e) {
			LOG.error("Error reading instance type data for product: " + nameOfProduct);
			throw e;
		}

		return returnData;

	}

	@Override
	protected InstanceModel createInstance(final OrchestratedInstance orchestratedInstance) {
		if (orchestratedInstance.getFirstSharedInstance() != null && orchestratedInstance.getFirstSharedInstance().getConfig().isClassical()) {
			return super.createInstance(orchestratedInstance);
		} else {
			return createInstance(orchestratedInstance.getInstanceData(true));
		}
	}

	/**
	 * Helper method to build instanceModels
	 *
	 * @param instanceData
	 * @return
	 */
	protected InstanceModel createInstance(final IInstanceData instanceData) {
		final String instanceId = instanceData.getInstId();
		final InstanceModel instanceModel = new InstanceModelImpl();
		instanceModel.setId(instanceId);
		instanceModel.setName(instanceData.getInstName());
		instanceModel.setLanguageDependentName(instanceData.getInstLname());
		instanceModel.setPosition(instanceData.getInstPosition());
		instanceModel.setConsistent(instanceData.isInstConsistent().booleanValue());
		instanceModel.setComplete(instanceData.isInstComplete().booleanValue());
		instanceModel.setRootInstance(instanceData.getIsRootInstance().booleanValue());

		// FIXME KB: just commented out to compile
		//instanceModel.getExtensionMap().put(SapcustdevsolutionconfigservicesConstants.INST_AUTHOR, instanceData.getInstAuthor());

		return instanceModel;
	}

	@Override
	protected void fillConfigInfo(final ConfigModel configModel, final IConfigInfoData configInfo)
	{
		configModel.setName(configInfo.getConfigName());
		configModel.setKbId(String.valueOf(configInfo.getKbId()));
		configModel.setConsistent(configInfo.isConsistent());
		configModel.setComplete(configInfo.isComplete());
		configModel.setSingleLevel(configInfo.isSingleLevel());

		// FIXME KB: configInfo.getConfigId() should be replaced with product id
		configModel.setKbKey(new KBKeyImpl(configInfo.getKbName(), configInfo.getKbName(), configInfo.getKbLogSys(),
				configInfo.getKbVersion()));
	}

	@Override
	protected ConfigModel fillConfigModel(final String qualifiedId) {
		final ConfigModel configModel = super.fillConfigModel(qualifiedId);
		final SolutionConfigModel solutionConfigModel;
		if (configModel instanceof SolutionConfigModel)
		{
			solutionConfigModel = (SolutionConfigModel) configModel;
		}
		else
		{
			solutionConfigModel = null;
		}


		try {
			final IConfigSession session = retrieveConfigSession(qualifiedId);

			final String configId = retrievePlainConfigId(qualifiedId);

			final IConfigInfoData configInfo = session.getConfigInfo(configId, false);
			// check whether we have classical model
			final String sceMode = configInfo.getSceMode();
			if (IIPCConstants.SCE.equals(sceMode)) {
				// non-classical
				if (solutionConfigModel != null)
				{
					solutionConfigModel.getExtensionMap().put(SapcustdevsolutionconfigservicesConstants.IS_CLASSIC_MODEL,
							SapcustdevsolutionconfigservicesConstants.FALSE);
				}
				final List<InstanceModel> solutionComponents = getSolutionComponents(configModel);
				solutionConfigModel.setSolutionComponents(solutionComponents);
			} else {
				// classical
				if (solutionConfigModel != null)
				{
					solutionConfigModel.getExtensionMap().put(SapcustdevsolutionconfigservicesConstants.IS_CLASSIC_MODEL,
							SapcustdevsolutionconfigservicesConstants.TRUE);
				}
			}
		}

		catch (final IpcCommandException e) {
			throw new IllegalStateException("Cannot fill configuration model", e);
		}

		return configModel;
	}

	@Override
	public boolean updateConfiguration(final ConfigModel configModel) {

		final boolean sscUpdated = super.updateConfiguration(configModel);
		boolean atLeastOneSolutionComponentsUpdated = false;

		final SolutionConfigModel solutionConfigModel = (SolutionConfigModel) configModel;
		final String qualifiedId = configModel.getId();

		final List<InstanceModel> solutionComponents = solutionConfigModel.getSolutionComponents();

		final IConfigSessionClient session = retrieveConfigSession(qualifiedId);

		final String configId = retrievePlainConfigId(qualifiedId);

		for (final InstanceModel solutionComponent : solutionComponents) {
			try {
				boolean solutionComponentsUpdated;
				if (session.getInstance(configId, solutionComponent.getId()) == null) {
					solutionComponentsUpdated = true; // instance has been
														// deleted from the
														// configuration by the
														// engine.
				} else {
					solutionComponentsUpdated = false; //updateInstance(qualifiedId, solutionComponent, configModel);
				}

				if (solutionComponentsUpdated) {
					atLeastOneSolutionComponentsUpdated = true;
				}
			} catch (final IpcCommandException e) {
				throw new IllegalStateException("Could not update instance", e);
			}
		}

		return sscUpdated || atLeastOneSolutionComponentsUpdated;

	}

	protected List<CsticGroupModel> prepareCsticGroups(final OrchestratedInstance orchestratedInstance)
	{
		// Group name
		final String[] groupNames = orchestratedInstance.getCsticGroups(false);
		// Group descriptions
		final String[] groupLanguageDependentNames = orchestratedInstance.getCsticGroups(true);

		// All cstics in instance
		final OrchestratedCstic[] orchastratedCstics = orchestratedInstance.getCstics();
		final List<String> csticNamesInInstance = new ArrayList<>();
		for (final OrchestratedCstic orchastratedCstic : orchastratedCstics)
		{
			csticNamesInInstance.add(orchastratedCstic.getName());
		}

		// Initialize cstic groups
		final List<CsticGroupModel> csticGroupModelList = new ArrayList<>();

		for (int i = 0; i < groupNames.length; i++)
		{
			final CsticGroupModel csticGroupModel = getConfigModelFactory().createInstanceOfCsticGroupModel();
			csticGroupModel.setName(groupNames[i]);
			csticGroupModel.setDescription(groupLanguageDependentNames[i]);
			csticGroupModel.setCsticNames(new ArrayList<String>());
			csticGroupModelList.add(csticGroupModel);

			final OrchestratedCstic[] orchastratedCsticsInGroup = orchestratedInstance.getCstics(groupNames[i]);
			final List<String> csticList = new ArrayList<>();

			for (final OrchestratedCstic orchastratedCstic : orchastratedCsticsInGroup)
			{
				final String csticName = orchastratedCstic.getName();
				csticList.add(csticName);

				if (csticNamesInInstance.contains(csticName))
				{
					csticNamesInInstance.remove(csticName);
				}
			}
			csticGroupModel.setCsticNames(csticList);
		}

		// Add default group
		if (!csticNamesInInstance.isEmpty())
		{
			final CsticGroupModel defaultGroup = getConfigModelFactory().createInstanceOfCsticGroupModel();
			if (!orchestratedInstance.isPartInstance()) {
				Instance lastSharedInstance = orchestratedInstance.getLastSharedInstance();
				if (lastSharedInstance != null && lastSharedInstance.getType() != null) {
					defaultGroup.setName(lastSharedInstance.getType().getName());
					final String ldn = lastSharedInstance.getLangDepName();
					if (ldn != null) {
						final int sepPos = ldn.indexOf('-');
						String description = ldn.substring(sepPos + 1);
						if (description != null && !description.equals("")) {
							defaultGroup.setDescription(description);
						}
					}
				}
			} else {
				defaultGroup.setName(InstanceModel.GENERAL_GROUP_NAME);
			}
			
			defaultGroup.setCsticNames(csticNamesInInstance);
			csticGroupModelList.add(0, defaultGroup);
		}

		return csticGroupModelList;
	}
	
	//Removed in Hybris 6.4, as null check added in product config BCP: 1780042839
/*	@Override
	protected boolean isValueContained(final String valueName, final String[] values)
	{
		boolean isValueContained = false;
		if (values != null && values.length > 0) {
		for (final String value : values)
		{
			if (valueName.equals(value))
			{
				isValueContained = true;
				break;
			}
		}
		}
		return isValueContained;
	}*/

	@Override
	public String changeConfiguration(final ConfigModel configModel)
	{
		final String qualifiedId = configModel.getId();

		final String plainId = retrievePlainConfigId(qualifiedId);
		final IConfigSessionClient session = retrieveConfigSession(qualifiedId);
		if (getConfigurationUpdateAdapter().updateConfiguration(configModel, plainId, session))
		{
			return incrementVersion(configModel);
		}

		return configModel.getVersion();
	}

	protected String incrementVersion(final ConfigModel configModel)
	{
		return String.valueOf(Integer.valueOf(configModel.getVersion()) + 1);
	}
}

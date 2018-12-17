package com.sap.projects.custdev.fbs.slc.frontend.setup;

import com.sap.projects.custdev.fbs.slc.constants.Ysapcustdevsolutionconfigb2bfrontendConstants;

import de.hybris.platform.commerceservices.dataimport.impl.CoreDataImportService;
import de.hybris.platform.commerceservices.dataimport.impl.SampleDataImportService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.commerceservices.setup.events.CoreDataImportedEvent;
import de.hybris.platform.commerceservices.setup.events.SampleDataImportedEvent;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


@SystemSetup(extension = Ysapcustdevsolutionconfigb2bfrontendConstants.EXTENSIONNAME)
public class SolutionConfigSetup extends AbstractSystemSetup
{

	public static final String IMPORT_SYNC_CATALOGS = "syncProducts&ContentCatalogs";
	private static final String POWERTOOLS = "powertools";

	private CoreDataImportService coreDataImportService;
	private SampleDataImportService sampleDataImportService;


	//	@SystemSetup(type = Type.PROJECT, process = Process.ALL)
	//	public void createProjectData3(final SystemSetupContext context)
	//	{
	//		final String impexPatternKey = context.getExtensionName() + "." + ImpExSystemSetup.PARAMETER_PROJECT;
	//		final String oldPatternCfg = Config.getParameter(impexPatternKey);
	//
	//		Config.setParameter(impexPatternKey, "resources/impex/powertools/projectdata*.impex");
	//		// execute IMPEX before CATALOG SYNC
	//		logInfo(context, "############# SAPCUSTDEV SOLUTION CONFIG STARTING IMPEX IMPORT ##############");
	//		final ImpExSystemSetup impexImporter = new ImpExSystemSetup();
	//		impexImporter.createAutoImpexProjectData(context);
	//		logInfo(context, "############# SAPCUSTDEV SOLUTION CONFIG END IMPEX IMPORT ##############");
	//
	//		// execute CATALOG SYNC after IMPEX
	//		final boolean syncCatalogs = getBooleanSystemSetupParameter(context, IMPORT_SYNC_CATALOGS);
	//		if (syncCatalogs)
	//		{
	//			boolean executeSync = true;
	//			int syncCounter = 1;
	//			logInfo(context, "############# SAPCUSTDEV SOLUTION CONFIG STARTING CATALOG SYNC ##############");
	//			while (executeSync && syncCounter <= 5)
	//			{
	//				logInfo(context, syncCounter + " try to trigger catalog sync");
	//				// final PerformResult productSyncReult = executeCatalogSyncJob(
	//				// context, POWERTOOLS + "ProductCatalog");
	//				// final PerformResult contentSyncReult = executeCatalogSyncJob(
	//				// context, POWERTOOLS + "ContentCatalog");
	//				executeSync = synchronizeContentCatalog(context, POWERTOOLS + "ContentCatalog", true);
	//
	//				// logInfo(context, "ContentCatalog sync result: "
	//				// + contentSyncReult.getResult());
	//				// logInfo(context, "ProductCatalog sync result: "
	//				// + productSyncReult.getResult());
	//				// executeSync = !CronJobResult.SUCCESS.equals(contentSyncReult
	//				// .getResult());
	//
	//				// || !CronJobResult.SUCCESS.equals(productSyncReult
	//				// .getResult());
	//				syncCounter++;
	//			}
	//			logInfo(context, "############# SAPCUSTDEV SOLUTION CONFIG END CATALOG SYNC ##############");
	//		}
	//		else
	//		{
	//			logInfo(context, "SAPCUSTDEV SOLUTION CONFIG CATALOG SYNC NOT REQUESTED!");
	//		}
	//		Config.setParameter(impexPatternKey, oldPatternCfg);
	//	}

	/**
	 * Implement this method to create data that is used in your project. This method will be called during the system
	 * initialization.
	 * 
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = SystemSetup.Type.PROJECT, process = SystemSetup.Process.ALL)
	public void createProjectData(final SystemSetupContext context)
	{

		logInfo(context, "############# SAPCUSTDEV SOLUTION CONFIG STARTING IMPEX IMPORT ##############");
		final List<ImportData> importData = new ArrayList<ImportData>();

		final ImportData hybrisImportData = new ImportData();

		hybrisImportData.setProductCatalogName(POWERTOOLS);
		hybrisImportData.setContentCatalogNames(Arrays.asList(POWERTOOLS));
		hybrisImportData.setStoreNames(Arrays.asList(POWERTOOLS));
		importData.add(hybrisImportData);

		getCoreDataImportService().execute(this, context, importData);
		getEventService().publishEvent(new CoreDataImportedEvent(context, importData));

		getSampleDataImportService().execute(this, context, importData);
		getEventService().publishEvent(new SampleDataImportedEvent(context, importData));

		logInfo(context, "############# SAPCUSTDEV SOLUTION CONFIG END CATALOG SYNC ##############");

	}

	public CoreDataImportService getCoreDataImportService()
	{
		return coreDataImportService;
	}

	@Required
	public void setCoreDataImportService(final CoreDataImportService coreDataImportService)
	{
		this.coreDataImportService = coreDataImportService;
	}

	public SampleDataImportService getSampleDataImportService()
	{
		return sampleDataImportService;
	}

	@Required
	public void setSampleDataImportService(final SampleDataImportService sampleDataImportService)
	{
		this.sampleDataImportService = sampleDataImportService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.acceleratorservices.setup.AbstractSystemSetup# getInitializationOptions()
	 */
	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();
		params.add(createBooleanSystemSetupParameter(IMPORT_SYNC_CATALOGS, "Sync Products & Content Catalogs", true));
		return params;
	}

}

/**
 *
 */
package com.sap.projects.custdev.fbs.slc.order.backend.impl.erp.strategy;

import com.sap.projects.custdev.fbs.slc.model.intf.SolutionConfigModel;
import com.sap.projects.custdev.fbs.slc.order.constants.SapcustdevordermgmgtbolConstants;
import de.hybris.platform.sap.core.bol.backend.jco.JCoHelper;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.salesdocument.backend.impl.erp.strategy.ProductConfigurationStrategyImpl;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItem;

import java.util.ArrayList;
import java.util.List;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;


/**
 *
 */
public class SolutionConfigurationStrategyImpl extends ProductConfigurationStrategyImpl
{
	private static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(SolutionConfigurationStrategyImpl.class.getName());


	@Override
	public void writeConfiguration(final JCoConnection connection, final ConfigModel configModel, final String handle,
			final BusinessObject bo)
	{

		//if the configModel is null no changes have been made to the configModel
		//for classical models use standard implementation
		if (configModel != null && isClassicModel(configModel))
		{
			super.writeConfiguration(connection, configModel, handle, bo);
			return;
		}

		//do nothing (should be handled in call to /SLCE/ERP_LORD_SET
		return;
	}

	/**
	 * @param configModel
	 * @return
	 */

	// TODO KB: duplicate of ItemMapperCustDev.isClassicModel method
	private boolean isClassicModel(final ConfigModel configModel)
	{
		if (configModel instanceof SolutionConfigModel)
		{
			final SolutionConfigModel solutionConfigModel = (SolutionConfigModel) configModel;
			// TODO KB: TRUE const should not be used. Boolean.parseBoolean() should be instead
			return SapcustdevordermgmgtbolConstants.TRUE.equals(solutionConfigModel
					.getExtensionData(SapcustdevordermgmgtbolConstants.IS_CLASSIC_MODEL));
		}
		else
		{
			return false;
		}
	}

	/**
	 * @param configModel
	 * @param externalConfigs
	 */
	protected void toExtConfigTable(final ConfigModel configModel, final JCoTable externalConfigs)
	{
		// TODO KB: code duplication ItemMapperCustDev.toExtConfigTable.
		final String extConfiguration;
		if (configModel instanceof SolutionConfigModel)
		{
			final SolutionConfigModel solutionConfigModel = (SolutionConfigModel) configModel;
			extConfiguration = solutionConfigModel.getExtensionMap().get("EXT_CONFIG");
		}
		else
		{
			extConfiguration = "";
		}

		final InstanceModel rootInstance = configModel.getRootInstance();
		final int maxLen = 984;

		if (rootInstance != null)
		{
			final String posex = rootInstance.getPosition();

			sapLogger.debug("Configuration XML to be transfered to backend  " + extConfiguration);

			String extConfigurationPart_i = extConfiguration;
			int i = 0;

			while (extConfigurationPart_i.length() > 0)
			{
				if (maxLen > extConfigurationPart_i.length())
				{
					externalConfigs.setValue("CONTENT", extConfigurationPart_i.substring(0, maxLen));
					extConfigurationPart_i = extConfigurationPart_i.substring(maxLen);
				}
				else
				{
					externalConfigs.setValue("CONTENT", extConfigurationPart_i);
					extConfigurationPart_i = ""; // final step
				}
				extConfigurationPart_i = extConfiguration.substring(i * maxLen, i * maxLen + maxLen);
				externalConfigs.appendRow();
				externalConfigs.setValue("POSEX", posex);
				externalConfigs.setValue("LINE_NO", "" + i);
				i++;
			}

		}

	}

	@Override
	public void readConfiguration(final JCoConnection connection, final SalesDocument salesDoc,
			final List<String> configurableItems)
	{
		final List<String> configurableItemsCleanedUp = removeSSCProducts(salesDoc, configurableItems);
		if (configurableItemsCleanedUp.isEmpty())
		{
			return;
		}

		try
		{
			final JCoFunction function = connection.getFunction(RFC_NAME_READ);
			fillImportParametersRead(function.getImportParameterList(), configurableItemsCleanedUp);
			connection.execute(function);
			final JCoParameterList exportParameterList = function.getExportParameterList();
			final JCoTable instanceTable = exportParameterList.getTable(TABLE_ET_VCFG_INST);
			final JCoTable characteristicTable = exportParameterList.getTable(TABLE_ET_VCFG_CHAR);
			JCoHelper.logCall(RFC_NAME_READ, null, characteristicTable, sapLogger);
			JCoHelper.logCall(RFC_NAME_READ, null, instanceTable, sapLogger);
			createConfigModels(configurableItems, salesDoc, instanceTable, characteristicTable, function.getTableParameterList()
					.getTable(TABLE_TT_REFCHAR));

			addMessages(salesDoc, function.getExportParameterList().getTable(TABLE_MESSAGES));


		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("Could not access function module: " + RFC_NAME_READ, e);
		}

	}

	private List<String> removeSSCProducts(final SalesDocument salesDoc, final List<String> configurableItems)
	{
		final List<String> configurableItemsCleanedUp = new ArrayList<>();
		for (final String itemHandle : configurableItems)
		{
			for (final Item item : salesDoc.getItemList())
			{
				if (itemHandle.equals(item.getHandle()))
				{
					//we have a configurable item
					final ConfigModel configModel = ((CPQItem)item).getProductConfiguration();

					//check whether it is classical
					if (configModel != null && isClassicModel(configModel))
					{
						//yes, put it in cleaned up list
						configurableItemsCleanedUp.add(itemHandle);
						continue;
					}
				}
			}
		}

		return configurableItemsCleanedUp;
	}

}

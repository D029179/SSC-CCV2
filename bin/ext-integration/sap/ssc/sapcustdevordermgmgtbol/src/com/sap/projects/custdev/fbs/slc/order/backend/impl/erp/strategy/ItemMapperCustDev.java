/**
 *
 */
package com.sap.projects.custdev.fbs.slc.order.backend.impl.erp.strategy;

import com.sap.projects.custdev.fbs.slc.model.intf.SolutionConfigModel;
import de.hybris.platform.sap.core.bol.backend.jco.JCoHelper;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.impl.erp.BackendConfigurationException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.ItemMapper;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.BackendState;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItem;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import com.sap.conn.jco.JCoTable;
import com.sap.projects.custdev.fbs.slc.order.constants.SapcustdevordermgmgtbolConstants;


/**
 * @author D032358
 *
 */
public class ItemMapperCustDev extends ItemMapper
{

	public static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(ItemMapper.class.getName());


	/**
	 * Helper to fill item table.
	 *
	 * @param salesDoc
	 *           The sales document
	 * @param itemComV
	 *           Item data (values)
	 * @param itemComX
	 *           Item data (change flag)
	 * @throws BackendConfigurationException
	 */

	public void write(final SalesDocument salesDoc, final Set<String> itemsToBeChanged, final BackendState salesDocR3Lrd,
			final JCoTable itemComV, final JCoTable itemComX, final JCoTable objInst, final JCoTable externalConfigs)
			throws BackendConfigurationException
	{

		boolean isNewItem = false;

		final Iterator<Item> it = salesDoc.iterator();
		int itemCounter = 0;
		while (it.hasNext())
		{

			final Item item = it.next();

			// Do not send sub items
			if (TechKey.isEmpty(item.getParentId()))
			{

				// Decide whether we need to send this item
				if (itemsToBeChanged.contains(item.getHandle()))
				{

					// handle new items
					if (TechKey.isEmpty(item.getTechKey()))
					{
						if (item.getHandle().isEmpty())
						{
							item.createUniqueHandle();
						}
						item.setTechKey(new TechKey(item.getHandle()));

						isNewItem = true;
						// also update the list of items to be changed and
						// those of items to be re-read, because
						// the item handle has changed
						itemsToBeChanged.remove(item.getHandle());
						itemsToBeChanged.add(item.getTechKey().getIdAsString());

					}

					//CustDev extension
					final ConfigModel configModel = ((CPQItem)item).getProductConfiguration();
					if ((null != configModel) && (!isClassicModel(configModel)))
					{
						toExtConfigTable(configModel, externalConfigs);
					}


					// Fill itemComV
					itemComV.appendRow();
					itemComX.appendRow();

					JCoHelper.setValue(itemComV, item.getHandle(), ConstantsR3Lrd.FIELD_HANDLE);

					// Set item number to force items to stay in SD session even
					// if a erroneous item has been corrected through
					// initialising its material number
					if (item.getNumberInt() == 0)
					{
						itemCounter = determineItemPosnr(item, itemCounter);
						JCoHelper.setValue(itemComV, itemCounter, ConstantsR3Lrd.FIELD_POSNR);
						JCoHelper.setValue(itemComX, ConstantsR3Lrd.ABAP_TRUE, ConstantsR3Lrd.FIELD_POSNR);
					}
					else
					{
						itemCounter = item.getNumberInt();
					}

					final String productId = item.getProductId();

					if (productId != null && !productId.isEmpty())
					{
						JCoHelper.setValue(itemComV, item.getProductId(), "MABNR");
					}
					else
					{
						JCoHelper.setValue(itemComV, item.getProductGuid().getIdAsString(), "MABNR");
					}

					salesDocR3Lrd.removeMessageFromMessageList(item.getTechKey(), "b2b.r3lrd.quantityerror");

					// if the item has an error we need to clear the item
					// category
					// because that way the error will be gone
					if (item.isErroneous())
					{
						itemComV.setValue("PSTYV", "");
						itemComX.setValue("PSTYV", ConstantsR3Lrd.ABAP_TRUE);
					}

					if (isNewItem && (item.getQuantity() == null))
					{
						JCoHelper.setValue(itemComV, "1", "KWMENG");
						item.setQuantity(BigDecimal.ONE);
					}

					if (null == item.getQuantity())
					{
						sapLogger.debug("Given quantity was not valid");

						final Message msg = new Message(Message.ERROR, "b2b.r3lrd.quantityerror", null, "");
						salesDocR3Lrd.getOrCreateMessageList(item.getTechKey()).add(msg);
						item.addMessage(msg);
					}

					itemComV.setValue("KWMENG", item.getQuantity());
					itemComV.setValue("VRKME", item.getUnit());


					// The cancellation of items is transferred as rejection
					// code,
					// which
					// is maintained in the shop
					if (item.getOverallStatus().isCancelled())
					{
						final String rejectionCode = item.getRejectionCode();

						JCoHelper.setValue(itemComV, rejectionCode, "ABGRU");
						JCoHelper.setValue(itemComX, ConstantsR3Lrd.ABAP_TRUE, "ABGRU");
					}

					// Requested Delivery Date
					if (item.getReqDeliveryDate() == null
							&& ((salesDoc.getHeader().getReqDeliveryDate() != null) && (!salesDoc.getHeader().getReqDeliveryDate()
									.equals(ConstantsR3Lrd.DATE_INITIAL))))
					{

						item.setReqDeliveryDate(salesDoc.getHeader().getReqDeliveryDate());
					}
					final Date reqDlvDate = item.getReqDeliveryDate();
					if (reqDlvDate != null)
					{
						itemComV.setValue("EDATU", reqDlvDate);
						itemComX.setValue("EDATU", ConstantsR3Lrd.ABAP_TRUE);
					}

					// Fill itemComX
					JCoHelper.setValue(itemComX, item.getHandle(), ConstantsR3Lrd.FIELD_HANDLE);
					JCoHelper.setValue(itemComX, ConstantsR3Lrd.ABAP_TRUE, "MABNR");
					JCoHelper.setValue(itemComX, ConstantsR3Lrd.ABAP_TRUE, "KWMENG");
					JCoHelper.setValue(itemComX, ConstantsR3Lrd.ABAP_TRUE, "VRKME");
					// JCoHelper.setValue(itemComX, "X", "VBELN_REF");
					// JCoHelper.setValue(itemComX, "X", "POSNR_REF");

					if (sapLogger.isDebugEnabled())
					{
						final StringBuffer debugOutput = new StringBuffer("\nItem sent to ERP: ");
						debugOutput.append("\nHandle          : " + itemComV.getString(ConstantsR3Lrd.FIELD_HANDLE));
						debugOutput.append("\nMatId           : " + itemComV.getString("MABNR"));
						debugOutput.append("\nRejection reason: " + itemComV.getString("ABGRU"));
						debugOutput.append("\nQuantity        : " + itemComV.getValue("KWMENG"));
						debugOutput.append("\nUnit            : " + itemComV.getValue("VRKME"));
						debugOutput.append("\nDelivery Date   : " + itemComV.getValue("EDATU"));
						debugOutput.append(" \n");
						sapLogger.debug(debugOutput);
					}

					addToObjInst(objInst, item.getHandle(), "", OBJECT_ID_ITEM);

					if (sapLogger.isDebugEnabled())
					{
						final StringBuffer debugOutput = new StringBuffer("\nOBJINST sent to ERP: ");
						debugOutput.append("\nHandle   : " + item.getHandle());
						debugOutput.append("\nObject Id: " + "ITEM");
						debugOutput.append(" \n");
						sapLogger.debug(debugOutput);
					}
				}
				else
				{
					itemCounter = item.getNumberInt();
					if (sapLogger.isDebugEnabled())
					{
						sapLogger.debug("We don't need to send item: " + item.getHandle());
					}
				}
			}
			else
			{
				if (sapLogger.isDebugEnabled())
				{
					sapLogger.debug("We don't send sub item: " + item.getHandle());
				}
			}

		}

	}

	/**
	 * @param configModel
	 * @param externalConfigs
	 */
	protected void toExtConfigTable(final ConfigModel configModel, final JCoTable externalConfigs)
	{

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
				externalConfigs.appendRow();
				if (maxLen < extConfigurationPart_i.length())
				{
					externalConfigs.setValue("CONTENT", extConfigurationPart_i.substring(0, maxLen));
					extConfigurationPart_i = extConfigurationPart_i.substring(maxLen);
				}
				else
				{
					externalConfigs.setValue("CONTENT", extConfigurationPart_i);
					extConfigurationPart_i = ""; // final step
				}
				externalConfigs.setValue("POSEX", posex);
				externalConfigs.setValue("LINE_NO", "" + i);
				i++;
			}

		}

	}


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
}

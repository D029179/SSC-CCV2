/**
 *
 */
package com.sap.projects.custdev.fbs.slc.order.backend.impl.erp.strategy;

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.HeadTextMapper;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.ItemTextMapper;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.PartnerMapper;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.SetStrategyERP;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.BackendState;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.ERPLO_APICustomerExits;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult.Result;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;


/**
 * @author D032358
 *
 */
public class SetStrategyERPCustDev extends SetStrategyERP
{

	private static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(SetStrategyERPCustDev.class.getName());

	@Override
	public BackendCallResult execute(final BackendState salesDocR3Lrd, final SalesDocument salesDoc,
			final Map<String, Item> itemsERPStatus, final TransactionConfiguration shop, final JCoConnection cn,
			final List<String> itemNewShipTos, final boolean onlyUpdateHeader) throws BackendException
	{

		BackendCallResult retVal;

		final String METHOD_NAME = "execute()";
		final boolean paytypeCOD = false;

		final HeaderMapperCustDev headMapper = (HeaderMapperCustDev) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER_MAPPER);
		final ItemMapperCustDev itemMapper = (ItemMapperCustDev) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM_MAPPER);
		final PartnerMapper partnerMapper = (PartnerMapper) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_MAPPER);
		final ItemTextMapper itemTextMapper = (ItemTextMapper) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM_TEXT_MAPPER);
		itemTextMapper.setConfigTextId(shop.getItemTextID());
		itemTextMapper.setConfigLangIso(shop.getLanguageIso());
		final HeadTextMapper headerTextMapper = (HeadTextMapper) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER_TEXT_MAPPER);
		headerTextMapper.setConfigTextId(shop.getHeaderTextID());
		headerTextMapper.setConfigLangIso(shop.getLanguageIso());


		sapLogger.entering(METHOD_NAME);

		try
		{
			//final JCoFunction function = cn.getFunction(ConstantsR3Lrd.FM_LO_API_SET);
			final JCoFunction function = cn.getFunction("/SLCE/ERP_LORD_SET");

			// getting import parameter
			final JCoParameterList importParams = function.getImportParameterList();
			// get the import structure for the header
			final JCoStructure headComv = importParams.getStructure("IS_HEAD_COMV");
			final JCoStructure headComx = importParams.getStructure("IS_HEAD_COMX");
			final JCoTable ObjInst = importParams.getTable("IT_OBJINST");
			// fill header
			headMapper.write(salesDoc.getHeader(), salesDocR3Lrd, headComv, headComx, shop);

			// Header extension data
			final JCoTable ttObjectContentComV = importParams.getTable("IT_OBJECT_CONTENT_COMV");
			fillHeaderFieldExtensions(salesDoc.getHeader(), ttObjectContentComV);
			Set<String> itemsToBeChanged = null;

			// setting the import table basket_item
			if (itemNewShipTos == null)
			{
				// fill items
				if (!onlyUpdateHeader)
				{

					// first check which items we need to change
					itemsToBeChanged = findItemsToChange(itemsERPStatus, salesDoc, shop);
					final JCoTable ItemComV = importParams.getTable("IT_ITEM_COMV");
					final JCoTable ItemComX = importParams.getTable("IT_ITEM_COMX");
					final JCoTable externalConfigs = importParams.getTable("IT_SLCE_TT_CUXML");
					itemMapper.write(salesDoc, itemsToBeChanged, salesDocR3Lrd, ItemComV, ItemComX, ObjInst, externalConfigs);

					// fill item extension fields
					fillItemFieldExtensions(salesDoc, itemsToBeChanged, ttObjectContentComV);

				}

				// Fill Text
				final JCoTable textComV = importParams.getTable("IT_TEXT_COMV");
				final JCoTable textComX = importParams.getTable("IT_TEXT_COMX");
				headerTextMapper.write(salesDoc.getHeader(), textComV, textComX, ObjInst);
				itemTextMapper.write(salesDoc, itemsToBeChanged, textComV, textComX, ObjInst);
			}
			// set partner info
			final JCoTable PartnerComV = importParams.getTable("IT_PARTY_COMV");
			final JCoTable PartnerComX = importParams.getTable("IT_PARTY_COMX");
			if (!onlyUpdateHeader)
			{
				//partnerMapper.write(salesDoc, PartnerComV, PartnerComX, shop, paytypeCOD, ObjInst);
				partnerMapper.write(salesDoc, PartnerComV, PartnerComX, shop, ObjInst);
			}
			final ERPLO_APICustomerExits custExit = getCustExit();
			if (custExit != null)
			{
				custExit.customerExitBeforeSet(salesDoc, function, cn, sapLogger);
				// call the function
			}

			cn.execute(function);

			if (custExit != null)
			{
				custExit.customerExitAfterSet(salesDoc, function, cn, sapLogger);
			}

			// get export parameter list
			final JCoParameterList exportParams = function.getExportParameterList();

			if (sapLogger.isDebugEnabled())
			{
				logCall(ConstantsR3Lrd.FM_LO_API_SET, importParams, null);
			}

			final JCoStructure esError = exportParams.getStructure("ES_ERROR");

			// do we need to handle error situations on header level?
			retVal = new BackendCallResult();
			if (isRecoverableHeaderError(esError))
			{
				retVal = new BackendCallResult(Result.failure);
			}

			salesDocR3Lrd.setErroneous(esError.getString("ERRKZ").equals("X"));
			markInvalidItems(salesDoc, esError);

			salesDoc.clearMessages();
			salesDoc.getHeader().clearMessages();

			dispatchMessages(salesDoc, exportParams.getTable("ET_MESSAGES"), exportParams.getStructure("ES_ERROR"));

			salesDocR3Lrd.setDocumentInitial(false);

			return retVal;
		}
		finally
		{
			sapLogger.exiting();
		}
	}

}

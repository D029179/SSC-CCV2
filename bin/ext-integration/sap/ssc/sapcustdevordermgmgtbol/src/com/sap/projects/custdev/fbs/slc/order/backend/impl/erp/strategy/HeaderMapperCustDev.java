/**
 * 
 */
package com.sap.projects.custdev.fbs.slc.order.backend.impl.erp.strategy;

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.HeaderMapper;
import de.hybris.platform.sap.core.bol.backend.jco.JCoHelper;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapcommonbol.common.businessobject.interf.Converter;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionHelper;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.LoadOperation;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.BackendState;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.CustomizingHelper;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.GetAllReadParameters;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.sap.conn.jco.ConversionException;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

/**
 * @author D032358
 *
 */
public class HeaderMapperCustDev extends HeaderMapper
{
	
	CommonI18NService commonI18NService;
	public static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(HeaderMapperCustDev.class.getName());

	
	/**
	 * Helper to fill header.
	 * 
	 * @param config
	 * 
	 */
	protected void write(final Header salesDocHeader, final BackendState salesDocR3Lrd, final JCoStructure headComV,
			final JCoStructure headComX, final TransactionConfiguration config)
	{
		// Handle
		headComV.setValue(ConstantsR3Lrd.FIELD_HANDLE, salesDocHeader.getHandle());
		headComX.setValue(ConstantsR3Lrd.FIELD_HANDLE, salesDocHeader.getHandle());

		final Date reqDeliveryDate = salesDocHeader.getReqDeliveryDate();
		final String shipCond = salesDocHeader.getShipCond();
		final String currency = commonI18NService.getCurrentCurrency().getSapCode();
		if (sapLogger.isDebugEnabled())
		{
			final StringBuffer debugOutput = new StringBuffer(75);
			debugOutput.append("Method fillHeader");
			debugOutput.append("\n ID         :         " + salesDocHeader.getTechKey());
			debugOutput.append("\n handle     :         " + salesDocHeader.getHandle());
			debugOutput.append("\n external ID:         " + salesDocHeader.getPurchaseOrderExt());
			debugOutput.append("\n shipping conditions: " + shipCond);
			debugOutput.append("\n req. delivery date : " + reqDeliveryDate);
			debugOutput.append("\n inco1:               " + salesDocHeader.getIncoTerms1());
			debugOutput.append("\n inco2:               " + salesDocHeader.getIncoTerms2());
			debugOutput.append("\n curr:                " + currency);
			sapLogger.debug(debugOutput);
		}
		headComV.setValue("BSTKD", salesDocHeader.getPurchaseOrderExt());
		headComX.setValue("BSTKD", ConstantsR3Lrd.ABAP_TRUE);


		if (currency != null)
		{
			headComV.setValue("WAERK", currency);
			headComX.setValue("WAERK", ConstantsR3Lrd.ABAP_TRUE);
		}

		// shipping condition
		// as one shipping condition is always active, we only set it in case
		// something is available.
		// Otherwise defaulting in backend would not work
		if (shipCond != null && !shipCond.isEmpty())
		{
			headComV.setValue("VSBED", shipCond);
			headComX.setValue("VSBED", ConstantsR3Lrd.ABAP_TRUE);
		}

		// requested delivery date
		if (reqDeliveryDate != null)
		{
			headComX.setValue("VDATU", ConstantsR3Lrd.ABAP_TRUE);
			if (reqDeliveryDate.equals(ConstantsR3Lrd.DATE_INITIAL))
			{
				final Date initial = null;
				headComV.setValue("VDATU", initial);
			}
			else
			{
				headComV.setValue("VDATU", reqDeliveryDate);
			}
		}

		// Incoterms
		if (salesDocHeader.getIncoTerms1() != null)
		{
			headComV.setValue("INCO1", salesDocHeader.getIncoTerms1());
			headComX.setValue("INCO1", ConstantsR3Lrd.ABAP_TRUE);
		}
		if (salesDocHeader.getIncoTerms2() != null)
		{
			headComV.setValue("INCO2", salesDocHeader.getIncoTerms2());
			headComX.setValue("INCO2", ConstantsR3Lrd.ABAP_TRUE);
		}

		// Delivery Block
		if (config.getDeliveryBlock() != null)
		{
			headComV.setValue("LIFSK", config.getDeliveryBlock());
			headComX.setValue("LIFSK", ConstantsR3Lrd.ABAP_TRUE);
		}

		// Only set the field in create mode
		if (salesDocR3Lrd.getLoadState().getLoadOperation().equals(LoadOperation.create))
		{
			// Customer Purch Order Type
			if (config.getDeliveryBlock() != null)
			{
				headComV.setValue("BSARK", config.getCustomerPurchOrderType());
				headComX.setValue("BSARK", ConstantsR3Lrd.ABAP_TRUE);
			}
		}

	}
	
	/**
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}


}

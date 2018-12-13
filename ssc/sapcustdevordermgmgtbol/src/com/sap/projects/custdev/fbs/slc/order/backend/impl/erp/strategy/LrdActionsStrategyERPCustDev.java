/**
 * 
 */
package com.sap.projects.custdev.fbs.slc.order.backend.impl.erp.strategy;

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.LrdActionsStrategyERP;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.SetActiveFieldsListEntry;

import java.util.List;


/**
 * @author Administrator
 * 
 */
public class LrdActionsStrategyERPCustDev extends LrdActionsStrategyERP
{

	@Override
	protected void setActiveFieldsListCreateChange(final List<SetActiveFieldsListEntry> activeFieldsListCreateChange)
	{
		super.setActiveFieldsListCreateChange(activeFieldsListCreateChange);

		//posex required to associate the solution XML with the order line item
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("ITEM", "POSEX"));

	}
}

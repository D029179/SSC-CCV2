/**
 *
 */
package com.sap.projects.custdev.fbs.slc.order.facades;

import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.impl.UiTypeFinderImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;

import java.util.List;


/**
 * @author d051840
 *
 */
public class SolutionConfigUiTypeFinderImpl extends UiTypeFinderImpl
{
	@Override
	protected UiType chooseUiType(final List<UiType> posibleTypes, final CsticModel model)
	{
		UiType uiType;
		if (posibleTypes.isEmpty())
		{
			uiType = UiType.NOT_IMPLEMENTED;
		}
		else if (posibleTypes.size() == 1)
		{
			uiType = posibleTypes.get(0);
		}
		else
		{
			throw new IllegalArgumentException("Cstic: [" + model + "] has an ambigious uiType: [" + posibleTypes + "]");
		}
		return uiType;
	}
}

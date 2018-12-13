package com.sap.projects.custdev.fbs.slc.frontend.util.impl;

import com.sap.projects.custdev.fbs.slc.constants.Ysapcustdevsolutionconfigb2bfrontendWebConstants;

import de.hybris.platform.sap.productconfig.facades.impl.UniqueUIKeyGeneratorImpl;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiStateHandler;

public class SolutionUiStateHandler extends UiStateHandler{
	
	@Override
	protected String extractInstanceNameFromGroupId(final String groupId)
	{
		if (!groupId.contains("$")){
			return super.extractInstanceNameFromGroupId(groupId);
		}
		else{
			final int instanceSeparatorIndex = groupId.indexOf('$');
			int keySeparatorIndex = groupId.indexOf(UniqueUIKeyGeneratorImpl.KEY_SEPARATOR);
			if (instanceSeparatorIndex != -1)
			{
				final int _index = groupId.indexOf('-', instanceSeparatorIndex);
				if (keySeparatorIndex == -1)
				{
					keySeparatorIndex = groupId.length();
				}
				return groupId.substring(_index, keySeparatorIndex);

			}
			return null;
		}
	}

}

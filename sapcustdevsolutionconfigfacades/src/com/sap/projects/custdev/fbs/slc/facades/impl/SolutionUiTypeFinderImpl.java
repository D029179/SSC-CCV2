/*
 * [y] hybris Platform
 *
 * Copyright (c) 2016 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.sap.projects.custdev.fbs.slc.facades.impl;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.sap.productconfig.facades.CPQImageType;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.UiTypeFinder;
import de.hybris.platform.sap.productconfig.facades.UiValidationType;
import de.hybris.platform.sap.productconfig.facades.impl.UiTypeFinderImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Default implementation of the {@link UiTypeFinder}.
 */
public class SolutionUiTypeFinderImpl extends UiTypeFinderImpl
{
	private static final Logger LOG = Logger.getLogger(SolutionUiTypeFinderImpl.class);
	private static final String LOG_CSTIC_NAME = "CsticModel [CSTIC_NAME='";
	
	protected List<UiValidationType> collectPossibleValidationTypes(final CsticModel model, final boolean isDebugEnabled)
	{
		List<UiValidationType> possibleTypes;

		if (isReadonly(model, isDebugEnabled))
		{
			possibleTypes = Collections.singletonList(UiValidationType.NONE);
		}
		else if (isSimpleNumber(model, isDebugEnabled) && (isInput(model, isDebugEnabled)
				|| (isSingleSelection(model, isDebugEnabled) && editableWithAdditionalValue(model, isDebugEnabled))))
		{
			possibleTypes = Collections.singletonList(UiValidationType.NUMERIC);
		}
		else
		{
			possibleTypes = Collections.emptyList();
		}

		return possibleTypes;
	}

	protected boolean isCheckboxList(final CsticModel model, final boolean isDebugEnabled, final boolean hasValueImages)
	{
		boolean isCheckboxList;
		isCheckboxList = isMultiSelection(model, isDebugEnabled) && model.getStaticDomainLength() != 1 && !hasValueImages;

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isCheckboxList='" + isCheckboxList + "']");
		}

		return isCheckboxList;
	}

	protected boolean isMultiSelection(final CsticModel model, final boolean isDebugEnabled)
	{
		final boolean isMultiSelection = isValueTypeSupported(model, isDebugEnabled) && !model.isIntervalInDomain()
				&& !model.isAllowsAdditionalValues() && model.isMultivalued();

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isMultiSelection='" + isMultiSelection + "']");
		}

		return isMultiSelection;
	}

}

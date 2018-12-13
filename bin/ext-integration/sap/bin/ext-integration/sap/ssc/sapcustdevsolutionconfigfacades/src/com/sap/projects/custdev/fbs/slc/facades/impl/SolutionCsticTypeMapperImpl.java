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

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.sap.productconfig.facades.ClassificationSystemCPQAttributesProvider;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.CsticTypeMapper;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.UiTypeFinder;
import de.hybris.platform.sap.productconfig.facades.UiValidationType;
import de.hybris.platform.sap.productconfig.facades.UniqueUIKeyGenerator;
import de.hybris.platform.sap.productconfig.facades.ValueFormatTranslator;
import de.hybris.platform.sap.productconfig.facades.impl.CsticTypeMapperImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.facades.IntervalInDomainHelper;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link CsticTypeMapper}.
 */
public class SolutionCsticTypeMapperImpl extends CsticTypeMapperImpl
{
	private static final Logger LOG = Logger.getLogger(SolutionCsticTypeMapperImpl.class);
	private IntervalInDomainHelper intervalHandler;
	private static final String EMPTY = "";
	private static final Pattern HTML_MATCHING_PATTERN = Pattern.compile(".*\\<.+?\\>.*");


	@Override
	public CsticData mapCsticModelToData(final CsticModel model, final String prefix,
			final Map<String, ClassificationSystemCPQAttributesContainer> nameMap)
	{
		// This method might be called very often (several thousand times) for large customer models.
		// LOG.isDebugEnabled() causes some memory allocation internally, which adds up a lot (2 MB for 90.000 calls)
		// so we read it only once per cstic
		final boolean isDebugEnabled = LOG.isDebugEnabled();
		final boolean isDebugEnabledNameProvider = getNameProvider().isDebugEnabled();
		final CsticData data = new CsticData();
		data.setKey(generateUniqueKey(model, prefix));

		final String name = model.getName();
		final ClassificationSystemCPQAttributesContainer cpqAttributes = getNameProvider().getCPQAttributes(name, nameMap);
		data.setName(name);
		data.setLangdepname(getNameProvider().getDisplayName(model, cpqAttributes, isDebugEnabledNameProvider));
		final String longText = getNameProvider().getLongText(model, cpqAttributes, isDebugEnabledNameProvider);
		data.setLongText(longText);
		data.setLongTextHTMLFormat(containsHTML(longText, isDebugEnabled));

		data.setInstanceId(model.getInstanceId());
		data.setVisible(model.isVisible());
		data.setRequired(model.isRequired());
		data.setIntervalInDomain(model.isIntervalInDomain());

		data.setMaxlength(model.getTypeLength());
		data.setEntryFieldMask(emptyIfNull(model.getEntryFieldMask()));
		fillPlaceholder(model, data);
		data.setAdditionalValue(EMPTY);
		data.setMedia(getNameProvider().getCsticMedia(cpqAttributes));
		
		final boolean useDeltaPrices = getPricingConfigurationParameters().showDeltaPrices();
		final List<CsticValueData> domainValues = createDomainValues(model, cpqAttributes, isDebugEnabled, useDeltaPrices);
		handlePriceData(model, data, domainValues);
		data.setDomainvalues(domainValues);

		data.setConflicts(Collections.emptyList());
		if (CsticModel.AUTHOR_USER.equals(model.getAuthor()))
		{
			data.setCsticStatus(CsticStatusType.FINISHED);
		}
		else
		{
			data.setCsticStatus(CsticStatusType.DEFAULT);
		}

		final UiType uiType = getUiTypeFinder().findUiTypeForCstic(model, data);
		data.setType(uiType);
		final UiValidationType validationType = getUiTypeFinder().findUiValidationTypeForCstic(model);
		data.setValidationType(validationType);

		final String singleValue = model.getSingleValue();
		final String formattedValue = getValueFormatTranslator().format(model, singleValue);
		data.setValue(singleValue);
		data.setFormattedValue(formattedValue);
		data.setLastValidValue(formattedValue);

		if (UiValidationType.NUMERIC == validationType)
		{
			mapNumericSpecifics(model, data);
		}

		if (isDebugEnabled)
		{
			LOG.debug("Map CsticModel to CsticData [CSTIC_NAME='" + name + "';CSTIC_UI_KEY='" + data.getKey() + "';CSTIC_UI_TYPE='"
					+ data.getType() + "';CSTIC_VALUE='" + data.getValue() + "']");
		}
		return data;
	}

	protected void fillPlaceholder(final CsticModel model, final CsticData data)
	{
		if (CsticModel.TYPE_INTEGER == model.getValueType() || CsticModel.TYPE_FLOAT == model.getValueType())
		{
			data.setPlaceholder(emptyIfNull(getIntervalHandler().retrieveIntervalMask(model)));
		}
		else
		{
			data.setPlaceholder(EMPTY);
		}
	}

	protected List<CsticValueData> createDomainValues(final CsticModel model,
			final ClassificationSystemCPQAttributesContainer hybrisNames, final boolean isDebugEnabled, final boolean useDeltaPrices)
	{
		final boolean isDebugEnabledNameProvider = getNameProvider().isDebugEnabled();
		int capa = model.getAssignableValues().size();
		if (model.isConstrained() || model.isMultivalued())
		{
			capa += model.getAssignedValues().size();
		}
		final List<CsticValueData> domainValues;
		if (capa == 0)
		{
			domainValues = Collections.emptyList();
		}
		else
		{
			domainValues = new ArrayList<>(capa);
		}

		for (final CsticValueModel csticValue : model.getAssignableValues())
		{
			final CsticValueData domainValue = createDomainValue(model, csticValue, hybrisNames, isDebugEnabled,
					isDebugEnabledNameProvider, useDeltaPrices);
			domainValues.add(domainValue);
		}
		if (model.isConstrained() || model.isMultivalued())
		{
			for (final CsticValueModel assignedValue : model.getAssignedValues())
			{
				if (!model.getAssignableValues().contains(assignedValue))
				{
					final CsticValueData domainValue = createDomainValue(model, assignedValue, hybrisNames, isDebugEnabled,
							isDebugEnabledNameProvider, useDeltaPrices);
					domainValues.add(domainValue);
				}
			}
		}
		return domainValues;
	}

	@Override
	public void updateCsticModelValuesFromData(final CsticData data, final CsticModel model)
	{
		// This method might be called very often (several thousand times) for large customer models.
		// LOG.isDebugEnabled() causes some memory allocation internally, which adds up a lot (2 MB for 90.000 calls)
		// so we read it only once per cstic
		final boolean isDebugEnabled = LOG.isDebugEnabled();
		handleRetraction(data, model, isDebugEnabled);
		final UiType uiType = data.getType();
		if (isUiTypeMultiselectionValue(uiType) && data.getDomainvalues() != null)
		{
			for (final CsticValueData valueData : data.getDomainvalues())
			{
				final String value = valueData.getName();
				final String parsedValue = getValueFormatTranslator().parse(uiType, value);
				if (valueData.isSelected())
				{
					model.addValue(parsedValue);
				}
				else
				{
					model.removeValue(parsedValue);
				}
			}
		}
		else
		{
			String value = getValueFromCstcData(data, isDebugEnabled);
			if (isUiTypeWithAdditionalValue(uiType))
			{
				value = getValueForUiTypeWithAdditionalValue(data, model, value);
			}
			else
			{
				value = getValueFormatTranslator().parse(uiType, value);
			}

			if (isUiTypeDrownDownAndNullValue(uiType, value))
			{
				model.setSingleValue(null);
			}
			else
			{
				model.setSingleValue(value);
			}
		}

		if (isDebugEnabled)
		{
			final Object[] args = new Object[5];
			args[0] = model.getName();
			args[1] = Integer.valueOf(model.getValueType());
			args[2] = data.getKey();
			args[3] = data.getType();
			args[4] = data.getValue();
			final String format = "Update CsticData to CsticModel [CSTIC_NAME='%s'; CSTIC_VALUE_TYPE='%d'; CSTIC_UI_KEY='%s'; CSTIC_UI_TYPE='%s'; CSTIC_VALUE='%s']";
			final String msg = String.format(format, args);
			LOG.debug(msg);
		}
	}

	protected IntervalInDomainHelper getIntervalHandler()
	{
		return intervalHandler;
	}

	/**
	 * @param intervalHandler
	 */
	public void setIntervalHandler(final IntervalInDomainHelper intervalHandler)
	{
		this.intervalHandler = intervalHandler;
	}
	
}

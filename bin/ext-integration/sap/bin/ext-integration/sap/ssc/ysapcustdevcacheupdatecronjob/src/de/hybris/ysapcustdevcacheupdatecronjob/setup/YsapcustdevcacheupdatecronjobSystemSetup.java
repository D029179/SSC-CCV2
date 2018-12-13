/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.ysapcustdevcacheupdatecronjob.setup;

import static de.hybris.ysapcustdevcacheupdatecronjob.constants.YsapcustdevcacheupdatecronjobConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import de.hybris.ysapcustdevcacheupdatecronjob.constants.YsapcustdevcacheupdatecronjobConstants;
import de.hybris.ysapcustdevcacheupdatecronjob.service.YsapcustdevcacheupdatecronjobService;


@SystemSetup(extension = YsapcustdevcacheupdatecronjobConstants.EXTENSIONNAME)
public class YsapcustdevcacheupdatecronjobSystemSetup
{
	private final YsapcustdevcacheupdatecronjobService ysapcustdevcacheupdatecronjobService;

	public YsapcustdevcacheupdatecronjobSystemSetup(final YsapcustdevcacheupdatecronjobService ysapcustdevcacheupdatecronjobService)
	{
		this.ysapcustdevcacheupdatecronjobService = ysapcustdevcacheupdatecronjobService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		ysapcustdevcacheupdatecronjobService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return YsapcustdevcacheupdatecronjobSystemSetup.class.getResourceAsStream("/ysapcustdevcacheupdatecronjob/sap-hybris-platform.png");
	}
}

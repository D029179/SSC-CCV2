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
package com.hybris.ssclibloader.setup;

import static com.hybris.ssclibloader.constants.SsclibloaderConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.hybris.ssclibloader.constants.SsclibloaderConstants;
import com.hybris.ssclibloader.service.SsclibloaderService;


@SystemSetup(extension = SsclibloaderConstants.EXTENSIONNAME)
public class SsclibloaderSystemSetup
{
	private final SsclibloaderService ssclibloaderService;

	public SsclibloaderSystemSetup(final SsclibloaderService ssclibloaderService)
	{
		this.ssclibloaderService = ssclibloaderService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		ssclibloaderService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return SsclibloaderSystemSetup.class.getResourceAsStream("/ssclibloader/sap-hybris-platform.png");
	}
}

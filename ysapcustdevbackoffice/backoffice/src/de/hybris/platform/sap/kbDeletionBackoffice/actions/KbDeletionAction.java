/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.sap.kbDeletionBackoffice.actions;

import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.util.Config;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zul.Messagebox;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.sap.custdev.projects.fbs.slc.dataloader.facade.DataloaderUtils;
import com.sap.custdev.projects.fbs.slc.dataloader.standalone.DataloaderConfiguration;
import com.sap.sce.kbmt.db.kbmtc_delete_kb;
import com.sap.sxe.db.conn;
import com.sap.sxe.db.db;
import com.sap.sxe.db.imp.jdbc.conn_jdbc;


public class KbDeletionAction implements CockpitAction<SAPConfigurationModel, String>
{
	private static final Logger LOG = Logger.getLogger(KbDeletionAction.class);
	private static final String SSC_DATABASE_TYPE = "crm.system_type";
	private static final String SSC_DATABASE_HOSTNAME = "crm.database_hostname";
	private static final String SSC_DATABASE_NAME = "crm.database";
	private static final String SSC_DATABASE_PORT = "crm.database_port";
	private static final String SSC_DATABASE_USER = "crm.database_user";
	private static final String SSC_DATABASE_PASSWORD = "crm.database_password";
	private static final String SSC_JNDI_USAGE = "crm.ssc_jndi_usage";
	private static final String SSC_JNDI_DATASOURCE = "crm.ssc_jndi_datasource";
	private static final String SSC_DATABASE_CLIENT = "crm.client";

	@Override
	public ActionResult<String> perform(final ActionContext<SAPConfigurationModel> ctx)
	{
		final Map<String, String> dataloaderConfigMap = new HashMap<>();
		final kbmtc_delete_kb delete_kb = new kbmtc_delete_kb();
		ActionResult<String> result = null;
		final String client = "000";
		db sscDb = null;


		LOG.info("++++++++++++++++++++++++++++++++++++++ KB DELETION PERFORM METHOD CALL++++++++++++++++++++++++++++++++++");
		final SAPConfigurationModel configuration = ctx.getData();
		final String kbName = configuration.getSapproductconfig_kbName() != null ? configuration.getSapproductconfig_kbName() : "";
		final String kbVersion = configuration.getSapproductconfig_kbVersion() != null
				? configuration.getSapproductconfig_kbVersion() : "";
		final String kbLogsys = configuration.getSapproductconfig_logsys() != null ? configuration.getSapproductconfig_logsys()
				: "";
		LOG.info("++++++++++++++++++++++++++ KbName: " + kbName + " ++++++++++++++++++++++");
		LOG.info("++++++++++++++++++++++++++ KbVersion: " + kbVersion + " ++++++++++++++++++++++");
		LOG.info("++++++++++++++++++++++++++ KbLogsys: " + kbLogsys + " ++++++++++++++++++++++");

		if (kbName.isEmpty() || kbVersion.isEmpty() || kbLogsys.isEmpty())
		{
			Messagebox.show(ctx.getLabel("action.kbdeletion.mandatoryFields.missing"));
			LOG.error(ctx.getLabel("action.kbdeletion.mandatoryFields.missing"));
			return new ActionResult<String>(ActionResult.ERROR);
		}
		//Populate the configMap with CPQ DB details
		dataloaderConfigMap.put(DataloaderConfiguration.DB_TYPE, Config.getParameter(SSC_DATABASE_TYPE));
		dataloaderConfigMap.put(DataloaderConfiguration.DB_HOST, Config.getParameter(SSC_DATABASE_HOSTNAME));
		dataloaderConfigMap.put(DataloaderConfiguration.DB_NAME, Config.getParameter(SSC_DATABASE_NAME));
		dataloaderConfigMap.put(DataloaderConfiguration.DB_PORT, Config.getParameter(SSC_DATABASE_PORT));
		dataloaderConfigMap.put(DataloaderConfiguration.DB_USERNAME, Config.getParameter(SSC_DATABASE_USER));
		dataloaderConfigMap.put(DataloaderConfiguration.DB_PASSWORD, Config.getParameter(SSC_DATABASE_PASSWORD));
		dataloaderConfigMap.put(DataloaderConfiguration.DB_JNDI_USAGE, Config.getParameter(SSC_JNDI_USAGE));
		dataloaderConfigMap.put(DataloaderConfiguration.DB_JNDI_DATASOURCE, Config.getParameter(SSC_JNDI_DATASOURCE));
		dataloaderConfigMap.put(DataloaderConfiguration.DB_CLIENT, Config.getString(SSC_DATABASE_CLIENT, "000"));

		try
		{
			//Open DB connection
			sscDb = db.getDb();
			if (sscDb != null)
			{
				LOG.info("Existing open SSC DB connection retrieved!");
			}
			else
			{
				LOG.info("Opening new SSC DB connection...");
				sscDb = DataloaderUtils.openSSCDatabase(dataloaderConfigMap);
				if (sscDb == null)
				{
					LOG.error("Cannot open connection to SSC Database!");
				}
			}

			final conn sscConnection = sscDb.db_get_connection();
			if (!(sscConnection instanceof conn_jdbc))
			{
				//throwNewRuntimeException("KB delete: purging of a database is only supported for ssc connections of type conn_jdbc");
				LOG.error("KB delete: Deletion of KB is only supported for ssc connections of type conn_jdbc");
			}

			//calling KB deletion API
			LOG.info("Calling KBDeletion API in Dataloader");
			final boolean deleteStatus = kbmtc_delete_kb.delete_kb(sscDb, client, kbLogsys, kbName, kbVersion);
			LOG.info("Returning from KBDeletion API in Dataloader");
			if (deleteStatus)
			{
				result = new ActionResult<String>(ActionResult.SUCCESS, ctx.getLabel("action.kbdeletion.success", new String[]
				{ kbName, kbVersion, kbLogsys }));
			}
			else
			{
				result = new ActionResult<String>(ActionResult.ERROR, ctx.getLabel("action.kbdeletion.failure", new String[]
				{ kbName, kbVersion, kbLogsys }));
			}
		}
		catch (final Exception e)

		{
			//throwNewRuntimeException("Exception in KB delete");
			LOG.error("Exception in KB delete", e);
		}

		finally

		{
			if (sscDb != null)
			{
				sscDb.db_close();
			}
		}

		if(null!=result){
		Messagebox.show(result.getData());
		LOG.info(result.getData() + " (" + result.getResultCode() + ")");
		}

		return result;
	}

}

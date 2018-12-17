package com.sap.projects.custdev.fbs.slc.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.client.IInstanceTypeData;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;
import com.sap.custdev.projects.fbs.slc.cfg.imp.ConfigSessionImpl;
import com.sap.custdev.projects.fbs.slc.pricing.spc.api.SPCConstants.DataModel;
import com.sap.projects.custdev.fbs.slc.intf.SapcustdevslcservicesProductHandling;

public class SapcustdevslcservicesProductHandlingImpl implements SapcustdevslcservicesProductHandling{
	
	private static final Logger LOG = Logger
			.getLogger(SapcustdevslcservicesProductHandlingImpl.class);
	
	public String[] getRelatedProductsForProduct(final String productId)
	{

		List<String> mara_products = new ArrayList<String>();
		final String MARA = "MARA";
		
		String configId;

		IConfigSession session;

		String sessionId;

		session = new ConfigSessionImpl();

		sessionId = UUID.randomUUID().toString();

		session.createSession("true", sessionId, "crm", false, true, "EN");

		try {
			session.setPricingDatamodel(DataModel.CRM);
		} catch (IpcCommandException e) {
			LOG.error(e);
		}

		try {

			final boolean setRichConfigId = true;

			configId = session.createConfig(null, productId,
					MARA, null, null, null, null,
					null, null, null, null, null,
					setRichConfigId);
			
			IInstanceTypeData[] instanceTypeData = session.getInstanceTypes(configId);
			
			if(instanceTypeData != null)
			{
				for(int i = 0; i < instanceTypeData.length; i++)
				{
					// Add to return list if type is MARA and exclude self reference
					if(instanceTypeData[i].getExternalType().equals(MARA) && !instanceTypeData[i].getName().equals(productId))
					{
						mara_products.add(instanceTypeData[i].getName());
					}
				}
			}
			
		} catch (IpcCommandException e) {
			LOG.error(e);
		}
		
		String[] returnList = new String[mara_products.size()];
		 
		return mara_products.toArray(returnList);
	}
	
}

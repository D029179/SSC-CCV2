package com.sap.projects.custdev.fbs.slc.facades.populator;

import java.util.List;

import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.populator.SolvableConflictPopulator;

public class SolutionConfigSolvableConflictPopulator extends SolvableConflictPopulator {

	protected CsticData findCsticInCsticList(final List<CsticData> cstics, final String csticName, final String instanceId)
	{
		if (cstics != null)
		{
			for (final CsticData cstic : cstics)
			{

				String instanceAtChar = cstic.getInstanceId();
				//handling for advanced mode configuration
				if(instanceAtChar.contains("$") && instanceAtChar.contains("-")){
					int index = instanceAtChar.length();
					instanceAtChar = instanceAtChar.substring(index-1);
				}
				
				if (instanceAtChar == null)
				{
					throw new IllegalArgumentException("Cstic " + cstic.getName() + " must carry an instance ID");
				}
				if (cstic.getName().equals(csticName) && instanceAtChar.equals(instanceId))
				{
					return cstic;
				}
			}
		}
		return null;
	}
}
package com.sap.projects.custdev.fbs.slc.model.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.projects.custdev.fbs.slc.model.intf.SolutionConfigModel;

import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;

public class SolutionConfigModelImpl extends ConfigModelImpl implements
		SolutionConfigModel {
	
	List<InstanceModel> solutionComponents = new ArrayList<InstanceModel>();
	private Map<String, String> extensionMap = new HashMap<>();


	@Override
	public List<InstanceModel> getSolutionComponents() {
		return solutionComponents;
	}

	@Override
	public void setSolutionComponents(List<InstanceModel> solutionComponents) {
		this.solutionComponents = solutionComponents;
	}


	public Map<String, String> getExtensionMap()
	{
		return extensionMap;
	}

	public void setExtensionMap(final Map<String, String> extensionMap)
	{
		this.extensionMap = extensionMap;
	}

	public void putExtensionData(final String key, final String value)
	{
		extensionMap.put(key, value);
	}

	public String getExtensionData(final String key)
	{
		if (extensionMap == null)
		{
			return null;
		}
		return extensionMap.get(key);
	}
}

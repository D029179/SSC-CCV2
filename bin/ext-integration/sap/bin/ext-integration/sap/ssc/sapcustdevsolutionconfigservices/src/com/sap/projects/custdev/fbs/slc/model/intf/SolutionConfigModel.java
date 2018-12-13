package com.sap.projects.custdev.fbs.slc.model.intf;

import java.util.List;
import java.util.Map;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

public interface SolutionConfigModel extends ConfigModel {

	public List<InstanceModel> getSolutionComponents();

	public void setSolutionComponents(List<InstanceModel> solutionComponents);

	Map<String, String> getExtensionMap();
	void setExtensionMap(final Map<String, String> extensionMap);
	void putExtensionData(final String key, final String value);
	String getExtensionData(final String key);
}

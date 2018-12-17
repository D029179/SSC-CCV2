package com.sap.projects.custdev.fbs.slc.intf;

public interface SapcustdevslcservicesProductHandling {
	
	/**
	 * Retrieves related products (type MARA) for a given product Id
	 * @param productId 
	 * @return List of product ids (implicit type ID = MARA)
	 */
	public String[] getRelatedProductsForProduct(final String productId);
}

package com.sap.projects.custdev.fbs.slc.impl;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import com.sap.projects.custdev.fbs.slc.jalo.SapcustdevsolutionconfigservicesTest;


import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

public class SapcustdevslcservicesProductHandlingTest extends
		HybrisJUnit4TransactionalTest {

	private SapcustdevslcservicesProductHandlingImpl productHandler = new SapcustdevslcservicesProductHandlingImpl();
	
	/**
	 * Edit the local|project.properties to change logging behaviour (properties
	 * log4j.*).
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger
			.getLogger(SapcustdevsolutionconfigservicesTest.class.getName());

	@Before
	public void setUp() {
		// implement here code executed before each test
	}

	@After
	public void tearDown() {
		// implement here code executed after each test
	}

	
	/**
	 * Tests related product search
	 */
	@Test
	public void testRelatedProductSearch() {

		final String PRODUCT = "WCEM_MULTILEVEL";

		String[] products = productHandler.getRelatedProductsForProduct(PRODUCT);
		
		assertTrue(products.length == 9);
	}
}

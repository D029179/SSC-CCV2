package com.sap.projects.custdev.fbs.slc.impl;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import com.sap.custdev.projects.fbs.slc.cfg.client.IInstanceTypeData;
import com.sap.custdev.projects.fbs.slc.logging.ServerUtil;
import com.sap.projects.custdev.fbs.slc.SolutionConfigurationService;
import com.sap.projects.custdev.fbs.slc.intf.ConfigurationProviderSlc;
import com.sap.projects.custdev.fbs.slc.jalo.SapcustdevsolutionconfigservicesTest;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.services.impl.ProductConfigurationServiceImpl;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.internal.service.ServicelayerUtils;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;
import de.hybris.platform.sap.productconfig.services.impl.ProviderFactoryImpl;

public class SolutionConfigurationServiceImplTest extends
		HybrisJUnit4TransactionalTest {

	private SolutionConfigurationServiceImpl solutionHandler = null;

	/**
	 * Edit the local|project.properties to change logging behaviour (properties
	 * log4j.*).
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger
			.getLogger(SapcustdevsolutionconfigservicesTest.class.getName());

	private ApplicationContext applicationContext;
	
	@Mock
	private I18NService i18nService;
	
	//private ConfigurationProviderFactoryImpl configurationProviderFactory;
	private ProviderFactoryImpl configurationProviderFactory;
	

	private final String PRODUCT = "WCEM_MULTILEVEL";
	
	@Before
	public void setUp() 
	{
		try
		{
			//System.getProperties().put(ServerUtil.RUNTIME_ENVIRONMENT, ServerUtil.RUNTIME_ENVIRONMENT_STANDALONE);
			MockitoAnnotations.initMocks(this);
			applicationContext = ServicelayerUtils.getApplicationContext();
			solutionHandler = new SolutionConfigurationServiceImpl();

			configurationProviderFactory = (ProviderFactoryImpl) applicationContext.getBean("sapProductConfigConfigurationProviderFactory");

			//configurationProviderFactory.getProvider().setI18NService(i18nService);
			//solutionHandler.setConfigurationProviderFactory(configurationProviderFactory);
			solutionHandler.setProviderFactory(configurationProviderFactory);
			Mockito.when(i18nService.getCurrentLocale()).thenReturn(Locale.ENGLISH);


		}
		catch(Exception e)
		{
			fail("Error initializing SolutionConfigurationService");
		}
	}

	@After
	public void tearDown() 
	{
		// implement here code executed after each test
	}

	/**
	 * Tests related product search
	 */
	@Test
	public void testRelatedProductSearch() 
	{
		KBKey key = new KBKeyImpl(PRODUCT);
		ConfigModel model = solutionHandler.createDefaultConfiguration(key);
		
		//Actual Test Start
		List<IInstanceTypeData> products = solutionHandler.getRelevantProducts(model, PRODUCT);

		assertTrue(products.size() == 6);
		// Test End
	}
	
	/**
	 * Tests addSolutionComponent and getSolutionComponent
	 */
	@Test
	public void testAddComponent()
	{
		
		
		KBKey key = new KBKeyImpl(PRODUCT);
		
		ConfigModel model = solutionHandler.createDefaultConfiguration(key);
		
		
		// Actual Test Start
		// Test 1: Empty List
		List<InstanceModel> components = solutionHandler.getSolutionComponents(model);
		
		assertTrue(components.size() == 0);

		
		// Get TestData
		
		List<IInstanceTypeData> products = solutionHandler.getRelevantProducts(model, PRODUCT);
		
		IInstanceTypeData product1 = products.get(0);
		IInstanceTypeData product2 = products.get(1);
		IInstanceTypeData product3 = products.get(2);
		
		// Test 2: Add 6 components
		solutionHandler.addSolutionComponent(model, product1);
		solutionHandler.addSolutionComponent(model, product1);
		solutionHandler.addSolutionComponent(model, product1);
		
		solutionHandler.addSolutionComponent(model, product2);
		solutionHandler.addSolutionComponent(model, product2);
		
		solutionHandler.addSolutionComponent(model, product3);
		
		components = solutionHandler.getSolutionComponents(model);
		
		assertTrue(components.size() == 6);
		// Actual Test End
		
		for(InstanceModel instanceModel : components)
		{
			solutionHandler.removeSolutionComponent(model, instanceModel.getId());
		}
		
		components = solutionHandler.getSolutionComponents(model);
		
		assertTrue(components.size() == 0);
	}

}

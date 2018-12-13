/*package com.sap.projects.custdev.fbs.slc.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import com.sap.custdev.projects.fbs.slc.cfg.client.IInstanceTypeData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.InstanceTypeData;
import com.sap.projects.custdev.fbs.slc.SolutionConfigurationService;
import com.sap.projects.custdev.fbs.slc.constants.SolutionConfigurationModificationDataStatus;
import com.sap.projects.custdev.fbs.slc.impl.SolutionConfigurationServiceStub;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.sap.productconfig.facades.ConfigConsistenceChecker;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationTestData;
import de.hybris.platform.sap.productconfig.facades.CsticTypeMapper;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.UiTypeFinder;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigConsistenceCheckerImpl;
import de.hybris.platform.sap.productconfig.facades.impl.CsticTypeMapperImpl;
import de.hybris.platform.sap.productconfig.facades.impl.UiTypeFinderImpl;
import de.hybris.platform.sap.productconfig.facades.impl.ValueFormatTranslatorImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;


@UnitTest
public class SolutionConfigurationFacadeImplTest
{

	private KBKeyData kbKey;

	private CartModel shoppingCart;

	private List<AbstractOrderEntryModel> itemsInCart;

	private ProductModel product;

	private UnitModel unit;

	private ConfigurationData configData;

	private final static String PRODUCT_CODE = "YSAP_SIMPLE_POC";


	@Mock
	private ProductConfigurationService configService;

	@Mock
	private CartService cartService;


	@Mock
	private CartEntryModel otherCartItem;

	@Mock
	private ProductDao productDao;

	@Mock
	private ProductFacade productFacade;


	@Mock
	private ConfigPricing configPricing;

	private CommerceCartModification modification;

	private SolutionConfigurationFacadeImpl configFacade;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		final CsticTypeMapper typeMapper = new CsticTypeMapperImpl();
		final UiTypeFinder uiTypeFinder = new UiTypeFinderImpl();
		final ConfigConsistenceChecker configConsistenceChecker = new ConfigConsistenceCheckerImpl();
		typeMapper.setUiTypeFinder(uiTypeFinder);
		typeMapper.setValueFormatTranslater(new ValueFormatTranslatorImpl());

		configFacade = new SolutionConfigurationFacadeImpl();
		configFacade.setConfigurationService(configService);
		configFacade.setCsticTypeMapper(typeMapper);
		configFacade.setConfigConsistenceChecker(configConsistenceChecker);
		configFacade.setProductDao(productDao);
		configFacade.setProductFacade(productFacade);
		configFacade.setConfigPricing(configPricing);
		configFacade.setProductConfigConfigurationService(configService);

		kbKey = new KBKeyData();
		kbKey.setProductCode(PRODUCT_CODE);
		kbKey.setKbName("YSAP_SIMPLE_POC");
		kbKey.setKbLogsys("ABC");
		kbKey.setKbVersion("123");

		shoppingCart = new CartModel();
		shoppingCart.setEntries(itemsInCart);
		product = new ProductModel();
		unit = new UnitModel();

		product.setCode(PRODUCT_CODE);
		product.setUnit(unit);

		configData = new ConfigurationData();
		configData.setKbKey(kbKey);

		final PricingData pricingData = new PricingData();
		pricingData.setBasePrice(ConfigPricing.NO_PRICE);
		pricingData.setSelectedOptions(ConfigPricing.NO_PRICE);
		pricingData.setCurrentTotal(ConfigPricing.NO_PRICE);

		given(cartService.getSessionCart()).willReturn(shoppingCart);
		given(otherCartItem.getPk()).willReturn(PK.parse("1234567890"));
		given(configPricing.getPricingData(any(ConfigModel.class))).willReturn(pricingData);

		final AbstractOrderEntryModel cartItem = new CartEntryModel();
		cartItem.setProduct(product);
		modification = new CommerceCartModification();
		modification.setEntry(cartItem);

		given(productDao.findProductsByCode(any(String.class))).willReturn(null);

		final ProductData subinstance1 = new ProductData();
		subinstance1.setCode("SUBINSTANCE1");
		subinstance1.setDescription("Testdescription");

		final ProductData product1 = new ProductData();
		product1.setCode("product1");
		product1.setDescription("Testdescription");

		given(
				productFacade.getProductForCodeAndOptions("SUBINSTANCE1", Arrays.asList(ProductOption.BASIC, ProductOption.PRICE,
						ProductOption.SUMMARY, ProductOption.DESCRIPTION, ProductOption.GALLERY, ProductOption.CATEGORIES,
						ProductOption.PROMOTIONS, ProductOption.VARIANT_FULL, ProductOption.STOCK, ProductOption.VOLUME_PRICES,
						ProductOption.PRICE_RANGE, ProductOption.VARIANT_MATRIX))).willReturn(subinstance1);

		given(
				productFacade.getProductForCodeAndOptions("product1", Arrays.asList(ProductOption.BASIC, ProductOption.PRICE,
						ProductOption.SUMMARY, ProductOption.DESCRIPTION, ProductOption.GALLERY, ProductOption.CATEGORIES,
						ProductOption.PROMOTIONS, ProductOption.VARIANT_FULL, ProductOption.STOCK, ProductOption.VOLUME_PRICES,
						ProductOption.PRICE_RANGE, ProductOption.VARIANT_MATRIX))).willReturn(product1);

		SolutionConfigurationService solutionConfigConfigurationService = new SolutionConfigurationServiceStub();
		configFacade.setSolutionConfigurationService(solutionConfigConfigurationService);
	}

	private ConfigModel initializeFirstCall()
	{
		final ConfigModel createdConfigModel = ConfigurationTestData.createConfigModelWithSubInstance();
		given(configService.createDefaultConfiguration(any(KBKey.class))).willReturn(createdConfigModel);
		given(configService.retrieveConfigurationModel(createdConfigModel.getId())).willReturn(createdConfigModel);
		return createdConfigModel;
	}

	@Test
	public void testGetConfiguration() throws Exception
	{
		initializeFirstCall();
		ConfigurationData configContent = configFacade.getConfiguration(kbKey);

		assertNotNull(configContent);
		assertEquals(PRODUCT_CODE, configContent.getKbKey().getProductCode());

		configContent = configFacade.getConfiguration(configContent);


		assertNotNull(configContent);
		assertEquals(PRODUCT_CODE, configContent.getKbKey().getProductCode());
	}


	@Test
	public void testPopulateSolutionConfiguration() throws Exception
	{
		initializeFirstCall();
		ConfigurationData configContent = configFacade.getConfiguration(kbKey);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.getModelMap().addAttribute("config", configContent);
		configFacade.populateSolutionConfigData(configContent);

		configContent = (ConfigurationData) modelAndView.getModelMap().get("config");
		List<SolutionConfigurationItemData> solutionItems = configContent.getSolutionItems();
		assertNotNull(solutionItems);

	}



	@Test
	public void testGetRelevantProducts() throws Exception
	{
		initializeFirstCall();
		ConfigurationData configContent = configFacade.getConfiguration(kbKey);
		String configId = configContent.getConfigId();
		String productCode = "product1";

		List<String> relevantProducts = configFacade.getRelevantProductsForSolution(configId, productCode);
		assertEquals(3, relevantProducts.size());
	}

	@Test
	public void testAddToSolutionConfiguration() throws Exception
	{
		//Preparation
		initializeFirstCall();
		ConfigurationData configContent = configFacade.getConfiguration(kbKey);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.getModelMap().addAttribute("config", configContent);
		configFacade.populateSolutionConfigData(configContent);

		String configId = configContent.getConfigId();

		List<SolutionConfigurationItemData> solutionItems = configContent.getSolutionItems();
		int oldSize = solutionItems.size();

		String expectedProductCode = "product1";
		long quantity = 1;

		//Test Execution and populate
		SolutionConfigurationModificationData modificationData = configFacade
				.addToSolution(configId, expectedProductCode, quantity);

		assertEquals(quantity, modificationData.getQuantity());

		configFacade.populateSolutionConfigData(configContent);

		//Read results and test assertions

		ConfigurationData actualConfigContent = (ConfigurationData) modelAndView.getModelMap().get("config");
		List<SolutionConfigurationItemData> actualSolutionItems = actualConfigContent.getSolutionItems();
		assertNotNull(actualSolutionItems);

		int newSize = actualSolutionItems.size();

		assertTrue(newSize > oldSize);
		String actualProductCode = actualSolutionItems.get(newSize - 1).getProduct().getCode();
		assertEquals(expectedProductCode, actualProductCode);
	}

	@Test
	public void testRemoveProductFromSolutionConfiguration() throws Exception
	{
		//Preparation
		initializeFirstCall();
		ConfigurationData configContent = configFacade.getConfiguration(kbKey);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.getModelMap().addAttribute("config", configContent);
		configFacade.populateSolutionConfigData(configContent);

		String configId = configContent.getConfigId();

		List<SolutionConfigurationItemData> solutionItems = configContent.getSolutionItems();
		int oldSize = solutionItems.size();

		String expectedProductCode = "product1";
		long quantity = 1;

		//Test Execution and populate
		SolutionConfigurationModificationData modificationData = configFacade
				.addToSolution(configId, expectedProductCode, quantity);

		assertEquals(quantity, modificationData.getQuantity());
		assertEquals(SolutionConfigurationModificationDataStatus.SUCCESS_ADDED, modificationData.getStatusCode());
		
		configFacade.populateSolutionConfigData(configContent);

		//Read results and test assertions

		ConfigurationData actualConfigContent = (ConfigurationData) modelAndView.getModelMap().get("config");
		List<SolutionConfigurationItemData> actualSolutionItems = actualConfigContent.getSolutionItems();
		assertNotNull(actualSolutionItems);

		int newSize = actualSolutionItems.size();

		assertTrue(newSize > oldSize);
		String actualProductCode = actualSolutionItems.get(newSize - 1).getProduct().getCode();
		assertEquals(expectedProductCode, actualProductCode);

		// Remove and test
		int sizeBeforeRemove = actualConfigContent.getSolutionItems().size();
		SolutionConfigurationItemData solutionItem = actualConfigContent.getSolutionItems().get(0);
		String instanceId = solutionItem.getInstanceId();
		configFacade.removeFromSolution(configId, expectedProductCode, instanceId);
		configFacade.populateSolutionConfigData(configContent);
		int sizeAfterRemove = actualConfigContent.getSolutionItems().size();
		assertEquals(sizeBeforeRemove-1, sizeAfterRemove);


	}

}*/

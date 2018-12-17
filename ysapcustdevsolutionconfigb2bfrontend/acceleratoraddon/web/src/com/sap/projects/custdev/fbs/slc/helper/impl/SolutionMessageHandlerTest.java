package com.sap.projects.custdev.fbs.slc.helper.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sap.projects.custdev.fbs.slc.constants.SolutionConfigurationModificationDataStatus;
import com.sap.projects.custdev.fbs.slc.facades.impl.SolutionConfigurationItemData;
import com.sap.projects.custdev.fbs.slc.facades.impl.SolutionConfigurationModificationData;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessage;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;


public class SolutionMessageHandlerTest
{
	protected SolutionMessageHandler messageHandler = new SolutionMessageHandlerImpl();
	protected RedirectAttributes model = null;
	ProductData testProduct = null;
	private SolutionConfigurationItemData solutionComponentData;
	private SolutionConfigurationModificationData modificationData;

	@Before
	public void setUp()
	{
		model = new RedirectAttributesModelMap();
		testProduct = new ProductData();
		testProduct.setDescription("test desc");
		testProduct.setName("Test Name");
		solutionComponentData = new SolutionConfigurationItemData();
		solutionComponentData.setProduct(testProduct);


		modificationData = new SolutionConfigurationModificationData();
		modificationData.setQuantity(1);
		modificationData.setQuantityAdded(1);
		modificationData.setEntry(solutionComponentData);
	}


	@Test
	public void testPopulateSolutionConfigurationMessagesGeneralSuccess()
	{

		modificationData.setStatusCode(SolutionConfigurationModificationDataStatus.SUCCESS);

		messageHandler.populateModificationAsMessage(model, modificationData);

		List<GlobalMessage> messages = (List<GlobalMessage>) model.getFlashAttributes().get(GlobalMessages.CONF_MESSAGES_HOLDER);
		assertEquals(null, messages);
	}

	@Test
	public void testPopulateSolutionConfigurationMessagesGeneralError()
	{

		modificationData.setStatusCode(SolutionConfigurationModificationDataStatus.ERROR);

		messageHandler.populateModificationAsMessage(model, modificationData);

		List<GlobalMessage> messages = (List<GlobalMessage>) model.getFlashAttributes().get(GlobalMessages.ERROR_MESSAGES_HOLDER);
		assertEquals(1, messages.size());
	}

	@Test
	public void testPopulateSolutionConfigurationMessagesAddedSuccess()
	{
		modificationData.setStatusCode(SolutionConfigurationModificationDataStatus.SUCCESS_ADDED);

		messageHandler.populateModificationAsMessage(model, modificationData);

		List<GlobalMessage> messages = (List<GlobalMessage>) model.getFlashAttributes().get(GlobalMessages.CONF_MESSAGES_HOLDER);
		assertEquals(1, messages.size());
	}

	@Test
	public void testPopulateBindingResultAsMessagesSuccess()
	{

		BindingResult result = mock(BindingResult.class);
		when(result.hasErrors()).thenReturn(false);

		messageHandler.populateBindingResultAsMessage(model, result);

		List<GlobalMessage> confMessages = (List<GlobalMessage>) model.getFlashAttributes()
				.get(GlobalMessages.CONF_MESSAGES_HOLDER);
		assertEquals(null, confMessages);

		List<GlobalMessage> errorMessages = (List<GlobalMessage>) model.getFlashAttributes().get(
				GlobalMessages.ERROR_MESSAGES_HOLDER);
		assertEquals(null, errorMessages);

	}

	@Test
	public void testPopulateBindingResultAsMessagesErrors()
	{

		BindingResult result = mock(BindingResult.class);
		when(result.hasErrors()).thenReturn(true);
		List<ObjectError> errors = new ArrayList<ObjectError>();
		String[] codes = new String[1];
		codes[0] = "typeMismatch";
		ObjectError error = new ObjectError("objectname", codes, null, "testMessage");
		errors.add(error);
		when(result.getAllErrors()).thenReturn(errors);

		messageHandler.populateBindingResultAsMessage(model, result);

		List<GlobalMessage> confMessages = (List<GlobalMessage>) model.getFlashAttributes()
				.get(GlobalMessages.CONF_MESSAGES_HOLDER);
		assertEquals(null, confMessages);

		List<GlobalMessage> errorMessages = (List<GlobalMessage>) model.getFlashAttributes().get(
				GlobalMessages.ERROR_MESSAGES_HOLDER);
		assertEquals(1, errorMessages.size());

	}
}

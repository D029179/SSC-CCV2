package com.sap.projects.custdev.fbs.slc.controllers;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sap.projects.custdev.fbs.slc.facades.SolutionConfigurationFacade;

import de.hybris.platform.acceleratorservices.controllers.page.PageType;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.SearchBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.util.MetaSanitizerUtil;
import de.hybris.platform.b2bacceleratorfacades.api.search.SearchFacade;
import de.hybris.platform.b2bacceleratorfacades.search.data.ProductSearchStateData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.SessionAccessFacade;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.pages.SearchPageController;


@Controller
@Scope("tenant")
@RequestMapping()
public class FindProductForSolutionController extends SearchPageController
{
	private static final String SEARCH_CMS_PAGE_ID = "search";

	@Resource(name = "searchBreadcrumbBuilder")
	private SearchBreadcrumbBuilder searchBreadcrumbBuilder;

	@Resource(name = "b2bProductFlexibleSearchFacade")
	private SearchFacade<ProductData, ProductSearchStateData> flexibleSearchProductSearchFacade;

	@Resource(name = "b2bProductFacade")
	private ProductFacade productFacade;

	@Resource(name = "productService")
	private ProductService productService;

	@Resource(name = "sapcustdevSolutionConfigFacade")
	private SolutionConfigurationFacade solutionConfigFacade;

	@Resource(name = "sapProductConfigSessionAccessFacade")
	private SessionAccessFacade sessionAccessFacade;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(FindProductForSolutionController.class);

	@RequestMapping(value = "/**/{productCode:.*}/configur*/findRelatedProducts", method = RequestMethod.GET)
	public String findRelatedProducts(@PathVariable("productCode") final String productCode, final Model model, HttpServletRequest request)
			throws CMSItemNotFoundException, UnsupportedEncodingException
	{
		final UiStatus uiStatus = sessionAccessFacade.getUiStatusForProduct(productCode);

		final String configId;
		if (uiStatus != null)
		{
			configId = uiStatus.getConfigId();
		}
		else
		{
			configId = null;
		}

		List<String> relevantProductCodes = solutionConfigFacade.getRelevantProductsForSolution(configId, productCode);
		String keywords = StringUtils.join(relevantProductCodes, ',');
		//String keywords = "WCEM_MULTILEVEL,WCEM_CRM,WCEM_PAYMENT";
		String searchQuery = "";
		String sortCode = null;
		AbstractSearchPageController.ShowMode showMode = AbstractSearchPageController.ShowMode.Page;
		int page = 0;

		if (StringUtils.isNotBlank(keywords))
		{
			searchQuery = keywords;
		}
		else
		{
			if (StringUtils.isNotBlank(searchQuery))
			{
				keywords = StringUtils.split(searchQuery, ":")[0];
			}
		}


		//performAdvancedSearch(searchQuery, onlyProductIds, isCreateOrderForm, page, showMode, sortCode, searchResultType, model);
		//advanceSearchResults(StringUtils.EMPTY, searchResultType, false, onlyProductIds, isCreateOrderForm, searchQuery, page, showMode, sortCode, model);

		//refineSearch(searchQuery, page, showMode, sortCode, null, request, model); //passing searchText as null
		final ProductSearchStateData searchState = new ProductSearchStateData();
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(keywords);
		searchState.setPopulateVariants(false);
		searchState.setQuery(searchQueryData);

		//SearchPageData<ProductData> searchPageData = new ProductSearchPageData();
		final PageableData pageableData = createPageableData(page, 10, sortCode, showMode);

		SearchPageData<ProductData> searchPageData = flexibleSearchProductSearchFacade.search(searchState, pageableData);
		populateModel(model, searchPageData, showMode);

		String metaInfoText = null;
		if (StringUtils.isEmpty(keywords))
		{
			metaInfoText = MetaSanitizerUtil.sanitizeDescription(getMessageSource().getMessage(
					"search.advanced.meta.description.title", null, getCurrentLocale()));
		}
		else
		{
			metaInfoText = MetaSanitizerUtil.sanitizeDescription(keywords);
		}

		model.addAttribute(WebConstants.BREADCRUMBS_KEY, searchBreadcrumbBuilder.getBreadcrumbs(null, productCode, false));


		model.addAttribute("isFindRelatedProducts", "true");

		final ProductData productData = productFacade.getProductForCodeAndOptions(productCode, Arrays.asList(ProductOption.BASIC,
				ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION, ProductOption.GALLERY,
				ProductOption.CATEGORIES, ProductOption.PROMOTIONS, ProductOption.VARIANT_FULL, ProductOption.STOCK,
				ProductOption.VOLUME_PRICES, ProductOption.PRICE_RANGE, ProductOption.VARIANT_MATRIX));


		model.addAttribute("productToSearchForRelatedProducts", productData);

		storeCmsPageInModel(model, getContentPageForLabelOrId(SEARCH_CMS_PAGE_ID));

		addMetaData(model, "search.meta.description.results", metaInfoText, "search.meta.description.on", PageType.PRODUCTSEARCH,
				"no-index,follow");

		String pageName =  "addon:/ysapcustdevsolutionconfigb2bfrontend/pages/search/searchGridPage";//getViewForPage(model);

		return pageName;
	}

	protected void addMetaData(final Model model, final String metaPrefixKey, final String searchText, final String metaPostfixKey,
			final PageType pageType, final String robotsBehaviour)
	{
		final String metaDescription = MetaSanitizerUtil.sanitizeDescription(getMessageSource().getMessage(metaPrefixKey, null,
				getCurrentLocale())
				+ " "
				+ searchText
				+ " "
				+ getMessageSource().getMessage(metaPostfixKey, null, getCurrentLocale())
				+ " "
				+ getSiteName());
		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(searchText);
		setUpMetaData(model, metaKeywords, metaDescription);

		model.addAttribute("pageType", pageType.name());
		model.addAttribute("metaRobots", robotsBehaviour);
	}

	private Locale getCurrentLocale()
	{
		return getI18nService().getCurrentLocale();
	}

	protected ProductFacade getProductFacade()
	{
		return productFacade;
	}

	protected void setProductFacade(ProductFacade productFacade)
	{
		this.productFacade = productFacade;
	}


	protected ProductService getProductService()
	{
		return productService;
	}


	protected void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected SolutionConfigurationFacade getSolutionConfigFacade()
	{
		return solutionConfigFacade;
	}

	protected void setSolutionConfigFacade(SolutionConfigurationFacade solutionConfigFacade)
	{
		this.solutionConfigFacade = solutionConfigFacade;
	}



}

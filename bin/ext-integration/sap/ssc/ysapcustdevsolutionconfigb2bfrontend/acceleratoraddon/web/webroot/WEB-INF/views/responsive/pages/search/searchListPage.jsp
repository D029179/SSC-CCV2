<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="productConfig" tagdir="/WEB-INF/tags/addons/ysapproductconfigb2baddon/responsive/product" %>
<%@ taglib prefix="solutionConfig" tagdir="/WEB-INF/tags/addons/ysapcustdevsolutionconfigb2bfrontend/responsive/product" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="navPage" tagdir="/WEB-INF/tags/addons/ysapcustdevsolutionconfigb2bfrontend/responsive/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>

<template:page pageTitle="${pageTitle}">
	<jsp:attribute name="pageScripts">
		<script type="text/javascript" src="${commonResourcePath}/js/acc.productlisting.js"></script>
	</jsp:attribute>
	<jsp:body>

	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>

	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<div class="span-6 facetNavigation">
		<nav:facetNavAppliedFilters pageData="${searchPageData}"/>
		<nav:facetNavRefinements pageData="${searchPageData}"/>
		<cms:pageSlot position="Section4" var="feature">
			<cms:component component="${feature}" />
		</cms:pageSlot>
	</div>

	<div class="span-18 last">

		<cms:pageSlot position="Section2" var="feature" element="div" >
			<cms:component component="${feature}" />
		</cms:pageSlot>


		<c:choose>
		  <c:when test="${isFindRelatedProducts}">
		    	<div class="results">
					 <h1><spring:theme code="sapcustdev.search.page.searchText.findRelatedProducts" arguments="${productToSearchForRelatedProducts.name}"/></h1>
				</div>
		  </c:when>
		  <c:otherwise>
		  		<div class="results">
					<h1><spring:theme code="search.page.searchText" arguments="${searchPageData.freeTextSearch}"/></h1>
				</div>
		  </c:otherwise>
		</c:choose>



		<nav:searchSpellingSuggestion spellingSuggestion="${searchPageData.spellingSuggestion}" />

		<navPage:pagination top="true"  supportShowPaged="false"
									 supportShowAll="false"
									 searchPageData="${searchPageData}"
									 searchUrl="${searchPageData.currentQuery.url}"
									 numberPagesShown="${numberPagesShown}"/>
		<c:choose>
		  <c:when test="${isFindRelatedProducts}">
		    	<solutionConfig:productLister pageData="${searchPageData}" path="/search" productToSearchForRelatedProducts="${productToSearchForRelatedProducts}"/>
		  </c:when>
		  <c:otherwise>
		  		<productConfig:productLister pageData="${searchPageData}" path="/search"/>
		  </c:otherwise>
		</c:choose>


		<navPage:pagination top="false"  supportShowPaged="false"
									 supportShowAll="false"
									 searchPageData="${searchPageData}"
									 searchUrl="${searchPageData.currentQuery.url}"
									 numberPagesShown="${numberPagesShown}"/>




	</div>
</jsp:body>

</template:page>
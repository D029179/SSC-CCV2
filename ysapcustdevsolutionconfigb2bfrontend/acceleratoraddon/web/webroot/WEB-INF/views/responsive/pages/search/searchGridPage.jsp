<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/responsive/storepickup" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="solutionConfig" tagdir="/WEB-INF/tags/addons/ysapcustdevsolutionconfigb2bfrontend/responsive/product" %>
<%@ taglib prefix="navPage" tagdir="/WEB-INF/tags/addons/ysapcustdevsolutionconfigb2bfrontend/responsive/nav" %>

<template:page pageTitle="${pageTitle}">

	<div class="row">
		<div class="col-xs-3">
			<cms:pageSlot position="ProductLeftRefinements" var="feature">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
		</div>
		<div class="col-sm-12 col-md-9">
            <div class="row">
                <div class="col-xs-12 product-list-wrapper">
					<div class="results">
						<h1><spring:theme code="sapcustdev.search.page.searchText.findRelatedProducts" arguments="${productToSearchForRelatedProducts.code}"/></h1>
					</div>

					<nav:searchSpellingSuggestion spellingSuggestion="${searchPageData.spellingSuggestion}" />

					<navPage:pagination top="true"  supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}"  searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"  numberPagesShown="${numberPagesShown}"/>

					<ul class="product-listing product-grid row">
						<c:forEach items="${searchPageData.results}" var="product" varStatus="status">
							<solutionConfig:productListerGridItem product="${product}"/>
						</c:forEach>
					</ul>

					<div id="addToCartTitle" style="display:none">
						<div class="add-to-cart-header">
							<div class="headline">
								<span class="headline-text"><spring:theme code="basket.added.to.basket"/></span>
							</div>
						</div>
					</div>

					<navPage:pagination top="false"  supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}"  searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"  numberPagesShown="${numberPagesShown}"/>
				</div>
            </div>
		</div>
	</div>

	<storepickup:pickupStorePopup />

</template:page>

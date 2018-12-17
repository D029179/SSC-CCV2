<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData" %>
<%@ attribute name="path" required="true" type="java.lang.String" %>
<%@ attribute name="itemType" required="false" type="java.lang.String" %> <%-- Possible values CATALOG, ORDERFORM, FILTER --%>
<%@ taglib prefix="productConfig" tagdir="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/product" %>
<%@ taglib prefix="solutionConfig" tagdir="/WEB-INF/tags/addons/ysapcustdevsolutionconfigb2bfrontend/responsive/product" %>
<%-- Order form related parameters  --%>
<%@ attribute name="isOnlyProductIds" required="false" type="java.lang.Boolean" %>
<%@ attribute name="filterSkus" required="false" type="java.util.List" %>
<%@ attribute name="productToSearchForRelatedProducts" required="false"
	type="de.hybris.platform.commercefacades.product.data.ProductData"%>

<%@ taglib prefix="product" tagdir="/WEB-INF/tags/addons/b2bacceleratoraddon/responsive/product" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>


<c:set value="${not empty isOnlyProductIds && isOnlyProductIds}" var="isOnlyProductIds"/>
<c:if test="${empty itemType}">
	<c:set value="CATALOG" var="itemType"/>
</c:if>
<c:set value="${itemType eq 'ORDERFORM'}" var="isOrderForm"/>
<c:url value="${path}" var="currentURL"/>

<%--  the id=resultsList is used in the script to get all the scrolling data  --%>
<div id="resultsList" class="productList"
	data-isOrderForm="${isOrderForm}"
	data-current-path="${currentURL}"
	data-current-query="${pageData.currentQuery.query.value}"
	data-isOnlyProductIds="${isOnlyProductIds}"
	 >

	<common:globalMessages/>

	<c:forEach items="${pageData.results}" var="product">
		<c:choose>
			<c:when test="${itemType eq 'CATALOG' || (itemType eq 'ORDERFORM' && empty product.variantMatrix)}">
				<solutionConfig:productListerItem product="${product}" isOrderForm="${isOrderForm}"/>
			</c:when>
			<c:when test="${itemType eq 'ORDERFORM' && not empty product.variantMatrix}">
				<product:productListerOrderForm product="${product}" filterSkus="${filterSkus}" />
			</c:when>
			<c:when test="${itemType eq 'FILTER'}">
				<product:productFilterOrderForm product="${product}" />
			</c:when>
			<c:otherwise>
				<solutionConfig:productListerItem product="${product}" isOrderForm="${isOrderForm}" productToSearchForRelatedProducts="${productToSearchForRelatedProducts}"/>
			</c:otherwise>
		</c:choose>
	</c:forEach>
</div>
<common:infiniteScroll/>

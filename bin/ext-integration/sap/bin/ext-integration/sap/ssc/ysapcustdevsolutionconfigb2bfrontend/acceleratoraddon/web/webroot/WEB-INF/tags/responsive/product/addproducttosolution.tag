<%@ attribute name="product" required="true"
	type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ attribute name="productToSearchForRelatedProducts" required="false"
	type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:if test="${isFindRelatedProducts}">
	<c:choose>
		<c:when test="${isFindRelatedProducts}">
			<div class="prod_add_to_cart">
		</c:when>
		<c:otherwise>
			<div class="prod_add_to_cart" style="padding-left:13px">
		</c:otherwise>
	</c:choose>
	<spring:url value="${product.code}/addRelatedProduct" var="addRelatedProductUrl"/>
	<form:form id="addRelatedProductForm-stay" class="configure_form additional_button" action="${addRelatedProductUrl}">
		<input type="hidden" id="productToSearchForRelatedProducts" name="productToSearchForRelatedProducts"
			value="${productToSearchForRelatedProducts.code}" />
		<input type="hidden" id="productToAddToSolution" name="productToAddToSolution"
			value="${product.code}" />
	<c:choose>
		<c:when test="${isFindRelatedProducts}">
			<button type="submit" class="btn btn-primary">
				<spring:theme text="Add to solution (not translated)" code="sapcustdev.solutionconfig.addrelatedproduct.button" />
			</button>
		</c:when>
		<c:otherwise>
			<button type="submit" class="btn btn-primary btn-block">
				<spring:theme text="Add to solution (not translated)" code="sapcustdev.solutionconfig.addrelatedproduct.button" />
			</button>
		</c:otherwise>
	</c:choose>
	</form:form>
	</div>
</c:if>
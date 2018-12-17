<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="configData" required="true" type="de.hybris.platform.sap.productconfig.facades.ConfigurationData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>

<c:set var="bindResult"
	value="${requestScope['org.springframework.validation.BindingResult.config']}" />
	
<div id="cartItems" class="clear">
	<div class="headline">
		<spring:theme code="sapcustdev.solutionconfig.listrelatedproducts.title"/>
		<span class="cartId"/>
	</div>
	
	<table class="cart">
		<thead>
			<tr>
				<th id="header2" colspan="2"><spring:theme code="basket.page.title"/></th>
				<th id="header3"><spring:theme code="basket.page.itemPrice"/></th>
				<th id="header4"><spring:theme code="sapcustdev.solutionconfig.listrelatedproducts.columns.actions"/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${configData.solutionItems}" var="entry">
				<c:url value="${entry.product.url}" var="productUrl"/>
				<tr class="cartItem">
					
					<td headers="header2" class="thumb">
						<product:productPrimaryImage product="${entry.product}" format="thumbnail"/>
					</td>
					
					<td headers="header2" class="details">
						<ycommerce:testId code="cart_product_name">
							<div class="itemName">${entry.product.name}</div>
						</ycommerce:testId>
						
						<c:set var="entryStock" value="${entry.product.stock.stockLevelStatus.code}"/>
						
						<c:forEach items="${entry.product.baseOptions}" var="option">
							<c:if test="${not empty option.selected and option.selected.url eq entry.product.url}">
								<c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
									<div>
										<strong>${selectedOption.name}:</strong>
										<span>${selectedOption.value}</span>
									</div>
									<c:set var="entryStock" value="${option.selected.stock.stockLevelStatus.code}"/>
									<div class="clear"></div>
								</c:forEach>
							</c:if>
						</c:forEach>
						
					</td>
					
					
					<td headers="header3" class="itemPrice">
						<format:price priceData="${entry.basePrice}" displayFreeForZero="true"/>
					</td>	
					
					
					<td headers="header4" class="quantity">				
						<c:if test="${entry.updateable}" >
							<ycommerce:testId code="cart_product_removeProduct">
								<spring:theme code="text.iconCartRemove" var="iconCartRemove"/>
								<a href="#" id="RemoveSolutionProduct_${entry.entryNumber}" class="submitRemoveSolutionProduct">
									<theme:image code="img.iconCartRemove" alt="${iconCartRemove}" title="${iconCartRemove}"/>
								</a>
							</ycommerce:testId>
						</c:if>
						<c:url value="config/updateSolution" var="solutionUpdateFormAction" />
						<form:form id="updateSolutionForm${entry.entryNumber}" action="${solutionUpdateFormAction}" method="post" modelAttribute="updateSolutionQuantityForm${entry.entryNumber}">
							<input type="hidden" name="entryNumber" value="${entry.entryNumber}"/>
							<input type="hidden" name="solutionComponentProductCode" value="${entry.product.code}"/>
							<input type="hidden" name="solutionComponentInstanceId" value="${entry.instanceId}"/>
						</form:form>
					</td>
					 
				 </tr>
			</c:forEach>
		</tbody>
	</table>
	
	
	
</div>


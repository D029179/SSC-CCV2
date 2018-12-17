<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="config" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/configuration"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>
<%@ taglib prefix="conf" uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>

<c:set var="bindResult" value="${requestScope['org.springframework.validation.BindingResult.config']}" />
<c:set var="validationErrors" value="${conf:validationError(bindResult)}" />
<c:set var="conflicts" value="${conf:conflicts(bindResult)}" />
<c:set var="devModeOn" value="${requestScope['cpq.devmode.enabled']}"/>
<c:if test="${devModeOn eq true}">
	<script type="text/javascript">
        var CPQ = CPQ || {};
        CPQ.devMode = true;
	</script>
</c:if>

<div id="configurationForm" class="cpq-form">
	<form:form id="configform" method="POST" modelAttribute="config">
		<script>
			var addedToCart = false;
			var errorExist = false;
			var conflicts = false;
		</script>
		<c:choose>
			<c:when test="${not empty addedToCart}">
				<c:choose>
					<c:when test="${addedToCart}">
						<script>
							addedToCart = true;
						</script>
					</c:when>
				</c:choose>
			</c:when>
		</c:choose>

		<c:if test="${fn:length(conflicts) gt 0}">
		   <!-- used in JavaScript to check if it is needed to jump to top of page -->
		  <script>
			conflicts = true;
		  </script>
		</c:if>
		
		<c:if test="${fn:length(validationErrors) gt 0}" >
		  <script>
		   errorExist = true;
		  </script>
		</c:if>

		<div class="cpq-groups">
			<config:group group="${config.groupToDisplay.group}" pathPrefix="${config.groupToDisplay.path}" hideExpandCollapse="true" />
		</div>

		<form:input type="hidden" value="${config.kbKey.productCode}" path="kbKey.productCode" />
		<form:input type="hidden" value="${config.configId}" path="configId" />
		<form:input type="hidden" value="" path="selectedGroup" />
		<form:input type="hidden" value="${config.cartItemPK}" path="cartItemPK" />
		<form:input type="hidden" value="${autoExpand}" path="autoExpand" />
		<form:input type="hidden" value="${focusId}" path="focusId" />
		<form:input type="hidden" value="${config.groupIdToDisplay}" path="groupIdToDisplay" />
		<form:input type="hidden" value="${config.quantity}" path="quantity" />
		<form:input type="hidden" value="${config.groupToDisplay.groupIdPath}" path="groupToDisplay.groupIdPath" />
		<form:input type="hidden" value="${config.groupToDisplay.path}" path="groupToDisplay.path" />
		<form:input type="hidden" value="" path="groupIdToToggle" />
		<form:input type="hidden" value="" path="groupIdToToggleInSpecTree" />
		<form:input type="hidden" value="false" path="forceExpand" />
		<form:input type="hidden" value="${config.hideImageGallery}" path="hideImageGallery" />
		<form:input type="hidden" value="" path="cpqAction" />
		

	</form:form>
</div>


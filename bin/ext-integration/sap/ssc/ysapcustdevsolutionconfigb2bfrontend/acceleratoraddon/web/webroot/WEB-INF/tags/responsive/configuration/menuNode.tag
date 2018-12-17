<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="solutionconfig" tagdir="/WEB-INF/tags/addons/ysapcustdevsolutionconfigb2bfrontend/responsive/configuration"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cssConf" uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>  
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ attribute name="group" required="true" type="de.hybris.platform.sap.productconfig.facades.UiGroupData"%>
<%@ attribute name="level" required="true" type="java.lang.Integer"%>
<%@ attribute name="title" required="false" type="java.lang.String"%>


<jsp:useBean id="cons" class="de.hybris.platform.sap.productconfig.frontend.util.impl.ConstantHandler" scope="session" />
<!--  temporary, will be removed once conflicts are displayed on the UI -->
<c:if test="${group.groupType ne 'CONFLICT' and group.groupType ne 'CONFLICT_HEADER'}">

	<c:set var="isMenuNode" value="${cssConf:menuNodeStyleClass(group, level)}" />

	<c:choose>
		<c:when test="${not empty title}">
			<c:set var="groupTitle" value="${title}" />
		</c:when>
		<c:when test="${group.name eq cons.getGeneralGroupName()}">
			<spring:message code="sapproductconfig.group.general.title" text="General (Default)" var="groupTitle" />
		</c:when>
		<c:otherwise>
			<c:set var="groupTitle" value="${group.description}" />
		</c:otherwise>
	</c:choose>

	<c:set var="mergeWithSubGroup" value="${group.oneConfigurableSubGroup && fn:length(group.subGroups) == 1}" />
	<c:set var="hasOnlyGeneralSubGroup" value="${(fn:length(group.subGroups) == 1) && (group.oneConfigurableSubGroup && !(group.subGroups[0] eq cons.getGeneralGroupName()))}" />

	<c:if test="${!mergeWithSubGroup}">
		<spring:message code="sapproductconfig.menu.noCfg" text="${groupTitle} is not configurable" var="notConfigurableMessage"
			arguments="${group.description}" />
		<div id="menuNode_${group.id}"
			class="${cssConf:menuNodeStyleClass(group, level)} <c:if test="${group.id eq config.groupIdToDisplay}"> cpq-menu-leaf-selected</c:if>">
			<div id="menuTitle_${group.id}" class="cpq-menu-title"
				<c:if test="${!group.configurable}"> title="${notConfigurableMessage}"</c:if>
				<c:if test="${group.configurable}"> title="${groupTitle}"</c:if>>${groupTitle}</div>
				<c:if test="${!fn:containsIgnoreCase(isMenuNode,'cpq-menu-node cpq-menu-expanded')}">
					<div id="cpqMenuStatusIcon" class="cpq-status-icon">
							<c:if test="${group.numberErrorCstics != 0}">${group.numberErrorCstics}</c:if>
					</div>
				</c:if>
		</div>
	</c:if>


		<c:if test="${!group.collapsedInSpecificationTree || mergeWithSubGroup}">
			<c:choose>
				<c:when test="${mergeWithSubGroup}">
					<c:choose>
						<c:when test="${hasOnlyGeneralSubGroup}">
							<solutionconfig:menuNode group="${group.subGroups[0]}" level="${level}" title="${group.description}"/>
						</c:when>
						<c:otherwise>
							<solutionconfig:menuNode group="${group.subGroups[0]}" level="${level}"/>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<c:set value="${level + 1}" var="subLevel" />
					<c:forEach var="subGroup" items="${group.subGroups}">
						<solutionconfig:menuNode group="${subGroup}" level="${subLevel}" />
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</c:if>

</c:if>

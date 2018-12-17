<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="solutionConfig" tagdir="/WEB-INF/tags/addons/ysapcustdevsolutionconfigb2bfrontend/responsive/product"%>


<div class="solution-config-find-related-product">
<c:if test="${not config.classicalModel}">
			<solutionConfig:solutionitems configData="${config}" />		
</c:if>					
</div>
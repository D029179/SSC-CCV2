<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

  <div class="solution-config-find-related-product">
  <c:if test="${not config.classicalModel}">
                        <spring:url value="findRelatedProducts" var="filteredCatalogUrl">
                        </spring:url>
                        <form id="filteredCatalogUrlForm" class="configure_form"
                            action="${filteredCatalogUrl}" method="get">
                            <button type="submit" class="btn btn-primary col-xs-4 col-md-4 col-lg-4">
                                <spring:theme text="Configure (not translated)" code="sapcustdev.solutionconfig.findrelatedproduct.button" />
                            </button>
                        </form>
  </c:if>
  </div>
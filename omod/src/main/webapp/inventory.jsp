<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.PrivilegeWebConstants" %>

<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require allPrivileges="<%= PrivilegeWebConstants.INVENTORY_PAGE_PRIVILEGES %>"
                 otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.INVENTORY_PAGE %>" />
<openmrs:message var="pageTitle" code="openhmis.inventory.title" scope="page"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="template/localHeader.jsp"%>

<input type=hidden class="isOperationAutoCompleted" value="${isOperationAutoCompleted}"/>
<input type="hidden" class="showOperationCancelReasonField" id="showOperationCancelReasonField" value="${showOperationCancelReasonField}">
<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "css/operations.css" %>' />
<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "js/screen/inventory.js" %>' />

<h2><spring:message code="openhmis.inventory.title" /></h2>
<table style="width: 99%">
    <tr>
        <td style="vertical-align: top; width: 250px;">
            <br />
            <b>
            <spring:message code="openhmis.inventory.admin.pending"/>
        </b>
            <br />
            <a href="${pageContext.request.contextPath}<%= ModuleWebConstants.INVENTORY_CREATION_PAGE %>"><spring:message code="openhmis.inventory.admin.create"/></a><br />
            <c:if test="${showStockTakeLink}">
	            <a href="${pageContext.request.contextPath}<%= ModuleWebConstants.INVENTORY_STOCK_TAKE_PAGE %>"><spring:message code="openhmis.inventory.admin.stockTake"/></a><br />
            </c:if>
            <a href="${pageContext.request.contextPath}<%= ModuleWebConstants.INVENTORY_REPORTS_PAGE %>"><spring:message code="openhmis.inventory.admin.reports"/></a>
        </td>
        <td>
            <b class="boxheader"><spring:message code="openhmis.inventory.page.operations" /></b><br /><br />
            <div id="operationList"></div>
            <div id="viewOperation"></div>
        </td>
    </tr>
</table>

<%@ include file="/WEB-INF/template/footer.jsp" %>

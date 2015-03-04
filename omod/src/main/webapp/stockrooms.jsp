<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.PrivilegeWebConstants" %>

<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="<%= PrivilegeWebConstants.STOCKROOM_PAGE_PRIVILEGES %>" otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.STOCKROOMS_PAGE %>" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "css/stockroom.css" %>' />
<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "js/screen/stockrooms.js" %>' />

<%@ include file="template/linksHeader.jsp"%>
<h2>
    <spring:message code="openhmis.inventory.admin.stockrooms" />
</h2>

<div id="itemDetailsDialog" style="display: none">
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>

<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.PrivilegeWebConstants" %>

<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="<%= PrivilegeWebConstants.OPERATIONS_PAGE_PRIVILEGES %>" otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.OPERATIONS_PAGE %>" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "css/operations.css" %>' />
<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "js/screen/operations.js" %>' />

<%@ include file="template/linksHeader.jsp"%>
<h2>
	<spring:message code="openhmis.inventory.admin.operations" />
</h2>

<div id="processingDialog" style="display: none">
	<spring:message htmlEscape="false" code="openhmis.inventory.admin.create.processing"/>
</div>
<%@ include file="/WEB-INF/template/footer.jsp" %>

<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.PrivilegeWebConstants" %>

<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="<%= PrivilegeWebConstants.MY_OPERATIONS_PAGE_PRIVILEGES %>" otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.OPERATIONS_PAGE %>" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "css/operations.css" %>' />
<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "js/screen/operations.js" %>' />

<h2>
	<spring:message code="openhmis.inventory.admin.operations" />
</h2>

<%@ include file="/WEB-INF/template/footer.jsp" %>

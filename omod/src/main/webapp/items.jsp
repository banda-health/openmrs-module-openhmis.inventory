<%@ page import="org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>

<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="<%= PrivilegeConstants.MANAGE_ITEMS %>, <%= PrivilegeConstants.VIEW_ITEMS %>" otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.ITEMS_PAGE %>" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>
<openmrs:htmlInclude file="<%=ModuleWebConstants.MODULE_RESOURCE_ROOT %>js/screen/items.js" />

<h2>
	<spring:message code="openhmis.inventory.admin.items" />
</h2>
<%--@elvariable id="PrivilegeConstants" type="org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants"--%>
<%--@elvariable id="ModuleWebConstants" type="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants"--%>
<%@ page import="org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>

<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require allPrivileges="<%= PrivilegeConstants.ITEM_PAGE_PRIVILEGES %>" otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.ITEMS_PAGE %>" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>
<openmrs:htmlInclude file="/moduleResources/openhmis/inventory/js/screen/items.js" />

<h2>
	<spring:message code="openhmis.inventory.admin.items" />
</h2>
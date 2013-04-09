<%@ page import="org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>

<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="<%= PrivilegeConstants.STOCK_ROOM_PAGE_PRIVILEGES %>" otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.STOCK_ROOMS_PAGE %>" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>
<openmrs:htmlInclude file="<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "js/screen/stockRooms.js" %>" />

<h2>
	<spring:message code="openhmis.inventory.admin.stockRooms" />
</h2>
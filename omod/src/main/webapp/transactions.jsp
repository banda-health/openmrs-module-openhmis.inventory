<%@ page import="org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>


<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="<%= PrivilegeConstants.TRANSACTION_PAGE_PRIVILEGES %>" otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.TRNASACTIONS_PAGE%>" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>


<openmrs:htmlInclude file="<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "js/screen/transactions.js" %>" />

<h2>
	<spring:message code="openhmis.inventory.admin.transactions" />
</h2>

<div id="stockRoomContent" style="width: 100%;">
	<div id="stockRoomList" style="width: 30%; float: left"></div>
	<div id="stockRoomInfo" style="width: 68%; float: right"></div>
	<div style="float: none"></div>
</div>

<div id="txDialog" style="display: none"></div>
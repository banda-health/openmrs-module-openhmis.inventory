<%@ page import="org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>


<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="<%= PrivilegeConstants.TRANSACTION_PAGE_PRIVILEGES %>" otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.TRANSACTIONS_PAGE%>" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>


<openmrs:htmlInclude file="<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "js/screen/transactions.js" %>" />

<h2>
	<spring:message code="openhmis.inventory.admin.transactions" />
</h2>

<div id="detailTabs">
	<ul id="detailTabList">
		<li><a href="#pending">Pending</a></li>
		<li><a href="#completed">Completed</a></li>
	</ul>
	<div id="pending"></div>
	<div id="completed"></div>
</div>
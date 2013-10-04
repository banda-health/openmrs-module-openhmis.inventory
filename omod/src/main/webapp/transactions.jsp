<%@ page import="org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>


<%@ include file="/WEB-INF/template/include.jsp"%>
<%--
  ~ The contents of this file are subject to the OpenMRS Public License
  ~ Version 2.0 (the "License"); you may not use this file except in
  ~ compliance with the License. You may obtain a copy of the License at
  ~ http://license.openmrs.org
  ~
  ~ Software distributed under the License is distributed on an "AS IS"
  ~ basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
  ~ License for the specific language governing rights and limitations
  ~ under the License.
  ~
  ~ Copyright (C) OpenMRS, LLC.  All Rights Reserved.
  --%>
<openmrs:require allPrivileges="<%= PrivilegeConstants.TRANSACTION_PAGE_PRIVILEGES %>" otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.TRANSACTIONS_PAGE%>" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>


<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "js/screen/transactions.js" %>' />

<h2>
	<spring:message code="openhmis.inventory.admin.transactions" />
</h2>

<div id="detailTabs">
	<div id="create" style="display: none"></div>
	<ul id="detailTabList">
		<li><a href="#my">My Pending</a></li>
		<li><a href="#pending">All Pending</a></li>
		<li><a href="#completed">Completed</a></li>
	</ul>
	<div id="my"></div>
	<div id="pending"></div>
	<div id="completed"></div>
</div>
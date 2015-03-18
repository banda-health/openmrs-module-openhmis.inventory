<%@ page import="org.openmrs.module.openhmis.inventory.web.PrivilegeWebConstants" %>
<%@ page import="org.openmrs.util.*" %>
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
<ul id="menu">
	<li class="first">
		<a href="${pageContext.request.contextPath}/admin"><openmrs:message code="admin.title.short"/></a>
	</li>
	<openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.INVENTORY_PAGE_PRIVILEGES %>">
		<li <c:if test='<%= request.getRequestURI().contains("inventory/inventory") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}<%= ModuleWebConstants.INVENTORY_PAGE %>">
				<spring:message code="openhmis.inventory.admin.inventory"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.ROLE_CREATION_PAGE_PRIVILEGES %>">
		<li <c:if test='<%= request.getRequestURI().contains("inventory/roleCreation") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}<%= ModuleWebConstants.ROLE_CREATION_PAGE %>">
				<spring:message code="openhmis.inventory.admin.role"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.ITEM_PAGE_PRIVILEGES %>">
		<li <c:if test='<%= request.getRequestURI().contains("inventory/items") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}<%= ModuleWebConstants.ITEMS_PAGE %>">
				<spring:message code="openhmis.inventory.admin.items"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.ITEM_ATTRIBUTE_TYPE_PAGE_PRIVILEGES %>">
		<li <c:if test='<%= request.getRequestURI().contains("inventory/itemAttributeTypes") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}<%= ModuleWebConstants.ITEM_ATTRIBUTE_TYPES_PAGE %>">
				<spring:message code="openhmis.inventory.admin.item.attribute.types"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.DEPARTMENT_PAGE_PRIVILEGES %>">
		<li <c:if test='<%= request.getRequestURI().contains("inventory/departments") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}<%= ModuleWebConstants.DEPARTMENTS_PAGE %>">
				<spring:message code="openhmis.inventory.admin.departments"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.INSTITUTION_PAGE_PRIVILEGES %>">
		<li <c:if test='<%= request.getRequestURI().contains("inventory/institutions") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}<%= ModuleWebConstants.INSTITUTIONS_PAGE %>">
				<spring:message code="openhmis.inventory.admin.institutions"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.STOCKROOM_PAGE_PRIVILEGES %>">
		<li <c:if test='<%= request.getRequestURI().contains("inventory/stockrooms") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}<%= ModuleWebConstants.STOCKROOMS_PAGE %>">
				<spring:message code="openhmis.inventory.admin.stockrooms"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.OPERATION_TYPES_PAGE_PRIVILEGES %>">
		<li <c:if test='<%= request.getRequestURI().contains("inventory/operationTypes") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}<%= ModuleWebConstants.OPERATION_TYPES_PAGE %>">
				<spring:message code="openhmis.inventory.admin.operationTypes"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.OPERATIONS_PAGE_PRIVILEGES %>">
		<li <c:if test='<%= request.getRequestURI().contains("inventory/operations") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}<%= ModuleWebConstants.OPERATIONS_PAGE %>">
				<spring:message code="openhmis.inventory.admin.operations"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.ITEM_CONCEPT_SUGGESTION_PAGE_PRIVILEGES %>">
		<li <c:if test='<%= request.getRequestURI().contains("inventory/itemConceptSuggestion") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}<%= ModuleWebConstants.ITEM_CONCEPT_SUGGESTION_PAGE %>">
				<spring:message code="openhmis.inventory.admin.items.concept.mapping"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.SETTINGS_PAGE_PRIVILEGES %>">
		<li <c:if test='<%= request.getRequestURI().contains("inventory/settings") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}<%= ModuleWebConstants.SETTINGS_PAGE %>">
				<spring:message code="openhmis.inventory.admin.settings"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
</ul>

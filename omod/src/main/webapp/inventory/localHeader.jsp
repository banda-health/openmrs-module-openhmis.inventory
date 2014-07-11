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
    <openmrs:hasPrivilege privilege="<%= PrivilegeConstants.ITEM_PAGE_PRIVILEGES %>">
        <li>
            <a href="${pageContext.request.contextPath}/module/openhmis/inventory/items.form">
                <openmrs:message code="openhmis.inventory.admin.items"/>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/module/openhmis/inventory/itemToConceptMapping.form">
                <openmrs:message code="openhmis.inventory.admin.items.concept.mapping"/>
            </a>
        </li>
    </openmrs:hasPrivilege>
    <openmrs:hasPrivilege privilege="<%= PrivilegeConstants.DEPARTMENT_PAGE_PRIVILEGES %>">
        <li>
            <a href="${pageContext.request.contextPath}/module/openhmis/inventory/departments.form">
                <openmrs:message code="openhmis.inventory.admin.departments"/>
            </a>
        </li>
    </openmrs:hasPrivilege>
    <openmrs:hasPrivilege privilege="<%= PrivilegeConstants.CATEGORY_PAGE_PRIVILEGES %>">
        <li>
            <a href="${pageContext.request.contextPath}/module/openhmis/inventory/categories.form">
                <openmrs:message code="openhmis.inventory.admin.categories"/>
            </a>
        </li>
    </openmrs:hasPrivilege>
        <openmrs:hasPrivilege privilege="<%= PrivilegeConstants.INSTITUTION_PAGE_PRIVILEGES %>">
        <li>
            <a href="${pageContext.request.contextPath}/module/openhmis/inventory/institution.form">
                <openmrs:message code="openhmis.inventory.admin.institution"/>
            </a>
        </li>
    </openmrs:hasPrivilege>
    <openmrs:hasPrivilege privilege="<%= PrivilegeConstants.STOCKROOM_PAGE_PRIVILEGES %>">
        <li>
            <a href="${pageContext.request.contextPath}/module/openhmis/inventory/stockrooms.form">
                <openmrs:message code="openhmis.inventory.admin.stockrooms"/>
            </a>
        </li>
    </openmrs:hasPrivilege>
</ul>
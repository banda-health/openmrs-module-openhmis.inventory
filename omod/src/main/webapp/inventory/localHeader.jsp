<%@ page import="org.openmrs.module.openhmis.inventory.web.PrivilegeWebConstants" %>

<ul id="menu">
    <openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.ITEM_PAGE_PRIVILEGES %>">
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
    <openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.DEPARTMENT_PAGE_PRIVILEGES %>">
        <li>
            <a href="${pageContext.request.contextPath}/module/openhmis/inventory/departments.form">
                <openmrs:message code="openhmis.inventory.admin.departments"/>
            </a>
        </li>
    </openmrs:hasPrivilege>
    <openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.CATEGORY_PAGE_PRIVILEGES %>">
        <li>
            <a href="${pageContext.request.contextPath}/module/openhmis/inventory/categories.form">
                <openmrs:message code="openhmis.inventory.admin.categories"/>
            </a>
        </li>
    </openmrs:hasPrivilege>
        <openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.INSTITUTION_PAGE_PRIVILEGES %>">
        <li>
            <a href="${pageContext.request.contextPath}/module/openhmis/inventory/institution.form">
                <openmrs:message code="openhmis.inventory.admin.institution"/>
            </a>
        </li>
    </openmrs:hasPrivilege>
    <openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.STOCKROOM_PAGE_PRIVILEGES %>">
        <li>
            <a href="${pageContext.request.contextPath}/module/openhmis/inventory/stockrooms.form">
                <openmrs:message code="openhmis.inventory.admin.stockrooms"/>
            </a>
        </li>
    </openmrs:hasPrivilege>
</ul>
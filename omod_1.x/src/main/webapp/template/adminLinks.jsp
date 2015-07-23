<%@ page import="org.openmrs.module.openhmis.inventory.web.PrivilegeWebConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>
<%@ page import="org.openmrs.util.PrivilegeConstants" %>

<ul id="menu">
	<openmrs:hasPrivilege privilege="<%= PrivilegeConstants.MANAGE_ROLES %>">
		<li>
			<a href="${pageContext.request.contextPath}<%= ModuleWebConstants.ROLE_CREATION_PAGE %>">
				<openmrs:message code="openhmis.inventory.admin.role"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
    <openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.ITEM_PAGE_PRIVILEGES %>">
        <li>
            <a href="${pageContext.request.contextPath}<%= ModuleWebConstants.ITEMS_PAGE %>">
                <openmrs:message code="openhmis.inventory.admin.items"/>
            </a>
        </li>
    </openmrs:hasPrivilege>
    <openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.DEPARTMENT_PAGE_PRIVILEGES %>">
        <li>
            <a href="${pageContext.request.contextPath}<%= ModuleWebConstants.DEPARTMENTS_PAGE %>">
                <openmrs:message code="openhmis.inventory.admin.departments"/>
            </a>
        </li>
    </openmrs:hasPrivilege>
        <openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.INSTITUTION_PAGE_PRIVILEGES %>">
        <li>
            <a href="${pageContext.request.contextPath}<%= ModuleWebConstants.INSTITUTIONS_PAGE %>">
                <openmrs:message code="openhmis.inventory.admin.institutions"/>
            </a>
        </li>
    </openmrs:hasPrivilege>
    <openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.STOCKROOM_PAGE_PRIVILEGES %>">
        <li>
            <a href="${pageContext.request.contextPath}<%= ModuleWebConstants.STOCKROOMS_PAGE %>">
                <openmrs:message code="openhmis.inventory.admin.stockrooms"/>
            </a>
        </li>
	    <li>
		    <a href="${pageContext.request.contextPath}<%= ModuleWebConstants.OPERATION_TYPES_PAGE %>">
			    <openmrs:message code="openhmis.inventory.admin.operationTypes"/>
		    </a>
	    </li>
	    <li>
		    <a href="${pageContext.request.contextPath}<%= ModuleWebConstants.OPERATIONS_PAGE %>">
			    <openmrs:message code="openhmis.inventory.admin.operations"/>
		    </a>
	    </li>
    </openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="<%= PrivilegeWebConstants.ITEM_PAGE_PRIVILEGES %>">
		<li>
			<a href="${pageContext.request.contextPath}<%= ModuleWebConstants.ITEM_CONCEPT_SUGGESTION_PAGE %>">
				<openmrs:message code="openhmis.inventory.admin.items.concept.mapping"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
</ul>

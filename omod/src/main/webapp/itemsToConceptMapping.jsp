<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.PrivilegeWebConstants" %>

<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="<%= PrivilegeWebConstants.ITEM_PAGE_PRIVILEGES %>" otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.ITEMS_PAGE %>" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<h2>
    <spring:message code="openhmis.inventory.admin.items.concept.drug.mapping" />
</h2>

<form method="POST">
    <div id=itemToConceptMappingList></div>
    <input type="submit" value="Save Items">
    <button class="cancel"><?= __("Cancel") ?></button>
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
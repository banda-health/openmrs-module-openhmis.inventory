<%@ page import="org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>

<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="<%= PrivilegeConstants.STOCKROOM_PAGE_PRIVILEGES %>" otherwise="/login.htm"
                 redirect="<%= ModuleWebConstants.STOCKROOMS_PAGE %>" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>


<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "js/screen/stockrooms.js" %>' />

<div id="stockroomContent" style="width: 100%;">
    <div>
        <div>
            <h2>
                <spring:message code="openhmis.inventory.admin.stockrooms" />
            </h2>
        </div>
        <div id="stockroomList"></div>
    </div>
</div>
<div id="stockroomInfo"></div>

<div id="txDialog" style="display: none"></div>
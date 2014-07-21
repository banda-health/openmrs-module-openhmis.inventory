<%@ page import="org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>

<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="View Administration Functions" otherwise="/login.htm" redirect="/admin/index.htm" />
<openmrs:message var="pageTitle" code="admin.titlebar" scope="page"/>

<%@ include file="/WEB-INF/template/header.jsp" %>

<style>
    .adminMenuList #menu li {
        display: list-item;
        border-left-width: 0px;
        
    }
    .adminMenuList #menu li.first {
        display: none;
    }
    .adminMenuList #menu {
        list-style: none;
        margin-left: 10px;
        margin-top: 0;
    }
    h4 {
        margin-bottom: 0;
    }
</style>

<h2><spring:message code="openhmis.inventory.title" /></h2>

<table border="0" width="93%">
    <tbody>
    <tr>
        <td valign="top" width="30%">
            <openmrs:hasPrivilege privilege="View Concepts,Manage Concept Classes,Manage Concept Datatypes,Manage Concept Proposals">
                <div class="adminMenuList">
                    <h4><spring:message code="openhmis.inventory.page"/></h4>
                    <%@ include file="inventory/localHeader.jsp" %>
                </div>
            </openmrs:hasPrivilege>
            
        </td>
    </tr>
    </tbody>
</table>

<%@ include file="/WEB-INF/template/footer.jsp" %>

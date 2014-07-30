<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="View Administration Functions" otherwise="/login.htm" redirect="/admin/index.htm" />
<openmrs:message var="pageTitle" code="admin.titlebar" scope="page"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="template/localHeader.jsp"%>

<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "css/operations.css" %>' />
<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "js/screen/inventory.js" %>' />

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

<table border="0" width="99%">
    <tbody>
    <tr>
        <td valign="top" width="150px">
            <div class="adminMenuList">
                    <h4><spring:message code="openhmis.inventory.page.admin"/></h4>
                    <%@ include file="template/adminLinks.jsp" %>
            </div>
        </td>
	    <td>
		    <b class="boxheader"><spring:message code="openhmis.inventory.page.operations" /></b><br /><br />
		    <div id="operationList"></div>
			<div id="viewOperation"></div>
		    <div id="newOperation">
			    <a id="createOperationLink" href="#">New Stock Operation</a>
			    <div id="newOperationDialog" style="visibility: hidden"></div>
		    </div>
	    </td>
    </tr>
    </tbody>
</table>

<%@ include file="/WEB-INF/template/footer.jsp" %>

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

<%@ page import="org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>

<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="<%= PrivilegeConstants.ITEM_PAGE_PRIVILEGES %>" otherwise="/login.htm"redirect="<%= ModuleWebConstants.ITEMS_PAGE %>" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>
<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "js/screen/itemToConceptMapping.js" %>' />




<h2>
    <spring:message code="openhmis.inventory.admin.items.concept.mapping" />
</h2>

<div id="existing-form">
	<form method="POST">
	    <div id=itemToConceptMappingList>
	        <b class="boxHeader">Current Items</b>
            <div class="box">
                <table class="display" width="100%">
	                <theader>
	                    <th>Item Name</th>
	                    <th>Concept Suggestion</th>
	                    <th>Accept Setting</th>
	                </theader>
	                <tbody class="list">
			            <c:forEach var="itemConcept" items="${itemConcepts}" varStatus="loopStatus">
		                    <tr class="${loopStatus.index % 2 == 0 ? 'evenRow' : 'oddRow'}">
		                        <td>${status.count} ${itemConcept.key.name}</td>
		                        <c:choose>
			                        <c:when test="${itemConcept.value != null}">
	    		                        <td>${itemConcept.value.name}</td>
			                        </c:when>
			                        <c:otherwise>
	    		                        <td>
                                            <openmrs_tag:conceptField formFieldName="${status.expression}" formFieldId="concept-${loopStatus.index}" />
	    		                        </td>
			                        </c:otherwise>
		                        </c:choose>
		                        <td>
		                            <input type="checkbox" name="bla" id="conceptSet" />
                                </td>
		                    </tr>
			            </c:forEach>
	                </tbody>
	            </table>
	        </div>
	    </div>
	    <input type="submit" value="Save Items">
	    <button class="cancel">Cancel</button>
	</form>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
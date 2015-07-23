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

<%@ page import="org.openmrs.module.openhmis.inventory.web.PrivilegeWebConstants" %>
<%@ page import="org.openmrs.module.openhmis.inventory.web.ModuleWebConstants" %>

<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="<%= PrivilegeWebConstants.ITEM_CONCEPT_SUGGESTION_PAGE_PRIVILEGES %>" otherwise="/login.htm"
                 redirect="<%=ModuleWebConstants.ITEM_CONCEPT_SUGGESTION_PAGE %>" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>
<openmrs:htmlInclude file='<%= ModuleWebConstants.MODULE_RESOURCE_ROOT + "js/screen/itemConceptSuggestion.js" %>' />

<%@ include file="template/linksHeader.jsp"%>
<h2>
    <spring:message code="openhmis.inventory.admin.items.concept.mapping" />
</h2>
<input id="returnUrl" type="hidden" value="${returnUrl}" />
<div class="spinner"></div>

<%@ include file="/WEB-INF/template/footer.jsp" %>

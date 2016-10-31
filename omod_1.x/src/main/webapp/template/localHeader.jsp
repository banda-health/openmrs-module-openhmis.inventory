<%@ page import="org.openmrs.module.openhmis.backboneforms.web.BackboneWebConstants" %>

<spring:htmlEscape defaultHtmlEscape="true" />
<openmrs:htmlInclude file="<%= BackboneWebConstants.BACKBONE_INIT_JS %>" />
<openmrs:htmlInclude file="<%= ModuleWebConstants.MODULE_INIT_JS %>" />
<openmrs:htmlInclude file="<%= BackboneWebConstants.BACKBONE_CURL_JS %>" />
<openmrs:htmlInclude file="<%= ModuleWebConstants.MESSAGE_PROPERTIES_JS %>" />

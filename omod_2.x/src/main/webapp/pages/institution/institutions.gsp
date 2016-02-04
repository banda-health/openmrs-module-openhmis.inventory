<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("openhmis.inventory.admin.institutions") ])
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-ui/angular-ui-router.min.js")
%>

<script type="text/javascript" src="/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/configs/load.messages.require.js"></script>
<script type="text/javascript" src="/openmrs/moduleResources/openhmis/commons/scripts/reusable-components/config.js"></script>
<script data-main="institution/configs/institutions.main" src="/openmrs/moduleResources/uicommons/scripts/require/require.js"></script>
<link rel="stylesheet" href="/openmrs/moduleResources/openhmis/commons/css/bootstrap.css" />
<link rel="stylesheet" href="/openmrs/moduleResources/openhmis/commons/css/entities2x.css" />

<div id="institutionsAp">
	<div ui-view></div>
</div>
<%
	ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("openhmis.inventory.admin.operationTypes") ])
	ui.includeJavascript("uicommons", "angular.min.js")
	ui.includeJavascript("uicommons", "angular-ui/angular-ui-router.min.js")

	ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
	ui.includeJavascript("uicommons", "angular-common.js")
	ui.includeJavascript("uicommons", "ngDialog/ngDialog.js")

	ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")
	ui.includeCss("openhmis.commons", "bootstrap.css")
	ui.includeCss("openhmis.commons", "entities2x.css")
	ui.includeCss("openhmis.inventory", "entity.css")
%>

<script type="text/javascript" src="/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/operationtypes/configs/load.messages.require.js"></script>
<script type="text/javascript" src="/openmrs/moduleResources/openhmis/commons/scripts/reusable-components/config.js"></script>
<script data-main="operationtypes/configs/entities.main" src="/openmrs/moduleResources/uicommons/scripts/require/require.js"></script>

<div id="entitiesApp">
	<div ui-view></div>
</div>

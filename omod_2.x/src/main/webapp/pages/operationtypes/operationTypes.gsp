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
%>

<script type="text/javascript" src="/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/operationtypes/configs/load.messages.require.js"></script>
<script type="text/javascript" src="/openmrs/moduleResources/openhmis/commons/scripts/reusable-components/config.js"></script>
<script data-main="operationtypes/configs/operationTypes.main" src="/openmrs/moduleResources/uicommons/scripts/require/require.js"></script>

<script type="text/javascript">
	var breadcrumbs = [
		{ icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
		{ label: "${ ui.message("openhmis.inventory.page")}" , link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'},
		{ label: "${ ui.message("openhmis.inventory.manage.module")}", link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/inventory/manageModule.page' },
		{ label: "${ ui.message("openhmis.inventory.admin.operationTypes")}"}
	];
</script>

<div id="operationTypesApp">
	<div ui-view></div>
</div>

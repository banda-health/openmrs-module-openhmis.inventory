<%
	ui.decorateWith("appui", "standardEmrPage", [title: ui.message("openhmis.inventory.admin.operationtypes")])
	ui.includeJavascript("uicommons", "angular.min.js")
	ui.includeJavascript("uicommons", "angular-ui/angular-ui-router.min.js")
	
	ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
	ui.includeJavascript("uicommons", "angular-common.js")
	ui.includeJavascript("uicommons", "ngDialog/ngDialog.js")
	
	ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")
	ui.includeCss("openhmis.commons", "bootstrap.css")
	ui.includeCss("openhmis.commons", "entities2x.css")
	ui.includeCss("openhmis.inventory", "entity.css")
	
	/* load re-usables/common modules */
	ui.includeFragment("openhmis.commons", "load.reusable.modules")
	
	/* load operationtypes modules */
	ui.includeJavascript("openhmis.inventory", "operationtypes/models/entity.model.js")
	ui.includeJavascript("openhmis.inventory", "operationtypes/services/entity.restful.services.js")
	ui.includeJavascript("openhmis.inventory", "operationtypes/controllers/entity.controller.js")
	ui.includeJavascript("openhmis.inventory", "operationtypes/controllers/manage-entity.controller.js")
	ui.includeJavascript("openhmis.inventory", "operationtypes/services/entity.functions.js")
%>

<script data-main="operationtypes/configs/entities.main"
        src="/openmrs/moduleResources/uicommons/scripts/require/require.js"></script>

<div id="entitiesApp">
	<div ui-view></div>
</div>

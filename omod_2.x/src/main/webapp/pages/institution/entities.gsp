<%
	ui.decorateWith("appui", "standardEmrPage", [title: ui.message("openhmis.inventory.admin.institutions")])
	ui.includeJavascript("uicommons", "angular.min.js")
	ui.includeJavascript("uicommons", "angular-ui/angular-ui-router.min.js")
	ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
	ui.includeJavascript("uicommons", "angular-common.js")

	ui.includeCss("openhmis.commons", "bootstrap.css")
	ui.includeCss("openhmis.commons", "entities2x.css")
	ui.includeCss("openhmis.inventory", "entity.css")

	/* load re-usables/common modules */
	ui.includeFragment("openhmis.commons", "loadReusableModules")

	ui.includeJavascript("openhmis.inventory", "institution/models/entity.model.js")
	ui.includeJavascript("openhmis.inventory", "institution/controllers/entity.controller.js")
	ui.includeJavascript("openhmis.inventory", "institution/controllers/manage-entity.controller.js")
    ui.includeJavascript("openhmis.inventory", "constants.js")
%>

<script data-main="institution/configs/entity.main"
        src="/${ ui.contextPath() }/moduleResources/uicommons/scripts/require/require.js"></script>

<div id="entitiesApp">
	<div ui-view></div>
</div>

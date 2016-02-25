<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("openhmis.inventory.admin.stockrooms") ])

    /* load stylesheets */
    ui.includeCss("openhmis.commons", "bootstrap.css")
    ui.includeCss("openhmis.commons", "entities2x.css")
    ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")
    ui.includeCss("openhmis.inventory", "stockoperations.css")

    /* load angular libraries */
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-ui/angular-ui-router.min.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "ngDialog/ngDialog.js")

    /* load re-usables/common modules */
    ui.includeFragment("openhmis.commons", "load.reusable.modules")

    /* load stockroom modules */
    ui.includeJavascript("openhmis.inventory", "stockoperations/models/entity.model.js")
    ui.includeJavascript("openhmis.inventory", "stockoperations/services/entity.restful.services.js")
    ui.includeJavascript("openhmis.inventory", "stockoperations/controllers/entity.controller.js")
    ui.includeJavascript("openhmis.inventory", "stockoperations/controllers/manage-entity.controller.js")
    ui.includeJavascript("openhmis.inventory", "stockoperations/services/entity.functions.js")
%>

<script data-main="stockoperations/configs/entity.main" src="/openmrs/moduleResources/uicommons/scripts/require/require.js"></script>

<div id="stockOperationApp">
    <div ui-view></div>
</div>
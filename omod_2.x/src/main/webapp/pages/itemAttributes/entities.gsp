<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("openhmis.inventory.admin.item.attribute.types") ])
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-ui/angular-ui-router.min.js")

    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")

    /* load re-usables/common modules */
    ui.includeFragment("openhmis.commons", "load.reusable.modules")

    /* load item attribute types modules */
    ui.includeJavascript("openhmis.inventory", "itemAttributes/models/entity.model.js")
    ui.includeJavascript("openhmis.inventory", "itemAttributes/services/entity.restful.services.js")
    ui.includeJavascript("openhmis.inventory", "itemAttributes/controllers/entity.controller.js")
    ui.includeJavascript("openhmis.inventory", "itemAttributes/controllers/manage-entity.controller.js")
    ui.includeJavascript("openhmis.inventory", "itemAttributes/services/entity.functions.js")

    /* load stylesheets */
    ui.includeCss("openhmis.commons", "bootstrap.css")
    ui.includeCss("openhmis.commons", "entities2x.css")

%>

<script data-main="itemAttributes/configs/entity.main" src="/openmrs/moduleResources/uicommons/scripts/require/require.js"></script>

<div id="itemAttributeTypesApp">
    <div ui-view></div>
</div>

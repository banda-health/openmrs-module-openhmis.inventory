<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("openhmis.inventory.admin.items") ])
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-ui/angular-ui-router.min.js")

    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")

%>

<script type="text/javascript" src="/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/itemAttributes/configs/load.messages.require.js"></script>
<script type="text/javascript" src="/openmrs/moduleResources/openhmis/commons/scripts/reusable-components/config.js"></script>
<script data-main="itemAttributes/configs/item.attribute.types.main" src="/openmrs/moduleResources/uicommons/scripts/require/require.js"></script>
<link rel="stylesheet" href="/openmrs/moduleResources/openhmis/commons/css/bootstrap.css" />
<link rel="stylesheet" href="/openmrs/moduleResources/openhmis/commons/css/entities2x.css" />
<div id="itemAttributeTypesApp">
    <div ui-view></div>
</div>
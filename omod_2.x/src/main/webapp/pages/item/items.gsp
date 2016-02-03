<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("openhmis.inventory.admin.institutions") ])
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-ui/angular-ui-router.min.js")

    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "ngDialog/ngDialog.js")

    ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")
%>
<script type="text/javascript" src="/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/item/configs/load.messages.require.js"></script>
<script type="text/javascript" src="/openmrs/moduleResources/openhmis/commons/scripts/reusable-components/config.js"></script>
<script data-main="item/configs/items.main" src="/openmrs/moduleResources/uicommons/scripts/require/require.js"></script>
<link rel="stylesheet" href="/openmrs/moduleResources/openhmis/commons/css/bootstrap.css" />
<link rel="stylesheet" href="/openmrs/moduleResources/openhmis/commons/css/entities2x.css" />

<div id="itemsApp">
    <div ui-view></div>
</div>
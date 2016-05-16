<script type="text/javascript">
    var breadcrumbs = [
        {icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
        {
            label: "${ ui.message("openhmis.inventory.page")}",
            link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'
        },
        {
            label: "${ ui.message("openhmis.inventory.manage.module")}",
            link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/inventory/manageModule.page'
        },
        {label: "${ ui.message("openhmis.inventory.admin.operations")}",}
    ];
    jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));
</script>

${ui.includeFragment("openhmis.inventory", "stockOperations/entities", [
        pageTitle : [ui.message('openhmis.inventory.admin.pending')]
])}

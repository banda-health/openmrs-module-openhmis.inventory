<script type="text/javascript">
	var breadcrumbs = [
		{icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
		{
			label: "${ ui.message("openhmis.inventory.page")}",
			link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'
		},
		{
			label: "${ ui.message("openhmis.inventory.admin.task.dashboard")}",
			link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/inventory/inventoryTasksDashboard.page'
		},
		{
			label: "${ ui.message("openhmis.inventory.admin.operations")}",
			link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/stockOperations/entities.page##/'
		},
		{label: "${ui.message("openhmis.inventory.stock.operation.name")}"}
	];

	jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));

	jQuery(".tabs").tabs();

</script>

${ui.includeFragment("openhmis.inventory", "stockOperations/entity")}

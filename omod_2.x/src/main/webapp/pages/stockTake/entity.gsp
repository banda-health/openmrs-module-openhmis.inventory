<script type="text/javascript">
	var breadcrumbs = [
		{
			icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'
		},
		{
			label: "${ ui.message("openhmis.inventory.page")}",
			link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'
		},
		{
			label: "${ ui.message("openhmis.inventory.admin.task.dashboard")}",
			link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/inventory/inventoryTasksDashboard.page'
		},
		{
			label: "${ui.message("openhmis.inventory.admin.stockTake")}"
		}
	];

	jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));
</script>

<div style="font-size:inherit">
	<table class="header-title">
		<span class="h1-substitue-left" style="float:left;">
			${ui.message('openhmis.inventory.admin.stockTake')}
		</span>
		<span style="float:right;">

		</span>
	</table>
	<br/>
	<table>
		<div class="row">
			<div class="col-md-2">
				<span>${ui.message('openhmis.inventory.stockroom.name')}</span>
			</div>

			<div class="col-md-4">
				<select class="form-control" ng-model="entity.stockroom"
				        ng-options='stockroom.name for stockroom in stockrooms track by stockroom.uuid'
				        ng-change="loadStockDetails(stockTakeCurrentPage)">
					<option value="" selected="selected">Any</option>
				</select>
			</div>

			<div class="col-md-2">
				<input type="button" value="Search" class="confirm form-control" ng-click="loadStockDetails()">
			</div>
		</div>
		<br/>
	</table>
</div>

<div ng-show="showStockDetails == false" class="detail-section-border-top">
	<br/>
	<span>
		${ui.message('openhmis.inventory.stocktake.no.items')}
	</span>
	<br/>
</div>

<div id="entities" ng-show="showStockDetails == true" class="detail-section-border-top">
	<form name="entityForm" class="entity-form" ng-class="{'submitted': submitted}">
		<br/>
		<table class="manage-entities-table" id="stockTakeTable">
			<thead>
			<tr>
				<th>${ui.message('openhmis.inventory.item.name')}</th>
				<th>${ui.message('openhmis.inventory.department.name')}</th>
				<th>${ui.message('openhmis.inventory.stockroom.expiration')}</th>
				<th>${ui.message('openhmis.inventory.item.quantity')}</th>
				<th>${ui.message('openhmis.inventory.item.actual.quantity')}</th>
			</tr>
			</thead>
			<tbody>
			<tr class="clickable-tr" pagination-id="__stockTake"
			    dir-paginate="entity in fetchedEntities | itemsPerPage: stockTakeLimit"
			    total-items="totalNumOfResults" current-page="stockTakeCurrentPage">
				<td>{{entity.item.name}}</td>
				<td>{{entity.item.department.name}}</td>
				<td>{{entity.expiration | date: "yyyy-MM-dd"}}</td>
				<td>{{entity.quantity}}</td>
				<td><input name="actualQuantity" id="actualQuantity" type="number" class="form-control input-sm"></td>
			</tr>
			</tbody>
		</table>
	</form>
	${ui.includeFragment("openhmis.commons", "paginationFragment", [
			paginationId      : "__stockTake",
			onPageChange      : "loadStockDetails(stockTakeCurrentPage)",
			onChange          : "loadStockDetails()",
			model             : "stockTakeLimit",
			pagingFrom        : "stockTakePagingFrom(stockTakeCurrentPage, stockTakeLimit)",
			pagingTo          : "stockTakePagingTo(stockTakeCurrentPage, stockTakeLimit, totalNumOfResults)",
			showRetiredSection: "false"
	])}
	<br/>
	<br/>
	<br/>
</div>

<div ng-show="showStockChangeDetails == true" class="detail-section-border-top">
	<br/>
</div>

<div ng-show="showStockDetails == true" class="detail-section-border-top">
	<br/>
	<input type="button" class="cancel" value="{{messageLabels['general.cancel']}}" ng-click="cancel()"/>
	<input type="button" class="confirm right" value="{{messageLabels['general.save']}}" ng-click="saveOrUpdate()"/>
	<br/>
</div>

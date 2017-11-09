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

<div ng-show="loading" class="loading-msg">
	<span>${ui.message("openhmis.inventory.stocktake.saving")}</span>
	<br/>
	<span class="loading-img">
		<img src="${ui.resourceLink("uicommons", "images/spinner.gif")}"/>
	</span>
</div>

<div ng-hide="loading">
	<div style="font-size:inherit">
		<table class="header-title">
			<span class="h1-substitue-left" style="float:left;">
				${ui.message('openhmis.inventory.admin.stockTake')}
			</span>
			<span style="float:right;">

			</span>
		</table>
		<br/>

		<div class="row">
			<div class="col-xs-9">
				<div class="col-xs-2">
					<strong>
						${ui.message('openhmis.inventory.stockroom.name')}:
					</strong>
				</div>

				<div class="col-xs-6">
					<select class="form-control" ng-model="entity.stockroom"
					        ng-options='stockroom.name for stockroom in stockrooms track by stockroom.uuid'
					        ng-change="stockroomDialog('stockroomChange',stockTakeCurrentPage)">
						<option value="" selected="selected">Any</option>
					</select>
				</div>

				<div class="col-xs-2">
					<input type="button" value="Search" class="confirm"
					       ng-click="stockroomDialog('stockroomChange',stockTakeCurrentPage)">
				</div>
			</div>

		</div>
		<br/>
	</div>

	<div ng-show="showNoStockroomSelected == true" class="detail-section-border-top">
		<br/>
		<span>
			${ui.message('openhmis.inventory.stocktake.no.stockroom.selected')}
		</span>
		<br/>
		<br/>
	</div>

	<div ng-show="showNoStockSummaries == true" class="detail-section-border-top">
		<br/>
		<span>
			${ui.message('openhmis.inventory.stocktake.no.items')}
		</span>
		<br/>
		<br/>
	</div>

	<div id="entities" ng-show="showStockDetails == true" class="detail-section-border-top">
		<br/>
		<table class="manage-entities-table" id="stockTakeTable">
			<thead>
			<tr>
				<th>${ui.message('openhmis.inventory.item.name')}</th>
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
				<td>{{entity.expiration | date: "yyyy-MM-dd"}}</td>
				<td>{{entity.quantity}}</td>
				<td><input name="actualQuantity" min="0"
				           id="{{'actualQuantity-'+entity.item.uuid+'_'+entity.expiration}}{{entity.expiration | date: 'yyyy-MM-dd'}}"
				           type="number" class="form-control input-sm" ng-model="entity.actualQuantity"
				           ng-blur="getActualQuantity(entity)"></td>
			</tr>
			</tbody>
		</table>
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

	<div ng-show="stockTakeDetails.length != 0" class="detail-section-border-top">
		<br/>
		<p>
			<a ng-show="showStockDetailsTable == false" id="stockTakehchange" class="btn btn-grey" ui-sref="new"
			   ng-click="showTableDetails()">
				${ui.message('openhmis.inventory.stocktake.change.showDetails')}
			</a>
			<a ng-show="showStockDetailsTable == true" id="stockTakehchange" class="btn btn-grey" ui-sref="new"
			   ng-click="hideTableDetails()">
				${ui.message('openhmis.inventory.stocktake.change.hideDetails')}
			</a>
			&nbsp;${ui.message('openhmis.inventory.stocktake.change.counter.label')} {{stockTakeDetails.length}}
		</p>
		<br/>
	</div>

	<div id="showStockDetailsTable" ng-show="showStockDetailsTable == true">
		<table class="manage-entities-table" id="stockTakeChangeDetailsTable">
			<thead>
			<tr>
				<th>${ui.message('openhmis.inventory.item.name')}</th>
				<th>${ui.message('openhmis.inventory.stockroom.expiration')}</th>
				<th>${ui.message('openhmis.inventory.item.quantity')}</th>
				<th>${ui.message('openhmis.inventory.item.actual.quantity')}</th>
			</tr>
			</thead>
			<tbody><tr class="clickable-tr" pagination-id="__stockTakeChangeReview"
			           total-items="stockTakeChangeCounter"
			           dir-paginate="stock in stockTakeDetails | itemsPerPage: stockTakeLimitReview">
				<td>{{stock.item.name}}</td>
				<td>{{stock.expiration | date: "yyyy-MM-dd"}}</td>
				<td>{{stock.quantity}}</td>
				<td>{{stock.actualQuantity}}</td>
			</tr>
			</tbody>
		</table>
		<br/>
		<br/>
	</div>

	<div ng-show="showStockDetails == true" class="detail-section-border-top">
		<br/>
		<input type="button" class="cancel" value="{{messageLabels['general.cancel']}}" ng-click="cancel()"/>
		<input ng-disabled="stockTakeDetails.length == 0" type="button" class="confirm right"
		       value="{{messageLabels['general.save']}}" ng-click="saveOrUpdate()"/>
		<br/>
	</div>

	<div id="stockroomChange" class="dialog" style="display:none;">
		<div class="dialog-header">
			<span>
				<i class="icon-info-sign"></i>

				<h3>${ui.message('openhmis.inventory.stocktake.stockroom.change.prompt.header')}</h3>
			</span>
			<i class="icon-remove cancel" style="float:right; cursor: pointer;" ng-click="closeThisDialog()"></i>
		</div>

		<div class="dialog-content form">
			<div>
				<p>${ui.message('openhmis.inventory.stocktake.stockroom.change.prompt.message')}</p>
			</div>

			<div class="row ngdialog-buttons detail-section-border-top">
				<br/>
				<input type="button" class="cancel" value="{{messageLabels['general.cancel']}}"
				       ng-click="closeThisDialog('Cancel')"/>
				<input type="submit" class="confirm right"
				       value="${ui.message('openhmis.inventory.stocktake.ok')}"/>
			</div>
		</div>
	</div>
</div>

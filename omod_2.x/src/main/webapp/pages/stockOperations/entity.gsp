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
		{
			label: "${ ui.message("openhmis.inventory.admin.operations")}",
			link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/stockOperations/entities.page#/'
		},
		{label: "${ui.message("openhmis.inventory.stock.operation.name")}"}
	];

	jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));

	jQuery(".tabs").tabs();

</script>

<h1>${ui.message('openhmis.inventory.stock.operation.name')}</h1>

<div class="detail-section-border-bottom">
	<ul class="table-layout">
		<li>
			<span>{{messageLabels['openhmis.inventory.operations.operationDate']}}</span>
		</li>
		<li>
			<span>{{stockOperation.operationDate | date: 'dd-MM-yyyy, h:mma'}}</span>
		</li>
	</ul>
	<ul class="table-layout">
		<li>
			<span>{{messageLabels['openhmis.inventory.operations.operationNumber']}}</span>
		</li>
		<li>
			<span>{{stockOperation.operationNumber}}</span>
		</li>
	</ul>
	<ul class="table-layout">
		<li>
			<span>{{messageLabels['openhmis.inventory.general.status']}}</span>
		</li>
		<li>
			<span>{{stockOperation.status}}</span>
		</li>
	</ul>
	<ul class="table-layout">
		<li>
			<span>{{messageLabels['openhmis.inventory.operations.operationCreators']}}</span>
		</li>
		<li>
			<span>{{stockOperation.creator.person.display}}</span>
		</li>
	</ul>
	<ul class="table-layout">
		<li>
			<span>{{messageLabels['openhmis.inventory.operations.operationType']}}</span>
		</li>
		<li>
			<span>{{stockOperation.instanceType.name}}</span>
		</li>
	</ul>
	<ul class="table-layout">
		<li>
			<span>{{messageLabels['openhmis.inventory.operations.sourceStockroom']}}</span>
		</li>
		<li>
			<span>{{stockOperation.source.name}}</span>
		</li>
	</ul>
	<ul class="table-layout">
		<li>
			<span>{{messageLabels['openhmis.inventory.operations.destinationStockroom']}}</span>
		</li>
		<li>
			<span>{{stockOperation.destination.name}}</span>
		</li>
	</ul>
	<ul class="table-layout" ng-repeat="attributeType in stockOperation.instanceType.attributeTypes">
		<li>
			<span>{{attributeType.name}}</span>
		</li>
		<li>
			<span></span>
		</li>
	</ul>
	<ul class="table-layout">
		<li style="width:100%;">
			<span ng-show="stockOperation.instanceType.role !== null || stockOperation.instanceType.user !== null">
				Can be processed
				<span ng-show="stockOperation.instanceType.role !== null">by users with the {{stockOperation.instanceType.role.display}} role</span>
				<span ng-show="stockOperation.instanceType.role !== null && stockOperation.instanceType.user !== null">or</span>
				<span ng-show="stockOperation.instanceType.user !== null">by the {{stockOperation.instanceType.user.display}} user.</span>
			</span>
		</li>
	</ul>
</div>
<br/>
<hr>
<div class="tabs">
	<ul>
		<li ng-hide="entity.uuid === ''">
			<a href="#items">{{messageLabels['openhmis.inventory.item.namePlural']}}</a>
		</li>
		<li ng-hide="entity.uuid === ''">
			<a href="#transactions">{{messageLabels['openhmis.inventory.stockroom.transaction.name']}}</a>
		</li>
	</ul>

	<div id="items" style="border: 0px;">
		<table style="margin-bottom:5px; border:0px" class="manage-entities-table manage-stockOperations-table">
			<thead>
			<tr>
				<th>{{messageLabels['openhmis.inventory.item.name']}}</th>
				<th>{{messageLabels['openhmis.inventory.item.quantity']}}</th>
				<th>{{messageLabels['openhmis.inventory.general.batchOperation']}}</th>
				<th>{{messageLabels['openhmis.inventory.general.expiration']}}</th>
			</tr>
			</thead>
			<tbody>
			<tr class="clickable-tr" pagination-id="__items"
			    dir-paginate="item in stockOperationItems | itemsPerPage: stockOperationItemLimit"
			    total-items="stockOperationItemTotalNumberOfResults" current-page="stockOperationItemCurrentPage">
				<td>{{item.item.name}}</td>
				<td>{{item.quantity}}</td>
				<td>{{item.batchOperation.operationNumber || '(Auto)'}}</td>
				<td>{{item.expiration | date: 'dd-MM-yyyy, h:mma'}}</td>
			</tr>
			</tbody>
		</table>

		<div class="not-found" ng-show="stockOperationItems.length == 0">
			${ui.message('No items found')}
		</div>

		<div id="below-entities-table" ng-hide="stockOperationItems.length == 0">
			<span style="float:right;">
				<div class="entity-pagination">
					<dir-pagination-controls pagination-id="__items"
					                         on-page-change="stockOperationItem(entity.uuid, stockOperationItemCurrentPage)"></dir-pagination-controls>
				</div>
			</span>
			<br/>

			<div class="pagination-options" style="float:left;">
				<div id="showing-entities">
					<span>
						<b>
							${ui.message('openhmis.inventory.general.showing')}
							{{stockOperationPagingFrom(stockOperationItemCurrentPage, stockOperationItemLimit)}}
							${ui.message('openhmis.inventory.general.to')}
							{{stockOperationPagingTo(stockOperationItemCurrentPage, stockOperationItemLimit, stockOperationItemTotalNumberOfResults)}}
						</b>
					</span>
					<span>
						<b>
							${ui.message('openhmis.inventory.general.of')}
							{{stockOperationItemTotalNumberOfResults}}
							${ui.message('openhmis.inventory.general.entries')}
						</b>
					</span>
				</div>

				<div id="includeVoided-entities">
					${ui.message('openhmis.inventory.general.show')}
					<select id="pageSize" ng-model="stockOperationItemLimit" ng-change="stockOperationItem(entity.uuid)">
						<option value="2">2</option>
						<option value="5">5</option>
						<option value="10">10</option>
						<option value="25">25</option>
						<option value="50">50</option>
						<option value="100">100</option>
					</select>
					${ui.message('openhmis.inventory.general.entries')}
				</div>
			</div>
		</div>
	</div>

	<div id="transactions" style="border: 0px;">
		<table style="margin-bottom:5px;" class="manage-entities-table manage-stockOperations-table">
			<thead>
			<tr>
				<th>{{messageLabels['openhmis.inventory.stockroom.name']}}</th>
				<th>{{messageLabels['openhmis.inventory.item.name']}}</th>
				<th>{{messageLabels['openhmis.inventory.general.batchOperation']}}</th>
				<th>{{messageLabels['openhmis.inventory.general.expiration']}}</th>
				<th>{{messageLabels['openhmis.inventory.item.quantity']}}</th>
			</tr>
			</thead>
			<tbody>
			<tr class="clickable-tr" pagination-id="__stockOperationTransactions"
			    dir-paginate="stockOperationTransaction in stockOperationTransactions | itemsPerPage: stockOperationTransactionLimit"
			    total-items="stockOperationTransactionTotalNumberOfResults"
			    current-page="stockOperationTransactionCurrentPage">
				<td>{{stockOperationTransaction.stockroom.name}}</td>
				<td>{{stockOperationTransaction.item.name}}</td>
				<td>{{stockOperationTransaction.batchOperation.operationNumber}}</td>
				<td>{{stockOperationTransaction.expiration | date: 'dd-MM-yyyy, h:mma'}}</td>
				<td>{{stockOperationTransaction.quantity}}</td>
			</tr>
			</tbody>
		</table>

		<div class="not-found" ng-show="stockOperationTransactions.length == 0">
			${ui.message('No Transactions found')}
		</div>

		<div ng-hide="stockOperationTransactions.length == 0">
			<span style="float:right;">
				<div class="entity-pagination">
					<dir-pagination-controls pagination-id="__stockOperationTransactions"
					                         on-page-change="stockOperationTransaction(entity.uuid, stockOperationTransactionCurrentPage)"></dir-pagination-controls>
				</div>
			</span>
			<br/>

			<div class="pagination-options" style="float:left;">
				<div>
					<span>
						<b>
							${ui.message('openhmis.inventory.general.showing')}
							{{stockOperationPagingFrom(stockOperationTransactionCurrentPage, stockOperationTransactionLimit)}}
							${ui.message('openhmis.inventory.general.to')}
							{{stockOperationPagingTo(stockOperationTransactionCurrentPage, stockOperationTransactionLimit, stockOperationTransactionTotalNumberOfResults)}}
						</b>
					</span>
					<span>
						<b>
							${ui.message('openhmis.inventory.general.of')}
							{{stockOperationTransactionTotalNumberOfResults}}
							${ui.message('openhmis.inventory.general.entries')}
						</b>
					</span>
				</div>

				<div>
					${ui.message('openhmis.inventory.general.show')}
					<select ng-model="stockOperationTransactionLimit" ng-change="stockOperationTransaction(entity.uuid)">
						<option value="2">2</option>
						<option value="5">5</option>
						<option value="10">10</option>
						<option value="25">25</option>
						<option value="50">50</option>
						<option value="100">100</option>
					</select>
					${ui.message('openhmis.inventory.general.entries')}
				</div>
			</div>
		</div>
	</div>
</div>
<hr/><br/>
<div class="detail-section-border-top">
	<br/>
	<p>
	<span><input type="button" class="cancel" value="{{messageLabels['openhmis.inventory.general.close']}}"
	             ng-click="cancel()"/></span>
	<span><input type="button" class="confirm right" ng-hide="stockOperation.status === 'ROLLBACK'"
	             value="{{messageLabels['openhmis.inventory.general.rollbackOperation']}}"
	             ng-click="rollbackOperation(stockOperation.uuid)"/></span>

	</p>
</div>

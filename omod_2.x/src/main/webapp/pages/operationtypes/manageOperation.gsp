<div id="operationTypes-body">
	 
	<br /><br /><br />
	<div id="manage-operationTypes-header">
		<span class="h1-substitue-left" style="float:left;">
			${ ui.message('openhmis.inventory.admin.operationTypes') }
		</span>
	</div>
	<br /><br /><br />
	<div ng-controller="ManageOperationTypesController">
		<div id="operationTypes">
			<div class="btn-group">
				<input type="text" ng-model="searchField" ng-change="updateContent()" class="field-display ui-autocomplete-input form-control searchinput" placeholder="${ ui.message('openhmis.inventory.general.enterSearchPhrase') }" size="80" autofocus>
				<span id="searchclear" class="searchclear icon-remove-circle"></span>
			</div>
			
			<br /><br />
			<table style="margin-bottom:5px;">
				<thead>
					<tr>
						<th>${ ui.message('general.name') }</th>
						<th>${ ui.message('general.description') }</th>
					</tr>
				</thead>
				<tbody>
					<tr class="clickable-tr" dir-paginate="entity in fetchedEntities | itemsPerPage: limit" total-items="totalNumOfResults" current-page="currentPage"  ui-sref="edit({uuid: entity.uuid})">
						<td ng-style="strikeThrough(entity.retired)">{{entity.name}}</td>
						<td ng-style="strikeThrough(entity.retired)">{{entity.description}}</td>
					</tr>
				</tbody>
			</table>
			<div id="below-institutions-table">
				<span style="float:left;">
					<div id="showing-operationTypes">
						<span><b>${ ui.message('openhmis.inventory.general.showing') } {{pagingFrom(currentPage, limit)}} ${ ui.message('openhmis.inventory.general.to') } {{pagingTo(currentPage, limit, totalNumOfResults)}}</b></span>
						<span><b>${ ui.message('openhmis.inventory.general.of') } {{totalNumOfResults}} ${ ui.message('openhmis.inventory.general.entries') }</b></span>
					</div>
				</span>
				<span style="float:right;">
					<div class="operationTypes-pagination">
    					<dir-pagination-controls on-page-change="paginate(currentPage)"></dir-pagination-controls>	
					</div>
				</span>
				<br />
				<span style="float:left;">
					<div id="includeVoided-operationTypes">
						${ui.message('openhmis.inventory.general.show')} 
						<select id="pageSize" ng-model="limit" ng-change="updateContent()">
							<option value="2">2</option>
							<option value="5">5</option>
							<option value="10">10</option>
							<option value="25">25</option>
							<option value="50">50</option>
							<option value="100">100</option>
						</select> 
						${ui.message('openhmis.inventory.general.entries')}
						<span>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp <input type="checkbox" ng-checked="includeRetired" ng-model="includeRetired" ng-change="updateContent()"></span>
						<span>${ ui.message('openhmis.inventory.general.includeRetired') }</span>
					</div>
				</span>
			</div>
		</div>
	</div>
</div>

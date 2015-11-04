<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("openhmis.inventory.admin.institutions") ])
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeCss("openhmis.inventory", "bootstrap.css")
    ui.includeCss("openhmis.inventory", "institutions2x.css")
%>

<script data-main="/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/manage-institution.main.js" src="/openmrs/moduleResources/uicommons/scripts/require/require.js"></script>

<script type="text/javascript">
	var breadcrumbs = [
	    { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
	    { label: "${ ui.message("openhmis.inventory.page")}" , link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'},
	    { label: "${ ui.message("openhmis.inventory.manage.module")}", link: 'inventory/manageModule.page' },
	    { label: "${ ui.message("openhmis.inventory.admin.institutions")}"}
	];
</script>

<div id="institutions-body">
	<div id="manage-institutions-header">
		<span class="h1-substitue-left" style="float:left;">
			${ ui.message('openhmis.inventory.admin.institutions') }
		</span>
		<span style="float:right;">
			<a class="button confirm" href="institution.page" >
				<i class ="icon-plus"></i>
		        ${ sprintf(ui.message('openhmis.inventory.general.new'), ui.message('openhmis.inventory.institution.name'))}
		    </a>
		</span>
	</div>
	<br /><br /><br />
	<div id="manageInstitutionApp" ng-controller="ManageInstitutionController">
		<div id="institutions">
			<div class="btn-group">
				<input type="text" ng-model="searchByName" ng-change="reloadPage()" class="field-display ui-autocomplete-input form-control searchinput" placeholder="${ ui.message('openhmis..enterSearchPhrase') }" size="80" autofocus>
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
					<tr class="clickable-tr" dir-paginate="entity in fetchedEntities | itemsPerPage: limit" total-items="totalNumOfResults" current-page="currentPage" ng-click="loadEntityPage('institution.page?uuid=' + entity.uuid)">
						<td ng-style="strikeThrough(entity.retired)">{{entity.name}}</td>
						<td ng-style="strikeThrough(entity.retired)">{{entity.description}}</td>
					</tr>
				</tbody>
			</table>
			<div id="below-institutions-table">
				<span style="float:left;">
					<div id="showing-institutions">
						<span><b>${ ui.message('openhmis.inventory.general.showing') } {{pagingFrom(currentPage, limit)}} ${ ui.message('openhmis.inventory.general.to') } {{pagingTo(currentPage, limit, totalNumOfResults)}}</b></span>
						<span><b>${ ui.message('openhmis.inventory.general.of') } {{totalNumOfResults}} ${ ui.message('openhmis.inventory.general.entries') }</b></span>
					</div>
				</span>
				<span style="float:right;">
					<div class="institution-pagination">
    					<dir-pagination-controls on-page-change="paginate(currentPage)"></dir-pagination-controls>	
					</div>
				</span>
				<br />
				<span style="float:left;">
					<div id="includeVoided-institutions">
						${ui.message('openhmis.inventory.general.show')} 
						<select id="pageSize" ng-model="limit" ng-change="reloadPage()">
							<option value="5">5</option>
							<option value="10">10</option>
							<option value="25">25</option>
							<option value="50">50</option>
							<option value="100">100</option>
						</select> 
						${ui.message('openhmis.inventory.general.entries')}
						<span>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp <input type="checkbox" ng-model="includeRetired" ng-change="reloadPage()"></span>
						<span>${ ui.message('openhmis.inventory.general.includeRetired') }</span>
					</div>
				</span>
			</div>
		</div>
	</div>
</div>

<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("openhmis.inventory.admin.departments") ])
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeCss("openhmis.inventory", "departments2x.css")
    ui.includeJavascript("openhmis.inventory", "departmentsController.js")
%>


<script type="text/javascript">
	var breadcrumbs = [
	    { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
	    { label: "${ ui.message("openhmis.inventory.page")}" , link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'},
	    { label: "${ ui.message("openhmis.inventory.manage.module")}", link: 'inventory/manageModule.page' },
	    { label: "${ ui.message("openhmis.inventory.admin.departments")}" }
	];
</script>

<h1 align="center">${ ui.message('openhmis.inventory.department.namePlural') }</h1>
<div id="departments-body">
	<h2>${ ui.message('openhmis.inventory.department.existing') }</h2>
	<div id="display-departments"  ng-app="departmentsApp" ng-controller="departmentsController">
		<h3>${ ui.message('openhmis.inventory.department.search') }</h3>
		<div id="search-departments">
			<span><input type="text" ng-model="searchByName" class="field-display ui-autocomplete-input" placeholder="${ ui.message('openhmis.inventory.department.enterSearchPhrase') }" size="40" autofocus></span>
			<span>${ ui.message('general.show') }: </span>
			<span><select ng-model="numberToShow"><option value='5'>5</option><option value='10'>10</option><option value='25'>25</option><option value='50'>50</option><option value='100'>100</option><select></span>
		</div>
		
		<h3>${ ui.message('openhmis.inventory.department.namePlural') }: ${ ui.message('openhmis.inventory.department.total')}({{departments.length}})</h3>
		<div id="departments">
			<table>
				<thead>
					<tr>
						<th>${ ui.message('general.name') }</th>
						<th>${ ui.message('general.description') }</th>
					</tr>
				</thead>
				<tbody>
					<tr class="clickable-tr" ng-repeat="department in departments | filter:searchByName | orderBy: 'name' | startFrom:currentPage*numberToShow | limitTo:numberToShow" ng-click="loadDepartment(department.uuid)">
						<td>{{department.name}}</td>
						<td>{{department.description}}</td>
					</tr>
				</tbody>
			</table>
			<br />
			<div class="department-pagination">
				<span><button ng-disabled="currentPage == 0" ng-click="currentPage=currentPage-1">${ ui.message('general.previous') }</button></span>
				    <span>{{currentPage+1}}/{{numberOfPages()}}</span>
				<span><button ng-disabled="currentPage >= (departments.length/numberOfPages) - 1" ng-click="currentPage=currentPage+1">${ ui.message('general.next') }</button></span>
			</div>
		</div>
	</div>
	
	<br />
	
	<h2>${ ui.message('openhmis.inventory.department.addNew') }</h2>
	<div id="add-new-department">
		<a class="button" href="department.page" >
			<i class ="icon-plus"></i>
            ${ ui.message('openhmis.inventory.department.new') }
        </a>
	</div>
</div>
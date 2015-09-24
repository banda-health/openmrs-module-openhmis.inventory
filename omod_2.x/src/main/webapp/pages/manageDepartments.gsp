<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("openhmis.inventory.admin.departments") ])
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeCss("openhmis.inventory", "bootstrap.css")
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
	var jq = jQuery;
	
	jq(document).ready(function () {//supports reseting search phrase to blank
	    jq(".searchinput").keyup(function () {
	        jq(this).next().toggle(Boolean(jq(this).val()));
	    });
	    jq(".searchclear").toggle(Boolean(jq(".searchinput").val()));
	    jq(".searchclear").click(function () {
	    	jq(this).prev().val('').focus();
	        jq(this).hide();
	    });
	});
</script>

<div id="departments-body">
	<div id="manage-departments-header">
		<span class="h1-substitue-left" style="float:left;">
			${ ui.message('openhmis.inventory.admin.departments') }
		</span>
		<span style="float:right;">
			<a class="button confirm" href="department.page" >
				<i class ="icon-plus"></i>
		        ${ ui.message('openhmis.inventory.department.new') }
		    </a>
		</span>
	</div>
	<br /><br /><br />
	<div id="display-departments" ng-app="departmentsApp" ng-controller="departmentsController">
		<div id="departments">
			<div class="btn-group">
				<input type="text" ng-model="searchByName" class="field-display ui-autocomplete-input form-control searchinput" placeholder="${ ui.message('openhmis.inventory.department.enterSearchPhrase') }" size="80" autofocus>
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
					<tr class="clickable-tr" ng-repeat="department in departments | filter:searchByName | orderBy: 'name' | startFrom:currentPage*10 | limitTo:10" ng-click="loadDepartment(department.uuid)">
						<td>{{department.name}}</td>
						<td>{{department.description}}</td>
					</tr>
				</tbody>
			</table>
			<div id="below-departments-table">
				<span style="float:left;">
					<div id="showing-departments">
						<span>${ ui.message('openhmis.inventory.general.showing') } <b>{{pagingFrom()}}</b> ${ ui.message('openhmis.inventory.general.to') } <b>{{pagingTo()}}</b></span>
						<span> ${ ui.message('openhmis.inventory.general.of') } <b>{{departments.length}}</b> ${ ui.message('openhmis.inventory.general.entries') }</span>
					</div>
				</span>
				<span style="float:right;">
					<div class="department-pagination">
						<span><button class="paging-button" ng-disabled="currentPage == 0" ng-click="currentPage=currentPage-1">${ ui.message('general.previous') }</button></span>
						<span>${ ui.message('openhmis.inventory.general.page') } {{currentPage+1}}/{{numberOfPages()}}</span>
						<span><button class="paging-button" ng-disabled="currentPage == numberOfPages() - 1" ng-click="currentPage=currentPage+1">${ ui.message('general.next') }</button></span>
					</div>
				</span>
				<span style="float:center;">
					<div id="includeVoided-departments">
						<span>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp <input type="checkbox" ng-model="includeRetired" ng-change="includeRetiredDepartments()"></span>
						<span>${ ui.message('openhmis.inventory.general.includeRetired') }</span>
					</div>
				</span>
			</div>
		</div>
	</div>
</div>
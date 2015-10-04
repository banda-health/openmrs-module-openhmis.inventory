<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("openhmis.inventory.admin.institutions") ])
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeCss("openhmis.inventory", "bootstrap.css")
    ui.includeCss("openhmis.inventory", "institutions2x.css")
    ui.includeJavascript("openhmis.inventory", "institutionsController.js")
%>


<script type="text/javascript">
	var breadcrumbs = [
	    { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
	    { label: "${ ui.message("openhmis.inventory.page")}" , link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'},
	    { label: "${ ui.message("openhmis.inventory.manage.module")}", link: 'inventory/manageModule.page' },
	    { label: "${ ui.message("openhmis.inventory.admin.institutions")}"}
	];
	
	/*emr.loadMessages([
        "openhmis.inventory.institution.name",
        "general.edit",
        "general.new"
    ]);*/
    
    var jq = jQuery;
	
	jq(document).ready(function () {//supports reseting search phrase to blank
	    jq(".searchinput").keyup(function () {
	        jq(this).next().toggle(Boolean(jq(this).val()));
	    });
	    jq(".searchclear").toggle(Boolean(jq(".searchinput").val()));
	    jq(".searchclear").click(function () {
	    	jq(this).prev().val('').focus();
	    	jq(this).prev().trigger('input');
	        jq(this).hide();
	    });
	});
</script>

<div id="institutions-body">
	<div id="manage-institutions-header">
		<span class="h1-substitue-left" style="float:left;">
			${ ui.message('openhmis.inventory.admin.institutions') }
		</span>
		<span style="float:right;">
			<a class="button confirm" href="institution.page" >
				<i class ="icon-plus"></i>
		        ${ ui.message('openhmis.inventory.institution.new') }
		    </a>
		</span>
	</div>
	<br /><br /><br />
	<div id="display-institutions" ng-app="institutionApp" ng-controller="InstitutionController">
		<div id="institutions">
			<div class="btn-group">
				<input type="text" ng-model="searchByName" class="field-display ui-autocomplete-input form-control searchinput" placeholder="${ ui.message('openhmis.inventory.institution.enterSearchPhrase') }" size="80" autofocus>
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
					<tr class="clickable-tr" ng-repeat="institution in fetchedInstitutions | filter:searchByName | orderBy: 'name' | startFrom:currentPage*10 | limitTo:10" ng-click="loadInstitutionFromManagePage(institution.uuid)">
						<td ng-style="strikeThrough(institution.retired)">{{institution.name}}</td>
						<td ng-style="strikeThrough(institution.retired)">{{institution.description}}</td>
					</tr>
				</tbody>
			</table>
			<div id="below-institutions-table">
				<span style="float:left;">
					<div id="showing-institutions">
						<span>${ ui.message('openhmis.inventory.general.showing') } <b>{{pagingFrom()}}</b> ${ ui.message('openhmis.inventory.general.to') } <b>{{pagingTo()}}</b></span>
						<span> ${ ui.message('openhmis.inventory.general.of') } <b>{{fetchedInstitutions.length}}</b> ${ ui.message('openhmis.inventory.general.entries') }</span>
					</div>
				</span>
				<span style="float:right;">
					<div class="institution-pagination">
						<span><a a-disabled="currentPage == 0" ng-click="currentPage=0">${ ui.message('searchWidget.first') }</a>  </span>
						<span><a a-disabled="currentPage == 0" ng-click="currentPage=currentPage-1">${ ui.message('searchWidget.previous') }</a></span>
						<span><a ng-repeat="page in existingPages()" ng-click="loadPageByNumber(page)" a-disabled="disAbleSinglePage(page)">{{page}} </a></span>
						<span><a a-disabled="currentPage == numberOfPages() - 1" ng-click="currentPage=currentPage+1">${ ui.message('searchWidget.next') }</a></span>
						<span>  <a a-disabled="currentPage == numberOfPages()-1" ng-click="currentPage=numberOfPages() - 1">${ ui.message('searchWidget.last') }</a></span>
					</div>
				</span>
				<span style="float:center;">
					<div id="includeVoided-institutions">
						<span>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp <input type="checkbox" ng-model="includeRetired" ng-change="includeRetiredInstitutions()"></span>
						<span>${ ui.message('openhmis.inventory.general.includeRetired') }</span>
					</div>
				</span>
			</div>
		</div>
	</div>
</div>

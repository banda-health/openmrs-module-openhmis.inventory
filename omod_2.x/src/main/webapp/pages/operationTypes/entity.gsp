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
			label: "${ ui.message("openhmis.inventory.admin.operationTypes")}",
			link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/operationTypes/entities.page##/'
		},
		{label: "${ui.message("openhmis.inventory.operations.type.name")}"}
	];
	jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));

</script>

<div ng-show="loading" class="loading-msg">
	<span>${ui.message("openhmis.commons.general.processingPage")}</span>
	<br />
	<span class="loading-img">
		<img src="${ ui.resourceLink("uicommons", "images/spinner.gif") }"/>
	</span>
</div>

<form ng-hide="loading" onsubmit="return removeIndexFromItems()">

	${ui.includeFragment("openhmis.commons", "editEntityHeaderFragment")}

	<input type="hidden" ng-model="entity.uuid"/>

	<fieldset class="format">

		<ul class="table-layout">
			<li>
				<span>{{messageLabels['general.name']}}</span>
			</li>
			<li>
				<input class="form-control" type="text" ng-model="entity.name" style="min-width: 50%;"
				       placeholder="{{messageLabels['general.name']}}" required/>

				<p class="checkRequired"
				   ng-hide="nameIsRequiredMsg == '' || nameIsRequiredMsg == undefined">{{nameIsRequiredMsg}}</p>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<span>{{messageLabels['general.description']}}</span>
			</li>
			<li>
				<textarea class="form-control" ng-model="entity.description"
				          placeholder="{{messageLabels['general.description']}}" rows="3"></textarea>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<span>{{messageLabels['openhmis.inventory.operations.type.sourceLabel']}}</span>
			</li>
			<li>
				<input type="checkbox" ng-model="entity.hasSource" disabled="disabled" style="background-color:lightgrey"/>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<span>{{messageLabels['openhmis.inventory.operations.type.destinationLabel']}}</span>
			</li>
			<li>
				<input type="checkbox" ng-model="entity.hasDestination" disabled="disabled"
				       style="background-color:lightgrey"/>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<span>{{messageLabels['openhmis.inventory.operations.type.recipientLabel']}}</span>
			</li>
			<li>
				<input type="checkbox" ng-model="entity.hasRecipient" disabled="disabled"
				       style="background-color:lightgrey"/>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<span>{{messageLabels['openhmis.inventory.operations.type.availableWhenReservedLabel']}}</span>
			</li>
			<li>
				<input type="checkbox" ng-model="entity.availableWhenReserved" disabled="disabled"
				       style="background-color:lightgrey"/>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<span>{{messageLabels['openhmis.inventory.operations.type.userLabel']}}</span>
			</li>
			<li>
				<select class="form-control" ng-model="entity.user"
				        ng-options='user.display for user in users track by user.uuid'>
				</select>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<span>{{messageLabels['openhmis.inventory.operations.type.roleLabel']}}</span>
			</li>
			<li>
				<select class="form-control" ng-model="entity.role"
				        ng-options='role.display for role in roles track by role.uuid'>
				</select>
			</li>
		</ul>
		<br/>
		<ul class="table-layout">
			<li class="valign">
				<span>{{messageLabels['openhmis.commons.attribute.type.namePlural']}}</span>
			</li>
			<li>
				<div class="bbf-editor">
					<div class="bbf-list" name="attributeTypes">
						<ul class="attributes-layout">
							<li ng-repeat="attributeType in entity.attributeTypes track by attributeType.uuid || attributeType.id">
								<a href="" ng-click="removeAttributeType(attributeType)">
									<i class="icon-remove"></i>
								</a>
								<a href="" ng-click="editAttributeType(attributeType)">{{attributeType.name}}</a>
							</li>
						</ul>

						<div class="bbf-actions">
							<button type="button" data-action="add" ng-click="addAttributeType()">Add</button>
						</div>
						${ui.includeFragment("openhmis.commons", "attributeTypesFragment")}
					</div>
				</div>
			</li>
		</ul>
		<br/>

		<p class="detail-section-border-top">
			<br/>
			<span>
				<input type="button" class="cancel left" value="{{messageLabels['general.cancel']}}" ng-click="cancel()"/>
			</span>
			<span>
				<input type="button" class="confirm right"
				       value="{{messageLabels['general.save']}}"
				       ng-disabled="entity.name == '' || entity.name == undefined"
				       ng-click="saveOrUpdate()"/>
			</span>
		</p>
	</fieldset>
</form>
${ui.includeFragment("openhmis.commons", "retireUnretireDeleteFragment", [showDeleteSection: "false"])}

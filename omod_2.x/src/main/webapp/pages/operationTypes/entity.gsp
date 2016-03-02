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
			link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/operationTypes/entities.page'
		},
		{label: "${ ui.message("openhmis.inventory.general.edit")} ${ui.message("openhmis.inventory.operations.type.name")}"}
	];
	jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));

</script>

<form onsubmit="return removeIndexFromItems()">

	${ ui.includeFragment("openhmis.commons", "editEntityHeaderFragment")}

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
				<input type="checkbox" ng-model="entity.hasSource" disabled="disabled"/>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<span>{{messageLabels['openhmis.inventory.operations.type.destinationLabel']}}</span>
			</li>
			<li>
				<input type="checkbox" ng-model="entity.hasDestination" disabled="disabled"/>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<span>{{messageLabels['openhmis.inventory.operations.type.recipientLabel']}}</span>
			</li>
			<li>
				<input type="checkbox" ng-model="entity.hasRecipient" disabled="disabled"/>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<span>{{messageLabels['openhmis.inventory.operations.type.availableWhenReservedLabel']}}</span>
			</li>
			<li>
				<input type="checkbox" ng-model="entity.availableWhenReserved" disabled="disabled"/>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<span>{{messageLabels['openhmis.inventory.operations.type.userLabel']}}</span>
			</li>
			<li>
				<select class="form-control">
					<option ng-repeat="user in users track by user.uuid" ng-selected="entity.user.display == user.display">
						{{user.display}}
					</option>
				</select>
			</li>
		</ul>
		<ul class="table-layout">
			<li>
				<span>{{messageLabels['openhmis.inventory.operations.type.roleLabel']}}</span>
			</li>
			<li>
				<select class="form-control">
					<option ng-repeat="role in roles track by role.uuid" ng-selected="entity.role.display == role.display">
						{{role.display}}
					</option>
				</select>
			</li>
		</ul>
		<br/>
		<ul class="table-layout">
			<li>
				<span>{{messageLabels['openhmis.inventory.attribute.type.namePlural']}}</span>
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

						<div id="attribute-types-dialog" class="dialog" style="display:none;">
							<div class="dialog-header">
								<span ng-show="addAttributeTypeTitle != ''">
									<i class="icon-plus-sign"></i>

									<h3>{{addAttributeTypeTitle}}</h3>
								</span>
								<span ng-show="editAttributeTypeTitle != ''">
									<i class="icon-edit"></i>

									<h3>{{editAttributeTypeTitle}}</h3>
								</span>
							</div>

							<div class="dialog-content form" id="dialog-bottom">
								<ul class="table-layout dialog-table-layout">
									<li class="required">
										<span>{{messageLabels['general.name']}}</span>
									</li>
									<li>
										<input type="text" style="min-width: 100%;"
										       placeholder="" required ng-model="attributeType.name"/>
									</li>
								</ul>
								<ul class="table-layout dialog-table-layout">
									<li class="not-required">
										<span>{{messageLabels['PersonAttributeType.format']}}</span>
									</li>
									<li>
										<select class="form-control" style="font-size: 14px" ng-model="attributeType.format"
										        ng-options="field for field in formatFields track by field">
											<option value="0">-- Please Select Format --</option>
											<option ng-selected="attributeType.format == field">
											</option>
										</select>
									</li>
								</ul>
								<ul class="table-layout dialog-table-layout">
									<li class="not-required">
										<span>{{messageLabels['PersonAttributeType.foreignKey']}}</span>
									</li>
									<li>
										<input type="text" ng-model="attributeType.foreignKey"/>
									</li>
								</ul>
								<ul class="table-layout dialog-table-layout">
									<li class="not-required">
										<span>{{messageLabels['PatientIdentifierType.format']}}</span>
									</li>
									<li>
										<input type="text" ng-model="attributeType.regExp"/>
									</li>
								</ul>
								<ul class="table-layout dialog-table-layout">
									<li class="not-required">
										<span>{{messageLabels['FormField.required']}}</span>
									</li>
									<li>
										<input type="checkbox" ng-model="attributeType.required"/>
									</li>
								</ul>
								<ul class="table-layout dialog-table-layout">
									<li class="required">
										<span>{{messageLabels['Field.attributeName']}} {{messageLabels['Obs.order']}}</span>
									</li>
									<li>
										<input type="text" required ng-model="attributeType.attributeOrder"/>
									</li>
								</ul>

								<div class="ngdialog-buttons">
									<input type="button" class="cancel" value="{{messageLabels['general.cancel']}}"
									       ng-click="cancel()"/>
									<span ng-show="addAttributeTypeTitle != ''">
										<input type="button" class="confirm right"
										       ng-disabled="attributeType.name == '' || attributeType.name == undefined"
										       value="{{messageLabels['general.save']}}"
										       ng-click="saveOrUpdate()"/>
									</span>
									<span ng-show="editAttributeTypeTitle != ''">
										<input type="button" class="confirm right"
										       ng-disabled="attributeType.name == '' || attributeType.name == undefined"
										       value="{{messageLabels['openhmis.inventory.general.confirm']}}"
										       ng-click="saveOrUpdate()"/>
									</span>
								</div>
							</div>
						</div>
					</div>
				</div>
			</li>
		</ul>
		<br/>
		<p>
			<span>
				<input type="button" class="cancel" value="{{messageLabels['general.cancel']}}" ng-click="cancel()"/>
			</span>
			<span>
				<input type="button" class="confirm right"
					   value="{{messageLabels['openhmis.inventory.general.saveChanges']}}"
					   ng-disabled="entity.name == '' || entity.name == undefined"
					   ng-click="removeoperationTypesTemporaryIds(); saveOrUpdate()"/>
			</span>
		</p>
	</fieldset>

	${ ui.includeFragment("openhmis.commons", "retireUnretireDeleteFragment", [showDeleteSection: "false"]) }

</form>

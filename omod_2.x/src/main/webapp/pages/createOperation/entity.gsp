<script type="text/javascript">
    var breadcrumbs = [
        {icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
        {
            label: "${ ui.message("openhmis.inventory.page")}",
            link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'
        },
        {
            label: "${ ui.message("openhmis.inventory.admin.task.dashboard")}",
            link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/inventory/inventoryTasksDashboard.page'
        },
        {
            label: "${ ui.message("openhmis.inventory.admin.create")}",
        },
    ];

    jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));

    jQuery(".tabs").tabs();

    jQuery(function() {
        jQuery('body').on('focus', ".date", function(){
            jQuery(this).datetimepicker({
                minView : 2,
                autoclose : true,
                pickerPosition : "bottom-left",
                todayHighlight : false,
                format: "dd M yyyy",
                startDate : new Date(),
            });
        });
    });

</script>

<div ng-show="loading" style="margin:400px;">
    <img src="${ ui.resourceLink("uicommons", "images/spinner.gif") }"/>
</div>

<div ng-hide="loading">
    <h1>${ui.message('openhmis.inventory.admin.create')}</h1>
    <form name="entityForm" class="entity-form" ng-class="{'submitted': submitted}" style="font-size:inherit">

        <input type="hidden" ng-model="entity.uuid" />

        <fieldset class="operation">

            <div class="action-container" ng-show="operationType.name === 'Adjustment' && sourceStockroom.name !== ' - Not Defined - '">
                <ul>
                    <h3>${ui.message('openhmis.inventory.operations.itemStockActions')}</h3>
                    <li>
                        <i class="icon-download"></i>
                        <a ng-click="">${ui.message('openhmis.inventory.operations.adjustLowStockLevels')}</a>
                    </li>
                    <li>
                        <i class="icon-adjust"></i>
                        <a ng-click="">${ui.message('openhmis.inventory.operations.adjustExpiredStocks')}</a>
                    </li>
                    <li>
                        <i class="icon-fire"></i>
                        <a ng-click="">${ui.message('openhmis.inventory.operations.adjustNearlyExpiredStocks')}</a>
                    </li>
                </ul>
            </div>

            <ul class="table-layout">
                <li class="required" ng-show="!isOperationNumberGenerated">
                    <span>${ui.message('openhmis.inventory.operations.operationNumber')}</span>
                </li>
                <li class="not-required" ng-show="isOperationNumberGenerated">
                    <span>${ui.message('openhmis.inventory.operations.operationNumber')}</span>
                </li>
                <li>
                    <span>
                        <input ng-model="entity.operationNumber" ng-show="!isOperationNumberGenerated" required/>
                        <input ng-model="entity.operationNumber" ng-show="isOperationNumberGenerated" ng-disabled="true" />
                    </span>
                </li>
            </ul>
            <ul class="table-layout">
                <li class="required">
                    <span>${ui.message('openhmis.inventory.operations.operationType')}</span>
                </li>
                <li>
                    <select ng-model="operationType" required
                            ng-change="warningDialog(operationType, '{{operationType}}', 'operationType')"
                            ng-options='operationType.name for operationType in operationTypes track by operationType.uuid'>
                    </select>
                </li>
            </ul>
            <ul class="table-layout">
                <li class="required">
                    <span>${ui.message('openhmis.inventory.operations.operationDate')}</span>
                </li>
                <li class="change-operation-date">
                    <span>
                        {{operationDate}}
                        <input class="gray-button" type="button" value="${ui.message('openhmis.inventory.operations.changeDate')}"
                               ng-click="changeOperationDate()" />
                    </span>
                </li>
            </ul>
            <ul class="table-layout" ng-show="operationType.hasSource">
                <li class="required">
                    <span>${ui.message('openhmis.inventory.operations.sourceStockroom')}</span>
                </li>
                <li>
                    <select ng-model="sourceStockroom" required
                            ng-change="warningDialog(sourceStockroom, '{{sourceStockroom}}', 'stockroom')"
                            ng-options='sourceStockroom.name for sourceStockroom in sourceStockrooms track by sourceStockroom.uuid'>
                    </select>
                </li>
            </ul>
            <ul class="table-layout" ng-show="operationType.name === 'Distribution'">
                <li class="not-required">
                    <span>${ui.message('openhmis.inventory.operations.distributeTo')}</span>
                </li>
                <li>
                    <select ng-model="distributionType"
                            ng-options="distributionType for distributionType in distributionTypes">
                    </select>
                </li>
            </ul>
            <ul class="table-layout" ng-show="operationType.name === 'Return'">
                <li class="not-required">
                    <span>${ui.message('openhmis.inventory.operations.returnTo')}</span>
                </li>
                <li>
                    <select ng-model="returnOperationType"
                            ng-options="returnOperationType for returnOperationType in returnOperationTypes">
                    </select>
                </li>
            </ul>
            <ul class="table-layout" ng-show="operationType.hasDestination">
                <li class="required">
                    <span>${ui.message('openhmis.inventory.operations.destinationStockroom')}</span>
                </li>
                <li>
                    <select ng-model="destinationStockroom" required
                            ng-options='destinationStockroom.name for destinationStockroom in destinationStockrooms track by destinationStockroom.uuid'>
                    </select>
                </li>
            </ul>
            <ul class="table-layout"
                ng-show="((operationType.name === 'Distribution' && distributionType === 'Institution') || (operationType.name === 'Return' && returnOperationType === 'Institution'))">
                <li class="required">
                    <span>${ui.message('openhmis.inventory.institution.name')}</span>
                </li>
                <li>
                    <select ng-model="institutionStockroom" required
                            ng-options='institution.name for institution in institutions track by institution.uuid'>
                    </select>
                </li>
            </ul>
            <ul class="table-layout"
                ng-show="((operationType.name === 'Distribution' && distributionType === 'Department') || (operationType.name === 'Return' && returnOperationType === 'Department'))">
                <li class="required">
                    <span>${ui.message('openhmis.inventory.department.name')}</span>
                </li>
                <li>
                    <select ng-model="department" required
                            ng-options='department.name for department in departments track by department.uuid'>
                    </select>
                </li>
            </ul>
            <ul class="table-layout" ng-repeat="attributeTypeAttribute in attributeTypeAttributes">
                <li class="required" ng-if="attributeTypeAttribute.required">
                    <span>{{attributeTypeAttribute.name}}</span>
                </li>
                <li class="not-required" ng-if="!attributeTypeAttribute.required">
                    <span>{{attributeTypeAttribute.name}}</span>
                </li>
                <li>
                    <span>
                        <input ng-if="attributeTypeAttribute.required" required
                               ng-model="attributes[attributeTypeAttribute.uuid].value" />
                        <input ng-if="!attributeTypeAttribute.required"
                               ng-model="attributes[attributeTypeAttribute.uuid].value" />
                    </span>
                </li>
            </ul>
            ${ui.includeFragment("openhmis.commons", "patientSearchFragment", [
                    showPatientDetails: "operationType.hasRecipient && selectedPatient !== '' && ((operationType.name === 'Distribution' && distributionType === 'Patient') || (operationType.name === 'Return' && returnOperationType === 'Patient'))",
                    showPatientSearchBox: "operationType.hasRecipient && selectedPatient === '' && ((operationType.name === 'Distribution' && distributionType === 'Patient') || (operationType.name === 'Return' && returnOperationType === 'Patient'))"
            ])}
            <fieldset class="nested"
                      ng-show="(!operationType.hasRecipient && (operationType.hasSource || operationType.hasDestination) ||
                      (operationType.hasRecipient && (operationType.hasSource && !operationType.hasDestination)) ||
                      (operationType.hasRecipient && (!operationType.hasSource && operationType.hasDestination)))">
                <legend>${ui.message('openhmis.inventory.operations.time.namePlural')}</legend>
                <span class="desc" ng-show="!showOperationItemsSection()">
                    ${ui.message('openhmis.inventory.operations.selectStockroom')}
                </span>
                <table class="item-stock"
                       ng-show="showOperationItemsSection()">
                    <tr>
                        <th></th>
                        <th>${ui.message('openhmis.inventory.item.name')}</th>
                        <th>${ui.message('openhmis.inventory.item.quantity')}</th>
                        <th>${ui.message('openhmis.inventory.operations.existingQuantity')}</th>
                        <th>${ui.message('openhmis.inventory.operations.newQuantity')}</th>
                        <th>${ui.message('openhmis.inventory.stockroom.expiration')}</th>
                    </tr>
                    <tr ng-repeat="lineItem in lineItems">
                        <td class="item-actions">
                            <input type="image" src="/openmrs/images/trash.gif"
                                   title="${ui.message('openhmis.inventory.operations.removeItemStock')}"
                                   class="remove" ng-click="removeLineItem(lineItem)">
                        </td>
                        <td>
                            ${ ui.includeFragment("openhmis.commons", "searchFragment", [
                                    typeahead: ["stockOperationItem.name for stockOperationItem in searchStockOperationItems(\$viewValue)"],
                                    model: "lineItem.itemStock",
                                    typeaheadOnSelect: "selectStockOperationItem(\$item, lineItem)",
                                    typeaheadEditable: "true",
                                    class: ["form-control autocomplete-search"],
                                    placeholder: [ui.message('openhmis.inventory.item.enterItemSearch')],
                            ])}

                        </td>
                        <td>
                            <input type="number" ng-model="lineItem.itemStockQuantity"
                                   style="width:50px" ng-change="changeItemQuantity(lineItem)" />
                        </td>
                        <td class="existing-quantity">
                            <input style="width:20px;" ng-disabled="lineItem.selected"
                                   ng-show="lineItem.selected" value="{{lineItem.existingQuantity}}" />
                        </td>
                        <td ng-show="lineItem.newQuantity < 0" class="negative-quantity">
                            <input style="width:20px;" ng-disabled="lineItem.selected" class="negative-quantity"
                                   ng-show="lineItem.selected" value="{{lineItem.newQuantity}}" />
                        </td>
                        <td ng-show="lineItem.newQuantity >= 0">
                            <input style="width:20px;" ng-disabled="lineItem.selected"
                                   ng-show="lineItem.selected" value="{{lineItem.newQuantity}}" />
                        </td>
                        <td>
                            <select ng-model="lineItem.itemStockExpirationDate"
                                    ng-show="!lineItem.expirationHasDatePicker"
                                    ng-options="itemStockExpirationDate for itemStockExpirationDate in lineItem.expirationDates"
                                    style="width:100px">
                            </select>

                            <span ng-show="lineItem.expirationHasDatePicker">
                                ${ ui.includeFragment("uicommons", "field/datetimepicker", [
                                        formFieldName: "lineItemExpDate",
                                        label: "",
                                        useTime: false,
                                        startDate : new Date(),
                                ])}
                            </span>
                        </td>
                    </tr>
                </table>
            </fieldset>
        </fieldset>

        <fieldset class="operation">
            <span>
                <input type="button" class="cancel" value="${ui.message('general.cancel')}"
                       ng-click="cancel()" />
                <input type="button" class="confirm right" value="${ui.message('general.save')}"
                       ng-click="saveOrUpdate()" />
            </span>
        </fieldset>

        <div id="change-operation-date-dialog" class="dialog" style="display:none">
            <div class="dialog-header">
                <span>
                    <i class="icon-warning-sign"></i>
                    <h3>
                        ${ui.message('openhmis.inventory.operations.confirm.title.operationTypeDate')}
                    </h3>
                </span>
                <i class="icon-remove cancel show-cursor" style="float:right;"
                   ng-click="closeThisDialog()"></i>
            </div>
            <div class="dialog-content form">
                <table class="operation-date">
                    <tr>
                        <td style="width:30%">
                            ${ui.message('openhmis.inventory.operations.operationDate')}
                        </td>
                        <td style="width:60%">
                            ${ ui.includeFragment("uicommons", "field/datetimepicker", [
                                    id: "operationDateId",
                                    formFieldName: "operationDate",
                                    label: "",
                                    useTime: false,
                                    endDate: new Date(),
                            ])}
                        </td>
                    </tr>
                    <tr>
                        <td>${ui.message('openhmis.inventory.operation.occurs')}</td>
                        <td>
                            <select ng-model="operationOccurDate"
                                    ng-options="occur.name for occur in operationOccurs"
                                    style="width:inherit">
                            </select>
                        </td>
                    </tr>
                </table>
                <br />
                <div class="ngdialog-buttons detail-section-border-top">
                    <br />
                    <input type="button" class="cancel"
                           value="${ui.message('general.cancel')}"
                           ng-click="closeThisDialog('Cancel')" />
                    <input type="button" class="confirm right"
                           value="${ui.message('openhmis.inventory.operations.changeDate')}"
                           ng-click="confirm('OK')" />
                </div>
            </div>
        </div>

        <div id="warning-dialog" class="dialog" style="display:none;">
            <div class="dialog-header">
                <span>
                    <i class="icon-warning-sign"></i>
                    <h3>{{warningTitle}}</h3>
                </span>
                <i class="icon-remove cancel show-cursor"  style="float:right;" ng-click="closeThisDialog()"></i>
            </div>
            <div class="dialog-content form">
                <span>{{warningMessage}}</span>
                <br /><br />
                <div class="ngdialog-buttons detail-section-border-top">
                    <br />
                    <input type="button" class="cancel" value="${ui.message('general.cancel')}" ng-click="closeThisDialog('Cancel')" />
                    <input type="button" class="confirm right" value="Confirm"  ng-click="confirm('OK')" />
                </div>
            </div>
        </div>
</form>
</div>

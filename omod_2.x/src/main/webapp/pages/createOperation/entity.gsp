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

<div ng-show="loading" style="margin:200px;">
    <span>${ui.message("openhmis.inventory.admin.create.processing")}</span>
    <br />
    <span style="margin:150px;">
        <img src="${ ui.resourceLink("uicommons", "images/spinner.gif") }"/>
    </span>
</div>

<div ng-hide="loading">
    <h1>${ui.message('openhmis.inventory.admin.create')}</h1>
    <form name="entityForm" class="entity-form" ng-class="{'submitted': submitted}" style="font-size:inherit">
        <fieldset class="operation createOperation">
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
                        <input class="form-control" ng-model="entity.operationNumber" ng-show="!isOperationNumberGenerated" required/>
                        <span ng-show="isOperationNumberGenerated">{{entity.operationNumber}}</span>
                    </span>
                </li>
            </ul>
            <ul class="table-layout">
                <li class="required">
                    <span>${ui.message('openhmis.inventory.operations.operationType')}</span>
                </li>
                <li>
                    <select ng-model="operationType" required class="form-control"
                            options-disabled="!operationType.canProcess for operationType in operationTypes"
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
                        {{operationDate}} &nbsp;&nbsp;&nbsp;
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
                    <select ng-model="sourceStockroom" required class="form-control"
                            ng-change="warningDialog(sourceStockroom, '{{sourceStockroom}}', 'stockroom')"
                            ng-options='sourceStockroom.name for sourceStockroom in sourceStockrooms track by sourceStockroom.uuid'>
                    </select>
                </li>
            </ul>
            <ul class="table-layout" ng-show="operationType.name === 'Distribution' && distributionType !== 'Patient'">
                <li class="not-required">
                    <span>${ui.message('openhmis.inventory.operations.distributeTo')}</span>
                </li>
                <li>
                    <select ng-model="distributionType" class="form-control"
                            ng-options="distributionType for distributionType in distributionTypes">
                    </select>
                </li>
            </ul>
            <ul class="table-layout" ng-show="operationType.name === 'Return'">
                <li class="not-required">
                    <span>${ui.message('openhmis.inventory.operations.returnTo')}</span>
                </li>
                <li>
                    <select ng-model="returnOperationType" class="form-control"
                            ng-options="returnOperationType for returnOperationType in returnOperationTypes">
                    </select>
                </li>
            </ul>
            <ul class="table-layout" ng-show="operationType.hasDestination">
                <li class="required">
                    <span>${ui.message('openhmis.inventory.operations.destinationStockroom')}</span>
                </li>
                <li>
                    <select ng-model="destinationStockroom" required class="form-control"
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
                    <select ng-model="institutionStockroom" required class="form-control"
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
                    <select ng-model="department" required class="form-control"
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
                        <input ng-if="attributeTypeAttribute.required" required class="form-control"
                               ng-model="attributes[attributeTypeAttribute.uuid].value" />
                        <input ng-if="!attributeTypeAttribute.required" class="form-control"
                               ng-model="attributes[attributeTypeAttribute.uuid].value" />
                    </span>
                </li>
            </ul>
            <ul class="table-layout" ng-show="operationType.name === 'Distribution' && distributionType === 'Patient'">
                <li class="not-required">
                    <span>${ui.message('openhmis.inventory.operations.distributeTo')}</span>
                </li>
                <li>
                    <select ng-model="distributionType" class="form-control"
                            ng-options="distributionType for distributionType in distributionTypes">
                    </select>
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
                    <thead>
                        <tr>
                            <th></th>
                            <th>${ui.message('openhmis.inventory.item.name')}</th>
                        </tr>
                    </thead>
                    <tr ng-repeat="lineItem in lineItems" >
                        <td class="item-actions" ng-class="{'negative-quantity' : lineItem.newQuantity < 0}">
                            <table class="icons">
                                <tr>
                                    <td ng-class="{'negative-quantity' : lineItem.newQuantity < 0}">
                                        <input type="image" src="/openmrs/images/trash.gif" tabindex="-1"
                                               title="${ui.message('openhmis.inventory.operations.removeItemStock')}"
                                               ng-show="lineItem.selected"
                                               class="remove,{{lineItem.selected ? 'row_selected' : ''}}" ng-click="removeLineItem(lineItem)">
                                    </td>
                                    <td ng-class="{'negative-quantity' : lineItem.newQuantity < 0}">
                                        <i ng-show="lineItem.newQuantity < 0" class="icon-info-sign" title="Out of Stock"></i>
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td ng-class="{'negative-quantity' : lineItem.newQuantity < 0}">

                            <table class="item-details" ng-shdow="lineItem.selected">
                                <tr>
                                    <td ng-class="{'negative-quantity' : lineItem.newQuantity < 0}">
                                        ${ ui.includeFragment("openhmis.commons", "searchFragment", [
                                            typeahead: ["stockOperationItem.name for stockOperationItem in searchStockOperationItems(\$viewValue)"],
                                            model: "lineItem.itemStock",
                                            typeaheadOnSelect: "selectStockOperationItem(\$item, lineItem)",
                                            typeaheadEditable: "true",
                                            class: ["form-control autocomplete-search"],
                                            showRemoveIcon: "false",
                                            ngEnterEvent: "addLineItem()",
                                            placeholder: [ui.message('openhmis.inventory.item.enterItemSearch')],
                                    ])}</td>
                                    <td ng-class="{'negative-quantity' : lineItem.newQuantity < 0}">
                                        <b>${ui.message("openhmis.inventory.item.quantity")}:</b>
                                    </td>
                                    <td ng-class="{'negative-quantity' : lineItem.newQuantity < 0}">
                                        <input type="number" ng-model="lineItem.itemStockQuantity" class="form-control"
                                               ng-enter="changeItemQuantity(lineItem)" style="width:60px;"
                                               ng-change="changeItemQuantity(lineItem)" />
                                    </td>
                                    <td ng-class="{'negative-quantity' : lineItem.newQuantity < 0}">
                                        <b>${ui.message("openhmis.inventory.stockroom.expiration")}:</b>
                                    </td>
                                    <td ng-class="{'negative-quantity' : lineItem.newQuantity < 0}">
                                        <select ng-model="lineItem.itemStockExpirationDate"
                                                ng-change="changeExpiration(lineItem)"
                                                ng-show="!lineItem.expirationHasDatePicker"
                                                class="right-justify form-control"
                                                ng-options="itemStockExpirationDate for itemStockExpirationDate in lineItem.expirationDates"
                                                style="width:100px;">
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
                                <tr>
                                    <td></td>
                                    <td colspan="3" style="font-size:11px;" ng-class="{'negative-quantity' : lineItem.newQuantity < 0}">
                                        <b>${ui.message("openhmis.inventory.operations.existingQuantity")}:</b>
                                        <span ng-show="lineItem.selected">
                                            {{lineItem.existingQuantity}}
                                        </span>
                                        <span ng-show="!lineItem.selected">-</span>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </fieldset>
        </fieldset>
        <br/>
        <div>
            <input type="button" class="cancel" value="${ui.message('general.cancel')}"
                       ng-click="cancel()" />
            <input type="button" class="confirm right" value="${ui.message('general.save')}"
                       ng-click="saveOrUpdate()" />
        </div>
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
                            <select ng-model="operationOccurDate" class="form-control"
                                    ng-options="occur.name for occur in operationOccurs"
                                    style="width:235px">
                            </select>
                        </td>
                    </tr>
                </table>
                <div class="ngdialog-buttons detail-section-border-top">
                    <br />
                    <input type="button" class="cancel"
                           value="${ui.message('general.cancel')}"
                           ng-click="closeThisDialog('Cancel')" />
                    <input type="button" class="confirm right btn gray-button"
                           value="${ui.message('openhmis.inventory.operations.changeDate')}"
                           ng-disabled="operationOccurDate === undefined"
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

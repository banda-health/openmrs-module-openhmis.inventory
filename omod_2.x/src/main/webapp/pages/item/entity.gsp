<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("openhmis.inventory.page")}" , link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'},
        { label: "${ ui.message("openhmis.inventory.manage.module")}", link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/inventory/manageModule.page' },
        { label: "${ ui.message("openhmis.inventory.admin.items")}", link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/item/entities.page##/'},
        { label: "${ui.message("openhmis.inventory.item.name")}"}
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

<form ng-hide="loading" name="itemForm" class="entity-form" ng-class="{'submitted': submitted}" style="font-size:inherit">
    ${ ui.includeFragment("openhmis.commons", "editEntityHeaderFragment")}

    <input type="hidden" ng-model="entity.uuid" />

    <fieldset class="format">

        <ul class="table-layout">
            <li class="required">
                <span>{{messageLabels['general.name']}}</span>
            </li>
            <li>
                <input name="entityName" type="text" ng-model="entity.name" class="maximized" placeholder="{{messageLabels['general.name']}}" required />
                <p class="checkRequired" ng-hide="nameIsRequiredMsg == '' || nameIsRequiredMsg == undefined">{{nameIsRequiredMsg}}</p>
            </li>
        </ul>
        <ul class="table-layout">
            <li class="required">
                <span>{{messageLabels['openhmis.inventory.department.name']}}</span>
            </li>
            <li>
                <select ng-model="department" required
                        ng-options='department.name for department in departments track by department.uuid'>
                </select>
            </li>
        </ul>
        <ul class="table-layout">
            <li class="not-required">
                <span>{{messageLabels['openhmis.inventory.item.hasExpiration']}}</span>
            </li>
            <li>
                <input type="checkbox" ng-model="entity.hasExpiration"  />
            </li>
        </ul>
        <ul class="table-layout" ng-show="entity.hasExpiration">
            <li class="not-required">
                <span>{{messageLabels['openhmis.inventory.item.defaultExpirationPeriod']}}</span>
            </li>
            <li>
                <input type="number" ng-model="entity.defaultExpirationPeriod" class="minimized"/>
            </li>
        </ul>
        <ul class="table-layout autocomplete-table-layout">
            <li class="not-required">
                <span>{{messageLabels['Concept']}}</span>
            </li>
            <li>
                ${ ui.includeFragment("openhmis.commons", "searchFragment", [
                        typeahead: ["concept.display for concept in searchConcepts(\$viewValue)"],
                        model: "concept",
                        typeaheadOnSelect: "selectConcept(\$item)",
                        typeaheadEditable: "true",
                        class: ["form-control autocomplete-search"],
                        placeholder: [ui.message('openhmis.inventory.item.enterConceptName')],
                ])}
            </li>
        </ul>
        <ul class="table-layout">
            <li class="not-required">
                <span>{{messageLabels['openhmis.inventory.item.hasPhysicalInventory']}}</span>
            </li>
            <li>
                <input type="checkbox" ng-model="entity.hasPhysicalInventory" />
            </li>
        </ul>
        <ul class="table-layout">
            <li class="not-required">
                <span>{{messageLabels['openhmis.inventory.item.minimumQuantity']}}</span>
            </li>
            <li>
                <input type="number" ng-model="entity.minimumQuantity" class="minimized"/>
            </li>
        </ul>
        <ul class="table-layout">
            <li class="not-required">
                <span>{{messageLabels['openhmis.inventory.item.buyingPrice']}}</span>
            </li>
            <li>
                <input type="number" ng-model="entity.buyingPrice" class="minimized" min="0"/>
            </li>
        </ul>
        <ul class="table-layout">
            <li class="not-required valign">
                <span>{{messageLabels['openhmis.inventory.item.code.namePlural']}}</span>
            </li>
            <li>
                <div class="bbf-editor">
                    <div class="bbf-list" name="codes">
                        <ul class="attributes-layout">
                            <li ng-repeat="itemCode in entity.codes track by itemCode.uuid || itemCode.id">
                                <a href="" ng-click="removeItemCode(itemCode)">
                                    <i class="icon-remove"></i>
                                </a>

                                <a href="" ng-click="editItemCode(itemCode)">{{itemCode.code}}</a>
                            </li>
                        </ul>
                        <div class="bbf-actions">
                            <button type="button" data-action="add" ng-click="addItemCode()">{{messageLabels['openhmis.commons.general.add']}}</button>
                        </div>
                    </div>
                </div>
            </li>
        </ul>
        <ul class="table-layout">
            <li class="required valign">
                <span>{{messageLabels['openhmis.inventory.item.prices']}}</span>
            </li>
            <li>
                <div class="bbf-editor">
                    <div class="bbf-list" name="prices">
                        <ul class="attributes-layout">
                            <li ng-repeat="itemPrice in entity.prices track by (itemPrice.uuid || itemPrice.id)">
                                <a href="" ng-click="removeItemPrice(itemPrice)">
                                    <i class="icon-remove"></i>
                                </a>
                                <a href="" ng-click="editItemPrice(itemPrice)">{{formatItemPrice(itemPrice)}}</a>
                            </li>
                        </ul>
                        <div class="bbf-actions">
                            <button type="button" data-action="add" ng-click="addItemPrice()">{{messageLabels['openhmis.commons.general.add']}}</button>
                        </div>
                    </div>
                </div>
            </li>
        </ul>
        <ul class="table-layout">
            <li class="required">
                <span>{{messageLabels['openhmis.inventory.item.defaultPrice']}}</span>
            </li>
            <li>
                <select required ng-model="entity.defaultPrice"
                        ng-options="formatItemPrice(itemPrice) for itemPrice in entity.prices track by (itemPrice.uuid || itemPrice.id)">
                </select>
            </li>
        </ul>

        <div ng-repeat="itemAttributeType in itemAttributeTypes track by itemAttributeType.uuid" ng-init="index = \$index">
            <ul class="table-layout">
                <li class="required" ng-if="itemAttributeType.required">
                    <span>{{itemAttributeType.name}}</span>
                </li>
                <li class="not-required" ng-if="!itemAttributeType.required">
                    <span>{{itemAttributeType.name}}</span>
                </li>
                <li>
                    <input ng-if="itemAttributeType.required" name="attributeType{{index}}" type="text" ng-model="attributes[itemAttributeType.uuid]" required />
                    <input ng-if="!itemAttributeType.required" type="text" ng-model="attributes[itemAttributeType.uuid]" />
                </li>
            </ul>
        </div>
    </fieldset>

    <br />
        <table ng-hide="itemStock == ''" class="stockroom-table">
            <thead>
                <tr>
                    <th>{{messageLabels['openhmis.inventory.stockroom.name']}}</th>
                    <th>{{messageLabels['openhmis.inventory.item.quantity']}}</th>
                </tr>
            </thead>
            <tbody>
                <tr class="clickable-tr" ng-repeat="stock in itemStock track by stock.uuid" >
                    <td>{{stock.stockroom.name}}</td>
                    <td>{{stock.quantity}}</td>
                </tr>
            </tbody>
        </table>
    <fieldset class="format">
        <span>
            <input type="button" class="cancel" value="{{messageLabels['general.cancel']}}" ng-click="cancel()" />
            <input type="button" class="confirm right" value="{{messageLabels['general.save']}}" ng-click="saveOrUpdate()" />
        </span>
    </fieldset>
    <div id="item-price-dialog" class="dialog" style="display:none;">
        <div class="dialog-header">
            <span ng-show="addItemPriceTitle !=''">
                <i class="icon-plus"></i>
                <h3>{{addItemPriceTitle}}</h3>
            </span>
            <span ng-show="editItemPriceTitle !=''">
                <i class="icon-edit"></i>
                <h3>{{editItemPriceTitle}}</h3>
            </span>
            <i class="icon-remove cancel show-cursor"  style="float:right;" ng-click="closeThisDialog()"></i>
        </div>
        <div class="dialog-content form">
            <ul class="table-layout dialog-table-layout">
                <li class="not-required">{{messageLabels['general.name']}}</li>
                <li><input type="text" ng-model="itemPrice.name" /></li>
            </ul>
            <ul class="table-layout dialog-table-layout">
                <li class="required">{{messageLabels['openhmis.inventory.item.price.name']}}</li>
                <li><input type="number" ng-model="itemPrice.price" min="0" required /></li>
            </ul>
            <br />
            <div class="ngdialog-buttons detail-section-border-top">
                <br />
                <input type="button" class="cancel" value="{{messageLabels['general.cancel']}}" ng-click="closeThisDialog('Cancel')" />
                <input type="button" class="confirm right" value="Confirm" ng-disabled="itemPrice.price == undefined"  ng-click="confirm('OK')" />
            </div>
        </div>
    </div>

    <div id="item-code-dialog" class="dialog" style="display:none;">
        <div class="dialog-header">
            <span ng-show="addItemCodeTitle !=''">
                <i class="icon-plus"></i>
                <h3>{{addItemCodeTitle}}</h3>
            </span>
            <span ng-show="editItemCodeTitle != ''">
                <i class="icon-edit"></i>
                <h3>{{editItemCodeTitle}}</h3>
            </span>
            <i class="icon-remove cancel show-cursor"  style="float:right;" ng-click="closeThisDialog()"></i>
        </div>
        <div class="dialog-content form">
            <ul class="table-layout dialog-table-layout">
                <li>{{messageLabels['openhmis.inventory.item.code.name']}}</li>
                <li><input type="text" ng-model="itemCode.code" /></li>
            </ul>
            <br />
            <div class="ngdialog-buttons detail-section-border-top">
                <br />
                <input type="button" class="cancel" value="{{messageLabels['general.cancel']}}" ng-click="closeThisDialog('Cancel')" />
                <input type="button" class="confirm right" value="Confirm"  ng-disabled="itemCode.code == undefined" ng-click="confirm('OK')" />
            </div>
        </div>
    </div>
</form>
${ ui.includeFragment("openhmis.commons", "retireUnretireDeleteFragment", [retireUnretireCall : "retireUnretire()"]) }

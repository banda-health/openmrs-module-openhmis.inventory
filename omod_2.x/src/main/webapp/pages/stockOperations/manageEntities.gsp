<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("openhmis.inventory.page")}" , link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'},
        { label: "${ ui.message("openhmis.inventory.manage.module")}", link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/inventory/manageModule.page' },
        { label: "${ ui.message("openhmis.inventory.admin.operations")}", }
    ];
    jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));
</script>
<div id="entities-body">
    <br />
    <div id="manage-entities-header">
        <span class="h1-substitue-left" style="float:left;">
            ${ ui.message('openhmis.inventory.admin.operations') }
        </span>
    </div>
    <br /><br/>
    <div ng-controller="ManageStockOperationsController">
        <div id="entities">
            <form style="" class="search">
                <fieldset class="search">
            <table class="search">
                <tr>
                    <td>${ ui.message('openhmis.inventory.general.status')}:
                        <select ng-model="operation_status" ng-change="searchStockOperation()">
                            <option value="" selected="selected">Any</option>
                            <option value="Pending">Pending</option>
                            <option value="Completed">Completed</option>
                            <option value="Cancelled">Cancelled</option>
                            <option value="Rollback">Rollback</option>
                        </select>
                    </td>
                    <td>
                        ${ ui.message('openhmis.inventory.operations.type.name')}:
                        <select ng-change="searchStockOperation()" ng-model="operationType" ng-options="operationType.name for operationType in stockOperationTypes">
                            <option value="" selected="selected">Any</option>
                        </select>
                    </td>
                    <td>
                        ${ ui.message('openhmis.inventory.stockroom.name')}:
                        <select ng-model="stockroom" ng-options="stockroom.name for stockroom in stockrooms" ng-change="searchStockOperation()">
                            <option value="" selected="selected">Any</option>
                        </select>
                    </td>
                    <td>
                        ${ ui.message('openhmis.inventory.item.name')}:

                        ${ ui.includeFragment("openhmis.commons", "searchFragment", [
                                onChangeEvent: "searchItems()",
                                typeahead: ["stockOperationItem.name for stockOperationItem in stockOperationItems"],
                                model: "searchOperationItem",
                                typeaheadOnSelect: "selectItem(\$item)",
                                typeaheadEditable: "true",
                                class: ["form-control"],
                                placeholder: [ui.message('Enter Item to search')],
                        ])}

                    </td>
                    <td>
                        <input type="button" class="confirm right" value="Search" style="width:100px;margin-top:20px;" ng-click="searchStockOperation()" />
                    </td>
                </tr>
            </table></fieldset>
                </form>
            <br /><br />
            <table style="margin-bottom:5px;margin-top:5%;" class="manage-entities-table manage-stockOperations-table">
                <thead>
                <tr>
                    <th>${ ui.message('openhmis.inventory.operations.dateCreated')}</th>
                    <th>${ ui.message('openhmis.inventory.operations.operationDate')}</th>
                    <th>${ ui.message('openhmis.inventory.operations.operationType')}</th>
                    <th>${ ui.message('openhmis.inventory.operations.operationNumber')}</th>
                    <th>${ ui.message('openhmis.inventory.general.status')}</th>
                </tr>
                </thead>
                <tbody>
                <tr class="clickable-tr" dir-paginate="entity in fetchedEntities | itemsPerPage: limit" total-items="totalNumOfResults" current-page="currentPage"  ui-sref="edit({uuid: entity.uuid})">
                    <td ng-style="strikeThrough(entity.retired)">{{entity.dateCreated | date: 'dd-MM-yyyy, h:mma'}}</td>
                    <td ng-style="strikeThrough(entity.retired)">{{entity.operationDate | date: 'dd-MM-yyyy, h:mma'}}</td>
                    <td ng-style="strikeThrough(entity.retired)">{{entity.instanceType.name}}</td>
                    <td ng-style="strikeThrough(entity.retired)">{{entity.operationNumber}}</td>
                    <td ng-style="strikeThrough(entity.retired)">{{entity.status}}</td>
                </tr>
                </tbody>
            </table>
            <div class="not-found"  ng-show="fetchedEntities.length == 0 && searchField == ''">
                ${ ui.message('openhmis.inventory.operations.noStocksFound') }
            </div>
            <div ng-show="fetchedEntities.length == 0 && searchField != ''">
                <br />
                ${ ui.message('Your search - <b>') } {{searchField}} ${ ui.message('</b> - did not match any stockrooms')}
                <br /><br />
                <span><input type="checkbox" ng-checked="includeRetired" ng-model="includeRetired" ng-change="updateContent()"></span>
                <span>${ ui.message('openhmis.inventory.general.includeRetired') }</span>
            </div>
            <div id="below-entities-table" ng-hide="fetchedEntities.length == 0">
                <span style="float:right;">
                    <div class="entity-pagination">
                        <dir-pagination-controls on-page-change="paginate(currentPage)"></dir-pagination-controls>
                    </div>
                </span>
                <br />
                <div class="pagination-options">
                    <div id="showing-entities">
                        <span><b>${ ui.message('openhmis.inventory.general.showing') } {{pagingFrom(currentPage, limit)}} ${ ui.message('openhmis.inventory.general.to') } {{pagingTo(currentPage, limit, totalNumOfResults)}}</b></span>
                        <span><b>${ ui.message('openhmis.inventory.general.of') } {{totalNumOfResults}} ${ ui.message('openhmis.inventory.general.entries') }</b></span>
                    </div>
                    <div id="includeVoided-entities">
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
                </div>
            </div>
        </div>
    </div>
</div>
<%
    def title = config.pageTitle;
%>
<div id="entities-body">
    <br/>
    <div id="manage-entities-header">
        <span class="h1-substitue-left" style="float:left;">
            <%
                if(title){
                    title.each{ %>
                        ${ it }
                    <% }
                }
                else{ %>
                    ${ui.message('openhmis.inventory.admin.operations')}
            <%  } %>
        </span>
        <span style="float:right;">
            <a class="button confirm" href="/${ ui.contextPath() }/openhmis.inventory/createOperation/entities.page">
                <i class="icon-plus"></i>
                ${ui.message('openhmis.inventory.operations.createOperation')}
            </a>
        </span>

    </div>
    <br/><br/>

    <div>
        <div id="entities">
            <form style="" class="search">
                <fieldset class="search">
                    <table class="search">
                        <tr>
                            <td>${ui.message('openhmis.commons.general.status')}:
                                <select ng-model="operation_status" ng-change="searchStockOperation(currentPage)">
                                    <option value="" selected="selected">${ui.message('openhmis.commons.general.any')}</option>
                                    <option value="Pending">Pending</option>
                                    <option value="Completed">Completed</option>
                                    <option value="Cancelled">Cancelled</option>
                                    <option value="Rollback">Rollback</option>
                                </select>
                            </td>
                            <td>
                                ${ui.message('openhmis.inventory.operations.type.name')}:
                                <select ng-change="searchStockOperation(currentPage)" ng-model="operationType"
                                        ng-options="operationType.name for operationType in stockOperationTypes">
                                    <option value="" selected="selected">${ui.message('openhmis.commons.general.any')}</option>
                                </select>
                            </td>
                            <td>
                                ${ui.message('openhmis.inventory.stockroom.name')}:
                                <select ng-model="stockroom" ng-options="stockroom.name for stockroom in stockrooms"
                                        ng-change="searchStockOperation(currentPage)">
                                    <option value="" selected="selected">${ui.message('openhmis.commons.general.any')}</option>
                                </select>
                            </td>
                            <td>
                                ${ui.message('openhmis.inventory.item.name')}:
                                ${ ui.includeFragment("openhmis.commons", "searchFragment", [
                                        typeahead: ["stockOperationItem.name for stockOperationItem in searchItems(\$viewValue)"],
                                        model: "searchOperationItem",
                                        typeaheadOnSelect: "selectItem(\$item)",
                                        typeaheadEditable: "true",
                                        class: ["form-control"],
                                        placeholder: [ui.message('openhmis.inventory.item.enterItemSearch')],
                                        ngEnterEvent: "searchStockOperation(1)"
                                ])}
                            </td>
                        </tr>
                    </table></fieldset>
            </form>
            <br/><br/>
            <table style="margin-bottom:5px;margin-top:5%;" class="manage-entities-table manage-stockOperations-table">
                <thead>
                <tr>
                    <th>${ui.message('openhmis.inventory.operations.dateCreated')}</th>
                    <th>${ui.message('openhmis.inventory.operations.operationDate')}</th>
                    <th>${ui.message('openhmis.inventory.operations.operationType')}</th>
                    <th>${ui.message('openhmis.inventory.operations.operationNumber')}</th>
                    <th>${ui.message('openhmis.commons.general.status')}</th>
                </tr>
                </thead>
                <tbody>
                <tr class="clickable-tr" dir-paginate="entity in fetchedEntities | itemsPerPage: limit"
                    total-items="totalNumOfResults" current-page="currentPage" ui-sref="edit({uuid: entity.uuid})">
                    <td ng-style="strikeThrough(entity.retired)">{{entity.dateCreated | date: 'dd-MM-yyyy, h:mma'}}</td>
                    <td ng-style="strikeThrough(entity.retired)">{{entity.operationDate | date: 'dd-MM-yyyy, h:mma'}}</td>
                    <td ng-style="strikeThrough(entity.retired)">{{entity.instanceType.name}}</td>
                    <td ng-style="strikeThrough(entity.retired)">{{entity.operationNumber}}</td>
                    <td ng-style="strikeThrough(entity.retired)">{{entity.status}}</td>
                </tr>
                </tbody>
            </table>

            <div class="not-found" ng-show="fetchedEntities.length == 0 && searchField == ''">
                ${ui.message('openhmis.inventory.operations.noStocksFound')}
            </div>

            <div ng-show="fetchedEntities.length == 0 && searchField != ''">
                <br/>
                ${ui.message('openhmis.commons.general.preSearchMessage')} - <b> {{searchField}} </b> - {{postSearchMessage}}
                <br/><br/>
                <span><input type="checkbox" ng-checked="includeRetired" ng-model="includeRetired"
                             ng-change="searchStockOperation(currentPage)"></span>
                <span>${ui.message('openhmis.commons.general.includeRetired')}</span>
            </div>
            ${ui.includeFragment("openhmis.commons", "paginationFragment", [
                    showRetiredSection  : "false",
                    onPageChange : "searchStockOperation(currentPage)",
                    onChange : "searchStockOperation(currentPage)"
            ])}
        </div>
    </div>
</div>

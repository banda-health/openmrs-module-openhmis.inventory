<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("openhmis.inventory.page")}" , link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'},
        { label: "${ ui.message("openhmis.inventory.manage.module")}", link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/inventory/manageModule.page' },
        { label: "${ ui.message("openhmis.inventory.admin.items")}", }
    ];

    jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));

</script>
<div id="entities-body">
    <br />
    <div id="manage-entities-header">
        <span class="h1-substitue-left" style="float:left;">
            ${ ui.message('openhmis.inventory.admin.items') }
        </span>
        <span style="float:right;">
            <a class="button confirm" ui-sref="new" >
                <i class ="icon-plus"></i>
                {{newEntityLabel}}
            </a>
        </span>
    </div>
    <br /><br /><br />
    <div ng-controller="ManageItemController">
        <div id="entities">
            ${ ui.includeFragment("openhmis.commons", "searchFragment", [
                    model: "searchField",
                    onChangeEvent: "updateContent()",
                    class: ["field-display ui-autocomplete-input form-control searchinput"],
                    placeholder: [ui.message("Enter Stockroom to search")]
            ])}
            <br /><br />
            <table style="margin-bottom:5px;" class="manage-entities-table">
                <thead>
                <tr>
                    <th>${ ui.message('general.name') }</th>
                    <th>${ ui.message('openhmis.inventory.department.name') }</th>
                    <th>Codes</th>
                    <th>${ ui.message('openhmis.inventory.item.defaultPrice') }</th>
                </tr>
                </thead>
                <tbody>
                <tr class="clickable-tr" dir-paginate="entity in fetchedEntities | itemsPerPage: limit" total-items="totalNumOfResults" current-page="currentPage"  ui-sref="edit({uuid: entity.uuid})">
                    <td ng-style="strikeThrough(entity.retired)">{{entity.name}}</td>
                    <td ng-style="strikeThrough(entity.retired)">{{entity.department.name}}</td>
                    <td ng-style="strikeThrough(entity.retired)">
                        <span ng-repeat="code in entity.codes">{{code.code}}</span>
                    </td>
                    <td ng-style="strikeThrough(entity.retired)">{{entity.defaultPrice.price | number:2}} <span ng-if="entity.defaultPrice.name != ''">({{entity.defaultPrice.name}})</span></td>
                </tr>
                </tbody>
            </table>
            <div ng-show="fetchedEntities.length == 0">
                <br />
                ${ ui.message('Your search - <b>') } {{searchField}} ${ ui.message('</b> - did not match any items')}
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

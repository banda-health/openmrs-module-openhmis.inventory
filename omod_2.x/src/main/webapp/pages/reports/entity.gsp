<script type="text/javascript" xmlns="http://www.w3.org/1999/html">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        {
            label: "${ ui.message("openhmis.inventory.page")}" ,
            link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'
        },
        {
            label: "${ ui.message("openhmis.inventory.admin.task.dashboard")}",
            link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/inventory/inventoryTasksDashboard.page'
        },
        {
            label: "${ ui.message("openhmis.inventory.admin.reports")}",
            link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/reports/entities.page#/'
        }
    ];

    jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));

</script>
<style>
    .report{
        max-width:700px;
    }
    .report legend{
        width:inherit;
        margin-bottom: 0px;
    }

    .report input[type="date"]{
        display: inline-block;
    }
</style>

<input id="reportUrl" type="hidden" value="{{ ui.message('openhmis.inventory.admin.reports')}}" />

<h2>{{ ui.message("openhmis.inventory.admin.reports")}}</h2>

<div class="report">
    <fieldset>
        <legend>
            <i class="icon-list-alt"></i>
            {{stockTakeReport.name}}
        </legend>
        <small>{{stockTakeReport.description}}</small>

        <form id="stockTakeReport" onsubmit="return false;">
            <ul class="table-layout">
                <li><label>Stockroom </label></li>
                <li>
                    <select ng-model="stockroom" ng-options='stockroom.name for stockroom in stockrooms'>
                        <option value="" selected="selected">Select Stockroom</option>
                    </select>
                </li>
            </ul>
            <ul class="table-layout">
                <li></li>
                <li><input id="generateTakeReport" type="submit" value="Generate Report" /></li>
            </ul>

            <input id="stockTakeReportId" type="hidden" value="{{stockTakeReport.reportId}}" />
        </form>
    </fieldset>
</div>
<hr>

<div class="report">
    <fieldset>
        <legend>
            <i class="icon-list-alt"></i>
            {{stockCardReport.name}}
        </legend>
        <small>{{stockCardReport.description}}</small>
        <form id="stockCardReport" onsubmit="return false;">
            <ul class="table-layout">
                <li><label>Item</label></li>
                <li>
                    ${ ui.includeFragment("openhmis.commons", "searchFragment", [
                            typeahead: ["reportItem.name for reportItem in searchReportItems(\$viewValue)"],
                            model: "searchItemAllStockrooms",
                            typeaheadOnSelect: "console.log(\$item)",
                            typeaheadEditable: "true",
                            class: ["form-control"],
                            placeholder: [ui.message('openhmis.inventory.item.enterItemSearch')],
                            ngEnterEvent: "console.log(\$item)"
                    ])}
                </li>
            </ul>
            <ul class="table-layout">
                <li><label for="beginDate">Begin Date</label></li>
                <li>
                    <span id="beginDatespan" class="date">
                        <input type="date" id="beginDate" value="">
                        <span class="add-on"><i class="icon-calendar small"></i></span>
                    </span>
                </li>
            </ul>
            <ul class="table-layout">
                <li><label for="endDate">End Date</label></li>
                <li>
                    <span id="endDatespan" class="date">
                        <input type="date" id="endDate" value="">
                        <span class="add-on"><i class="icon-calendar small"></i></span>
                    </span>
                </li>
            </ul>
            <ul class="table-layout">
                <li></li>
                <li><input id="generateCardReport" type="submit" value="Generate Report" /></li>
            </ul>

            <input type="hidden" id="item-uuid" />
            <input type="hidden" id="datetime-field" name="date picker" value="">
            <input type="hidden" id="stockCardReportId" value="{{stockCardReport.reportId}}" />

        </form>
    </fieldset>
</div>
<hr>


<div class="report">
    <fieldset>
        <legend>
            <i class="icon-list-alt"></i>
            {{stockOperationsByStockroomReport.name}}
        </legend>
        <small>{{stockOperationsByStockroomReport.description}}</small>
        <form id="stockOperationsByStockroomReport" onsubmit="return false;">
            <ul class="table-layout">
                <li><label>Stockroom </label></li>
                <li>
                    <select id="stockroomOperationsByStockroom" ng-model="stockroom" ng-options='stockroom.name for stockroom in stockrooms'>
                        <option value="" selected="selected">Select Stockroom</option>
                    </select>
                </li>
            </ul>

            <ul class="table-layout">
                <li><label>Item</label></li>
                <li>
                    ${ ui.includeFragment("openhmis.commons", "searchFragment", [
                            typeahead: ["reportItem.name for reportItem in searchReportItems(\$viewValue)"],
                            model: "searchItemByStockroom",
                            typeaheadOnSelect: "console.log(\$item)",
                            typeaheadEditable: "true",
                            class: ["form-control"],
                            placeholder: [ui.message('openhmis.inventory.item.enterItemSearch')]
                    ])}
                </li>
            </ul>

            <ul class="table-layout">
                <li><label for="beginDate-operationsByStockroom">Begin Date</label></li>
                <li>
                    <span class="date">
                        <input type="date" id="beginDate-operationsByStockroom" value="">
                        <span class="add-on"><i class="icon-calendar small"></i></span>
                    </span>
                </li>
            </ul>

            <ul class="table-layout">
                <li><label for="endDate-operationsByStockroom">End Date</label></li>
                <li>
                    <span class="date">
                        <input type="date" id="endDate-operationsByStockroom" value="">
                        <span class="add-on"><i class="icon-calendar small"></i></span>
                    </span>
                </li>
            </ul>

            <ul class="table-layout">
                <li></li>
                <li><input id="generateOperationsByStockroomReport" type="submit" value="Generate Report" /></li>
            </ul>

            <input type="hidden" id="item-uuid-searchOperationsByStockroom" />
            <input type="hidden" id="stockOperationsByStockroomReportId"  value="{{stockOperationsByStockroomReport.reportId}}" />
        </form>
    </fieldset>
</div>
<hr>

<div class="report">
    <fieldset>
        <legend>
            <i class="icon-list-alt"></i>
            {{stockroomReport.name}}
        </legend>
        <small>{{stockroomReport.description}}</small>
        <form id="stockroomReport" onsubmit="return false;">
            <ul class="table-layout">
                <li><label>Stockroom </label></li>
                <li>
                    <select id="stockroomReport-stockroom" ng-model="stockroom" ng-options='stockroom.name for stockroom in stockrooms'>
                        <option value="" selected="selected">Select Stockroom</option>
                    </select>
                </li>
            </ul>

            <ul class="table-layout">
                <li><label for="stockroomReport-beginDate">Begin Date</label></li>
                <li>
                    <span class="date">
                        <input type="date" id="stockroomReport-beginDate" value="">
                        <span class="add-on"><i class="icon-calendar small"></i></span>
                    </span>
                </li>
            </ul>

            <ul class="table-layout">
                <li><label for="stockroomReport-endDate">End Date</label></li>
                <li>
                    <span class="date">
                        <input type="date" id="stockroomReport-endDate" value="" >
                        <span class="add-on"><i class="icon-calendar small"></i></span>
                    </span>
                </li>
            </ul>

            <ul class="table-layout">
                <li></li>
                <li><input id="generateStockroomReport" type="submit" value="Generate Report" /></li>
            </ul>

            <input type="hidden" id="stockroomReportId" value="{{stockroomReport.reportId}}" />

        </form>
    </fieldset>
</div>
<hr>


<div class="report">
    <fieldset>
        <legend>
            <i class="icon-list-alt"></i>
            {{expiringStockReport.name}}
        </legend>
        <small>{{expiringStockReport.description}}</small>
        <form id="expiringStockReport" onsubmit="return false;">

            <ul class="table-layout">
                <li><label>Stockroom </label></li>
                <li>
                    <select id="expiringStock-stockroom" ng-model="stockroom" ng-options='stockroom.name for stockroom in stockrooms'>
                        <option value="" selected="selected">All</option>
                    </select>
                </li>
            </ul>

            <ul class="table-layout">
                <li><label for="expiresBy">Expires by</label></li>
                <li>
                    <span class="date">
                        <input type="date" id="expiresBy" value="" >
                        <span class="add-on"><i class="icon-calendar small"></i></span>
                    </span>
                </li>
            </ul>

            <ul class="table-layout">
                <li></li>
                <li><input id="generateExpiringStockReport" type="submit" value="Generate Report" /></li>
            </ul>

            <input type="hidden" id="expiringStockReportId" value="{{expiringStockReport.reportId}}" />

        </form>
    </fieldset>
</div>

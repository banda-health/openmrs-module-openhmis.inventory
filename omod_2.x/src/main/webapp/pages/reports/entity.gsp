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

        <ul class="table-layout">
            <li><label>Stockroom </label></li>
            <li>
                <select ng-model="StockTakeReport_stockroom" ng-options='stockroom.name for stockroom in stockrooms'>
                    <option value="" selected="selected">Select Stockroom</option>
                </select>
            </li>
        </ul>
        <ul class="table-layout">
            <li></li>
            <li><input ng-click="generateReport_StockTakeReport()" type="button" value="Generate Report" /></li>
        </ul>
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
        <ul class="table-layout">
            <li><label>Stockroom </label></li>
            <li>
                <select ng-model="stockCardReport_stockroom" ng-options='stockroom.name for stockroom in stockrooms'>
                    <option value="" ng-selected="selected">All Stockrooms</option>
                </select>
            </li>
        </ul>

        <ul class="table-layout">
            <li><label>Item</label></li>
            <li>
                ${ ui.includeFragment("openhmis.commons", "searchFragment", [
                        typeahead: ["reportItem.name for reportItem in searchReportItems(\$viewValue)"],
                        model: "stockCardReport_item",
                        typeaheadOnSelect: "setStockCardReportItem(\$item)",
                        typeaheadEditable: "true",
                        class: ["form-control"],
                        placeholder: [ui.message('openhmis.inventory.item.enterItemSearch')]
                ])}
            </li>
        </ul>

        <ul class="table-layout">
            <li><label>Begin Date</label></li>
            <li>
                <span class="date">
                    <input type="date" ng-model="stockCardReport_beginDate">
                    <span class="add-on"><i class="icon-calendar small"></i></span>
                </span>
            </li>
        </ul>

        <ul class="table-layout">
            <li><label>End Date</label></li>
            <li>
                <span class="date">
                    <input type="date" ng-model="stockCardReport_endDate">
                    <span class="add-on"><i class="icon-calendar small"></i></span>
                </span>
            </li>
        </ul>

        <ul class="table-layout">
            <li></li>
            <li><input ng-click="generateReport_StockCardReport()" type="button"  value="Generate Report" /></li>
        </ul>
    </fieldset>
</div>
<hr>

<div class="report">
    <fieldset>
        <legend>
            <i class="icon-list-alt"></i>
            {{stockroomUsageReport.name}}
        </legend>
        <small>{{stockroomUsageReport.description}}</small>
        <ul class="table-layout">
            <li><label>Stockroom </label></li>
            <li>
                <select ng-model="stockroomUsage_stockroom" ng-options='stockroom.name for stockroom in stockrooms'>
                    <option value="" selected="selected">Select Stockroom</option>
                </select>
            </li>
        </ul>

        <ul class="table-layout">
            <li><label>Begin Date</label></li>
            <li>
                <span class="date">
                    <input type="date" ng-model="stockroomUsage_beginDate">
                    <span class="add-on"><i class="icon-calendar small"></i></span>
                </span>
            </li>
        </ul>

        <ul class="table-layout">
            <li><label>End Date</label></li>
            <li>
                <span class="date">
                    <input type="date" ng-model="stockroomUsage_endDate">
                    <span class="add-on"><i class="icon-calendar small"></i></span>
                </span>
            </li>
        </ul>

        <ul class="table-layout">
            <li></li>
            <li><input ng-click="generateReport_StockroomUsage()" type="button" value="Generate Report" /></li>
        </ul>
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
        <ul class="table-layout">
            <li><label>Stockroom </label></li>
            <li>
                <select id="expiringStock-stockroom" ng-model="expiringStock_stockroom" ng-options='stockroom.name for stockroom in stockrooms'>
                    <option value="" selected="selected">All Stockrooms</option>
                </select>
            </li>
        </ul>

        <ul class="table-layout">
            <li><label>Expires by</label></li>
            <li>
                <span class="date">
                    <input type="date" ng-model="expiringStock_expiresByDate">
                    <span class="add-on"><i class="icon-calendar small"></i></span>
                </span>
            </li>
        </ul>

        <ul class="table-layout">
            <li></li>
            <li><input ng-click="generateReport_ExpiringStock()" type="button" value="Generate Report" /></li>
        </ul>
    </fieldset>
</div>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("openhmis.inventory.page")}" , link: '${ui.pageLink("openhmis.inventory", "inventoryLanding")}'},
        { label: "${ ui.message("openhmis.inventory.manage.module")}", link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/inventory/manageModule.page' },
        { label: "${ ui.message("openhmis.inventory.admin.reports")}", link: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/reports/entities.page#/'},
    ];

    jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));

</script>


<input id="reportUrl" type="hidden" value="${ ui.message("openhmis.inventory.admin.reports")}" />

<h2>${ ui.message("openhmis.inventory.admin.reports")}" /></h2>

<table style="width: 99%">
    <tr>
        <td style="vertical-align: top; width: 250px;">
            <br />
            <b>
                ${ ui.message("openhmis.inventory.admin.reports")}
            </b>
        </td>
        <td>

            <h3>${stockTakeReport.name}</h3>
            <div style="color: grey">${stockTakeReport.description}</div>
            <br />
            <div>
                <form id="stockTakeReport" onsubmit="return false;">
                    <label for="stockroomId">Stockroom: </label>
                    <select id="stockroomId">
                        <option value=""></option>
                        %{--<c:forEach var="stockroom" items="${stockrooms}">--}%
                            %{--<option value="${stockroom.id}">${stockroom.name}</option>--}%
                        %{--</c:forEach>--}%
                    </select>
                    <input id="stockTakeReportId" type="hidden" value="${stockTakeReport.reportId}" />
                    <br /><br />
                    <input id="generateTakeReport" type="submit" value="Generate Report"/>
                </form>
            </div>
            <br />
            <hr>

            <h3>${stockCardReport.name}</h3>
            <div style="color: grey">${stockCardReport.description}</div>
            <br />
            <div>
                <form id="stockCardReport" onsubmit="return false;">
                    <label for="itemSearch">Item: </label>
                    <input id="itemSearch" style="width: 350px" type="text" placeholder="Item Name" />
                    <input id="item-uuid" type="hidden" />
                    <br />
                    <br />
                    <label for="beginDate">Begin Date</label>
                    <input id="beginDate" class="date" type="text" />

                    <label for="endDate">End Date</label>
                    <input id="endDate" class="date" type="text" />

                    <input id="stockCardReportId" type="hidden" value="${stockCardReport.reportId}" />
                    <br /><br />
                    <input id="generateCardReport" type="submit" value="Generate Report" />
                </form>
            </div>
            <hr>

            <h3>${stockOperationsByStockroomReport.name}</h3>
            <div style="color: grey">${stockOperationsByStockroomReport.description}</div>
            <br />
            <div>
                <form id="stockOperationsByStockroomReport" onsubmit="return false;">
                    <label for="stockroomIdOperationsByStockroom">Stockroom: </label>
                    <select id="stockroomIdOperationsByStockroom">
                        <option value=""></option>
                        %{--<c:forEach var="stockroom" items="${stockrooms}">--}%
                            %{--<option value="${stockroom.id}">${stockroom.name}</option>--}%
                        %{--</c:forEach>--}%
                    </select>
                    <br/>
                    <br/>
                    <label for="itemSearchOperationsByStockroom">Item: </label>
                    <input id="itemSearchOperationsByStockroom" style="width: 350px" type="text" placeholder="Item Name" />
                    <input id="item-uuid-searchOperationsByStockroom" type="hidden" />
                    <br />
                    <br />
                    <label for="beginDate-operationsByStockroom">Begin Date</label>
                    <input id="beginDate-operationsByStockroom" class="date" type="text" />

                    <label for="endDate-operationsByStockroom">End Date</label>
                    <input id="endDate-operationsByStockroom" class="date" type="text" />

                    <input id="stockOperationsByStockroomReportId" type="hidden" value="${stockOperationsByStockroomReport.reportId}" />
                    <br /><br />
                    <input id="generateOperationsByStockroomReport" type="submit" value="Generate Report" />
                </form>
            </div>
            <hr>

            <h3>${stockroomReport.name}</h3>
            <div style="color: grey">${stockroomReport.description}</div>
            <br />
            <div>
                <form id="stockroomReport" onsubmit="return false;">
                    <label for="stockroomReport-StockroomId">Stockroom: </label>
                    <select id="stockroomReport-StockroomId">
                        <option value=""></option>
                        %{--<c:forEach var="stockroom" items="${stockrooms}">--}%
                            %{--<option value="${stockroom.id}">${stockroom.name}</option>--}%
                        %{--</c:forEach>--}%
                    </select>
                    <br />
                    <br />

                    <label for="stockroomReport-beginDate">Begin Date</label>
                    <input id="stockroomReport-beginDate" class="date" type="text" />

                    <label for="stockroomReport-endDate">End Date</label>
                    <input id="stockroomReport-endDate" class="date" type="text" />

                    <input id="stockroomReportId" type="hidden" value="${stockroomReport.reportId}" />
                    <br /><br />
                    <input id="generateStockroomReport" type="submit" value="Generate Report"/>
                </form>
            </div>
            <br />
            <hr>

            <h3>${expiringStockReport.name}</h3>
            <div style="color: grey">${expiringStockReport.description}</div>
            <br />
            <div>
                <form id="expiringStockReport" onsubmit="return false;">

                    <label for="expiringStockReport-StockroomId">Stockroom: </label>
                    <select id="expiringStockReport-StockroomId">
                        <option value="">Any</option>
                        %{--<c:forEach var="stockroom" items="${stockrooms}">--}%
                            %{--<option value="${stockroom.id}">${stockroom.name}</option>--}%
                        %{--</c:forEach>--}%
                    </select>
                    <br />
                    <br />
                    <label for="expiresBy">Expires by</label>
                    <input id="expiresBy" class="date" type="text" />
                    <input id="expiringStockReportId" type="hidden" value="${expiringStockReport.reportId}" />
                    <br /><br />
                    <input id="generateExpiringStockReport" type="submit" value="Generate Report" />
                </form>
            </div>
            <hr>
        </td>
    </tr>
</table>

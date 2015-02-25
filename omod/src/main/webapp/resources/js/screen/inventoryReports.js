/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
curl(
    { baseUrl: openhmis.url.resources },
    [
        openhmis.url.backboneBase + 'js/lib/jquery',
        openhmis.url.backboneBase + 'js/openhmis',
        openhmis.url.backboneBase + 'js/lib/backbone-forms',
        openhmis.url.backboneBase + 'js/view/generic',
        openhmis.url.inventoryBase + 'js/model/item',
        'js!' + openhmis.url.inventoryBase + 'js/itemAutocomplete.js'

    ],
    function($, openhmis) {

        $(function() {
            if ($('#itemSearch').length > 0) {
                $('#itemSearch')
                    .autocomplete({
                        minLength: 2,
                        source: doSearch,
                        select: selectItem
                    })
                    .data("autocomplete")._renderItem = function (ul, item) {
                    return $("<li></li>").data("item.autocomplete", item)
                        .append("<a>" + item.label + "</a>").appendTo(ul);
                };
            }

            $('.date').datepicker();

            if ($("#generateTakeReport").length > 0) {
                $("#generateTakeReport").click(printTakeReport)
            }

            if ($("#generateCardReport").length > 0) {
                $("#generateCardReport").click(printCardReport);
            }

            if ($("#generateStockroomReport").length > 0) {
                $("#generateStockroomReport").click(printStockroomReport);
            }

            if ($("#generateExpiringStockReport").length > 0) {
                $("#generateExpiringStockReport").click(printExpiringStockReport);
            }
        });

        function printTakeReport() {
            var stockroomId = $("#stockroomId").val();
            if (!stockroomId) {
                alert("You must select a stockroom to generate the report.");
                return false;
            }

            var reportId = $('#stockTakeReportId').val();

            return printReport(reportId, "stockroomId=" + stockroomId);
        }

        function printCardReport() {
            var itemUuid = $("#item-uuid").val();
            if (!itemUuid) {
                alert("You must select an item to generate the report.");
                return false;
            }

            var beginDate = $("#beginDate").val();
            var endDate = $("#endDate").val();

            if (!beginDate || !endDate) {
                alert("You must select a begin and end date to generate the report.");
                return false;
            }

            // Get the dates into the expected format (dd-MM-yyyy)
            beginDate = openhmis.dateFormat(new Date(beginDate), false);
            endDate = openhmis.dateFormat(new Date(endDate), false);

            var reportId = $('#stockCardReportId').val();

            return printReport(reportId, "itemUuid=" + itemUuid + "&beginDate=" + beginDate + "&endDate=" + endDate);
        }

        function printStockroomReport() {
            var stockroomId = $("#stockroomReport-StockroomId").val();
            if (!stockroomId) {
                alert("You must select a stockroom to generate the report.");
                return false;
            }

            var beginDate = $("#stockroomReport-beginDate").val();
            var endDate = $("#stockroomReport-endDate").val();

            if (!beginDate || !endDate) {
                alert("You must select a begin and end date to generate the report.");
                return false;
            }

            // Get the dates into the expected format (dd-MM-yyyy)
            beginDate = openhmis.dateFormat(new Date(beginDate), false);
            endDate = openhmis.dateFormat(new Date(endDate), false);

            var reportId = $('#stockroomReportId').val();

            return printReport(reportId, "stockroomId=" + stockroomId + "&beginDate=" + beginDate + "&endDate=" + endDate);
        }

        function printExpiringStockReport() {

            var expiryDate = $("#expiresBy").val();

            if (!expiryDate) {
                alert("You must select an expiry date to generate the report.");
                return false;
            }

            var stockroomId = $("#expiringStockReport-StockroomId").val();

            // Get the dates into the expected format (dd-MM-yyyy)
            expiryDate = openhmis.dateFormat(new Date(expiryDate), false);

            var reportId = $('#expiringStockReportId').val();

            return printReport(reportId, "expiresBy=" + expiryDate + "&stockroomId=" + stockroomId);
        }

        function printReport(reportId, parameters) {
            var reportUrl = $("#reportUrl").val();
            var url = openhmis.url.openmrs + reportUrl + ".form?";
            url += "reportId=" + reportId  + "&" + parameters;
            window.open(url, "pdfDownload");

            return false;
        }
    }
);

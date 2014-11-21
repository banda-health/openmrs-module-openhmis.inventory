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
        openhmis.url.inventoryBase + 'js/model/item'

    ],
    function($, openhmis) {
        $(function() {
            $('#itemSearch')
                .autocomplete({
                    minLength: 2,
                    source: doSearch,
                    select: selectItem
                })
                .data("autocomplete")._renderItem = function(ul, item) {
                return $("<li></li>").data("item.autocomplete", item)
                    .append("<a>" + item.label + "</a>").appendTo(ul);
            };

            $("#generateTakeReport").click(printTakeReport)
            $("#generateCardReport").click(printCardReport);
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

        function printReport(reportId, parameters) {
            var reportUrl = $("#reportUrl").val();
            var url = openhmis.url.openmrs + reportUrl + ".form?";
            url += "reportId=" + reportId  + "&" + parameters;
            window.open(url, "pdfDownload");

            return false;
        }

        function doSearch(request, response) {
            // Query the item stock by name
            var query = "?q=" + encodeURIComponent(request.term);

            // We only want to return items that have physical stock
            query += "&has_physical_inventory=true";

            search(request, response, openhmis.Item, query, "item",
                function(model) {
                    return {
                        val: model.id,
                        label: model.get('name')
                    }
                }
            );
        }

        function search(request, response, model, query, cacheSection, mapFn) {
            var resultCollection = new openhmis.GenericCollection([], { model: model });
            var fetchQuery = query ? query : "?q=" + encodeURIComponent(request.term);

            resultCollection.fetch({
                url: resultCollection.url + fetchQuery,
                success: function(collection, resp) {
                    var data = collection.map(mapFn);

                    response(data);
                },
                error: openhmis.error,
                statusCode: {
                    401: function(data) {
                        alert("Auth Failure!");
                    }
                }
            });
        }

        function selectItem(event, ui) {
            var uuid = ui.item.val;
            var name = ui.item.label;

            $('#itemSearch').val(name);
            $('#item-uuid').val(uuid);
        }
    }
);

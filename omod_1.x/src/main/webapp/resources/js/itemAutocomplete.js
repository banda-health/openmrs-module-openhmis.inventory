/*
 * This item autocomplete only considers items that have phisical inventory
 */

var cache = {};

function doSearch(request, response) {
    // Query the item stock by name
    var query = "?q=" + encodeURIComponent(request.term);

    // We only want to return items that have physical stock
    query += "&has_physical_inventory=true";

    var cacheSection = "item"
    if (cacheSection + query in cache) {
        response(cache[cacheSection + query]);
        return;
    }

    search(request, response, openhmis.Item, query, cacheSection,
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
            cache[cacheSection + query] = data;
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
    $('#item-uuid').val(uuid).trigger('change');
}
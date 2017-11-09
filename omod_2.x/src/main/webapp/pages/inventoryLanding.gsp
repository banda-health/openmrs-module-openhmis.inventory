<%
    ui.decorateWith("appui", "standardEmrPage")

	def htmlSafeId = { extension ->
		"${ extension.id.replace(".", "-") }-${ extension.id.replace(".", "-") }-extension"
	}
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("openhmis.inventory.page") }"}
    ];
</script>

<div id="home-container">

	<h1>${ui.message("openhmis.inventory.task.page")}</h1>

    <div id="apps">
        <% extensions.each { ext -> %>
            <a id="${ htmlSafeId(ext) }" href="/${ contextPath }/${ ext.url }" class="button app big">
                <% if (ext.icon) { %>
                   <i class="${ ext.icon }"></i>
                <% } %>
                ${ ui.message(ext.label) }
            </a>
        <% } %>
    </div>

</div>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>Welcome</title>
		
		<script type="text/javascript">
		function tooltip() {
			$('tr').qtip({
				content: {
					text: function(api) {return $(this).children(":first").clone();}
				},
				position: {
					at: "bottom left",
					viewport: $(window)
				}
			});
		}
		</script>
		
	</head>
	
	<body>
		<table id="recos">
		<g:render template="filter" ></g:render>
		</table>
		<div id="loadingDivId" style="display: none;"><img src="${resource(dir:'images', file:'spinner.gif')}" alt=""/></div>
		
		<g:javascript>
		$(document).ready(function() {
			tooltip();
		});
		</g:javascript>
	</body>
</html>

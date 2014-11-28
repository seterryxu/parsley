<#macro button>
	<div id="${id}" class="yui3-module">
	    <div class="yui3-hd">
	        <h3>${title}</h3>
	    </div>
	    <div class="yui3-bd">
	        <p>${content}</p>
	    </div>
	</div>
	
	<script type="text/javascript">
		YUI().use('anim', function(Y) {
		    var module = Y.one('#${id}');
		
		    // add fx plugin to module body
		    var content = module.one('.yui3-bd').plug(Y.Plugin.NodeFX, {
		        from: { height: 0 },
		        to: {
		            height: function(node) { // dynamic in case of change
		                return node.get('scrollHeight'); // get expanded height (offsetHeight may be zero)
		            }
		        },
		
		        easing: Y.Easing.easeOut,
		        duration: 0.5
		    });
		
		    var onClick = function(e) {
		        e.preventDefault();
		        module.toggleClass('yui3-closed');
		        content.fx.set('reverse', !content.fx.get('reverse')); // toggle reverse
		        content.fx.run();
		    };
		
		    // use dynamic control for dynamic behavior
		    var control = Y.Node.create(
		        '<a title="collapse/expand element" class="yui3-toggle">' +
		            '<em>toggle</em>' +
		        '</a>'
		    );
		
		    // append dynamic control to header section
		    module.one('.yui3-hd').appendChild(control);
		    control.on('click', onClick);
		
		});
	</script>
	
</#macro>
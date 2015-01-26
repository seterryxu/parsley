package testgsps

def n=namespace(lib.ComponentLib)

n.breadcrumb(){
	
	li(){
		a(href:'#'){
			'AAA'
		}
	}
	
	"""
	<li><a href="#">AAA</a></li>
	<li><class="active">DEF</a></li>
	"""
}
n.button(text:'abc')
n.button(text:'def')

n.well(){
	'<b>Powered by Parsley!</b>'
}

<#macro pagination>
	<nav>
	  <li><a href="#"><span aria-hidden="true">&laquo;</span><span class="sr-only">${prev}</span></a></li>
  		<ul class="pagination">
		    ${content}
		</ul>
	  <li><a href="#"><span aria-hidden="true">&raquo;</span><span class="sr-only">${next}</span></a></li>
	</nav>
</#macro>
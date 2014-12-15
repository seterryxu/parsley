<#macro dropdown text>
	<div class="dropdown">
	  <button id="dropdownMenu1" type="button" class="btn btn-default dropdown-toggle" 
	          data-toggle="dropdown" aria-expanded="true">
	    ${text}
	    <span class="caret"></span>
	  </button>
	  <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">
	    <#nested>
	  </ul>
	</div>
</#macro>
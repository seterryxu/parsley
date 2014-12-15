<#macro panel title footer>
	<div class="panel panel-default">

	  <#if title??>
		<div class="panel-heading">${title}</div>
	  </#if>
	
	  <div class="panel-body">
	    <#nested>
	  </div>
	  
	  <#if footer??>
	    <div class="panel-footer">${footer}</div>
	  </#if>
	</div>
</#macro>
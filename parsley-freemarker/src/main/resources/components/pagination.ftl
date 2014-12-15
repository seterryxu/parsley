<#macro pagination type="normal">
	<#assign _type="">
	
	<#if type=="large">
		<#assign _type=" pagination-lg">
	<#elseif type=="small">
		<#assign _type=" pagination-sm">
	</#if>
	
	<nav>
	  <ul class="pagination${_type}">
		<#nested>
  	  </ul>
	</nav>
</#macro>
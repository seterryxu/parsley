<#macro well type="normal">
	<#assign _type="">
	
	<#if type=="large">
		<#assign _type=" well-lg">
	<#elseif type=="small">
		<#assign _type=" well-sm">
	</#if>
		
	<div class="well${_type}"><#nested></div>
</#macro>
<%@ page import="de.dewarim.grailsmagic.ImageType; de.dewarim.grailsmagic.ImageSize; de.dewarim.grailsmagic.CardImage" %>



<div class="fieldcontain ${hasErrors(bean: cardImage, field: 'imageData', 'error')} required">
	<label for="imageData">
		<g:message code="cardImage.imageData.label" default="Image Data" />
		<span class="required-indicator">*</span>
	</label>
	<input type="file" id="imageData" name="imageData" />

</div>

<div class="fieldcontain ${hasErrors(bean: cardImage, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${cardImage?.name}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: cardImage, field: 'imageSize', 'error')} required">
	<label for="imageSize">
		<g:message code="cardImage.imageSize.label" default="Image Size" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="imageSize" from="${ImageSize?.values()}" keys="${ImageSize.values()*.name()}" required="" value="${cardImage?.imageSize?.name()}" />

</div>

<div class="fieldcontain ${hasErrors(bean: cardImage, field: 'type', 'error')} required">
	<label for="type">
		<g:message code="cardImage.type.label" default="Type" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="type" from="${ImageType?.values()}" keys="${ImageType.values()*.name()}" required="" value="${cardImage?.type?.name()}" />

</div>


<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@include file="_header.jsp" %>

<s:actionerror escape="false"/>


<table border="2">
<tr>
<td style="vertical-align: top;padding: 20px">

<center>
<form name="svg_form">


<textarea name="svg_data" cols="60" rows="16" style="border:2px solid #000000;padding:15px">
<?xml version="1.0"?>
<svg xmlns="http://www.w3.org/2000/svg" width="12cm" height="12cm">
    <g style="fill-opacity:0.7; stroke:black; stroke-width:0.1cm;">
        <circle cx="6cm" cy="2cm" r="100" style="fill:red;"
                transform="translate(0,50)" />
        <circle cx="6cm" cy="2cm" r="100" style="fill:blue;"
                transform="translate(70,150)" />
        <circle cx="6cm" cy="2cm" r="100" style="fill:green;"
                transform="translate(-70,150)"/>
    </g>
</svg>
</textarea>

<br><br>
    <input type="button" value="Preview" onclick="convertAndDisplay()">
</form>
</center>

</td>
<td style="vertical-align: top;padding: 20px">

<div id="image_generated">
    No Image Generated.<br/><i><small>Psss. Click the button preview!</small></i>
</div>

</td>
</table>

<%@include file="_footer.jsp" %>
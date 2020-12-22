<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:b="http://docbook.org/ns/docbook"
                exclude-result-prefixes="b">

    <xsl:output omit-xml-declaration="yes" indent="yes"/>
    <xsl:strip-space elements="*"/>

    <xsl:template match="b:book">
        <h4>
            <xsl:value-of select="b:title"/>
        </h4>
        <xsl:apply-templates select="b:chapter"/>
    </xsl:template>

    <xsl:template match="b:chapter" xml:space="preserve">
        <h6><xsl:value-of select="b:title"/></h6>
        <p><xsl:value-of select="b:para"/></p>
    </xsl:template>
</xsl:stylesheet>
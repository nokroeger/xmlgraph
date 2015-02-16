<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  <xsl:output method="text" omit-xml-declaration="yes"/>
  <xsl:strip-space elements="*"/>
  
  <xsl:template match="/">
  	<xsl:text>graph xmltree</xsl:text>
  	<xsl:text>&#13;&#10;{&#13;&#10;</xsl:text>
  	<xsl:apply-templates/>
  	<xsl:text>&#13;&#10;}</xsl:text>
  </xsl:template>
  
  <xsl:template match="*">
  	
  	<xsl:text><xsl:value-of select="concat(local-name(.), '[shape=box];&#13;&#10;')"/></xsl:text>
  	<xsl:if test="string(local-name(..)) != ''">
  		<xsl:text><xsl:value-of select="concat(local-name(..), ' -- ', local-name(.), ';&#13;&#10;')"/></xsl:text>
  	</xsl:if>
  	
  	<xsl:apply-templates select="@*|*|text()"/>
    
  </xsl:template>
  
  <xsl:template match="text()">
  	<xsl:if test="string(.) != ''">
  		<xsl:text><xsl:value-of select="concat(., '[shape=box, style=dotted];&#13;&#10;')"/></xsl:text>
  		<xsl:text><xsl:value-of select="concat(local-name(..), ' -- ', ., ';&#13;&#10;')"/></xsl:text>
  	</xsl:if>
  </xsl:template>
  
  <xsl:template match="@*">
  	<xsl:text><xsl:value-of select="concat(., '[shape=box, style=dotted]', ';&#13;&#10;')"/></xsl:text>
  	<xsl:text><xsl:value-of select="concat(local-name(..), ' -- ', local-name(.), ' -- ', ., ';&#13;&#10;')"/></xsl:text>
  </xsl:template>
  
</xsl:stylesheet>
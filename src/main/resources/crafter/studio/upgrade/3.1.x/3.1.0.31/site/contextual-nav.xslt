<?xml version="1.0" encoding="UTF-8"?>
  <!--
  ~ Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program.  If not, see <http://www.gnu.org/licenses/>.
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

<!-- to keep the right formatting -->
<xsl:output method="xml" indent="yes" />
<xsl:strip-space elements="*"/>

<!-- copy all elements -->
<xsl:template match="node() | @*">
    <xsl:copy>
        <xsl:apply-templates select="node() | @*"/>
    </xsl:copy>
</xsl:template>

<!-- insert line breaks before and after top level comments -->
<xsl:template match="/comment()">
    <xsl:text>&#10;</xsl:text><xsl:copy-of select="."/><xsl:text>&#10;</xsl:text>
</xsl:template>

    <xsl:template match="app-context-menu/context/left/menuItem[not(item/modulehook='quick-create')]">
        <xsl:element name="menuItem">
            <xsl:for-each select="item">
                <xsl:copy>
                    <xsl:apply-templates select="node() | @*"/>
                </xsl:copy>
                <xsl:if test="modulehook='wcm_dropdown'">
                    <xsl:element name="item">
                        <xsl:element name="modulehook"><xsl:text>quick-create</xsl:text></xsl:element>
                    </xsl:element>
                    <xsl:text>&#10;</xsl:text>
                </xsl:if>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xsl:template match="app-context-menu/context/modules[not(module/moduleName='quick-create')]">
        <xsl:element name="modules">
            <xsl:for-each select="module">
                <xsl:copy>
                    <xsl:apply-templates select="node() | @*"/>
                </xsl:copy>
                <xsl:if test="moduleName='wcm_dropdown'">
                    <xsl:element name="module">
                        <xsl:element name="moduleName"><xsl:text>quick-create</xsl:text></xsl:element>
                        <xsl:element name="value"><xsl:text>quick-create</xsl:text></xsl:element>
                    </xsl:element>
                    <xsl:text>&#10;</xsl:text>
                </xsl:if>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>


</xsl:stylesheet>

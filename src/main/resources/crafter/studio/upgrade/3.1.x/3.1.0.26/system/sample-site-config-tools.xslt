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

    <xsl:template match="config/tools/tool/datasources">
        <xsl:copy>
            <xsl:apply-templates/>
            <xsl:if test="not(datasource/name = 'CMIS-upload')">
                <xsl:element name="datasource">
                    <xsl:element name="name"><xsl:text>CMIS-upload</xsl:text></xsl:element>
                    <xsl:element name="icon">
                        <xsl:element name="class"><xsl:text>fa-plug</xsl:text></xsl:element>
                    </xsl:element>
                </xsl:element>
                <xsl:text>&#10;</xsl:text>
            </xsl:if>
            <xsl:if test="not(datasource/name = 'img-CMIS-upload')">
                <xsl:element name="datasource">
                    <xsl:element name="name"><xsl:text>img-CMIS-upload</xsl:text></xsl:element>
                    <xsl:element name="icon">
                        <xsl:element name="class"><xsl:text>fa-square</xsl:text></xsl:element>
                        <xsl:element name="stackedclass"><xsl:text>fa-plug fa-inverse fa-image-plug</xsl:text></xsl:element>
                    </xsl:element>
                </xsl:element>
                <xsl:text>&#10;</xsl:text>
            </xsl:if>
            <xsl:if test="not(datasource/name = 'video-CMIS-upload')">
                <xsl:element name="datasource">
                    <xsl:element name="name"><xsl:text>video-CMIS-upload</xsl:text></xsl:element>
                    <xsl:element name="icon">
                        <xsl:element name="class"><xsl:text>fa-square-o fa-video-square</xsl:text></xsl:element>
                        <xsl:element name="stackedclass"><xsl:text>fa-plug fa-video-plug</xsl:text></xsl:element>
                    </xsl:element>
                </xsl:element>
                <xsl:text>&#10;</xsl:text>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
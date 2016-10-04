<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:common="http://exslt.org/common" xmlns:xalan="http://xml.apache.org"
	exclude-result-prefixes="common xalan">


	<xsl:attribute-set name="myBorder">
		<xsl:attribute name="border">solid 0.3mm black</xsl:attribute>
		<xsl:attribute name="padding">1pt</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="myHeadline">
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="font-size">16pt</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="myHeadline2">
		<xsl:attribute name="font-size">14pt</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="myHeadline3">
		<xsl:attribute name="font-size">11pt</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="tblText1">
		<xsl:attribute name="font-weight">normal</xsl:attribute>
		<xsl:attribute name="font-size">10pt</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="tblTextHeader">
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="font-size">11pt</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="tblText2">
		<xsl:attribute name="font-weight">normal</xsl:attribute>
		<xsl:attribute name="font-size">8pt</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="tblTextHeader2">
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="font-size">9pt</xsl:attribute>
	</xsl:attribute-set>


	<!-- Title -->
	<xsl:template name="title">
		<xsl:param name="value" />
		<fo:block xsl:use-attribute-sets="myHeadline">
			<xsl:value-of select="$value" />
		</fo:block>
	</xsl:template>

	<!-- Title 2 (smaller) -->
	<xsl:template name="title2">
		<xsl:param name="value" />
		<fo:block xsl:use-attribute-sets="myHeadline2">
			<xsl:value-of select="$value" />
		</fo:block>
	</xsl:template>

	<!-- Title 4 (small and bold) -->
	<xsl:template name="title4">
		<xsl:param name="value" />
		<fo:block xsl:use-attribute-sets="myHeadline2">
			<xsl:attribute name="font-weight">bold</xsl:attribute>
			<xsl:value-of select="$value" />
		</fo:block>
	</xsl:template>

	<!-- Title 3 (smaller) -->
	<xsl:template name="title3">
		<xsl:param name="value" />
		<fo:block xsl:use-attribute-sets="myHeadline3">
			<xsl:value-of select="$value" />
		</fo:block>
	</xsl:template>

	<!-- Graph and Chart - Summary (big graph with chart underneath -->
	<xsl:template name="summarygraphchart">
		<xsl:param name="urls" />
		<xsl:param name="tblhdr" />
		<xsl:param name="rows" />
		<xsl:param name="colwidths" />
		<xsl:param name="chartwidth" />
		<xsl:param name="imagewidth" />

<xsl:if test="$rows">
		<xsl:for-each select="$urls">
			<xsl:variable name="src" select="." />
			<fo:block>
				<fo:external-graphic src="{$src}" content-width="{$imagewidth}" />
			</fo:block>
		</xsl:for-each>
		<fo:block>

			<fo:table width="{$chartwidth}">
				<xsl:copy-of select="$colwidths" />
				<!-- column headers -->
				<xsl:call-template name="tableheaders">
					<xsl:with-param name="tblhdr" select="$tblhdr" />
				</xsl:call-template>
				<fo:table-body>
					<!-- values -->
					<xsl:call-template name="tablevalues">
						<xsl:with-param name="rows" select="$rows" />
					</xsl:call-template>
				</fo:table-body>
			</fo:table>

		</fo:block>
		</xsl:if>
	</xsl:template>

	<!-- Graph and Chart - Summary (big graph with chart next to it) -->
	<xsl:template name="summarygraphchart2">
		<xsl:param name="urls" />
		<xsl:param name="tblhdr" />
		<xsl:param name="rows" />
		<xsl:param name="imagewidth" />
		<xsl:param name="chartwidth" />
		<xsl:param name="colwidths" />
		<xsl:if test="$rows">
 

		
		<fo:table>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>

						<xsl:for-each select="$urls">
							<xsl:variable name="src" select="." />
							<fo:block>
								<fo:external-graphic src="{$src}"
									content-width="{$imagewidth}" />
							</fo:block>
						</xsl:for-each>
					</fo:table-cell>
					<fo:table-cell width="{$chartwidth}">



						<fo:block width="{$chartwidth}">
							<fo:table width="100%">
								<xsl:copy-of select="$colwidths" />
								<!-- column headers -->
								<xsl:call-template name="tableheaders">
									<xsl:with-param name="tblhdr" select="$tblhdr" />
								</xsl:call-template>
								<fo:table-body>
									<!-- values -->
									<xsl:call-template name="tablevalues">
										<xsl:with-param name="rows" select="$rows" />
									</xsl:call-template>
								</fo:table-body>
							</fo:table>

						</fo:block>
					</fo:table-cell>

				</fo:table-row>
			</fo:table-body>
		</fo:table>
</xsl:if>

	</xsl:template>

	<!-- Graph and Chart - multi image with chart underneath -->
	<xsl:template name="summarygraphchart3">
		<xsl:param name="urls" />
		<xsl:param name="tblhdr" />
		<xsl:param name="rows" />
		<xsl:param name="colwidths" />
		<xsl:param name="chartwidth" />
		<xsl:param name="imagewidth" />

<xsl:if test="$rows">
<fo:table width="100%">
<fo:table-body>
		<xsl:for-each select="$urls">
			<xsl:variable name="src" select="." />
			<fo:table-cell>
				<fo:block><fo:external-graphic src="{$src}" content-width="{$imagewidth}" /></fo:block>
			</fo:table-cell>
		</xsl:for-each>
		</fo:table-body>
</fo:table>
			<fo:table width="{$chartwidth}">
				<xsl:copy-of select="$colwidths" />
				<!-- column headers -->
				<xsl:call-template name="tableheaders">
					<xsl:with-param name="tblhdr" select="$tblhdr" />
				</xsl:call-template>
				<fo:table-body>
					<!-- values -->
					<xsl:call-template name="tablevalues">
						<xsl:with-param name="rows" select="$rows" />
					</xsl:call-template>
				</fo:table-body>
			</fo:table>

							<fo:leader leader-pattern="rule" leader-length="100%"
								rule-style="solid" rule-thickness="2pt" margin-top="1cm" />
		
		</xsl:if>
	</xsl:template>

	<!-- Graph only - no chart -->
	<xsl:template name="graphonly">
		<xsl:param name="urls" />
		<xsl:param name="imagewidth" />


		<xsl:for-each select="$urls">
			<xsl:variable name="src" select="." />
			<fo:block>
				<fo:external-graphic src="{$src}" content-width="{$imagewidth}" />
			</fo:block>
		</xsl:for-each>
	</xsl:template>

	<!-- Table - expense-table -->
	<xsl:template name="expensetable">
		<xsl:param name="tblhdr" />
		<xsl:param name="rows" />
		<fo:table>
			<fo:table-column column-width="12%" />
			<fo:table-column column-width="12%" />
			<fo:table-column column-width="20%" />
			<fo:table-column column-width="46%" />
			<fo:table-column column-width="10%" />
			<!-- column headers -->
			<xsl:call-template name="tableheaders">
				<xsl:with-param name="tblhdr" select="$tblhdr" />
			</xsl:call-template>
			<fo:table-body>
				<!-- values -->
				<xsl:call-template name="tablevalues">
					<xsl:with-param name="rows" select="$rows" />
				</xsl:call-template>
			</fo:table-body>
		</fo:table>

	</xsl:template>

	<xsl:template name="tableheaders">
		<xsl:param name="tblhdr" />
		<xsl:param name="headerclass" />
		<fo:table-header>
			<fo:table-row>
				<xsl:for-each select="$tblhdr">
					<fo:table-cell xsl:use-attribute-sets="myBorder tblTextHeader2">
						<fo:block font-weight="bold">
							<xsl:value-of select="." />
						</fo:block>
					</fo:table-cell>
				</xsl:for-each>
			</fo:table-row>
		</fo:table-header>
	</xsl:template>

	<xsl:template name="tablevalues">
		<xsl:param name="rows" />
		<xsl:param name="size" />
		<xsl:for-each select="$rows">
			<fo:table-row>
				<xsl:for-each select="columns">
					<fo:table-cell xsl:use-attribute-sets="myBorder tblText2">
						<fo:block text-align="left">
							<xsl:value-of select="." />
						</fo:block>
					</fo:table-cell>
				</xsl:for-each>
			</fo:table-row>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
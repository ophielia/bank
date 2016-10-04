<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:common="http://exslt.org/common" xmlns:xalan="http://xml.apache.org"
	exclude-result-prefixes="common xalan">
	<xsl:import href="reporttemplates.xsl" />

	<xsl:template match="/">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="first"
					page-height="29.7cm" page-width="21.0cm" margin="1.5cm" margin-top="1cm">
					<fo:region-body />
				</fo:simple-page-master>
			</fo:layout-master-set>

			<fo:page-sequence master-reference="first">
				<fo:flow flow-name="xsl-region-body">

					<!-- Title and Range -->
					<xsl:call-template name="title">
						<xsl:with-param name="value"
							select="reportdata/labels[@tag='lbltitle']/text" />
					</xsl:call-template>
					<xsl:call-template name="title2">
						<xsl:with-param name="value"
							select="reportdata/labels[@tag='lblpctthryear']/text" />
					</xsl:call-template>

<!-- Summary Data for year -->
					<fo:block space-before="1cm">
						<fo:leader leader-pattern="rule" leader-length="100%"
							rule-style="solid" rule-thickness="2pt" />
						<xsl:variable name="iwidth">
							105mm
						</xsl:variable>
						<xsl:call-template name="summarygraphchart2">
							<xsl:with-param name="urls"
								select="reportdata/element[@tag='summary']/urls" />
							<xsl:with-param name="tblhdr"
								select="reportdata/element[@tag='summary']/chart/headers/columns" />
							<xsl:with-param name="rows"
								select="reportdata/element[@tag='summary']/chart/rows" />
							<xsl:with-param name="imagewidth" select="$iwidth" />
							<xsl:with-param name="chartwidth">
								55mm
							</xsl:with-param>
							<xsl:with-param name="colwidths">
								<fo:table-column column-width="33%" />
								<fo:table-column column-width="33%" />
								<fo:table-column column-width="33%" />
							</xsl:with-param>
						</xsl:call-template>
					</fo:block>

<!-- Target Status for year -->
					<fo:block space-before="1cm">
						<xsl:variable name="twidth">
							80%
						</xsl:variable>
						<xsl:variable name="iwidth">
							105mm
						</xsl:variable>
						<xsl:call-template name="summarygraphchart">
							<xsl:with-param name="urls"
								select="reportdata/element[@tag='targetsumm']/urls" />
							<xsl:with-param name="tblhdr"
								select="reportdata/element[@tag='targetsumm']/chart/headers/columns" />
							<xsl:with-param name="rows"
								select="reportdata/element[@tag='targetsumm']/chart/rows" />
							<xsl:with-param name="chartwidth" select="$twidth" />
							<xsl:with-param name="imagewidth" select="$iwidth" />
							<xsl:with-param name="colwidths">
								<fo:table-column column-width="20%" />
								<fo:table-column column-width="20%" />
								<fo:table-column column-width="20%" />
								<fo:table-column column-width="40%" />
							</xsl:with-param>
							<xsl:with-param name="chartwidth">
								105mm
							</xsl:with-param>
						</xsl:call-template>
					</fo:block>


					<!-- Target Data -->
					<fo:block space-before="1cm">
						<xsl:variable name="twidth">
							105mm
						</xsl:variable>
						<xsl:variable name="iwidth">
							65mm
						</xsl:variable>
						<xsl:call-template name="summarygraphchart2">
							<xsl:with-param name="urls"
								select="reportdata/element[@tag='targetpoint']/urls" />
							<xsl:with-param name="tblhdr"
								select="reportdata/element[@tag='targetpoint']/chart/headers/columns" />
							<xsl:with-param name="rows"
								select="reportdata/element[@tag='targetpoint']/chart/rows" />
							<xsl:with-param name="chartwidth" select="$twidth" />
							<xsl:with-param name="imagewidth" select="$iwidth" />
							<xsl:with-param name="colwidths">
								<fo:table-column column-width="20%" />
								<fo:table-column column-width="20%" />
								<fo:table-column column-width="20%" />
								<fo:table-column column-width="40%" />
							</xsl:with-param>

						</xsl:call-template>
					</fo:block>

					<!-- Progress Graph -->
					<fo:block space-before="1cm">
					<fo:leader leader-pattern="rule" leader-length="100%"
								rule-style="solid" rule-thickness="2pt" />
						<xsl:variable name="iwidth">
							105mm
						</xsl:variable>
						<xsl:call-template name="graphonly">
							<xsl:with-param name="urls"
								select="reportdata/element[@tag='targetprogress']/urls" />
							<xsl:with-param name="imagewidth" select="$iwidth" />
						</xsl:call-template>
					</fo:block>


					<!-- Category Data -->
					<xsl:variable name="iwidth">
						65mm
					</xsl:variable>
					<xsl:for-each select="reportdata/element[@tag='allcategories']/members">
						<fo:block space-before="1cm">
							<fo:leader leader-pattern="rule" leader-length="100%"
								rule-style="solid" rule-thickness="2pt" />
							<xsl:call-template name="summarygraphchart2">
								<xsl:with-param name="urls" select="urls" />
								<xsl:with-param name="tblhdr" select="chart/headers/columns" />
								<xsl:with-param name="rows" select="chart/rows" />
								<xsl:with-param name="imagewidth" select="$iwidth" />
								<xsl:with-param name="chartwidth">
									75mm
								</xsl:with-param>
								<xsl:with-param name="colwidths">
									<fo:table-column column-width="33%" />
									<fo:table-column column-width="33%" />
									<fo:table-column column-width="33%" />
								</xsl:with-param>
							</xsl:call-template>
						</fo:block>
					</xsl:for-each>


				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

</xsl:stylesheet>
<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">

<xsl:output method="xml" indent="yes"/>

<xsl:variable name="defaultFontSize">12</xsl:variable>
<xsl:variable name="departureDateTimeFontSize">9</xsl:variable>
<xsl:variable name="fromToWaypointFontSize">9</xsl:variable>
<xsl:variable name="registrationFontSize">10</xsl:variable>
<xsl:variable name="frequencyFontSize">9</xsl:variable>

<xsl:variable name="frequencyFormat">###.##</xsl:variable>
<xsl:variable name="trackFormat">000</xsl:variable>
<xsl:variable name="headingFormat">000</xsl:variable>
<xsl:variable name="fuelFormat">##0.0</xsl:variable>


<!-- Only in xsl 2.0 -->
<!-- <xsl:function name="f:formatFrequency"> -->
<!-- 	<xsl:param name="frequency"/> -->
<!-- 			<xsl:choose> -->
<!-- 				<xsl:when test="frequency != ''"> -->
<!-- 					<xsl:value-of select="format-number(frequency,$frequencyFormat)"/> -->
<!-- 				</xsl:when> -->
<!-- 				<xsl:otherwise> -->
<!-- 					<xsl:value-of select="frequency"/> -->
<!-- 				</xsl:otherwise> -->
<!-- 			</xsl:choose> -->
<!-- </xsl:function> -->

<xsl:template match="navLogReportBean">
	<fo:root>
	
		<fo:layout-master-set>
			<fo:simple-page-master
				margin-right="15pt"
				margin-left="15pt"
				margin-bottom="15pt"
				margin-top="15pt"
				page-width="8.5in"
				page-height="11in"
				master-name="first">
				<fo:region-body />
			</fo:simple-page-master>
		</fo:layout-master-set>
		
		<fo:page-sequence master-reference="first">
			<fo:flow flow-name="xsl-region-body">

				<!-- table 1 nav log timers -->
				<fo:table table-layout="fixed" width="100%"> <!-- set to fixed and 100% as apache fop does not support the default of auto -->
					<fo:table-body>
						
						<fo:table-row>
				
							<xsl:apply-templates select="registration"/>
							<xsl:apply-templates select="pic"/>
							<xsl:apply-templates select="departureDateTime[1]"/>
							<xsl:apply-templates select="fromAirport"/>
							<xsl:apply-templates select="toAirport"/>
							<xsl:apply-templates select="alternate"/>
				
						</fo:table-row>

						<fo:table-row>

							<xsl:call-template name="navLogTimersRow"/>

						</fo:table-row>
						
					</fo:table-body>
				</fo:table>

				<!-- table 2 fuel log -->
				<fo:table table-layout="fixed" width="100%" space-before="10pt"> <!-- set to fixed and 100% as apache fop does not support the default of auto -->
					<fo:table-body>
						
						<fo:table-row>

							<xsl:call-template name="fuelLogRow1"/>

						</fo:table-row>
						<fo:table-row>

							<xsl:call-template name="fuelLogRow2or3"/>

						</fo:table-row>
						<fo:table-row>

							<xsl:call-template name="fuelLogRow2or3"/>

						</fo:table-row>
						
					</fo:table-body>
				</fo:table>

				<!-- table 3 destination and alternate route -->
				<fo:table table-layout="fixed" width="100%" space-before="10pt"> <!-- set to fixed and 100% as apache fop does not support the default of auto -->
					<fo:table-body>
						
						<fo:table-row>

							<xsl:call-template name="destinationNavLogDetailRouteHeader"/>

						</fo:table-row>



						<xsl:apply-templates select="destinationNavLogDetailRouteDtoList"/>

						<fo:table-row>

							<xsl:call-template name="approachLine"/>

						</fo:table-row>

						<fo:table-row>

							<xsl:apply-templates select="primaryTotalsMap"/>

						</fo:table-row>



						<xsl:apply-templates select="alternateNavLogDetailRouteDtoList"/>

						<fo:table-row>

							<xsl:call-template name="approachLine"/>

						</fo:table-row>

						<fo:table-row>

							<xsl:apply-templates select="alternateTotalsMap"/>

						</fo:table-row>

					</fo:table-body>
				</fo:table>

				<!-- page break -->
				<!-- table 4 upper winds -->
				<fo:table table-layout="fixed" width="80%" space-before="10pt" page-break-before="always"> <!-- set to fixed and 60% as apache fop does not support the default of auto -->
				
					<fo:table-column column-number="1" column-width="proportional-column-width(1.8)"/>
					<fo:table-column column-number="2" column-width="proportional-column-width(1)"/>
					<fo:table-column column-number="3" column-width="proportional-column-width(1)"/>
					<fo:table-column column-number="4" column-width="proportional-column-width(1)"/>
					<fo:table-column column-number="5" column-width="proportional-column-width(1)"/>
					<fo:table-column column-number="6" column-width="proportional-column-width(1)"/>
					
					<fo:table-body>
						
						<xsl:apply-templates select="navLogDetailUpperWindsDtoList"/>

					</fo:table-body>
				</fo:table>

				<!-- table with two columns to hold fuel calculation table and filing route -->
				<fo:table table-layout="fixed" width="100%" space-before="10pt"> <!-- set to fixed and 60% as apache fop does not support the default of auto -->

					<fo:table-column column-number="1" column-width="proportional-column-width(1)"/>
					<fo:table-column column-number="2" column-width="proportional-column-width(2.5)"/>

					<fo:table-body>

						<fo:table-row>
						
						<!-- fuel calculation table -->
						<fo:table-cell>
							<fo:table table-layout="fixed" width="100%"> <!-- set to fixed and 60% as apache fop does not support the default of auto -->
			
								<fo:table-column column-number="1" column-width="proportional-column-width(2.5)"/>
								<fo:table-column column-number="2" column-width="proportional-column-width(1)"/>
								
								<fo:table-body>

									<fo:table-row>

										<xsl:call-template name="defaultTableCell">
											<xsl:with-param name="value" select="'START &amp; TAXI'"/>
										</xsl:call-template>

										<xsl:call-template name="defaultTableCell">
											<xsl:with-param name="value" select="startAndTaxiFuel"/>
										</xsl:call-template>
									
									</fo:table-row>
								
									<fo:table-row>

										<xsl:call-template name="defaultTableCell">
											<xsl:with-param name="value" select="'CLIMB'"/>
										</xsl:call-template>
									
										<xsl:call-template name="defaultTableCell">
											<xsl:with-param name="value" select="climbFuel"/>
										</xsl:call-template>
									
									</fo:table-row>

									<fo:table-row>

										<xsl:call-template name="defaultTableCell">
											<xsl:with-param name="value" select="'TO DESTINATION'"/>
										</xsl:call-template>

										<xsl:call-template name="defaultTableCell">
											<xsl:with-param name="value" select="format-number(toPrimaryFuel,$fuelFormat)"/>
										</xsl:call-template>
									
									</fo:table-row>
								
									<fo:table-row>

										<xsl:call-template name="defaultTableCell">
											<xsl:with-param name="value" select="'TO ALTERNATE'"/>
										</xsl:call-template>
									
										<xsl:call-template name="defaultTableCell">
											<xsl:with-param name="value" select="format-number(toAlternateFuel,$fuelFormat)"/>
										</xsl:call-template>
									
									</fo:table-row>

									<fo:table-row>

										<xsl:call-template name="defaultTableCell">
											<xsl:with-param name="value" select="'RESERVE'"/>
										</xsl:call-template>

										<xsl:call-template name="defaultTableCell">
											<xsl:with-param name="value" select="reserveFuel"/>
										</xsl:call-template>
									
									</fo:table-row>
								
									<fo:table-row>

										<xsl:call-template name="defaultTableCell">
											<xsl:with-param name="value" select="'TOTAL NEEDED'"/>
										</xsl:call-template>
									
										<xsl:call-template name="defaultTableCell">
											<xsl:with-param name="value" select="format-number(totalFuelRequired,$fuelFormat)"/>
										</xsl:call-template>
									
									</fo:table-row>

									<fo:table-row>

										<xsl:call-template name="defaultTableCell">
											<xsl:with-param name="value" select="'ON BOARD'"/>
										</xsl:call-template>

										<xsl:call-template name="defaultTableCell">
											<xsl:with-param name="value" select="fuelOnBoard"/>
										</xsl:call-template>
									
									</fo:table-row>
								
									<fo:table-row>

										<xsl:call-template name="defaultTableCell">
											<xsl:with-param name="value" select="'REMAINING'"/>
										</xsl:call-template>
									
										<xsl:call-template name="defaultTableCell">
											<xsl:with-param name="value" select="format-number(remainingFuel,$fuelFormat)"/>
										</xsl:call-template>
									
									</fo:table-row>


								</fo:table-body>
								
							</fo:table>
						</fo:table-cell>
						
						<!-- filing route -->
						<fo:table-cell border="0pt" display-align="before" padding-left="20pt"> <!-- before is like top -->
							<fo:block font-family="Times Roman" font-weight="bold" text-align="left"     font-size="12">
								<xsl:value-of select="concat('FILING ROUTE: ',filingRoute)"/>
							</fo:block>
						</fo:table-cell>

						</fo:table-row>
						
					</fo:table-body>

				</fo:table>
				
			</fo:flow>
		</fo:page-sequence>
		
	</fo:root>
</xsl:template>

<xsl:template name="defaultTableCell">
	<xsl:param name="value" select="''"/>
	<xsl:param name="fontSize" select="$defaultFontSize"/> <!-- default is 12 -->
	<xsl:param name="backgroundColor" select="''"/> <!-- default is blank -->
	<xsl:param name="paddingTop" select="''"/> <!-- default is blank -->
	<xsl:param name="paddingBottom" select="''"/> <!-- default is blank -->
	<xsl:param name="numberColumnsSpanned" select="''"/> <!-- default is blank -->
	
<!-- 	apache fop does not support shorthand {variable} substitution in attribute values. have to use xsl:attribute -->
	
	<fo:table-cell border="0.7pt solid" display-align="center">
		<xsl:if test="$paddingTop != ''">
			<xsl:attribute name="padding-top">
				<xsl:value-of select="$paddingTop" />
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="$paddingBottom != ''">
			<xsl:attribute name="padding-bottom">
				<xsl:value-of select="$paddingBottom" />
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="$backgroundColor != ''">
			<xsl:attribute name="background-color">
				<xsl:value-of select="$backgroundColor" />
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="$numberColumnsSpanned != ''">
			<xsl:attribute name="number-columns-spanned">
				<xsl:value-of select="$numberColumnsSpanned" />
			</xsl:attribute>
		</xsl:if>
		
		<fo:block font-family="Times Roman" font-weight="bold" text-align="center">
			<xsl:attribute name="font-size">
				<xsl:value-of select="$fontSize" />
			</xsl:attribute>
			
			<xsl:value-of select="$value"/>
			
		</fo:block>
		
	</fo:table-cell>
	
</xsl:template>

<xsl:template match="registration">

	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'A/C'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="."/>
		<xsl:with-param name="fontSize" select="$registrationFontSize"/>
	</xsl:call-template>

</xsl:template>

<xsl:template match="pic">

	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'PIC'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="."/>
	</xsl:call-template>

</xsl:template>

<xsl:template match="departureDateTime">

	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'DATE'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="substring(.,1,10)"/>
		<xsl:with-param name="fontSize" select="$departureDateTimeFontSize"/>
	</xsl:call-template>

</xsl:template>

<xsl:template match="fromAirport">

	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'FROM'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="."/>
		<xsl:with-param name="backgroundColor" select="'grey'"/>
	</xsl:call-template>

</xsl:template>

<xsl:template match="toAirport">

	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'TO'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="."/>
		<xsl:with-param name="backgroundColor" select="'grey'"/>
	</xsl:call-template>

</xsl:template>

<xsl:template match="alternate">

	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'ALT'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="."/>
		<xsl:with-param name="backgroundColor" select="'grey'"/>
	</xsl:call-template>

</xsl:template>

<xsl:template name="navLogTimersRow">

	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'START'"/>
		<xsl:with-param name="paddingTop" select="'6'"/>
		<xsl:with-param name="paddingBottom" select="'5'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell"/>
	
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'UP'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell"/>
	
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'DOWN'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell"/>
	
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'SHUT'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell"/>
	
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'AIR'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell"/>

	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'FLT'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell"/>
						
</xsl:template>

<xsl:template name="fuelLogRow1">

	<xsl:call-template name="defaultTableCell"/>
	
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'TIME'"/>
		<xsl:with-param name="paddingTop" select="'6'"/>
		<xsl:with-param name="paddingBottom" select="'5'"/>
	</xsl:call-template>
	
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'GALONS'"/>
	</xsl:call-template>

	<xsl:call-template name="defaultTableCell"/>
	
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'TIME'"/>
	</xsl:call-template>
	
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'GALONS'"/>
	</xsl:call-template>
							
</xsl:template>

<xsl:template name="fuelLogRow2or3">

	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'LEFT TANK'"/>
		<xsl:with-param name="paddingTop" select="'6'"/>
		<xsl:with-param name="paddingBottom" select="'5'"/>
	</xsl:call-template>
	
	<xsl:call-template name="defaultTableCell"/>
	
	<xsl:call-template name="defaultTableCell"/>
	
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'RIGHT TANK'"/>
	</xsl:call-template>
	
	<xsl:call-template name="defaultTableCell"/>
	
	<xsl:call-template name="defaultTableCell"/>
	
</xsl:template>

<xsl:template name="destinationNavLogDetailRouteHeader">

	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'RT ALT'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'FROM TO'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'FREQ 1 FREQ 2'"/>
		<xsl:with-param name="fontSize" select="$frequencyFontSize"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'PWR RPM'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'IAS TAS'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'TRK'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'HDG'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'COMP HDG'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'G/S'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'DIST'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'TIME'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'ETA RETA'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'TIME OVER'"/>
	</xsl:call-template>
	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'FUEL'"/>
	</xsl:call-template>

</xsl:template>


<xsl:template match="destinationNavLogDetailRouteDtoList|alternateNavLogDetailRouteDtoList">

	<fo:table-row>

		<xsl:call-template name="defaultTableCell">
			<xsl:with-param name="value" select="concat(route,' ',altitude)"/>
		</xsl:call-template>

		<xsl:call-template name="defaultTableCell">
			<xsl:with-param name="value" select="concat(fromWaypoint,' ',toWaypoint)"/>
			<xsl:with-param name="fontSize" select="$fromToWaypointFontSize"/>		
		</xsl:call-template>

		<xsl:variable name="frequency1Formatted">
			<xsl:choose>
				<xsl:when test="frequency1 != ''">
					<xsl:value-of select="format-number(frequency1,$frequencyFormat)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="frequency1"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="frequency2Formatted">
			<xsl:choose>
				<xsl:when test="frequency2 != ''">
					<xsl:value-of select="format-number(frequency2,$frequencyFormat)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="frequency2"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:call-template name="defaultTableCell">
			<xsl:with-param name="value" select="concat($frequency1Formatted,' ',$frequency2Formatted)"/>
		</xsl:call-template>

		<xsl:call-template name="defaultTableCell"/>

		<xsl:call-template name="defaultTableCell"/>
		
		<xsl:call-template name="defaultTableCell">
			<xsl:with-param name="value" select="format-number(track,$trackFormat)"/>
		</xsl:call-template>
		
		<xsl:call-template name="defaultTableCell">
			<xsl:with-param name="value" select="format-number(heading,$headingFormat)"/>
		</xsl:call-template>

		<xsl:call-template name="defaultTableCell"/>
		
		<xsl:call-template name="defaultTableCell">
			<xsl:with-param name="value" select="groundSpeed"/>
		</xsl:call-template>

		<xsl:call-template name="defaultTableCell">
			<xsl:with-param name="value" select="distance"/>
		</xsl:call-template>

		<xsl:call-template name="defaultTableCell">
			<xsl:with-param name="value" select="time"/>
		</xsl:call-template>

		<xsl:call-template name="defaultTableCell"/>

		<xsl:call-template name="defaultTableCell"/>
		
		<xsl:call-template name="defaultTableCell">
			<xsl:with-param name="value" select="format-number(fuel,$fuelFormat)"/>
		</xsl:call-template>

	</fo:table-row>

</xsl:template>

<xsl:template name="approachLine">

	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'1 APPROACH'"/>
		<xsl:with-param name="numberColumnsSpanned" select="'9'"/>
	</xsl:call-template>

	<xsl:call-template name="defaultTableCell"/>
	
<!-- 	<xsl:call-template name="defaultTableCell"> -->
<!-- 		<xsl:with-param name="value" select="'15'"/> -->
<!-- 	</xsl:call-template> -->
	<xsl:call-template name="defaultTableCell"/>

	<xsl:call-template name="defaultTableCell"/>
	<xsl:call-template name="defaultTableCell"/>

	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="format-number(3,$fuelFormat)"/>
	</xsl:call-template>

</xsl:template>

<xsl:template match="primaryTotalsMap|alternateTotalsMap">

	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="'TOTALS'"/>
		<xsl:with-param name="numberColumnsSpanned" select="'9'"/>
	</xsl:call-template>

	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="entry[key='distance']/value"/>
	</xsl:call-template>

	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="entry[key='time']/value"/>
	</xsl:call-template>
	
	<xsl:call-template name="defaultTableCell"/>
	<xsl:call-template name="defaultTableCell"/>

	<xsl:call-template name="defaultTableCell">
		<xsl:with-param name="value" select="format-number(entry[key='fuel']/value,$fuelFormat)"/>
	</xsl:call-template>

</xsl:template>

<xsl:template match="navLogDetailUpperWindsDtoList">

	<!-- header row -->
	<xsl:if test="position() = 1">
	
		<fo:table-row>
	
			<xsl:call-template name="defaultTableCell">
				<xsl:with-param name="value" select="concat('FOR USE ',substring(forUseFrom,12,5),'-',substring(forUseTo,12,5))"/>
			</xsl:call-template>
		
			<xsl:call-template name="defaultTableCell">
				<xsl:with-param name="value" select="3000"/>
			</xsl:call-template>
		
			<xsl:call-template name="defaultTableCell">
				<xsl:with-param name="value" select="6000"/>
			</xsl:call-template>
		
			<xsl:call-template name="defaultTableCell">
				<xsl:with-param name="value" select="9000"/>
			</xsl:call-template>
		
			<xsl:call-template name="defaultTableCell">
				<xsl:with-param name="value" select="12000"/>
			</xsl:call-template>
		
			<xsl:call-template name="defaultTableCell">
				<xsl:with-param name="value" select="18000"/>
			</xsl:call-template>
		
		</fo:table-row>
	
	</xsl:if>

	<fo:table-row>

		<xsl:call-template name="defaultTableCell">
			<xsl:with-param name="value" select="stationId"/>
		</xsl:call-template>
	
		<xsl:call-template name="defaultTableCell">
			<xsl:with-param name="value" select="windsAt3k"/>
		</xsl:call-template>
	
		<xsl:call-template name="defaultTableCell">
			<xsl:with-param name="value" select="windsAt6k"/>
		</xsl:call-template>
	
		<xsl:call-template name="defaultTableCell">
			<xsl:with-param name="value" select="windsAt9k"/>
		</xsl:call-template>
	
		<xsl:call-template name="defaultTableCell">
			<xsl:with-param name="value" select="windsAt12k"/>
		</xsl:call-template>
	
		<xsl:call-template name="defaultTableCell">
			<xsl:with-param name="value" select="windsAt18k"/>
		</xsl:call-template>

	</fo:table-row>

</xsl:template>

<xsl:template match="climbFuel">
	<fo:block>
		climbFuel: <xsl:value-of select="."/>
	</fo:block>
</xsl:template>

</xsl:stylesheet>
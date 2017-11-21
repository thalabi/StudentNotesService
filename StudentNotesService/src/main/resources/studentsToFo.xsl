<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="fo">
  
  <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
  
  <xsl:param name="noData" select="'No stundent notes found'"/> 
  <xsl:param name="timeGenerated" select="'N/A'"/> 
  <xsl:param name="reportTitle" select="'Student Notes'"/>

  <!-- ========================= -->
  <!-- root element: students -->
  <!-- ========================= -->
  
  <xsl:template match="students">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
    
      <fo:layout-master-set>
        <fo:simple-page-master master-name="simpleA4" page-width="8.5in" page-height="11in"
        						margin-top="2em" margin-bottom="2em" margin-left="3em" margin-right="3em">
        						
          <fo:region-body margin-top="2em" margin-bottom="2em"/>
		  <fo:region-before extent="2em"/> <!-- extent should be equal or less than margin-top of region-body -->
		  <fo:region-after extent="2em"/> <!-- extent should be equal or less than margin-bottom of region-body -->
        </fo:simple-page-master>
      </fo:layout-master-set>
      
      <fo:page-sequence master-reference="simpleA4">

		<fo:static-content flow-name="xsl-region-before">
	        <fo:block text-align="center">
		        <xsl:value-of select="$reportTitle"/>
	        </fo:block>
        </fo:static-content>
		<fo:static-content flow-name="xsl-region-after">
            <fo:table table-layout="fixed" width="100%" border-collapse="separate">
              <fo:table-column column-width="45%"/>
              <fo:table-column column-width="10%"/>
              <fo:table-column column-width="45%"/>
              <fo:table-body>
                <fo:table-row>
			      <fo:table-cell>
				      <fo:block>
				      </fo:block>
			      </fo:table-cell>                
			      <fo:table-cell>
				      <fo:block text-align="center">
						<fo:page-number/>
				      </fo:block>
			      </fo:table-cell>                
			      <fo:table-cell>
				      <fo:block text-align="right" font-size="8pt">
						Report generated on <xsl:value-of select="$timeGenerated"/>			      
				      </fo:block>
			      </fo:table-cell>                
				</fo:table-row>              
              </fo:table-body>
            </fo:table>
        </fo:static-content>
        
        <fo:flow flow-name="xsl-region-body">
			<xsl:choose>
				<xsl:when test="student">
	                <xsl:apply-templates select="student"/>
	            </xsl:when>
	            <xsl:otherwise>
			  		<fo:block font-style="italic">
			  			<xsl:value-of select="$noData"></xsl:value-of>
					</fo:block>          
	            </xsl:otherwise>
			</xsl:choose>

        </fo:flow>

      </fo:page-sequence>
      
    </fo:root>    
  </xsl:template>
  <!-- ========================= -->
  <!-- child element: student     -->
  <!-- ========================= -->
  <xsl:template match="student">
  		<fo:block>
          <fo:inline font-size="12pt" font-weight="bold"><xsl:value-of select="concat(firstName, ' ', lastName, ', ', grade/grade)"/></fo:inline>
		</fo:block>          
        
		<fo:block space-before="0.3cm">
			<xsl:apply-templates select="notes"/>
		</fo:block>
		<fo:block space-before="0.3cm">
		</fo:block>
		
  </xsl:template>
  <!-- ========================= -->
  <!-- child element: notes     -->
  <!-- ========================= -->
  <xsl:template match="notes">
          <fo:block font-size="10pt">
<!--             <fo:table table-layout="fixed" width="100%" border-collapse="separate"> -->
<!--               <fo:table-column column-width="4cm"/> -->
<!--               <fo:table-column column-width="16cm"/> -->
<!--               <fo:table-body> -->
                <xsl:apply-templates select="note"/>
<!--               </fo:table-body> -->
<!--             </fo:table> -->
          </fo:block>
  </xsl:template>  
  <!-- ========================= -->
  <!-- child element: note     -->
  <!-- ========================= -->
  <xsl:template match="note">
<!--     <fo:table-row> -->
<!--       <xsl:if test="function = 'lead'"> -->
<!--         <xsl:attribute name="font-weight">bold</xsl:attribute> -->
<!--       </xsl:if> -->
<!--       <fo:table-cell> -->
        <fo:block space-after="0.2cm">
          <xsl:value-of select="timestamp"/> - <xsl:value-of select="text"/>
        </fo:block>
<!--       </fo:table-cell> -->
<!--     </fo:table-row> -->
  </xsl:template>
  
</xsl:stylesheet>
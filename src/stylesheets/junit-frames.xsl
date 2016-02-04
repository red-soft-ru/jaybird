<?xml version='1.0' encoding='ISO-8859-1'?>

<xsl:stylesheet	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
	xmlns:redirect="http://xml.apache.org/xalan/redirect"
	extension-element-prefixes="redirect">

<xsl:variable name='java.version' select="//property[@name='java.version']/@value"/>
<xsl:variable name='java.vendor' select="//property[@name='java.vendor']/@value"/>
<xsl:variable name='java.vm.name' select="//property[@name='java.vm.name']/@value"/>
<xsl:variable name='java.vm.version' select="//property[@name='java.vm.version']/@value"/>
<xsl:variable name='java.vm.info' select="//property[@name='java.vm.info']/@value"/>
<xsl:variable name='os.name' select="//property[@name='os.name']/@value"/>
<xsl:variable name='os.version' select="//property[@name='os.version']/@value"/>
<xsl:variable name='os.arch' select="//property[@name='os.arch']/@value"/>
<xsl:variable name='TODAY' select="//property[@name='TODAY']/@value"/>


<!-- ======================================================================

    Stylesheet to transform an XML file generated by the Ant JUnit task into
    a set of JavaDoc-like HTML page to make pages more convenient to be browsed.
    
    It use the Xalan redirect extension to write to multiple output files.
    
    Note: HTML output can be made much more clean by removing non css attributes

    ====================================================================== -->
<xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>
<xsl:decimal-format decimal-separator="." grouping-separator=","/>

<!--
    Xalan redirect extension writes relative file based on the parent directory
    from the main output file, unfortunately, this is never set and you have
    to do it yourself on the API. The code that does it in command line was
    commented out in Xalan 1.2.2 :-(
    
    Therefore I will use a stylesheet param for the output directory.
    
    This has to be invoked as follows from the command line:
    
    java -classpath bsf.jar;xalan.jar;xerces.jar org.apache.xalan.xslt.Process -IN testsuites.xml -XSL junit-frames.xsl -PARAM output.dir './report'
-->
<xsl:param name="output.dir" select="'.'"/>


<xsl:template match="testsuites">
	<!-- create the index.html -->
	<redirect:write file="{$output.dir}/index.html">
		<xsl:call-template name="index.html"/>
	</redirect:write>

	<!-- create the stylesheet.css -->
	<redirect:write file="{$output.dir}/stylesheet.css">
		<xsl:call-template name="stylesheet.css"/>
	</redirect:write>

	<!-- create the overview-packages.html at the root -->
	<redirect:write file="{$output.dir}/overview-summary.html">
		<xsl:apply-templates select="." mode="overview.packages"/>
	</redirect:write>

	<!-- create the all-packages.html at the root -->
	<redirect:write file="{$output.dir}/overview-frame.html">
		<xsl:apply-templates select="." mode="all.packages"/>
	</redirect:write>
	
	<!-- create the all-classes.html at the root -->
	<redirect:write file="{$output.dir}/allclasses-frame.html">
		<xsl:apply-templates select="." mode="all.classes"/>
	</redirect:write>
	
	<!-- process all packages -->
	<xsl:for-each select="./testsuite[not(./@package = preceding-sibling::testsuite/@package)]">
		<xsl:call-template name="package">
			<xsl:with-param name="name" select="@package"/>
		</xsl:call-template>
	</xsl:for-each>
</xsl:template>


<xsl:template name="package">
	<xsl:param name="name"/>
	<xsl:variable name="package.dir">
		<xsl:if test="not($name = '')"><xsl:value-of select="translate($name,'.','/')"/></xsl:if>
		<xsl:if test="$name = ''">.</xsl:if>
	</xsl:variable>	
	<!--Processing package <xsl:value-of select="@name"/> in <xsl:value-of select="$output.dir"/> -->
	<!-- create a classes-list.html in the package directory -->
	<redirect:write file="{$output.dir}/{$package.dir}/package-frame.html">
		<xsl:call-template name="classes.list">
			<xsl:with-param name="name" select="$name"/>
		</xsl:call-template>
	</redirect:write>
	
	<!-- create a package-summary.html in the package directory -->
	<redirect:write file="{$output.dir}/{$package.dir}/package-summary.html">
		<xsl:call-template name="package.summary">
			<xsl:with-param name="name" select="$name"/>
		</xsl:call-template>
	</redirect:write>
	
	<!-- for each class, creates a @name.html -->
	<!-- @bug there will be a problem with inner classes having the same name, it will be overwritten -->
	<xsl:for-each select="/testsuites/testsuite[@package = $name]">
		<redirect:write file="{$output.dir}/{$package.dir}/{@name}.html">
			<xsl:apply-templates select="." mode="class.details"/>
		</redirect:write>
	</xsl:for-each>
</xsl:template>

<xsl:template name="index.html">
<html>
	<head>
		<title>Firebirdsql pure java jca-jdbc driver Test Suite Results</title>
	</head>
	<frameset cols="20%,80%">
		<frameset rows="30%,70%">
			<frame src="overview-frame.html" name="packageListFrame"/>
			<frame src="allclasses-frame.html" name="classListFrame"/>
		</frameset>
		<frame src="overview-summary.html" name="classFrame"/>
	</frameset>
	<noframes>
		<h2>Frame Alert</h2>
		<p>
		This document is designed to be viewed using the frames feature. If you see this message, you are using a non-frame-capable web client.
		</p>
	</noframes>
</html>
</xsl:template>


<!-- this is the stylesheet css to use for nearly everything -->
<xsl:template name="stylesheet.css">
body {
	font:normal 68% verdana,arial,helvetica;
	color:#000000;
}
td {
	font-size: 68%
}
p {
	line-height:1.5em;
	margin-top:0.5em; margin-bottom:1.0em;
}
h1 {
	margin: 0px 0px 5px;
	font: 165% verdana,arial,helvetica
}
h2 {
	margin-top: 1em;
	margin-bottom: 0.5em;
	font: bold 125% verdana,arial,helvetica
}
h3 {
	margin-bottom: 0.5em;
	font: bold 115% verdana,arial,helvetica
}
h4 {
	margin-bottom: 0.5em;
	font: bold 100% verdana,arial,helvetica
}
h5 {
	margin-bottom: 0.5em;
	font: bold 100% verdana,arial,helvetica
}
h6 {
	margin-bottom: 0.5em;
	font: bold 100% verdana,arial,helvetica
}
.Error {
	font-weight:bold;
	color:#FFFFFF;
}
.Failure {
	font-weight:bold;
	color:#FFFFFF;
}
.ErrorDetail {
	font-weight:bold;
	color:#FF0000;
}
.FailureDetail {
	font-weight:bold;
	color:#800080;
}
.Info {
  font-size: x-small
}

</xsl:template>


<!-- ======================================================================
    This page is created for every testsuite class.
    It prints a summary of the testsuite and detailed information about
    testcase methods.
     ====================================================================== -->
<xsl:template match="testsuite" mode="class.details">
	<xsl:variable name="package.name" select="@package"/>
	<html>
		<head>
			<xsl:call-template name="create.stylesheet.link">
				<xsl:with-param name="package.name" select="$package.name"/>
			</xsl:call-template>
		</head>
		<body>
			<xsl:call-template name="pageHeader"/>	
			<h3>Class <xsl:if test="not($package.name = '')"><xsl:value-of select="$package.name"/>.</xsl:if><xsl:value-of select="@name"/></h3>

			
			<table border="0" cellpadding="5" cellspacing="2" width="95%">
				<xsl:call-template name="testsuite.test.header"/>
				<xsl:apply-templates select="." mode="print.test"/>
			</table>
	
			<h2>Tests</h2>
			<p>
			<table border="0" cellpadding="5" cellspacing="2" width="95%">
				<xsl:call-template name="testcase.test.header"/>
				<xsl:apply-templates select="./testcase" mode="print.test"/>
			</table>
			
			</p>
			<xsl:call-template name="pageFooter"/>	
		</body>
	</html>
</xsl:template>


<!-- ======================================================================
    This page is created for every package.
    It prints the name of all classes that belongs to this package.
    @param name the package name to print classes.
     ====================================================================== -->
<!-- list of classes in a package -->
<xsl:template name="classes.list">
	<xsl:param name="name"/>
	<html>
		<head>
			<xsl:call-template name="create.stylesheet.link">
				<xsl:with-param name="package.name" select="$name"/>
			</xsl:call-template>
		</head>
		<body>
			<table width="100%">
				<tr>
					<td nowrap="nowrap">
						<h2><a href="package-summary.html" target="classFrame"><xsl:value-of select="$name"/></a></h2>
					</td>
				</tr>
			</table>
	
			<h2>Classes</h2>
			<p>
			<table width="100%">
				<xsl:for-each select="/testsuites/testsuite[./@package = $name]">
					<xsl:sort select="@name"/>
					<tr>
						<td nowrap="nowrap">
							<a href="{@name}.html" target="classFrame"><xsl:value-of select="@name"/></a>
						</td>
					</tr>
				</xsl:for-each>
			</table>
			</p>
		</body>
	</html>
</xsl:template>


<!--
	Creates an all-classes.html file that contains a link to all package-summary.html
	on each class.
-->
<xsl:template match="testsuites" mode="all.classes">
	<html>
		<head>
			<xsl:call-template name="create.stylesheet.link">
				<xsl:with-param name="package.name"/>
			</xsl:call-template>
		</head>
		<body>
			<h2>Classes</h2>
			<p>
			<table width="100%">
				<xsl:apply-templates select="testsuite" mode="all.classes">
					<xsl:sort select="@name"/>
				</xsl:apply-templates>
			</table>
			</p>
		</body>
	</html>
</xsl:template>

<xsl:template match="testsuite" mode="all.classes">
	<!-- (ancestor::package)[last()] is buggy in MSXML3, fixed in SP1? -->
	<xsl:variable name="package.name" select="@package"/>
	<tr>
		<td nowrap="nowrap">
			<a target="classFrame">
				<xsl:attribute name="href">
					<xsl:if test="not($package.name='')">
						<xsl:value-of select="translate($package.name,'.','/')"/><xsl:text>/</xsl:text>
					</xsl:if><xsl:value-of select="@name"/><xsl:text>.html</xsl:text>
				</xsl:attribute>
				<xsl:value-of select="@name"/>
			</a>
		</td>
	</tr>
</xsl:template>


<!--
	Creates an html file that contains a link to all package-summary.html files on
	each package existing on testsuites.
	@bug there will be a problem here, I don't know yet how to handle unnamed package :(
-->
<xsl:template match="testsuites" mode="all.packages">
	<html>
		<head>
			<xsl:call-template name="create.stylesheet.link">
				<xsl:with-param name="package.name"/>
			</xsl:call-template>
		</head>
		<body>
			<h2><a href="overview-summary.html" target="classFrame">Home</a></h2>
			<h2>Packages</h2>
			<p>
				<table width="100%">
					<xsl:apply-templates select="testsuite[not(./@package = preceding-sibling::testsuite/@package)]" mode="all.packages">
						<xsl:sort select="@package"/>
					</xsl:apply-templates>
				</table>
			</p>
		</body>
	</html>
</xsl:template>

<xsl:template match="testsuite" mode="all.packages">
	<tr>
		<td nowrap="nowrap">
			<a href="{translate(@package,'.','/')}/package-summary.html" target="classFrame">
				<xsl:value-of select="@package"/>
			</a>
		</td>
	</tr>
</xsl:template>


<xsl:template match="testsuites" mode="overview.packages">
	<html>
		<head>
			<xsl:call-template name="create.stylesheet.link">
				<xsl:with-param name="package.name"/>
			</xsl:call-template>
		</head>
		<body>
		<xsl:call-template name="pageHeader"/>
		<h2>Summary</h2>
		<xsl:variable name="testCount" select="sum(testsuite/@tests)"/>
		<xsl:variable name="errorCount" select="sum(testsuite/@errors)"/>
		<xsl:variable name="failureCount" select="sum(testsuite/@failures)"/>
		<xsl:variable name="timeCount" select="sum(testsuite/@time)"/>
		<xsl:variable name="successRate" select="($testCount - $failureCount - $errorCount) div $testCount"/>
		<table border="0" cellpadding="5" cellspacing="2" width="95%">
		<tr bgcolor="#A6CAF0" valign="top">
			<td><strong>Tests</strong></td>
			<td><strong>Failures</strong></td>
			<td><strong>Errors</strong></td>
			<td><strong>Success rate</strong></td>
			<td><strong>Time</strong></td>
		</tr>
		<tr bgcolor="#FFEBCD" valign="top">
			<xsl:attribute name="class">
				<xsl:choose>
					<xsl:when test="$errorCount &gt; 0">ErrorDetail</xsl:when>
					<xsl:when test="$failureCount &gt; 0">FailureDetail</xsl:when>
					<xsl:otherwise>Pass</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<td><xsl:value-of select="$testCount"/></td>
			<td><xsl:value-of select="$failureCount"/></td>
			<td><xsl:value-of select="$errorCount"/></td>
			<td>
				<xsl:call-template name="display-percent">
					<xsl:with-param name="value" select="$successRate"/>
				</xsl:call-template>
			</td>
			<td>
				<xsl:call-template name="display-time">
					<xsl:with-param name="value" select="$timeCount"/>
				</xsl:call-template>
			</td>

		</tr>
		</table>
		<table border="0" width="95%">
		<tr>
		<td	style="text-align: justify;">
		Note: <em>failures</em> are anticipated and checked for with assertions while <em>errors</em> are unanticipated.
		</td>
		</tr>
		</table>
		
		<h2>Packages</h2>
		<table border="0" cellpadding="5" cellspacing="2" width="95%">
			<xsl:call-template name="testsuite.test.header"/>
			<xsl:for-each select="testsuite[not(./@package = preceding-sibling::testsuite/@package)]">
				<xsl:sort select="@package" order="ascending"/>
				<!-- get the node set containing all testsuites that have the same package -->
				<xsl:variable name="insamepackage" select="/testsuites/testsuite[./@package = current()/@package]"/>
				<tr bgcolor="#FFEBCD" valign="top">
				    <xsl:if test='sum($insamepackage/@errors)!=0 or sum($insamepackage/@failures)!=0'><xsl:attribute name='bgcolor'>#FF0000</xsl:attribute>
                    </xsl:if>

					<!-- display a failure if there is any failure/error in the package -->
					<xsl:attribute name="class">
						<xsl:choose>
							<xsl:when test="sum($insamepackage/@errors) &gt; 0">Error</xsl:when>
							<xsl:when test="sum($insamepackage/@failures) &gt; 0">Failure</xsl:when>
							<xsl:otherwise>Pass</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<td><a href="{translate(@package,'.','/')}/package-summary.html">
					    <font color='#000000'>
        				    <xsl:if test='sum($insamepackage/@errors)!=0 or 
                                                          sum($insamepackage/@failures)!=0'>
                                                  <xsl:attribute name='color'>#FFFFFF</xsl:attribute>
                                            </xsl:if>
					    <xsl:value-of select="@package"/></font></a></td>
					<td><xsl:value-of select="sum($insamepackage/@tests)"/></td>
					<td><xsl:value-of select="sum($insamepackage/@errors)"/></td>
					<td><xsl:value-of select="sum($insamepackage/@failures)"/></td>
					<td>
					<xsl:call-template name="display-time">
						<xsl:with-param name="value" select="sum($insamepackage/@time)"/>
					</xsl:call-template>
					</td>
				</tr>
			</xsl:for-each>
		</table>
    	<xsl:call-template name="pageFooter"/>	
		</body>
		</html>
</xsl:template>


<xsl:template name="package.summary">
	<xsl:param name="name"/>
	<html>
		<head>
			<xsl:call-template name="create.stylesheet.link">
				<xsl:with-param name="package.name" select="$name"/>
			</xsl:call-template>
		</head>
		<body>
			<xsl:attribute name="onload">open('package-frame.html','classListFrame')</xsl:attribute>
			<xsl:call-template name="pageHeader"/>
			<h3>Package <xsl:value-of select="$name"/></h3>
			
			<!--table border="0" cellpadding="5" cellspacing="2" width="95%">
				<xsl:call-template name="class.metrics.header"/>
				<xsl:apply-templates select="." mode="print.metrics"/>
			</table-->
			
			<xsl:variable name="insamepackage" select="/testsuites/testsuite[./@package = $name]"/>
			<xsl:if test="count($insamepackage) &gt; 0">
				<h2>Classes</h2>
				<p>
				<table border="0" cellpadding="5" cellspacing="2" width="95%">
					<xsl:call-template name="testsuite.test.header"/>
					<xsl:apply-templates select="$insamepackage" mode="print.test">
						<xsl:sort select="@name"/>
					</xsl:apply-templates>
				</table>
				</p>
			</xsl:if>
    	<xsl:call-template name="pageFooter"/>	
		</body>
	</html>
</xsl:template>


<!--
    transform string like a.b.c to ../../../
    @param path the path to transform into a descending directory path
-->
<xsl:template name="path">
	<xsl:param name="path"/>
	<xsl:if test="contains($path,'.')">
		<xsl:text>../</xsl:text>	
		<xsl:call-template name="path">
			<xsl:with-param name="path"><xsl:value-of select="substring-after($path,'.')"/></xsl:with-param>
		</xsl:call-template>	
	</xsl:if>
	<xsl:if test="not(contains($path,'.')) and not($path = '')">
		<xsl:text>../</xsl:text>	
	</xsl:if>
</xsl:template>


<!-- create the link to the stylesheet based on the package name -->
<xsl:template name="create.stylesheet.link">
	<xsl:param name="package.name"/>
	<link rel="stylesheet" type="text/css" title="Style"><xsl:attribute name="href"><xsl:if test="not($package.name = 'unnamed package')"><xsl:call-template name="path"><xsl:with-param name="path" select="$package.name"/></xsl:call-template></xsl:if>stylesheet.css</xsl:attribute></link>
</xsl:template>


<!-- Page HEADER -->
<xsl:template name="pageHeader">
	<h1>Unit Test Results</h1>
	<table width="100%">
	<tr>
		<td align="left"></td>
		<td align="right">Designed for use with <a href='http://www.junit.org'>JUnit</a> and <a href='http://jakarta.apache.org'>Ant</a>.
		Generated on <xsl:value-of select="$TODAY"/>.
		</td>
	</tr>
	</table>
	<hr size="1"/>
</xsl:template>

<!-- Page FOOTER -->
<xsl:template name="pageFooter">
    <p/>
	<hr size="1"/>
    <p/>
	<table class="Info">
	<tr>
	  <td>Java Version</td><td><xsl:value-of select="$java.version"/></td>
	</tr><tr>
	  <td>Java Vendor</td><td><xsl:value-of select="$java.vendor"/></td>
	</tr><tr>
	  <td>Java VM Name</td><td><xsl:value-of select="$java.vm.name"/></td>
	</tr><tr>
	  <td>Java VM Version</td><td><xsl:value-of select="$java.vm.version"/></td>
	</tr><tr>
	  <td>Java VM Info</td><td><xsl:value-of select="$java.vm.info"/></td>
	</tr><tr>
	  <td>OS Name</td><td><xsl:value-of select="$os.name"/></td>
	</tr><tr>
	  <td>OS Version</td><td><xsl:value-of select="$os.version"/></td>
	</tr><tr>
	  <td>OS Arch</td><td><xsl:value-of select="$os.arch"/></td>
	</tr>
	</table>
</xsl:template>

<!-- class header -->
<xsl:template name="testsuite.test.header">
	<tr bgcolor="#A6CAF0" valign="top">
		<td width="80%"><strong>Name</strong></td>
		<td><strong>Tests</strong></td>
		<td><strong>Errors</strong></td>
		<td><strong>Failures</strong></td>
		<td nowrap="nowrap"><strong>Time(s)</strong></td>
	</tr>
</xsl:template>

<!-- method header -->
<xsl:template name="testcase.test.header">
	<tr bgcolor="#A6CAF0" valign="top">
		<td><strong>Name</strong></td>
		<td><strong>Status</strong></td>
		<td width="80%"><strong>Type</strong></td>
		<td nowrap="nowrap"><strong>Time(s)</strong></td>
	</tr>
</xsl:template>


<!-- class information -->
<xsl:template match="testsuite" mode="print.test">
	<tr bgcolor="#FFEBCD" valign="top">		
	    <xsl:if test='@errors!=0 or @failures!=0'><xsl:attribute name='bgcolor'>#FF0000</xsl:attribute>
        </xsl:if>
		<xsl:attribute name="class">
			<xsl:choose>
				<xsl:when test="@errors[.&gt; 0]">Error</xsl:when>
				<xsl:when test="@failures[.&gt; 0]">Failure</xsl:when>
				<xsl:otherwise>Pass</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>
		<td><a href="{@name}.html"><xsl:value-of select="@name"/></a></td>
		<td><xsl:apply-templates select="@tests"/></td>
		<td><xsl:apply-templates select="@errors"/></td>
		<td><xsl:apply-templates select="@failures"/></td>
		<td><xsl:call-template name="display-time">
				<xsl:with-param name="value" select="@time"/>
			</xsl:call-template>
        </td>
	</tr>
</xsl:template>

<xsl:template match="testcase" mode="print.test">
	<tr bgcolor="#FFEBCD" valign="top">
	    <xsl:attribute name="class">
			<xsl:choose>
				<xsl:when test="error">ErrorDetail</xsl:when>
				<xsl:when test="failure">FailureDetail</xsl:when>
				<xsl:otherwise>TableRowColor</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>
		<td><xsl:value-of select="@name"/></td>
		<xsl:choose>
			<xsl:when test="failure">
				<td>Failure</td>
				<td><xsl:apply-templates select="failure"/></td>
			</xsl:when>
			<xsl:when test="error">
				<td>Error</td>
				<td><xsl:apply-templates select="error"/></td>
			</xsl:when>
			<xsl:otherwise>
				<td>Success</td>
				<td></td>
			</xsl:otherwise>
		</xsl:choose>
		<td>
		    <xsl:call-template name="display-time">
                <xsl:with-param name="value" select="@time"/>
		    </xsl:call-template>
		</td>
    </tr>
</xsl:template>


<!-- Note : the below template error and failure are the same style
            so just call the same style store in the toolkit template -->
<xsl:template match="failure">
	<xsl:call-template name="display-failures"/>
</xsl:template>

<xsl:template match="error">
	<xsl:call-template name="display-failures"/>
</xsl:template>

<!-- Style for the error and failure in the testcase template -->
<xsl:template name="display-failures">
	<xsl:choose>
		<xsl:when test="not(@message)">N/A</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="@message"/>
		</xsl:otherwise>
	</xsl:choose>
	<!-- display the stacktrace -->
	<code>
		<p/>
		<xsl:call-template name="br-replace">
			<xsl:with-param name="word" select="."/>
		</xsl:call-template>
	</code>
	<!-- the latter is better but might be problematic for non-21" monitors... -->
	<!--pre><xsl:value-of select="."/></pre-->
</xsl:template>

<!--
	template that will convert a carriage return into a br tag
	@param word the text from which to convert CR to BR tag
-->
<xsl:template name="br-replace">
	<xsl:param name="word"/>
	<xsl:choose>
		<xsl:when test="contains($word,'&#xA;')">
			<xsl:value-of select="substring-before($word,'&#xA;')"/>
			<br/>
			<xsl:call-template name="br-replace">
				<xsl:with-param name="word" select="substring-after($word,'&#xA;')"/>
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$word"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="display-time">
	<xsl:param name="value"/>
	<xsl:value-of select="format-number($value,'0.000')"/>
</xsl:template>

<xsl:template name="display-percent">
	<xsl:param name="value"/>
	<xsl:value-of select="format-number($value,'0.00%')"/>
</xsl:template>
</xsl:stylesheet>
	

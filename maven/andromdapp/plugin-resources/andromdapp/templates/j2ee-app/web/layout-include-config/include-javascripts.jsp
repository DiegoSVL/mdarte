<%@ page session="false" %>

<%
pageContext.setAttribute("layout",
org.andromda.presentation.bpm4struts.LayoutConfiguration.instance().getLayoutConfiguration());
%>

<%--
Adicionar javascript usando o seguinte formato:
<script type="text/javascript" language="Javascript1.1" src="/contexto/layout/${layout}/<nome_arquivo>.js"></script>
--%>

<script type="text/javascript" language="Javascript1.1" src="/SistemaAcademico/layout/${layout}/javaScripts/dimmingdiv.js"></script>
<script type="text/javascript" language="Javascript1.1" src="/SistemaAcademico/layout/${layout}/javaScripts/layout-common.js"></script>
<script type="text/javascript" language="Javascript1.1" src="/SistemaAcademico/layout/${layout}/javaScripts/key-events.js"></script>
<script type="text/javascript" language="Javascript1.1" src="/SistemaAcademico/layout/${layout}/javaScripts/scripts.js"></script>
<script type="text/javascript" language="Javascript1.1" src="/SistemaAcademico/layout/${layout}/javaScripts/hints.js"></script>
<%-- Struts 2 --%>
<script type="text/javascript" language="Javascript1.1" src="/SistemaAcademico/layout/${layout}/javaScripts/jquery.min.js"></script>
<script type="text/javascript" language="Javascript1.1" src="/SistemaAcademico/layout/${layout}/javaScripts/jquery.elastic.source.js"></script>
<script type="text/javascript" language="Javascript1.1" src="/SistemaAcademico/layout/${layout}/javaScripts/jquery.dataTables.js"></script>
<script type="text/javascript" language="Javascript1.1" src="/SistemaAcademico/layout/${layout}/javaScripts/jquery.form.js"></script>
<script type="text/javascript" language="Javascript1.1" src="/SistemaAcademico/layout/${layout}/javaScripts/jquery.displaytag-ajax-1.2-mdarte.js"></script>
<script type="text/javascript" language="Javascript1.1" src="/SistemaAcademico/layout/${layout}/javaScripts/jquery.easytabs.js"></script>
<script type="text/javascript" language="Javascript1.1" src="/SistemaAcademico/layout/${layout}/javaScripts/strutsValidations.js"></script>
<script type="text/javascript" language="Javascript1.1" src="/SistemaAcademico/layout/${layout}/javaScripts/jquery.ba-hashchange.min.js"></script>
<script type="text/javascript" language="Javascript1.1" src="/SistemaAcademico/layout/${layout}/javaScripts/bootstrap.min.js"></script>
<script type="text/javascript" language="Javascript1.1" src="/SistemaAcademico/layout/${layout}/javaScripts/bootstrap-datepicker.js"></script>

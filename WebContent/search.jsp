<%@ page language="java" contentType="text/html; charset=GBK"
	pageEncoding="GBK" import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>Insert title here</title>
<link type="text/css" rel="stylesheet" href="./css/new.css">
<script type="text/javascript" src="./js/mvc.js"></script>
</head>
<body >
	<%
	String ikv = (String) request.getAttribute("ikv");
	if (ikv != null && ikv != null)
		ikv = (String) request.getAttribute("ikv");
	else
		ikv = "";
	
	Integer idv = (Integer) request.getAttribute("idv");
	if (idv != null && idv >= 1)
		idv = (Integer) request.getAttribute("idv");
	else
		idv = 1;
	
	Integer fdv = (Integer) request.getAttribute("fdv");
	if (fdv != null && fdv >= 8)
		fdv = (Integer) request.getAttribute("fdv");
	else
		fdv = 8;
	
	Integer pn = (Integer) request.getAttribute("pn");
	
	%>
	<div id="Main">
		 <form id="searchForm" action="search" onsubmit="return check()">
			<table>
				<tbody>
					<tr>
						<td>
							<div>
								<a class="pic" href="http://210.51.166.48:8080/WebContent/search.jsp">
									<img width="90" style="vertical-align: middle" src="./images/logo.png">
								</a>
									<input name="searchWord" id="searchId" type="text" size="20" width="20px" value="<%=ikv%>"> 
									<input class="input_b" id="doSearch" type="submit" value="��   ��">
							</div>
							<div class="p">
								  <label class="lb"> 
									<input class="pub" type="radio" value="1" name="dot"> ����
								</label> 
								<label class="lb"> 
									<input class="pub" type="radio" value="2" name="dot"> ΢��
								</label> 
								<label class="lb"> 
									<input class="pub" type="radio" value="3" name="dot"> ��̳
								</label> 
								<label class="lb"> 
									<input class="pub" type="radio" value="4" name="dot"> ����
								</label>
								<script type="text/javascript">
									var id = <%=idv%>;
									setDot(id);
								</script>								
							</div>
							<div class="p">
								<label class="lb">
									<input class="pub" type="radio" value="8" name="dotF"> ����
								</label>
								<label class="lb">
									<input class="pub" type="radio" value="9" name="dotF"> ����
								</label>
								<script type="text/javascript">
									var fid = <%=fdv%>;
									setDotF(fid);
								</script>
							</div>
						</td>
					</tr>
				</tbody>
			</table>
		</form>
		<table class="tclass">
			<tbody>
			<%
			boolean isContinue = true;
			List<?> all = (List<?>) request.getAttribute("allinfo");
			if (all != null) {
				Iterator<?> iter = all.iterator();
				while (iter.hasNext()) {
					pageContext.setAttribute("info", iter.next());
			%>
			<tr>
				<td class="title">
					<a class="link" href="${info.inlink}" target="_blank">${info.title}</a>
					<div class="date">
						<a class="inlink" href="${info.link}" target="_blank">
							<font color="#4F4F4F">${info.origsrc}</font>
						</a>
						${info.time}
					</div>
				</td>
			</tr>
			<tr>
				<td>
					<div class="txt">${info.text}
						<a class="replink" href="${info.replink}" target="_blank">
							<font color="#008000">${info.repnum}</font>
						</a>
					</div>
				</td>
			</tr>
			<%
				}
			%>
			<%
				}
			%>
			</tbody>
		</table>
		<%
		if (ikv != null && ikv != "" && all.size() <= 20 && all != null) { 
		%>
		<p id="page" >
		<%
		if (pn > 1) { 
		%>
			<a class="n" href="search?searchWord=<%=ikv%>&dot=<%=idv%>&page=<%=pn - 1%>&dotF=<%=fdv%>">&lt;��һҳ</a>
			<%} %>
			<%
			/*final int BASE = 10;
			int remain = pn % BASE;
			int start = 0;
			int end = 0;
			if (remain == 1) {
				start = pn;
				end = start + 9;
			}
			else {
				
			}
			
			if (remain == 0) {
				start = pn - 9;
				end = pn;
			}*/
				
			for(int i = 1; i <= 20; i++) {
			%>
			<a class="n" href="search?searchWord=<%=ikv%>&dot=<%=idv%>&page=<%=i%>&dotF=<%=fdv%>">
				<%
				if (i == pn) {
				%>
				<strong>
					<span class="ssp">
						<%=i %>
					</span>
				</strong>
				<%
				}
				else {
				%>
				<span class="sp">
					<%=i %>
				</span>
				<%
				} 
				%>
			</a>
			<%
			if (i == 20)
				isContinue = false;
			}
			%>
			<%
			if (isContinue)
			%>
			<a class="n" href="search?searchWord=<%=ikv%>&dot=<%=idv%>&page=<%=pn + 1%>&dotF=<%=fdv%>">��һҳ&gt;</a>
			<%
			} 
			%>
		</p>
	</div>
	<div class="footer">
		 �Ż�������ҿ�������ṩ����֧��������Ѷ�Ĳɼ���������ɵ��Գ����Զ���������ʾ��ʱ������ڷ�ӳ���Ǳ���
		��ҿ����Ѷ�з�����ʱ�����µ�ʱ�䡣
	</div>
</body>
</html>
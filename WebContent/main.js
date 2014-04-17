$(document).ready(function(){
	
	var returnValues;
	var retrievalType = $("#retrievalType").val();
	
		$ ("#retrievalType").on("change",function(){
			retrievalType= $("#retrievalType").val();
			if (retrievalType == "PageRank")
			{
				$("#probabilityWDiv").css('display', 'inline-block');
				$("#probabilityW").val("0.8");
			}
			else
				$("#probabilityWDiv").css('display', 'none');
		});

		$("#Search").on("click",function (){
//			alert("Your book is overdue" + retrievalType + $("#query").val());
			if ($("#query").val() == "")
				alert("Please enter a query !!");
			
			if ($("#probabilityW").val() == "" && $("#probabilityWDiv").css("display") == 'inline-block')
				alert("Please enter the value of W !!");
			else
			{
				$.ajax({
			
				type: "GET",
				url: "http://localhost:8080/IRWebService/rest/queryresponse?retrievalType=" + retrievalType + "&query=" + $("#query").val() + "&probabilityW=" + $("#probabilityW").val(),
				contentType: "application/json; charset=utf-8",
				dataType: "json",
				success: function ajaxCallSucceed(response) {
					returnValues = response;
					$("#QueryResponse").html('');
					$("#authorityResults").html('');
					$("#hubResults").html('');
					if (retrievalType == "TFIDF")
					{
						for (var i = 0; i < returnValues.TFIDF.length; i ++ )
						{
							$("#QueryResponse").append(returnValues.TFIDF[i].id);
							$("#QueryResponse").append("<a href=\"" + returnValues.TFIDF[i].url +"\" > Document</a>");
							$("#QueryResponse").append("<br />");
						}
					}
					
					else if (retrievalType == "AuthorityHub")
					{
						for (var i = 0; i < returnValues.Authority.length ; i ++ )
						{
							$("#authorityResults").append(returnValues.Authority[i].id);
							$("#authorityResults").append("<a href=\"" + returnValues.Authority[i].url +"\" > Document</a>");
							$("#authorityResults").append("<br />");
						
							$("#hubResults").append(returnValues.Hub[i].id);
							$("#hubResults").append("<a href=\"" + returnValues.Hub[i].url +"\" > Document</a>");
							$("#hubResults").append("<br />");
						}
					}
					
					else if (retrievalType == "PageRank")
					{
						for (var i = 0; i < returnValues.PageRank.length; i ++ )
						{
							$("#QueryResponse").append(returnValues.PageRank[i].id);
							$("#QueryResponse").append("<a href=\"" + returnValues.PageRank[i].url +"\" > Document</a>");
							$("#QueryResponse").append("<br />");
						}
					}
				},
				failure: function ajaxCallFailure() {
					alert("Failure!!");
				}
				});
			}
		});
})
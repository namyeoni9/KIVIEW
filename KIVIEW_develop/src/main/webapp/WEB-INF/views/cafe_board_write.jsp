<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<% request.setCharacterEncoding("UTF-8"); %>
<% response.setContentType("text/html; charset=UTF-8"); %>

<%--  <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>   --%>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE html>
<html lang="en">
  <head>
    <title>KIVIEW &mdash; Blog</title>
    
    <%@ include file = "head.jsp" %>
    


  </head>
  <body id = "body">
 <%@include file="cafe_modal.jsp" %> 
     <!-- @@ header 부분 @@ -->
     <%@ include file = "header.jsp" %>
    
     <!-- @@ <h1 class = "mb-2 bread"> sub title 이 부분 우선 header에서 따로 빼놨어요!!! </h1> @@ -->
    <section class="hero-wrap hero-wrap-2" style="background-image: url('images/bg_2.jpg');">
      <div class="overlay"></div>
      <div class="container">
        <div class="row no-gutters slider-text align-items-center justify-content-center">
          <div class="col-md-9 ftco-animate text-center">
            <h1 class="mb-2 bread">Our Blog</h1>
            <p class="breadcrumbs"><span class="mr-2"><a href="index.jsp">Home <i class="ion-ios-arrow-forward"></i></a></span> <span>Blog <i class="ion-ios-arrow-forward"></i></span></p>
          </div>
        </div>
      </div>
    </section>
     <!-- @@ header 끝 @@ -->
     
      
      <section class="ftco-section bg-light">
         <div class="container" style="margin-left:30px; ">   
         <div class="row" style="width:1400px; overflow:auto;"> 
            <!-- 카페 메뉴 -->
            <div class="col-lg-3 sidebar ftco-animate" style="padding:25px; margin-right:30px;  background-color:white; border:1px solid lightgray;" >
                               
               <%@include file="cafe_sidebar.jsp" %>

            </div>

            <!-- 카페 홈  -->
            
            <div id="home" class="col-lg-8 ftco-animate" style="padding:25px; margin-left:0px; background-color:white; border:1px solid lightgray; "> 
               <h2 class="mb-3"><b># 글작성</b></h2>
               <hr>
               <form action="cafeboardinsert.do" method="get">
               <input type="hidden" name="cafe_menu_no" value="${cafe_menu_no }">
               <input type="hidden" name="cafe_no" value="${cafe_list[0].cafe_no }">
               <input type="hidden" name="writer" value="${login.member_id}">
               <input type="hidden" name="category" value="카테고리인서트테스트">
               <label>말머리 선택.</label><br>
             
               <select name="menu_name" id="boardcategory1">
               <c:forEach var="menu" items="${cafe_list[1] }">
                        
                  <c:if test="${menu.cafe_menu_no eq cafe_menu_no}">                  
                     <option value="${menu.cafe_menu_no }">${menu.name }</option>
                  </c:if>   
               </c:forEach>   
               </select> 

               <select name="categoryname" id="boardcategory2">
                  <option selected="selected">말머리 선택</option>                  
               </select>
                
               
               <br>               
               <label>제 목</label><br>
                  <input type="text"  placeholder="제목을 입력하세요." name="title" size="70" minlength="4" maxlength="30" required>
                  <br><br>
               <label>내 용</label><br>
                  <textarea cols="90" rows="10" name="content" minlength="4" maxlength="1000" required></textarea>  
                  
               <br>
               <br>
               <div align="center">
               <input type="submit" value="작 성" class="btn btn-secondary" style="width:20%">
               <input type="button" value="취 소" class="btn btn-primary" style="width:20%" onclick="location.href='cafeboardlist.do?cafe_menu_no=${cafe_menu_no}&cafe_no=${cafe_no }'"> 
               </div>
               </form>
               
               <br>
               <br>
            </div>
            

            

         </div>


         
         


      </div>
      </section>

      
   <!-- @@ footer 영역 @@ -->
   <%@ include file="footer.jsp"%>
   
   


    
  </body>
<script type="text/javascript">

$("#boardcategory1").on("change",function(){
   var no = $("#boardcategory1 option:selected").val();
   console.log("게시메뉴 번호     :    "+no);
   
   $("#boardcategory2").empty();
      
   if(no==null||no==""){
      alert("게시판을 선택하세요.");
      
   }else{
      $.ajax({
         type:"post",
         url:"ajaxcategory.do",
         data:{"no":no},
         
         dataType:"json",
         success:function(key){
            alert("통신성공!");        
           
           $.each(key.category2,function(index,item){
              console.log(index);
              console.log(item);
              console.log(item.category.length);
              if(item.category.length==0){
               alert("말머리가 존재하지 않습니다.");                            
               $("#boardcategory2").hide();
              }else{
                 
                 var newOpt=$('<option>').html();
                  var rlist = '';
                  
                  rlist += '<option value='+item.category+'>'+item.category+'</option>';
                   
                  console.log(rlist);            
                  
                  $("#boardcategory2").append(rlist);      

                 
              }
              
               
              
           });
            
               
               
            
         },
         error : function(request,status,error){
            alert("통신 실패");
            alert(request);
            alert(status);
            alert(error);
            
            
         }
      });
   }
});





</script>   
</html>
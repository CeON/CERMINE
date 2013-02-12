<%@page contentType="text/html" pageEncoding="windows-1250"%>
<div class="animation">
    <div id="block1" class="block"></div>
    <div id="block2" class="block"></div>
    <div id="block3" class="block"></div>
    <div id="block4" class="block"></div>
    <div id="block5" class="block"></div>
    <div id="block6" class="block"></div>
</div>

<script type="text/javascript" >
    
/* Loading */
var tab = new Array("#block1", "#block2", "#block3", "#block4", "#block5", "#block6");
var pointer = 0;
function growBlock(id){
	$(id).animate({padding : "8px", margin: "12px" }, 400);
}

function reset(){
	for(var i = 0; i <= 6; i ++){
		$(tab[i]).css({"padding":"0px", "margin":"20px"});
	}
	pointer = 0;
}

function action(){
	growBlock(tab[pointer]);
	pointer== 8 ? setTimeout("reset()", 200) : pointer+=1;
	setTimeout("action()", 200);
}    
    
$(document).ready(function(){
	setTimeout("action()", 0);
});    
    
</script>
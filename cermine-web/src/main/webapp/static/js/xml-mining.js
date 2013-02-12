
var rotationForw = function (){
	start();
	$("#xml-mining").find("img#axe").rotate({
		  duration: 2000,
	      angle:-60, 
	      animateTo:60, 
	      callback: rotationForw,
	      easing: //$.easing.easeInExpo
	    	  	    	  
	    	  function (x,t,b,c,d){        // t: current time, b: begInnIng value, c: change In value, d: duration
	    	  
	    	if(t >= d/2)
	    		  return -120-2*(t/(d))*c +b;
	    	  else
	    		  return 2*(t/(d))*c +b;
	    	 
	    	// return (t/(d))*c +b;
	      }
	   });
	
}
var rotationWait = function (){
	sleep(1000);
	rotationForw();
	
}


var rotationBack = function (){
	$("#xml-mining").find("img#axe").rotate({
		  duration: 1500,
	      angle:100, 
	      animateTo:-30, 
	      callback: rotationForw,
	      easing: $.easing.easeOutCirc
	    	  /* 	  
	    	 function (x,t,b,c,d){        // t: current time, b: begInnIng value, c: change In value, d: duration
	         return (t/(d))*c +b;
	      }*/
	   });
	start();
	//throwXmlLine();
}

var throwXmlLine = function(){
	$("#xml-mining").find("#xml-parser").animate({ 
							        left: "+=150px",
							    }, 1500 ,function(){
							    	comeBackXmlLine();
							    });
	
}
var comeBackXmlLine = function(){
	$("#xml-mining").find("#xml-parser").css("left", "200px");
	
}


$(document).ready(function(){
	rotationForw();
});


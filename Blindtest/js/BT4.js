/****Affiche les boutons permettant d'aimer une chanson et de l'ajouter a une nouvelle playlist******/
function buttons(){
	btnReStart = document.createElement("button");

	btnReStart.textContent = "Recommencer une partie";
		document.body.appendChild(btnReStart);
		btnReStart.className="btn btn btn-warning";

	btnReStart.onclick=function(){
		  document.location.href="base.html"; 

	}

	var btnAjoutPlaylist = document.createElement("button");
	btnAjoutPlaylist.textContent = "Ajouter les musiques  aimées a une playlist";
	btnAjoutPlaylist.className="btn  btn-success";
	document.body.appendChild(btnAjoutPlaylist);

	btnAjoutPlaylist.onclick=function(){
		//sendByFacebook();
	var label2 = document.createElement("label");
	label2.textContent = "Donnez un nom a votre playlist";

	input2 = document.createElement("input");
	input2.type="text";
	input2.style.display="block"
	input2.style.margin= 0;
	input2.style.fontFamily="sans-serif";
	input2.style.fontSize="18px";
	input2.style.appearance="none";
	input2.style.boxShadow="none";
	
	label2.appendChild(input2);

	document.getElementById("div3").appendChild(label2);
	var name;
		input2.onkeypress=function(e){
	        if (e.keyCode==13) {
				
					name=input2.value;
					document.getElementById("div3").removeChild(label2);

			}

	}
	var newPlaylist = {
	title: name,
	tracks: liked
	};
	ddp.call('plugin_createPlaylist', [appID, userID, newPlaylist], "Added");

	}

}




	
function loadEnd(){
	
 window.addEventListener("load",buttons,false);
 var tab= document.createElement("table");
 
}


	//var param1=eval(self.location.href.split('?')[1].split(';'));
